/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.zipline

import android.content.Context
import app.cash.zipline.Zipline
import app.cash.zipline.ZiplineManifest
import app.cash.zipline.loader.DefaultFreshnessCheckerNotFresh
import app.cash.zipline.loader.FreshnessChecker
import app.cash.zipline.loader.LoadResult
import app.cash.zipline.loader.ManifestVerifier
import app.cash.zipline.loader.ZiplineLoader
import dev.msfjarvis.claw.android.BuildConfig
import dev.msfjarvis.claw.api.LobstersParserClient
import dev.msfjarvis.claw.parser.LobstersParserService
import dev.msfjarvis.claw.parser.model.ParserSerializersModule
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okio.ByteString.Companion.decodeHex
import okio.FileSystem
import okio.Path.Companion.toPath

@Inject
@ContributesBinding(AppScope::class, binding = binding<LobstersParserClient>())
class AndroidZiplineParserClient(
  private val context: Context,
  private val httpClient: OkHttpClient,
  private val manifestUrl: String = BuildConfig.ZIPLINE_PARSER_MANIFEST_URL,
  private val verifySignatures: Boolean = BuildConfig.ZIPLINE_PARSER_VERIFY_SIGNATURES,
) : LobstersParserClient, AutoCloseable {
  private val ziplineThread = AtomicReference<Thread>()
  private val dispatcher: ExecutorCoroutineDispatcher =
    Executors.newSingleThreadExecutor { runnable ->
        Thread(
          null,
          {
            ziplineThread.set(Thread.currentThread())
            runnable.run()
          },
          "Claw-ZiplineParser",
          ZIPLINE_THREAD_STACK_SIZE_BYTES,
        )
      }
      .asCoroutineDispatcher()
  private val mutex = Mutex()
  private var loadedZipline: Zipline? = null
  private var loadedService: LobstersParserService? = null

  override suspend fun service(): LobstersParserService {
    loadedService?.let {
      return it
    }
    return mutex.withLock { loadedService ?: loadService().also { loadedService = it } }
  }

  override fun close() {
    loadedService?.close()
    loadedService = null
    loadedZipline?.let { zipline -> onZiplineThread { zipline.close() } }
    loadedZipline = null
    dispatcher.close()
  }

  private suspend fun loadService(): LobstersParserService {
    extractEmbeddedArtifacts()

    val embeddedDir = File(context.codeCacheDir, "zipline-embedded/zipline")
    val embeddedManifestFile = File(embeddedDir, "zipline-parser.manifest.zipline.json")

    val manifestVerifier =
      if (verifySignatures) {
        ManifestVerifier.Builder()
          .addEd25519(
            "key0",
            "c960a123f76e0312cc05d9e358377b82d1e655d9569e9cff5f4834b1c4f228ce".decodeHex(),
          )
          .build()
      } else {
        ManifestVerifier.NO_SIGNATURE_CHECKS
      }

    val loader =
      ZiplineLoader(
          dispatcher = dispatcher,
          manifestVerifier = manifestVerifier,
          httpClient = httpClient,
        )
        .withEmbedded(
          embeddedFileSystem = FileSystem.SYSTEM,
          embeddedDir = embeddedDir.absolutePath.toPath(),
        )

    val freshnessChecker = if (verifySignatures) DefaultFreshnessCheckerNotFresh else AlwaysFresh
    val effectiveManifestUrl =
      if (!verifySignatures && embeddedManifestFile.exists()) {
        embeddedManifestFile.toURI().toString()
      } else {
        manifestUrl
      }

    return withContext(dispatcher) {
      when (
        val result =
          loader.loadOnce(
            applicationName = "zipline-parser",
            freshnessChecker = freshnessChecker,
            manifestUrl = effectiveManifestUrl,
            serializersModule = ParserSerializersModule,
          )
      ) {
        is LoadResult.Success -> {
          loadedZipline = result.zipline
          DispatcherConfinedLobstersParserService(result.zipline.take("LobstersParserService"))
        }
        is LoadResult.Failure -> throw result.exception
      }
    }
  }

  internal fun <T> onZiplineThread(block: () -> T): T {
    return if (Thread.currentThread() === ziplineThread.get()) {
      block()
    } else {
      runBlocking(dispatcher) { block() }
    }
  }

  private inner class DispatcherConfinedLobstersParserService(
    private val delegate: LobstersParserService
  ) : LobstersParserService {
    override fun parsePostsPage(html: String) = onZiplineThread { delegate.parsePostsPage(html) }

    override fun parsePostDetails(html: String) = onZiplineThread {
      delegate.parsePostDetails(html)
    }

    override fun parseUser(html: String) = onZiplineThread { delegate.parseUser(html) }

    override fun parseTagsPage(html: String) = onZiplineThread { delegate.parseTagsPage(html) }

    override fun parseSearchResults(html: String) = onZiplineThread {
      delegate.parseSearchResults(html)
    }

    override fun parseCsrfToken(html: String) = onZiplineThread { delegate.parseCsrfToken(html) }

    override fun parseReplyForm(html: String) = onZiplineThread { delegate.parseReplyForm(html) }

    override fun parseFiltersPage(html: String) = onZiplineThread {
      delegate.parseFiltersPage(html)
    }

    override fun close() = onZiplineThread { delegate.close() }
  }

  private fun extractEmbeddedArtifacts() {
    val assets = context.assets
    val ziplineAssets = assets.list("zipline").orEmpty()
    if (ziplineAssets.isEmpty()) return

    val embeddedDir = File(context.codeCacheDir, "zipline-embedded/zipline")
    embeddedDir.mkdirs()

    for (assetName in ziplineAssets) {
      assets.open("zipline/$assetName").use { input ->
        File(embeddedDir, assetName).outputStream().use { output ->
          input.copyTo(output)
        }
      }
    }
  }

  private companion object {
    private const val ZIPLINE_THREAD_STACK_SIZE_BYTES = 8L * 1024L * 1024L
  }

  /** Always prioritize the embedded files over the remote copy. */
  private object AlwaysFresh : FreshnessChecker {
    override fun isFresh(
      manifest: ZiplineManifest,
      freshAtEpochMs: Long,
    ): Boolean {
      return true
    }
  }
}

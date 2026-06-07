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
import app.cash.zipline.loader.FreshnessChecker
import app.cash.zipline.loader.LoadResult
import app.cash.zipline.loader.ManifestVerifier
import app.cash.zipline.loader.ZiplineLoader
import dev.msfjarvis.claw.api.LobstersParserClient
import dev.msfjarvis.claw.parser.LobstersParserService
import dev.msfjarvis.claw.parser.model.ParserSerializersModule
import java.io.File
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.days
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.OkHttpClient
import okio.ByteString.Companion.decodeHex
import okio.FileSystem
import okio.Path.Companion.toPath

class AndroidZiplineParserClient(
  private val context: Context,
  private val manifestUrl: String,
  private val httpClient: OkHttpClient,
  private val verifySignatures: Boolean,
) : LobstersParserClient, AutoCloseable {
  private val dispatcher: ExecutorCoroutineDispatcher =
    Executors.newSingleThreadExecutor().asCoroutineDispatcher()
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
    loadedZipline?.close()
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

    val freshnessChecker = if (verifySignatures) EmbeddedFreshnessChecker else AlwaysFresh
    val effectiveManifestUrl =
      if (!verifySignatures && embeddedManifestFile.exists()) {
        embeddedManifestFile.toURI().toString()
      } else {
        manifestUrl
      }

    return when (
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
        result.zipline.take("LobstersParserService")
      }
      is LoadResult.Failure -> throw result.exception
    }
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

  private object EmbeddedFreshnessChecker : FreshnessChecker {
    private val maxAgeMs = 30.days.inWholeMilliseconds

    override fun isFresh(
      manifest: ZiplineManifest,
      freshAtEpochMs: Long,
    ): Boolean {
      if (freshAtEpochMs <= 0L) return false
      return System.currentTimeMillis() - freshAtEpochMs <= maxAgeMs
    }
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

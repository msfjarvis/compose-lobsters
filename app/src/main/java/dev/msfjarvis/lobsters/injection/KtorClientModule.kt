package dev.msfjarvis.lobsters.injection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer

@Module
@InstallIn(ApplicationComponent::class)
object KtorClientModule {
  @Provides
  fun provideClient() = HttpClient(OkHttp) {
    install(JsonFeature) {
      serializer = KotlinxSerializer()
    }
    engine {
      config {
        followSslRedirects(true)
      }
    }
  }
}

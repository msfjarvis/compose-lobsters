import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dev.msfjarvis.claw.api.LobstersApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create

@OptIn(ExperimentalSerializationApi::class)
class Api {
  private val json = Json { ignoreUnknownKeys = true }

  private fun getOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .addNetworkInterceptor { chain ->
        val request = chain.request()
        println("LobstersApi: ${request.method()}: ${request.url()}")
        chain.proceed(request)
      }
      .build()
  }

  private fun getRetrofit(
    okHttpClient: OkHttpClient,
  ): Retrofit {
    val contentType = MediaType.get("application/json")
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(LobstersApi.BASE_URL)
      .addConverterFactory(json.asConverterFactory(contentType))
      .build()
  }

  val api: LobstersApi = getRetrofit(getOkHttpClient()).create()
}

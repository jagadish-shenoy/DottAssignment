package com.assignment.foursquare

/**
 * Koin Module for com.dott.foursquare package.
 */
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL_FOURSQUARE = "https://api.foursquare.com/v2/"

private const val KEY_CLIENT_ID = "client_id"

private const val KEY_CLIENT_SECRET = "client_secret"

private const val KEY_VERSION = "v"

val foursquareModule = module {

    single {
       Retrofit.Builder()
            .baseUrl(BASE_URL_FOURSQUARE)
            .addConverterFactory(get(GsonConverterFactory::class.java))
            .client(get(OkHttpClient::class.java))
            .build().create(VenueService::class.java)
    }

    factory<GsonConverterFactory> {
        GsonConverterFactory.create(
            GsonBuilder()
                .registerTypeAdapter(
                    Venues::class.java,
                    VenueSearchResultTypeAdapter()
                )
                .registerTypeAdapter(
                    VenueDetails::class.java,
                    VenueDetailsResultTypeAdapter(androidContext())
                )
                .create())
    }

    factory<OkHttpClient> {

        OkHttpClient.Builder().let {

            it.connectTimeout(10, TimeUnit.SECONDS)

            it.readTimeout(10, TimeUnit.SECONDS)

            it.addInterceptor { chain ->
                chain.proceed(chain.request().let { originalRequest ->
                    val modifiedUrl = originalRequest.url().newBuilder()
                        .addQueryParameter(KEY_CLIENT_ID, BuildConfig.FSQUARE_CLIENT_ID)
                        .addQueryParameter(KEY_CLIENT_SECRET, BuildConfig.FSQUARE_CLIENT_SECRET)
                        .addQueryParameter(KEY_VERSION, BuildConfig.FSQUARE_API_VERSION)
                        .build()
                    originalRequest.newBuilder().url(modifiedUrl).build()
                })
            }
        }.build()
    }

    factory {
        FoursquareDataSource(venueService = get())
    }
}
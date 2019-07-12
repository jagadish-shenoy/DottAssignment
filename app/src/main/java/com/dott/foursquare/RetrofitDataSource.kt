package com.dott.foursquare

import com.dott.assignment.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * [DataSource] which fetches the Venue Details from Network
 */
class RetrofitDataSource {

    private val venueService: VenueService = createVenueService()

    suspend fun searchVenues(lat: Double, long: Double, categoryId:String, radius:Int, limit: Int): VenueSearchResult {
            val response = venueService.searchVenues("$lat,$long", categoryId, radius, limit)
            return if (!response.isSuccessful || response.body() == null) {
                VenueSearchResult.Failure
            } else {
                VenueSearchResult.Success((response.body() as Venues).list)
            }
    }

    fun fetchVenueDetails(venueId: String): VenueDetailsResult {
        return try {
            val response = venueService.getVenueDetails(venueId).execute()
            if (response == null || !response.isSuccessful || response.body() == null) {
                VenueDetailsResult.Failure
            } else {
                VenueDetailsResult.Success(response.body() as VenueDetails)
            }
        } catch (e: IOException) {
            VenueDetailsResult.Failure
        }
    }

    private fun createVenueService(): VenueService {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL_FOURSQUARE)
                .addConverterFactory(createGsonConverter())
                .client(createClientWithDefaultParams())
                .build()
        return retrofit.create(VenueService::class.java)
    }

    private fun createGsonConverter() = GsonConverterFactory.create(GsonBuilder()
            .registerTypeAdapter(Venues::class.java, VenueSearchResultTypeAdapter())
            .registerTypeAdapter(VenueDetails::class.java, VenueDetailsResultTypeAdapter())
            .create())

    /**
     * Helps create an [OkHttpClient] with default request Query parameters.
     */
    private fun createClientWithDefaultParams(): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()

        httpClientBuilder.addInterceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url()

            val url = originalHttpUrl.newBuilder()
                    .addQueryParameter(KEY_CLIENT_ID, BuildConfig.FSQUARE_CLIENT_ID)
                    .addQueryParameter(KEY_CLIENT_SECRET, BuildConfig.FSQUARE_CLIENT_SECRET)
                    .addQueryParameter(KEY_VERSION, BuildConfig.FSQUARE_API_VERSION)
                    .build()

            // Request customization: add request headers
            val requestBuilder = original.newBuilder().url(url)
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        //Keep the connect times short
        httpClientBuilder.connectTimeout(10, TimeUnit.SECONDS)
        httpClientBuilder.readTimeout(10, TimeUnit.SECONDS)
        return httpClientBuilder.build()
    }
}
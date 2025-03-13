package com.tt.muzien.data.network

import android.content.Context
import com.tt.muzien.BuildConfig
import com.zabihah.ui.data.network.TokenInterceptor
import com.zabihah.ui.data.network.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Establish and perform Remote API calls using this class
 *
 */
class RemoteDataSource @Inject constructor() {

    companion object {
        private const val BASE_URL = BuildConfig.baseUrl
    }

    fun <Api> buildApi(
        api: Class<Api>,
        context: Context?
    ): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(TokenInterceptor(TokenManager(context!!))).also { client ->
                        val logging = HttpLoggingInterceptor()
                        logging.setLevel(BODY)
                        client.addInterceptor(logging)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                    }.build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}
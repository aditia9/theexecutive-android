package com.delamibrands.theexecutive.api

import com.delamibrands.theexecutive.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Mohammad Sunny on 25/1/18.
 */
class ApiClient {

    companion object {

        var retrofit: Retrofit? = null
        get() {
            field = field ?: Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(BuildConfig.API_URL)
                    .client(client)
                    .build()
            return field
        }

        private var gson : Gson? = null
        get() {
            field = field ?: GsonBuilder().create()
            return field
        }

        private var interceptor = HttpLoggingInterceptor()
        get() {
            if (BuildConfig.DEBUG)
                field.level = HttpLoggingInterceptor.Level.BODY
            else
                field.level = HttpLoggingInterceptor.Level.NONE
            return field
        }

        var client : OkHttpClient? = null
        get() {
            val dispatcher = Dispatcher()
            dispatcher.maxRequests = 1
            field = field ?: OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .dispatcher(dispatcher)
                    .connectTimeout(ApiConstants.CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .readTimeout(ApiConstants.CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .build()
            return  field
        }

    }

}
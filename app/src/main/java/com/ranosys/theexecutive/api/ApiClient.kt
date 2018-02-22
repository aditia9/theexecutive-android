package com.ranosys.theexecutive.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ranosys.theexecutive.BuildConfig
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Mohammad Sunny on 25/1/18.
 */
class ApiClient {

    companion object {

        var retrofit: Retrofit? = null
        get() {
            field = field ?: Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl("http://raview.net/")
                    .client(client)
                    .build()
            return field
        }
        set(value) {
            field = value
        }

        private var gson : Gson? = null
        get() {
            field = field ?: GsonBuilder().create()
            return field
        }
        set(value) {
            field = value
        }

        private var interceptor = HttpLoggingInterceptor()
        get() {
            if (BuildConfig.DEBUG)
                field.level = HttpLoggingInterceptor.Level.BODY
            else
                field.level = HttpLoggingInterceptor.Level.NONE
            return field
        }
        set(value) {
            field = value
        }

        var client : OkHttpClient? = null
        get() {
            val dispatcher = Dispatcher()
            dispatcher.maxRequests = 1
            field = field ?: OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .dispatcher(dispatcher)
                    .build()
            return  field
        }
        set(value) {
            field = value
        }

    }

}
package com.hci.appstore.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitModule {

    fun provideApi(baseUrl: String): RemoteCatalogApi {
        val client = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl) // Ej: "https://hubetcardenasi.github.io/AppStoreCatalog/"
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(RemoteCatalogApi::class.java)
    }
}
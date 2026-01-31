package com.hci.appstore.data.remote

import com.hci.appstore.data.model.StoreApp
import retrofit2.http.GET

interface RemoteCatalogApi {

    // Ejemplo: https://hubetcardenasi.github.io/AppStoreCatalog/catalog.json
    @GET("catalog.json")
    suspend fun getCatalog(): List<StoreApp>
}

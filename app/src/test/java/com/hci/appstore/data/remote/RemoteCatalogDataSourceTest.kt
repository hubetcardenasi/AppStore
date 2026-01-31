package com.hci.appstore.data.remote

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteCatalogDataSourceTest {

    private lateinit var server: MockWebServer
    private lateinit var api: RemoteCatalogApi
    private lateinit var ds: RemoteCatalogDataSource

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()

        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RemoteCatalogApi::class.java)

        ds = RemoteCatalogDataSource(api)
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `loadCatalog returns list when server responds 200`() = runBlocking {
        val json = """
            [
              {
                "id": "calc",
                "name": "Calculadora Pro",
                "packageName": "com.example.calcpro",
                "versionCode": 3,
                "versionName": "1.2.0",
                "icon": "icons/calc.png",
                "apk": "apks/calc_v3.apk",
                "shortDescription": "Una calculadora moderna",
                "longDescription": "Descripción larga",
                "sizeBytes": 2048000
              }
            ]
        """.trimIndent()

        server.enqueue(MockResponse().setBody(json).setResponseCode(200))

        val result = ds.loadCatalog()

        Assert.assertNotNull(result)
        Assert.assertEquals(1, result!!.size)
        Assert.assertEquals("calc", result[0].id)
    }

    @Test
    fun `loadCatalog returns null when server responds error`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(500))

        val result = ds.loadCatalog()

        Assert.assertNull(result)
    }
}
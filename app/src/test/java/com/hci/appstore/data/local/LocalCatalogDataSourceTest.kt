package com.hci.appstore.data.local

import android.content.Context
import android.content.res.AssetManager
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*
import java.io.ByteArrayInputStream

class LocalCatalogDataSourceTest {

    /**
     * Crea un Context y AssetManager falsos para simular archivos en assets.
     */
    private fun mockContextWithAssets(json: String): Context {
        val context = mock(Context::class.java)
        val assets = mock(AssetManager::class.java)

        // Simula que assets.open("catalog.json") devuelve un InputStream con el JSON
        `when`(context.assets).thenReturn(assets)
        `when`(assets.open("catalog.json"))
            .thenReturn(ByteArrayInputStream(json.toByteArray()))

        return context
    }

    @Test
    fun `loadCatalog returns parsed list when JSON is valid`() {
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

        val context = mockContextWithAssets(json)
        val ds = LocalCatalogDataSource(context)

        val result = ds.loadCatalog()

        assertEquals(1, result.size)
        assertEquals("calc", result[0].id)
        assertEquals("Calculadora Pro", result[0].name)
    }

    @Test
    fun `loadCatalog returns empty list when JSON is invalid`() {
        val json = "INVALID JSON"

        val context = mockContextWithAssets(json)
        val ds = LocalCatalogDataSource(context)

        val result = ds.loadCatalog()

        assertTrue(result.isEmpty())
    }
}
package com.hci.appstore.data.local

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hci.appstore.data.model.StoreApp

/**
 * DataSource local que carga el catálogo desde /assets/catalog.json.
 *
 * Ventajas:
 *  - No depende de Android UI
 *  - Fácil de testear
 *  - Se puede reemplazar por un RemoteCatalogDataSource sin tocar la UI
 *  - Manejo seguro de errores
 */
class LocalCatalogDataSource(private val context: Context) {

    private val gson = Gson()
    private val TAG = "LocalCatalogDataSource"

    /**
     * Carga el catálogo desde assets/catalog.json.
     * Si ocurre un error, devuelve una lista vacía.
     */
    fun loadCatalog(): List<StoreApp> {
        return try {
            val json = readJsonFromAssets("catalog.json")
            parseCatalog(json)
        } catch (e: Exception) {
            Log.e(TAG, "Error cargando catálogo local", e)
            emptyList()
        }
    }

    /**
     * Lee un archivo JSON desde assets.
     */
    private fun readJsonFromAssets(fileName: String): String {
        return context.assets.open(fileName)
            .bufferedReader()
            .use { it.readText() }
    }

    /**
     * Convierte el JSON en una lista de StoreApp.
     */
    private fun parseCatalog(json: String): List<StoreApp> {
        val type = object : TypeToken<List<StoreApp>>() {}.type
        return gson.fromJson(json, type)
    }
}

package com.hci.appstore.data.remote

import android.util.Log
import com.hci.appstore.data.model.StoreApp

/**
 * DataSource remoto que obtiene el catálogo desde GitHub Pages o cualquier servidor HTTP.
 *
 * No depende de Android UI.
 * Totalmente testeable.
 */
class RemoteCatalogDataSource(
    private val api: RemoteCatalogApi
) {

    private val TAG = "RemoteCatalogDataSource"

    /**
     * Intenta descargar el catálogo remoto.
     * Si falla, devuelve null para permitir fallback a local.
     */
    suspend fun loadCatalog(): List<StoreApp>? {
        return try {
            api.getCatalog()
        } catch (e: Exception) {
            Log.e(TAG, "Error descargando catálogo remoto", e)
            null
        }
    }
}
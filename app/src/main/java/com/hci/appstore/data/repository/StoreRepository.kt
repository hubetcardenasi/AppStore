package com.hci.appstore.data.repository

import android.content.pm.PackageManager
import androidx.core.content.pm.PackageInfoCompat
import com.hci.appstore.core.install.ApkInstaller
import com.hci.appstore.data.local.LocalCatalogDataSource
import com.hci.appstore.data.model.StoreApp
import com.hci.appstore.data.remote.RemoteCatalogDataSource
import com.hci.appstore.ui.catalog.StoreAppUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio híbrido:
 *  - Intenta cargar catálogo remoto (GitHub Pages)
 *  - Si falla, usa catálogo local (assets/catalog.json)
 *  - Enriquecer apps con información del sistema
 *  - Simular instalación/actualización
 *
 * Totalmente testeable y desacoplado de la UI.
 */
class StoreRepository(
    private val local: LocalCatalogDataSource,
    private val remote: RemoteCatalogDataSource?,
    private val pm: PackageManager,
    private val apkInstaller: ApkInstaller
) {

    /**
     * Obtiene el catálogo:
     * 1. Intenta remoto
     * 2. Fallback a local
     */
    suspend fun getCatalog(): List<StoreApp> {
        // 1. Intentar remoto
        remote?.loadCatalog()?.let { remoteList ->
            if (remoteList.isNotEmpty()) {
                return remoteList
            }
        }

        // 2. Fallback a local
        return local.loadCatalog()
    }

    /**
     * Enriquecer catálogo con información del sistema:
     *  - Si está instalada
     *  - Versión instalada
     *  - Si hay actualización disponible
     */
    fun enrich(apps: List<StoreApp>): List<StoreAppUi> {
        return apps.map { app ->
            try {
                val info = pm.getPackageInfo(app.packageName, 0)
                val installedVersion = PackageInfoCompat.getLongVersionCode(info)
                val hasUpdate = installedVersion < app.versionCode

                StoreAppUi(
                    app = app,
                    isInstalled = true,
                    hasUpdate = hasUpdate,
                    installedVersionCode = installedVersion.toInt()
                )

            } catch (e: Exception) {
                // App no instalada
                StoreAppUi(
                    app = app,
                    isInstalled = false,
                    hasUpdate = false,
                    installedVersionCode = null
                )
            }
        }
    }

    /**
     * Descarga e inicia instalación real del APK.
     */
    suspend fun install(app: StoreApp) {
        // Aquí asumimos que app.apk es una URL absoluta
        // (p.ej. https://hubetcardenasi.github.io/AppStoreCatalog/apks/volado.apk)
        val file = withContext(Dispatchers.IO) {
            apkInstaller.downloadApk(app.apk, "${app.id}_${app.versionCode}.apk")
        }
        apkInstaller.installApk(file)
    }

}
package com.hci.appstore.data.repository

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.core.content.pm.PackageInfoCompat
import com.hci.appstore.core.install.ApkInstaller
import com.hci.appstore.data.local.LocalCatalogDataSource
import com.hci.appstore.data.model.StoreApp
import com.hci.appstore.data.remote.RemoteCatalogDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class StoreRepositoryTest {

    private fun sampleApp() = StoreApp(
        id = "calc",
        name = "Calculadora Pro",
        packageName = "com.example.calcpro",
        versionCode = 3,
        versionName = "1.2.0",
        icon = "",
        apk = "",
        shortDescription = "",
        longDescription = "",
        sizeBytes = 1000
    )

    @Test
    fun `getCatalog uses remote when available`() = runBlocking {
        val local = Mockito.mock(LocalCatalogDataSource::class.java)
        val remote = Mockito.mock(RemoteCatalogDataSource::class.java)
        val pm = Mockito.mock(PackageManager::class.java)
        val installer = Mockito.mock(ApkInstaller::class.java)

        val remoteList = listOf(sampleApp())
        Mockito.`when`(remote.loadCatalog()).thenReturn(remoteList)

        val repo = StoreRepository(local, remote, pm, installer)

        val result = repo.getCatalog()

        Assert.assertEquals(remoteList, result)
    }

    @Test
    fun `getCatalog falls back to local when remote fails`() = runBlocking {
        val local = Mockito.mock(LocalCatalogDataSource::class.java)
        val remote = Mockito.mock(RemoteCatalogDataSource::class.java)
        val pm = Mockito.mock(PackageManager::class.java)
        val installer = Mockito.mock(ApkInstaller::class.java)

        val localList = listOf(sampleApp())
        Mockito.`when`(remote.loadCatalog()).thenReturn(null)
        Mockito.`when`(local.loadCatalog()).thenReturn(localList)

        val repo = StoreRepository(local, remote, pm, installer)

        val result = repo.getCatalog()

        Assert.assertEquals(localList, result)
    }

    @Test
    fun `enrich marks app as installed when PackageManager finds it`() {
        val local = Mockito.mock(LocalCatalogDataSource::class.java)
        val remote = Mockito.mock(RemoteCatalogDataSource::class.java)
        val pm = Mockito.mock(PackageManager::class.java)
        val installer = Mockito.mock(ApkInstaller::class.java)

        val pkgInfo = PackageInfo()
        PackageInfoCompat.getLongVersionCode(pkgInfo)

        Mockito.`when`(pm.getPackageInfo("com.example.calcpro", 0)).thenReturn(pkgInfo)

        val repo = StoreRepository(local, remote, pm, installer)

        val result = repo.enrich(listOf(sampleApp()))

        Assert.assertTrue(result[0].isInstalled)
        Assert.assertTrue(result[0].hasUpdate)
    }
}
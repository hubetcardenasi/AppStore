package com.hci.appstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.hci.appstore.core.install.ApkInstaller
import com.hci.appstore.data.local.LocalCatalogDataSource
import com.hci.appstore.data.remote.RemoteCatalogDataSource
import com.hci.appstore.data.remote.RetrofitModule
import com.hci.appstore.data.repository.StoreRepository
import com.hci.appstore.domain.GetCatalogUseCase
import com.hci.appstore.ui.catalog.CatalogViewModel
import com.hci.appstore.ui.navigation.StoreNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = RetrofitModule.provideApi(
            "https://hubetcardenasi.github.io/AppStoreCatalog/"
        )

        val remote = RemoteCatalogDataSource(api)
        val local = LocalCatalogDataSource(this)
        val installer = ApkInstaller(this)

        val repo = StoreRepository(
            local = local,
            remote = remote,
            pm = packageManager,
            apkInstaller = installer
        )

        // Crear el use case correctamente
        val getCatalogUseCase = GetCatalogUseCase(repo)

        // Pasar ambos al ViewModel
        val vm = CatalogViewModel(
            getCatalogUseCase = getCatalogUseCase,
            repo = repo
        )


        setContent {
            val nav = rememberNavController()
            StoreNavHost(nav, vm)
        }
    }
}
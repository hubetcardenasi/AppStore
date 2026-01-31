package com.hci.appstore.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.compose.NavHost
import com.hci.appstore.ui.catalog.CatalogScreen
import com.hci.appstore.ui.catalog.CatalogViewModel
import com.hci.appstore.ui.detail.DetailScreen

@Composable
fun StoreNavHost(nav: NavHostController, vm: CatalogViewModel) {
    NavHost(nav, startDestination = "catalog") {
        composable("catalog") {
            CatalogScreen(vm) { appUi ->
                nav.navigate("detail/${appUi.app.id}")
            }
        }
        composable("detail/{id}") { backStack ->
            val id = backStack.arguments?.getString("id")!!
            DetailScreen(id, vm)
        }
    }
}
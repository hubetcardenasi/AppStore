package com.hci.appstore.ui.catalog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CatalogScreen(
    vm: CatalogViewModel,
    onAppClick: (StoreAppUi) -> Unit
) {
    val state = vm.uiState

    LazyColumn(Modifier.fillMaxSize()) {
        items(state.apps) { appUi ->
            AppItem(
                appUi = appUi,
                installing = state.installingId == appUi.app.id,
                onClick = { onAppClick(appUi) },
                onInstall = { vm.install(appUi) }
            )
        }
    }
}

@Composable
fun AppItem(
    appUi: StoreAppUi,
    installing: Boolean,
    onClick: () -> Unit,
    onInstall: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(appUi.app.name, style = MaterialTheme.typography.titleMedium)
            Text(appUi.app.shortDescription)
        }

        when {
            installing -> CircularProgressIndicator(Modifier.size(24.dp))
            !appUi.isInstalled -> Button(onClick = onInstall) { Text("Instalar") }
            appUi.hasUpdate -> Button(onClick = onInstall) { Text("Actualizar") }
            else -> Text("Instalada")
        }
    }
}
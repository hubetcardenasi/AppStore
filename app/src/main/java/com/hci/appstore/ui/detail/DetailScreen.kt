package com.hci.appstore.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hci.appstore.ui.catalog.CatalogViewModel
import com.hci.appstore.ui.catalog.StoreAppUi

@Composable
fun DetailScreen(
    appId: String,
    vm: CatalogViewModel
) {
    val state = vm.uiState
    val appUi = state.apps.find { it.app.id == appId }

    if (appUi == null) {
        Text("App no encontrada")
        return
    }

    DetailContent(
        appUi = appUi,
        installing = state.installingId == appUi.app.id,
        onInstall = { vm.install(appUi) }
    )
}

@Composable
fun DetailContent(
    appUi: StoreAppUi,
    installing: Boolean,
    onInstall: () -> Unit
) {
    val app = appUi.app

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Icono
        AsyncImage(
            model = app.icon,
            contentDescription = null,
            modifier = Modifier.size(96.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(app.name, style = MaterialTheme.typography.headlineSmall)
        Text("Versión ${app.versionName}")
        Text("Tamaño ${(app.sizeBytes / 1024f / 1024f).format(2)} MB")

        Spacer(Modifier.height(16.dp))

        // Botón dinámico
        when {
            installing -> {
                CircularProgressIndicator()
            }
            !appUi.isInstalled -> {
                Button(onClick = onInstall) { Text("Instalar") }
            }
            appUi.hasUpdate -> {
                Button(onClick = onInstall) { Text("Actualizar") }
            }
            else -> {
                Text("Ya instalada", color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(app.longDescription)

        Spacer(Modifier.height(24.dp))

        // Screenshots
        app.screenshots?.let { shots ->
            if (shots.isNotEmpty()) {
                Text("Capturas", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                LazyRow {
                    items(shots) { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .padding(end = 12.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

private fun Float.format(decimals: Int): String =
    "%.${decimals}f".format(this)
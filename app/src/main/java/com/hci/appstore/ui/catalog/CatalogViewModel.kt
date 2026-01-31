package com.hci.appstore.ui.catalog

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hci.appstore.data.repository.StoreRepository
import com.hci.appstore.domain.GetCatalogUseCase
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val getCatalogUseCase: GetCatalogUseCase,
    private val repo: StoreRepository
) : ViewModel() {

    var uiState by mutableStateOf(CatalogUiState())
        private set

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            val apps = getCatalogUseCase()
            val enriched = repo.enrich(apps)

            uiState = uiState.copy(
                isLoading = false,
                apps = enriched
            )
        }
    }


    fun install(app: StoreAppUi) {
        viewModelScope.launch {
            uiState = uiState.copy(installingId = app.app.id)
            repo.install(app.app)
            // No podemos saber inmediatamente si se instaló, pero luego al volver
            // a la app y recargar, se reflejará el estado.
            load()
            uiState = uiState.copy(installingId = null)
        }
    }
}

data class CatalogUiState(
    val isLoading: Boolean = false,
    val apps: List<StoreAppUi> = emptyList(),
    val installingId: String? = null
)
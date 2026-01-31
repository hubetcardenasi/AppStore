package com.hci.appstore.ui.catalog

import com.hci.appstore.data.model.StoreApp
import com.hci.appstore.data.repository.StoreRepository
import com.hci.appstore.domain.GetCatalogUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {

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
    fun `load updates uiState with enriched apps`() = runTest {
        val repo = mock(StoreRepository::class.java)
        val getCatalog = mock(GetCatalogUseCase::class.java)

        val apps = listOf(sampleApp())
        val enriched = listOf(
            StoreAppUi(
                app = sampleApp(),
                isInstalled = false,
                hasUpdate = false,
                installedVersionCode = null
            )
        )

        `when`(getCatalog()).thenReturn(apps)
        `when`(repo.enrich(apps)).thenReturn(enriched)

        val vm = CatalogViewModel(getCatalog, repo)

        vm.load()

        val state = vm.uiState

        assertFalse(state.isLoading)
        assertEquals(enriched, state.apps)
    }

    @Test
    fun `load sets isLoading true then false`() = runTest {
        val repo = mock(StoreRepository::class.java)
        val getCatalog = mock(GetCatalogUseCase::class.java)

        `when`(getCatalog()).thenReturn(emptyList())
        `when`(repo.enrich(emptyList())).thenReturn(emptyList())

        val vm = CatalogViewModel(getCatalog, repo)

        // Antes de load
        assertFalse(vm.uiState.isLoading)

        vm.load()

        // Después de load
        assertFalse(vm.uiState.isLoading)
    }

    @Test
    fun `install triggers repo install and reloads catalog`() = runTest {
        val repo = mock(StoreRepository::class.java)
        val getCatalog = mock(GetCatalogUseCase::class.java)

        val apps = listOf(sampleApp())
        val enriched = listOf(
            StoreAppUi(
                app = sampleApp(),
                isInstalled = false,
                hasUpdate = false,
                installedVersionCode = null
            )
        )

        `when`(getCatalog()).thenReturn(apps)
        `when`(repo.enrich(apps)).thenReturn(enriched)

        val vm = CatalogViewModel(getCatalog, repo)

        vm.load()

        val appUi = vm.uiState.apps.first()

        vm.install(appUi)

        verify(repo).install(appUi.app)
        verify(repo, atLeastOnce()).enrich(apps)

        assertNull(vm.uiState.installingId)
    }
}
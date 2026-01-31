package com.hci.appstore.domain

import com.hci.appstore.data.model.StoreApp
import com.hci.appstore.data.repository.StoreRepository
import com.hci.appstore.ui.catalog.StoreAppUi
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.*

class CheckUpdatesUseCaseTest {

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
    fun `invoke returns enriched list from repository`() {
        val repo = mock(StoreRepository::class.java)
        val useCase = CheckUpdatesUseCase(repo)

        val apps = listOf(sampleApp())
        val enriched = listOf(
            StoreAppUi(
                app = sampleApp(),
                isInstalled = true,
                hasUpdate = false,
                installedVersionCode = 3
            )
        )

        `when`(repo.enrich(apps)).thenReturn(enriched)

        val result = useCase(apps)

        assertEquals(enriched, result)
        verify(repo).enrich(apps)
    }
}
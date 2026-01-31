package com.hci.appstore.domain

import com.hci.appstore.data.model.StoreApp
import com.hci.appstore.data.repository.StoreRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.*

class GetCatalogUseCaseTest {

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
    fun `invoke returns catalog from repository`() = runTest {
        val repo = mock(StoreRepository::class.java)
        val useCase = GetCatalogUseCase(repo)

        val expected = listOf(sampleApp())
        `when`(repo.getCatalog()).thenReturn(expected)

        val result = useCase()

        assertEquals(expected, result)
        verify(repo).getCatalog()
    }
}
package com.hci.appstore.domain

import com.hci.appstore.data.model.StoreApp
import com.hci.appstore.data.repository.StoreRepository

class GetCatalogUseCase(private val repo: StoreRepository) {
    suspend operator fun invoke(): List<StoreApp> = repo.getCatalog()
}

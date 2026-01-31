package com.hci.appstore.domain

import com.hci.appstore.data.repository.StoreRepository
import com.hci.appstore.data.model.StoreApp

class CheckUpdatesUseCase(private val repo: StoreRepository) {
    operator fun invoke(apps: List<StoreApp>) = repo.enrich(apps)
}

package com.hci.appstore.ui.catalog

import com.hci.appstore.data.model.StoreApp

data class StoreAppUi(
    val app: StoreApp,
    val isInstalled: Boolean,
    val hasUpdate: Boolean,
    val installedVersionCode: Int?
)

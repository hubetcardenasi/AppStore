package com.hci.appstore.data.model

data class StoreApp(
    val id: String,
    val name: String,
    val packageName: String,
    val versionCode: Int,
    val versionName: String,
    val icon: String,
    val apk: String,
    val shortDescription: String,
    val longDescription: String,
    val sizeBytes: Long,
    val screenshots: List<String>? = null
)

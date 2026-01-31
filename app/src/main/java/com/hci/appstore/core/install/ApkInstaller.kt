package com.hci.appstore.core.install

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class ApkInstaller(
    private val context: Context,
    private val client: OkHttpClient = OkHttpClient()
) {

    /**
     * Descarga el APK desde una URL y devuelve el File resultante.
     */
    fun downloadApk(url: String, fileName: String = "temp_app.apk"): File {
        val request = Request.Builder()
            .url(url)
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IllegalStateException("Error descargando APK: ${response.code}")
        }

        val dir = File(context.cacheDir, "apks")
        if (!dir.exists()) dir.mkdirs()

        val apkFile = File(dir, fileName)

        response.body?.byteStream().use { input ->
            FileOutputStream(apkFile).use { output ->
                if (input != null) {
                    input.copyTo(output)
                } else {
                    throw IllegalStateException("Cuerpo de respuesta vacío")
                }
            }
        }

        return apkFile
    }

    /**
     * Lanza el Intent de instalación usando FileProvider.
     */
    fun installApk(apkFile: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)
    }
}
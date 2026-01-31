package com.hci.appstore.core.install

import android.content.Context
import com.hci.appstore.core.install.ApkInstaller
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.io.File

class ApkInstallerTest {

    private lateinit var server: MockWebServer

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `downloadApk downloads file successfully`() {
        val context = mock(Context::class.java)
        val cacheDir = File("build/tmp/testCache").apply { mkdirs() }

        `when`(context.cacheDir).thenReturn(cacheDir)

        val installer = ApkInstaller(context)

        val apkBytes = "FAKE APK CONTENT".toByteArray()

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(apkBytes.decodeToString())
        )

        val url = server.url("/app.apk").toString()

        val file = installer.downloadApk(url, "test.apk")

        assertTrue(file.exists())
        assertTrue(file.length() > 0)
    }

    @Test
    fun `installApk launches installation intent`() {
        val context = mock(Context::class.java)
        val installer = ApkInstaller(context)

        val file = File("build/tmp/testCache/test.apk").apply {
            parentFile.mkdirs()
            writeText("fake")
        }

        installer.installApk(file)

        verify(context).startActivity(any())
    }

    @Test(expected = Exception::class)
    fun `downloadApk throws on server error`() {
        val context = mock(Context::class.java)
        val cacheDir = File("build/tmp/testCache").apply { mkdirs() }
        `when`(context.cacheDir).thenReturn(cacheDir)

        val installer = ApkInstaller(context)

        server.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Server error")
        )

        val url = server.url("/app.apk").toString()

        installer.downloadApk(url, "test.apk")
    }
}
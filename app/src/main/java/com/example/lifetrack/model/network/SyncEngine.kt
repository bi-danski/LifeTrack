package com.example.lifetrack.model.network

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class SyncEngine(private val clientService: HttpClient) {
    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        fun createDefault(): SyncEngine {
            val client = KtorClientFactory().create()
            return SyncEngine(client)
        }
    }

    fun startSync() {
        scope.launch {
            clientService.init()
        }
    }

    fun retrySync(maxAttempts: Int = 3, delayMillis: Long = 1000L) {
        scope.launch {
            var attempt = 0
            while (attempt < maxAttempts) {
                try {
                    clientService.init()
                    return@launch
                } catch (e: Exception) {
                    attempt++
                    if (attempt == maxAttempts) {
                        e.printStackTrace()
                    } else {
                        kotlinx.coroutines.delay(delayMillis * attempt)
                    }
                }
            }
        }
    }

    fun startPeriodicSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "LifeTrackSync",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

        fun stopSync() {
            scope.cancel()
        }
    }
    suspend fun HttpClient.init() {
        val service = ApiService(this)
        service.testService("admin:test")
    }
}
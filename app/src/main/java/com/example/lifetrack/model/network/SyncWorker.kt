package com.example.lifetrack.model.network

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val client = KtorClientFactory().create()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = client.get("https://api.lifetrack.app/sync")
            if (result.status.isSuccess()) {
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}

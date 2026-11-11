package com.example.myapplication.data.sync

import android.content.Context
import androidx.work.*
import com.example.myapplication.data.local.database.AppDatabase
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.data.repository.ProductRepositoryImpl
import java.util.concurrent.TimeUnit

class CatalogSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val repository = ProductRepositoryImpl(
            AppDatabase.getInstance(applicationContext).productDao(), RetrofitInstance.api
        )
        val writes = repository.syncPending()
        val refresh = repository.refreshProducts()
        return if (writes.isSuccess && refresh.isSuccess) Result.success() else Result.retry()
    }

    companion object {
        private val network = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        fun enqueue(context: Context) {
            WorkManager.getInstance(context).enqueueUniqueWork(
                "catalog-sync", ExistingWorkPolicy.APPEND_OR_REPLACE,
                OneTimeWorkRequestBuilder<CatalogSyncWorker>()
                    .setConstraints(network)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                    .build()
            )
        }

        fun start(context: Context) {
            enqueue(context)
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "catalog-periodic", ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<CatalogSyncWorker>(15, TimeUnit.MINUTES)
                    .setConstraints(network).build()
            )
        }
    }
}

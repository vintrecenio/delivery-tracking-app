package com.treceniovin.deliverytracking.worker

import android.content.Context
import androidx.work.*
import com.treceniovin.deliverytracking.domain.usecase.RefreshOrdersUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class PollOrdersWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val refreshOrdersUseCase: RefreshOrdersUseCase by inject()

    override suspend fun doWork(): Result {
        return try {
            refreshOrdersUseCase()
            // Recursive scheduling for 30s interval
            enqueueNext(applicationContext)
            Result.success()
        } catch (e: Exception) {
            // Even on failure, try to poll again after 30s
            enqueueNext(applicationContext)
            Result.success()
        }
    }

    companion object {
        const val WORK_NAME = "PollOrdersWorker"

        fun enqueueNext(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<PollOrdersWorker>()
                .setInitialDelay(30, TimeUnit.SECONDS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}

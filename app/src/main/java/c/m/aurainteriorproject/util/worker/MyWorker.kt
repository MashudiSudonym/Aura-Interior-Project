package c.m.aurainteriorproject.util.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import c.m.aurainteriorproject.util.Constants

class MyWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Log.d(Constants.MY_WORKER, "Performing long running task in scheduled job")
        return Result.success()
    }
}
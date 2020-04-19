package c.m.aurainteriorproject.util.service

import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import c.m.aurainteriorproject.R
import c.m.aurainteriorproject.util.Constants
import c.m.aurainteriorproject.util.notificationSetup
import c.m.aurainteriorproject.util.worker.MyWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ApplicationFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(Constants.APP_FIREBASE_MESSAGING_SERVICE, "From: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            Log.d(
                Constants.APP_FIREBASE_MESSAGING_SERVICE,
                "Message data payload: " + remoteMessage.data
            )

            if (true) {
                scheduleJob()
            } else {
                handleNow()
            }
        }

        remoteMessage.notification?.let {
            Log.d(Constants.APP_FIREBASE_MESSAGING_SERVICE, "Message Notification Body: ${it.body}")

            notificationSetup(
                this,
                it.title,
                it.body,
                6969,
                getString(R.string.notification_channel)
            )
        }
    }

    private fun scheduleJob() {
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance(this).beginWith(work).enqueue()
    }

    private fun handleNow() {
        Log.d(
            Constants.APP_FIREBASE_MESSAGING_SERVICE,
            getString(R.string.short_lived_task_is_done)
        )
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }
}
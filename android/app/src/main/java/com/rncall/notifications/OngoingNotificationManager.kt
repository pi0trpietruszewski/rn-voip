package com.rncall.notifications


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rncall.MainActivity
import com.rncall.R
import io.wazo.callkeep.Constants
import java.util.*
import kotlin.collections.HashMap

class OngoingNotificationManager(private val context: Context) {
    private var currentId: String? = null
    private val ongoingCalls = HashMap<String, OngoingCall?>()
    private val ongoingHandler = Handler(Looper.getMainLooper())
    private val notificationBuilder = NotificationCompat.Builder(context, "APP_CALL_SERVICE")

    private data class OngoingCall(val callUuid: String, var name: String?) {
        val startDate = Calendar.getInstance().timeInMillis

        fun getDuration(timeInMillis: Long): String {
            val duration = timeInMillis - startDate
            val hours = duration / (3600 * 1000)
            val minutes = duration / (60 * 1000) % 60
            val seconds = duration / 1000 % 60
            return if(hours > 0) {
                "%02d:%02d:%02d".format(hours, minutes, seconds)
            } else {
                "%02d:%02d".format(minutes, seconds)
            }
        }
    }

    private val updateNotificationTask = object: Runnable {
        override fun run() {
            updateNotification()
            ongoingHandler.postDelayed(this, 1000)
        }
    }

    private fun startUpdatingInterval() {
        ongoingHandler.post(updateNotificationTask)
    }

    private fun stopUpdatingInterval() {
        ongoingHandler.removeCallbacksAndMessages(null)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(Constants.FOREGROUND_SERVICE_TYPE_MICROPHONE)
    }

    private fun updateNotification() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // Foreground services not required before SDK 28
            return
        }
        val appIconResourceId = context.applicationInfo.icon
        val intent = Intent(context, MainActivity::class.java)
        val currentCall = ongoingCalls[currentId] ?: return

        val currentTime = Calendar.getInstance().timeInMillis
        val duration = currentCall.getDuration(currentTime)
        val currentName = currentCall.name

        val notificationLayout = RemoteViews(context.packageName, R.layout.ongoing_call_notification)

        notificationLayout.setTextViewText(R.id.name, "Ongoing call with $currentName")
        notificationLayout.setTextViewText(R.id.duration, duration)

        val contentIntent = PendingIntent.getActivity(context,0, intent, 0)
        notificationBuilder.setOngoing(true)
                .setContentTitle("Ongoing call with: $currentName")
                .setContentText(duration)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationManagerCompat.IMPORTANCE_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentIntent(contentIntent)
                .setSmallIcon(appIconResourceId)
                .setCustomHeadsUpContentView(notificationLayout)
                .setCustomContentView(notificationLayout)
        val notification = notificationBuilder.build()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(Constants.FOREGROUND_SERVICE_TYPE_MICROPHONE, notification)
    }

    fun callStarted(callUuid: String, callerName: String) {
        ongoingCalls[callUuid] = OngoingCall(callUuid, callerName)
        currentId = callUuid
        if(ongoingCalls.size == 1) {
            startUpdatingInterval()
        }
    }

    fun callEnded(callUuid: String) {
        ongoingCalls.remove(callUuid)
        if(ongoingCalls.isEmpty()) {
            stopUpdatingInterval()
            currentId = null
        }
    }

}

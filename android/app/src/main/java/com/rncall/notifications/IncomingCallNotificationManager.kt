package com.rncall.notifications



import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.view.Display
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rncall.MainApplication
import com.rncall.AppCallActivity
import com.rncall.R
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import io.wazo.callkeep.Constants
import java.util.*


class IncomingCallNotificationManager {
    companion object{
        val callNotificationIds = HashMap<String, String>()

        fun showNewHeadsUpNotification(context: Context, callUuid: String, roomId: String, callerName: String, roomSid: String, enableVideo: Boolean) {
            // Check if there is notification id for the callUuid and use it if exists
            val notificationId: Int = if(callNotificationIds.containsKey(callUuid)) {
                callNotificationIds[callUuid]!!.toInt()
            } else {
                NotificationIDGenerator.generateNextID()
            }
            // Do not pass promise since it should be already stored in AppCallBroadcastReceiver
            showNotification(context, callUuid, roomId, callerName, notificationId, roomSid, enableVideo, null)
        }

        fun showIncomingCallNotification(context: ReactApplicationContext, callUuid: String, roomId: String, callerName: String, roomSid: String, enableVideo: Boolean, promise: Promise) {
            // check if the screen is ON
            var isScreenOn = false
            val dm = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            for (display in dm.displays) {
                if (display.state != Display.STATE_OFF) {
                    isScreenOn = true
                }
            }
            val notificationId = NotificationIDGenerator.generateNextID()

            // Do not show notification when the IncomingCallActivity is active and the screen is ON
            if (context.currentActivity is AppCallActivity && isScreenOn) {
                AppCallBroadcastReceiver.registerPromise(callUuid, callerName, roomId, roomSid, enableVideo, promise)
                callNotificationIds[callUuid] = notificationId.toString() + ""
                return
            }

            showNotification(context, callUuid, roomId, callerName, notificationId, roomSid, enableVideo, promise)
        }

        private fun showNotification(context: Context, callUuid: String, roomId: String, callerName: String, notificationId: Int, roomSid: String, enableVideo: Boolean,  promise: Promise?) {
            callNotificationIds[callUuid] = notificationId.toString() + ""
            val pendingIncomingCallFullScreenIntent = getIncomingCallIntent(context, callUuid, roomId, callerName, notificationId, roomSid, enableVideo)
            val notificationLayout = createIncomingCallNotificationLayout(context, callUuid, notificationId, callerName)
            val notificationManager = NotificationManagerCompat.from(context)

            // Build the notification as an ongoing high priority item; this ensures it will show as
            // a heads up notification which slides down over top of the current content.
            val notificationBuilder = NotificationCompat.Builder(context, MainApplication.RING_CHANNEL_ID)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
//                    .setAutoCancel(false) //Setting ongoing prevents the user from dismissing the notification (i.e. by sliding it to the side).
                    .setOngoing(true) // Set full screen intent to trigger display of the fullscreen UI when the notification manager deems it appropriate.
                    .setFullScreenIntent(pendingIncomingCallFullScreenIntent, true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setCustomHeadsUpContentView(notificationLayout)
                    .setCustomContentView(notificationLayout)

            val incomingCallNotification = notificationBuilder.build()

            //Register this promise with the broadcast receiver, so it can resolve it later
            if(promise != null) {
                AppCallBroadcastReceiver.registerPromise(callUuid, callerName, roomId, roomSid, enableVideo, promise)
            }


            notificationManager.notify(notificationId, incomingCallNotification)
        }

        private fun getIncomingCallIntent(context: Context, callUuid: String, roomId: String, callerName: String, notificationId: Int, roomSid: String, enableVideo: Boolean): PendingIntent {
            val incomingCallFullScreenIntent = Intent(Intent.ACTION_MAIN, null)
            incomingCallFullScreenIntent.putExtra(Constants.EXTRA_CALL_UUID, callUuid)
            incomingCallFullScreenIntent.putExtra("NOTIFICATION_ID", notificationId)
            incomingCallFullScreenIntent.putExtra("ROOM_NAME", roomId)
            incomingCallFullScreenIntent.putExtra("CALLER_NAME", callerName)
            incomingCallFullScreenIntent.putExtra("ROOM_SID", roomSid)
            incomingCallFullScreenIntent.putExtra("ENABLE_VIDEO", enableVideo)

            incomingCallFullScreenIntent.flags = Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_NEW_TASK
            incomingCallFullScreenIntent.setClass(context, AppCallActivity::class.java)
            return PendingIntent.getActivity(context, 1, incomingCallFullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        private fun getAnswerIntent(context: Context, callUuid: String, notificationId: Int): PendingIntent {
            val answerIntent = Intent(context, AppCallBroadcastReceiver::class.java)
            answerIntent.putExtra(Constants.EXTRA_CALL_UUID, callUuid)
            answerIntent.putExtra("actionPerformed", "ANSWER")
            answerIntent.putExtra("NOTIFICATION_ID", notificationId)
            return PendingIntent.getBroadcast(context, 0, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        private fun getRejectIntent(context:Context, callUuid: String, notificationId: Int): PendingIntent {
            val rejectIntent = Intent(context, AppCallBroadcastReceiver::class.java)
            rejectIntent.putExtra(Constants.EXTRA_CALL_UUID, callUuid)
            rejectIntent.putExtra("actionPerformed", "REJECT")
            rejectIntent.putExtra("NOTIFICATION_ID", notificationId)
            return PendingIntent.getBroadcast(context, 1, rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        private fun createIncomingCallNotificationLayout(context: Context, callUuid: String, notificationId: Int, callerName: String): RemoteViews {
            val answerPendingIntent = getAnswerIntent(context, callUuid, notificationId)
            val rejectPendingIntent = getRejectIntent(context, callUuid, notificationId)
            val notificationLayout = RemoteViews(context.packageName, R.layout.heads_up_notification)
            notificationLayout.setTextViewText(R.id.name, "Incoming Call From $callerName")
            notificationLayout.setOnClickPendingIntent(R.id.btnAnswer, answerPendingIntent)
            notificationLayout.setOnClickPendingIntent(R.id.btnReject, rejectPendingIntent)
            return notificationLayout
        }
    }
}

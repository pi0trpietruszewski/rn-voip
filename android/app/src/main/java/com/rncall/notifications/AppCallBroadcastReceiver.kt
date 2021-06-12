package com.rncall.notifications


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.rncall.AppCallActivity
import com.facebook.react.ReactApplication
import com.facebook.react.bridge.Promise
import io.wazo.callkeep.Constants
import java.util.*

class AppCallBroadcastReceiver : BroadcastReceiver() {
    private fun resolveRNPromise(callUuid: String, result: String) {
        val notification = promises[callUuid]
        if (notification != null && notification.promise !== null) {
            notification.promise.resolve(result)
            removePromise(callUuid)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val callUuid = intent.getStringExtra(Constants.EXTRA_CALL_UUID)
        val actionPerformed = intent.getStringExtra("actionPerformed")
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)
        if(callUuid != null && actionPerformed != null) {
            try {
                if (notificationId > 0) {
                    val notificationManager = NotificationManagerCompat.from(context)
                    notificationManager.cancel(notificationId)
                }
                val call = promises[callUuid]
                val appContext = context.applicationContext
                if (appContext is ReactApplication) {
                    val reactApplication = appContext as ReactApplication
                    val reactContext = reactApplication
                            .reactNativeHost
                            .reactInstanceManager
                            .currentReactContext
                    val currentActivity = reactContext!!.currentActivity
                    if ("ANSWER" == actionPerformed) {
                        if (currentActivity !is AppCallActivity) {
                            val mediaType = if (call?.enableVideo !== null && call.enableVideo) "video" else "audio"
                            val activeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("app://call/answer/${callUuid}/${call?.roomSid}/${call?.roomId}/${mediaType}/${call?.name}/").normalizeScheme())
                            activeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(activeIntent)
                            resolveRNPromise(callUuid, actionPerformed)
                        }
                    }
                    if ("REJECT" == actionPerformed) {
                        if (currentActivity !is AppCallActivity) {
                            val activeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("app://call/reject/${callUuid}/${call?.roomSid}/").normalizeScheme())
                            activeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(activeIntent)
                            resolveRNPromise(callUuid, actionPerformed)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in App call broadcast receiver", e)
                resolveRNPromise(callUuid, "ERROR")
            }
        }

    }

    companion object {
        private const val TAG = "CallBroadcastReceiver"
        val promises = HashMap<String, IncomingCallNotification?>()
        fun registerPromise(callUuid: String, name: String, roomId: String, roomSid: String, enableVideo: Boolean,  promise: Promise) {
            promises[callUuid] = IncomingCallNotification(callUuid, name, roomId, roomSid, enableVideo, promise)
        }

        private fun removePromise(callUuid: String?) {
            promises.remove(callUuid)
        }
    }

    data class IncomingCallNotification(val callUuid: String, val name: String, val roomId: String, val roomSid: String, val enableVideo: Boolean, val promise: Promise?) {}
}

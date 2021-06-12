package com.rncall.notifications


import android.content.Intent
import android.util.Log
import com.rncall.MainActivity
import com.rncall.AppCallActivity
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import io.wazo.callkeep.Constants

class CallNotificationsModule(private val context: ReactApplicationContext) : ReactContextBaseJavaModule(context) {
    private val ongoingNotificationManager: OngoingNotificationManager = OngoingNotificationManager(context)
    override fun getName(): String {
        return "AppCallNotifications"
    }

    @ReactMethod
    fun broadcastCallStarted(callUuid: String, callerName: String) {
        ongoingNotificationManager.callStarted(callUuid, callerName)
    }

    @ReactMethod
    fun broadcastCallEnded(callUuid: String) {
        ongoingNotificationManager.callEnded(callUuid)
    }

    @ReactMethod
    fun reportIncomingCallCancelled(callUuid: String) {
        sendBroadcast(callUuid, "CANCEL")
    }

    @ReactMethod
    fun answerCall(callUuid: String) {
        sendBroadcast(callUuid, "ANSWER")
    }

    @ReactMethod
    fun rejectCall(callUuid: String) {
        sendBroadcast(callUuid, "REJECT")
    }

    @ReactMethod
    fun showIncomingCallNotification(callUuid: String?, roomName: String?, callerName: String?, roomSid: String, enableVideo: Boolean, promise: Promise?) {
        IncomingCallNotificationManager.showIncomingCallNotification(context, callUuid!!, roomName!!, callerName!!, roomSid, enableVideo, promise!!)
    }

    private fun sendBroadcast(callUuid: String, action: String) {
        val notificationIdStr = IncomingCallNotificationManager.callNotificationIds[callUuid]
        if (notificationIdStr != null) {
            try {
                val notificationId = notificationIdStr.toInt()
                val intent = Intent(context, AppCallBroadcastReceiver::class.java)
                intent.putExtra(Constants.EXTRA_CALL_UUID, callUuid)
                intent.putExtra("actionPerformed", action)
                intent.putExtra("NOTIFICATION_ID", notificationId)
                context.sendBroadcast(intent)
                IncomingCallNotificationManager.callNotificationIds.remove(callUuid)
            } catch (e: NumberFormatException) {
            }
        }
    }


    @ReactMethod
    fun finishIncomingCallActivity() {
        val currentActivity = currentActivity
        if (currentActivity is AppCallActivity) {
            currentActivity.finishAndRemoveTask()
        }
    }

    companion object {
        private const val TAG = "AppCallNotifications"
    }

}

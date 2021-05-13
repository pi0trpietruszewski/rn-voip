package com.rncall.notifications


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rncall.MainActivity
import com.rncall.AppCallActivity

class PhoneUnlockReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (context is AppCallActivity) {
            context.finishAndRemoveTask()
            val activityIntent = Intent(context, MainActivity::class.java)

            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(activityIntent)
            // If there are incoming call notifications - show new heads up notifications
            if (AppCallBroadcastReceiver.promises.size > 0) {
                AppCallBroadcastReceiver.promises.forEach {
                    val value = it.value
                    if(value is AppCallBroadcastReceiver.IncomingCallNotification) {
                        IncomingCallNotificationManager.showNewHeadsUpNotification(context, it.key, value.roomId, value.name, value.roomSid, value.enableVideo)
                    }
                }
            }
        }
    }
}

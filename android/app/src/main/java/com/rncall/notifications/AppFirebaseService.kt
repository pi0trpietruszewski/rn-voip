package com.rncall.notifications

import android.content.Intent
import android.os.Bundle
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AppFirebaseService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val bundle = message.toIntent().extras
        val command = bundle!!.getString("command", null)
        // TODO determine if it is audio only (from PN probably)
        if ("incoming_call" == command) {
            val roomName = bundle.getString("roomName")
            val callUuid = bundle.getString("callId")
            val callerName = bundle.getString("callerName")
            val roomSid = bundle.getString("roomSid")
            val enableVideo = bundle.getString("enableVideo")

            //Invoke incoming call headless task
            if (roomName !== null && callUuid !== null && callerName !== null && roomSid !== null) {
                invokeAndroidIncomingCallHeadlessTask(roomName, callUuid, callerName, roomSid, enableVideo == "true")
            }
        }
    }


    private fun invokeAndroidIncomingCallHeadlessTask(roomName: String?, callUuid: String?, callerName: String?, roomSid: String?, enableVideo: Boolean = true) {
        val service = Intent(applicationContext, IncomingCallHeadlessTaskService::class.java)
        val bundle = Bundle()
        bundle.putString("roomName", roomName)
        bundle.putString("roomSid", roomSid)
        bundle.putString("callerName", callerName)
        bundle.putString("callUuid", callUuid)
        bundle.putBoolean("enableVideo", enableVideo)
        service.putExtras(bundle)
        applicationContext.startService(service)
    }
}

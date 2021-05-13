package com.rncall


import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.rncall.notifications.PhoneUnlockReceiver
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.ReactRootView
import com.swmansion.gesturehandler.react.RNGestureHandlerEnabledRootView
import io.wazo.callkeep.Constants

class AppCallActivity : ReactActivity() {
    private val phoneUnlockReceiver: BroadcastReceiver = PhoneUnlockReceiver()
    private var isReceiverRegistered = false
    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    override fun getMainComponentName(): String? {
        return "AndroidIncomingCall"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // For newer than Android Oreo: call setShowWhenLocked, setTurnScreenOn
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            // For older versions, do it as you did before.
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
        }
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardManager.requestDismissKeyguard(this, null)
        }
        val isDeviceLocked: Boolean = keyguardManager.isDeviceLocked

        if (isDeviceLocked) {
            Log.d(TAG, "registering phoneUnlockReceiver")
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_USER_PRESENT)
            registerReceiver(phoneUnlockReceiver, filter)
            isReceiverRegistered = true
        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        // Enables sticky immersive mode.
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "unregistering phoneUnlockReceiver")
        if(isReceiverRegistered) {
            unregisterReceiver(phoneUnlockReceiver)
        }
    }

    override fun createReactActivityDelegate(): ReactActivityDelegate {
        Log.d(TAG, "createReactActivityDelegate()")
        return object : ReactActivityDelegate(this, mainComponentName) {
            override fun createRootView(): ReactRootView {
                return RNGestureHandlerEnabledRootView(this@AppCallActivity)
            }

            override fun getLaunchOptions(): Bundle? {
                var roomName: String? = null
                var callerName: String? = null
                var callUuid: String? = null
                var roomSid: String? = null
                var enableVideo: Boolean? = null
                val initialIntent = intent
                if (initialIntent != null) {
                    roomName = initialIntent.getStringExtra("ROOM_NAME")
                    callerName = initialIntent.getStringExtra("CALLER_NAME")
                    roomSid = initialIntent.getStringExtra("ROOM_SID")
                    enableVideo = initialIntent.getBooleanExtra("ENABLE_VIDEO", true)
                    callUuid = initialIntent.getStringExtra(Constants.EXTRA_CALL_UUID)

                }
                val initialProps = Bundle()
                if (roomName != null) {
                    initialProps.putString("roomName", roomName)
                }
                if (callerName != null) {
                    initialProps.putString("username", callerName)
                }
                if (callUuid != null) {
                    initialProps.putString("callUuid", callUuid)
                }
                if (roomSid != null) {
                    initialProps.putString("roomSid", roomSid)
                }
                if (enableVideo != null) {
                    initialProps.putBoolean("onlyAudio", !enableVideo)
                }
                return initialProps
            }
        }
    }

    companion object {
        private const val TAG = "AppCallActivity"
    }
}

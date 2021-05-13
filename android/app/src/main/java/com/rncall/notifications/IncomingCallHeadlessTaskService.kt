package com.rncall.notifications

import android.content.Intent
import com.facebook.react.HeadlessJsTaskService
import com.facebook.react.bridge.Arguments
import com.facebook.react.jstasks.HeadlessJsTaskConfig

class IncomingCallHeadlessTaskService : HeadlessJsTaskService() {
    override fun getTaskConfig(intent: Intent): HeadlessJsTaskConfig? {
        val extras = intent.extras
        return if (extras != null) {
            HeadlessJsTaskConfig(
                    "AndroidIncomingCallTask",
                    Arguments.fromBundle(extras),
                    60000,
                    true
            )
        } else null
    }
}

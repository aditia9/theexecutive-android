package com.ranosys.theexecutive.modules.notification.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ranosys.theexecutive.modules.notification.broadcast.BroadcastUtils

class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        BroadcastUtils.send(context, intent.action, intent.extras)
    }
}

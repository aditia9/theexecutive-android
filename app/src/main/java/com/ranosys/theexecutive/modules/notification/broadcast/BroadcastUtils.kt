package com.ranosys.theexecutive.modules.notification.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager

object BroadcastUtils {

    private const val REDIRECT_NOTIFICATION = "redirect_notification"


    fun broadcastUpdateLocation(context: Context) {
        send(context, REDIRECT_NOTIFICATION, null)
    }

    fun send(context: Context, action: String, extra: Bundle?) {
        val intent = Intent(action)

        if (extra != null) {
            intent.putExtras(extra)
        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun unregister(context: Context, receiver: BroadcastReceiver) {
        LocalBroadcastManager.getInstance(context)
                .unregisterReceiver(receiver)
    }

    fun register(context: Context, receiver: BroadcastReceiver, intentFilter: IntentFilter) {
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(receiver, intentFilter)
    }
}

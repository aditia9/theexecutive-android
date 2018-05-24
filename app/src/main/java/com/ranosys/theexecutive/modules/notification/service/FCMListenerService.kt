package com.ranosys.theexecutive.modules.notification.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.modules.splash.SplashActivity
import com.ranosys.theexecutive.utils.Constants
import java.util.*


/**
 * @Details A service class for push notification
 * @Author Ranosys Technologies
 * @Date 02,May,2018
 */
class FCMListenerService : FirebaseMessagingService() {

    private lateinit var redirectType: String
    private lateinit var redirectValue: String
    private lateinit var redirectTitle: String
    private lateinit var notificationImg: String
    private lateinit var title: String
    internal lateinit var message: String
    private lateinit var notificationId: String
    private lateinit var notification: NotificationCompat.Builder
    var vibrationArray = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.data?.run {
            val dataMap = (remoteMessage.data)
            redirectType = dataMap.get(Constants.KEY_REDIRECTION_TYPE) ?: ""
            redirectValue = dataMap.get(Constants.KEY_REDIRECTION_VALUE) ?: ""
            redirectTitle = dataMap.get(Constants.KEY_REDIRECTION_TITLE) ?: ""
            notificationImg = dataMap.get(Constants.KEY_IMAGE) ?: ""
            title = dataMap.get(Constants.KEY_NOTIFICATION_TITLE) ?: ""
            message = dataMap.get(Constants.KEY_NOTIFICATION_MESSAGE) ?: ""
            notificationId = dataMap.get(Constants.KEY_NOTIFICATION_ID) ?: ""

            Constants.notificationCounter++

            //generate notification if body is not empty and Notification are enabled from settings
            createNotification(message, title)

        }
    }


    private fun createNotification(body: String, title: String) {

        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(Constants.KEY_REDIRECTION_TYPE, redirectType)
        intent.putExtra(Constants.KEY_REDIRECTION_TITLE, redirectTitle)
        intent.putExtra(Constants.KEY_REDIRECTION_VALUE, redirectValue)
        intent.putExtra(Constants.KEY_NOTIFICATION_ID, notificationId)
        intent.putExtra(Constants.KEY_NOTIFICATION_TITLE, title)
        intent.putExtra(Constants.KEY_NOTIFICATION_MESSAGE, message)
        intent.putExtra(Constants.KEY_IMAGE, notificationImg)

        val pendingIntent = PendingIntent.getActivity(this, Calendar.getInstance().timeInMillis.toInt(), intent,
                PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val big_bitmap_image = BitmapFactory.decodeResource(resources, R.mipmap.app_icon)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val notificationChannel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, resources.getString(R.string.app_name), importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(getNotificationIcon())
                    .setVibrate(vibrationArray)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setChannelId(Constants.NOTIFICATION_CHANNEL_ID)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(false)
                    .setStyle(NotificationCompat.BigPictureStyle()
                            .bigPicture(big_bitmap_image)
                            .setBigContentTitle(title))
        } else {
            notification = NotificationCompat.Builder(this)
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent)
                    .setStyle(NotificationCompat.BigPictureStyle()
                            .bigPicture(big_bitmap_image)
                            .setBigContentTitle(title))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.color = resources.getColor(R.color.black)
        }
        notificationManager.notify(Calendar.getInstance().timeInMillis.toInt(), notification.build())

    }

    private fun getNotificationIcon(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            R.mipmap.app_icon
        } else {
            R.mipmap.app_icon
        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Constants.notificationCounter--
    }
}

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
import android.text.TextUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.modules.splash.SplashActivity
import com.ranosys.theexecutive.utils.Constants
import java.util.*


/**
 * Created by ranosys-sys2 on 19/10/16.
 */
class FCMListenerService : FirebaseMessagingService() {

    internal lateinit var redirectType: String
    internal lateinit var redirectValue: String
    internal lateinit var redirectTitle: String
    internal lateinit var redirectUrl: String
    internal lateinit var title: String
    internal lateinit var message: String
    internal lateinit var notificationId: String
    private lateinit var notification: NotificationCompat.Builder


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        var dataMap = (remoteMessage.data as Map<String, String>)
        redirectType = dataMap.get(Constants.KEY_REDIRECTION_TYPE) ?: ""
        redirectValue = dataMap.get(Constants.KEY_REDIRECTION_VALUE) ?: ""
        redirectTitle = dataMap.get(Constants.KEY_REDIRECTION_TITLE) ?: ""
        redirectUrl = dataMap.get(Constants.KEY_IMAGE) ?: ""
        title = dataMap.get(Constants.KEY_NOTIFICATION_TITLE) ?: ""
        message = dataMap.get(Constants.KEY_NOTIFICATION_ALERT) ?: ""
        notificationId = dataMap.get(Constants.KEY_NOTIFICATION_ID) ?: ""

        Constants.notificationCounter++

        if (!TextUtils.isEmpty(remoteMessage.notification?.body)) {
            val msg = remoteMessage.notification?.body
            createNotification(msg!!, getString(R.string.app_name))
        }
    }


    private fun createNotification(body: String, title: String) {

        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
       /*   intent.putExtra(Constant.KEY_REDIRECT_TYPE, redirectType)
          intent.putExtra(Constant.KEY_REDIRECT_TITLE, redirectTitle)
          intent.putExtra(Constant.KEY_REDIRECT_URL, redirectUrl)
          intent.putExtra(Constant.KEY_ID, redirectValue)
          intent.putExtra(Constant.LAYER_NAME, Constant.LAYER_NOTIFICATION)
          intent.putExtra(Constant.KEY_NOTIFICATION_ID, notificationId)*/

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
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(notificationChannel)

            notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(getNotificationIcon())
                    .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
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
                            .setBigContentTitle("big title"))

            /* //here comes to load image by Picasso
             //it should be inside try block
             .setLargeIcon(GlideApp.with(context).load("URL_TO_LOAD_LARGE_ICON").get())
             //BigPicture Style
             .setStyle(NotificationCompat.BigPictureStyle()
                     //This one is same as large icon but it wont show when its expanded that's why we again setting
                     .bigLargeIcon(GlideApp.with(context).load("URL_TO_LOAD_LARGE_ICON").get())
                     //This is Big Banner image
                     .bigPicture(GlideApp.with(context).load("URL_TO_LOAD_BANNER_IMAGE").get())
                     //When Notification expanded title and content text
                     .setBigContentTitle(title)
                     .setSummaryText(message))*/
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
                            .setBigContentTitle("big title"))
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

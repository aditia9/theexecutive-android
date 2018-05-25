package com.ranosys.theexecutive.modules.notification.service

import AppLog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
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
    internal lateinit var body: String
    private lateinit var notificationId: String
    private lateinit var notification: NotificationCompat.Builder
    private val notificationImageBaseUrl: String = "http://192.168.10.66/delami/pub/media/notification_image"
    var vibrationArray = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.data?.run {
            val dataMap = remoteMessage.data
            AppLog.e(dataMap.toString())
            redirectType = dataMap[Constants.KEY_REDIRECTION_TYPE] ?: ""
            AppLog.e("redirectType " + redirectType)
            redirectValue = dataMap[Constants.KEY_REDIRECTION_VALUE] ?: ""
            AppLog.e("redirectValue " + redirectValue)
            redirectTitle = dataMap[Constants.KEY_REDIRECTION_TITLE] ?: ""
            AppLog.e("redirectTitle "+ redirectTitle)
            notificationImg = dataMap[Constants.KEY_IMAGE] ?: ""
            AppLog.e("notificationImg "+ notificationImg)
            title = remoteMessage.notification?.title ?: ""
            AppLog.e("title " + title)
            body = remoteMessage.notification?.body ?: ""
            AppLog.e("message "+body)
            notificationId = dataMap[Constants.KEY_NOTIFICATION_ID] ?: ""
            AppLog.e("notificationId " + notificationId)

            Constants.notificationCounter++

            //generate notification if body is not empty and Notification are enabled from settings
            createNotification(body, title)

        }
    }


    private fun createNotification(body: String, title: String) {

        val intent = Intent(this, SplashActivity::class.java)
        intent.putExtra(Constants.KEY_REDIRECTION_TYPE, redirectType)
        intent.putExtra(Constants.KEY_REDIRECTION_TITLE, redirectTitle)
        intent.putExtra(Constants.KEY_REDIRECTION_VALUE, redirectValue)
        intent.putExtra(Constants.KEY_NOTIFICATION_ID, notificationId)
        intent.putExtra(Constants.KEY_NOTIFICATION_TITLE, title)
        intent.putExtra(Constants.KEY_NOTIFICATION_MESSAGE, body)
        intent.putExtra(Constants.KEY_IMAGE, notificationImg)

        val pendingIntent = PendingIntent.getActivity(this, Calendar.getInstance().timeInMillis.toInt(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, resources.getString(R.string.app_name), importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                    .setDefaults(Notification.FLAG_AUTO_CANCEL)
                    .setSmallIcon(getNotificationIcon())
                    .setVibrate(vibrationArray)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setChannelId(Constants.NOTIFICATION_CHANNEL_ID)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setStyle(NotificationCompat.BigPictureStyle()
                            .bigPicture(getBitmapFromURL(notificationImageBaseUrl+notificationImg))
                            .setBigContentTitle(title)
                            .setSummaryText(body))
        } else {
            notification = NotificationCompat.Builder(this)
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setStyle(NotificationCompat.BigPictureStyle()
                            .bigPicture(getBitmapFromURL(notificationImageBaseUrl+notificationImg))
                            .setBigContentTitle(title)
                            .setSummaryText(body))
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

    fun getBitmapFromURL(strURL: String): Bitmap? {
        try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }
}

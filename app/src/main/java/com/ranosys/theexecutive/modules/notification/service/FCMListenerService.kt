package com.ranosys.theexecutive.modules.notification.service

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
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(Constants.KEY_REDIRECTION_TYPE, redirectType)
        intent.putExtra(Constants.KEY_REDIRECTION_TITLE, redirectTitle)
        intent.putExtra(Constants.KEY_REDIRECTION_VALUE, redirectValue)
        intent.putExtra(Constants.KEY_NOTIFICATION_ID, notificationId)
        intent.putExtra(Constants.KEY_NOTIFICATION_TITLE, title)
        intent.putExtra(Constants.KEY_NOTIFICATION_MESSAGE, message)
        intent.putExtra(Constants.KEY_IMAGE, notificationImg)

        val pendingIntent = PendingIntent.getActivity(this, Calendar.getInstance().timeInMillis.toInt(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val big_bitmap_image = BitmapFactory.decodeResource(resources, R.mipmap.app_icon)

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
                   /* .setStyle(NotificationCompat.BigPictureStyle()
                            .bigPicture(big_bitmap_image)
                            .setBigContentTitle(title))*/
                    .setStyle(NotificationCompat.BigPictureStyle()
                            //This one is same as large icon but it wont show when its expanded that's why we again setting
                            // .bigLargeIcon(GlideApp.with(this).load("http://magento.theexecutive.co.id/pub/media/home_promotion/c/o/corporate.jpg").get())
                            //This is Big Banner image
                            .bigPicture(getBitmapFromURL("http://magento.theexecutive.co.id/pub/media/home_promotion/c/o/corporate.jpg"))
                            //When Notification expanded title and content text
                            .setBigContentTitle(title)
                            .setSummaryText(message))
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
                    /*.setStyle(NotificationCompat.BigPictureStyle()
                            .bigPicture(big_bitmap_image)
                            .setBigContentTitle(title))*/
                   // .setLargeIcon(GlideApp.with(this).load("http://magento.theexecutive.co.id/pub/media/home_promotion/c/o/corporate.jpg").get())
                    //BigPicture Style
                    .setStyle(NotificationCompat.BigPictureStyle()
                            //This one is same as large icon but it wont show when its expanded that's why we again setting
                           // .bigLargeIcon(GlideApp.with(this).load("http://magento.theexecutive.co.id/pub/media/home_promotion/c/o/corporate.jpg").get())
                            //This is Big Banner image
                            .bigPicture(getBitmapFromURL("http://magento.theexecutive.co.id/pub/media/home_promotion/c/o/corporate.jpg"))
                            //When Notification expanded title and content text
                            .setBigContentTitle(title)
                            .setSummaryText(message))
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

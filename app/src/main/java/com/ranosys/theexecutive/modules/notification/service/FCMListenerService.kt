package com.ranosys.theexecutive.modules.notification.service

import AppLog
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
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ranosys.theexecutive.BuildConfig
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.activities.DashBoardActivity
import com.ranosys.theexecutive.modules.splash.SplashActivity
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
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
    private var vibrationArray = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.data?.run {
            val dataMap = remoteMessage.data
            AppLog.e(dataMap.toString())
            redirectType = dataMap[Constants.KEY_REDIRECTION_TYPE] ?: ""
            redirectValue = dataMap[Constants.KEY_REDIRECTION_VALUE] ?: ""
            redirectTitle = dataMap[Constants.KEY_REDIRECTION_TITLE] ?: ""
            notificationImg = dataMap[Constants.KEY_IMAGE] ?: ""
            title = dataMap[Constants.KEY_TITLE] ?: ""
            body = dataMap[Constants.KEY_BODY] ?: ""
            notificationId = dataMap[Constants.KEY_NOTIFICATION_ID] ?: ""

            Constants.notificationCounter++

            //generate notification if body is not empty and Notification are enabled from settings
            createNotification(body, title)

        }
    }


    private fun createNotification(body: String, title: String) {
        val intent : Intent
        if(Utils.isAppIsInBackground(this)){
            intent = Intent(this, SplashActivity::class.java)
            intent.putExtra(Constants.KEY_APP_IN_BACKGROUND, true)

        }else{
            intent = Intent(this, DashBoardActivity::class.java)
            intent.putExtra(Constants.KEY_APP_IN_BACKGROUND, false)
        }

        intent.putExtra(Constants.KEY_REDIRECTION_TYPE, redirectType)
        if(!TextUtils.isEmpty(redirectTitle))
        intent.putExtra(Constants.KEY_REDIRECTION_TITLE, redirectTitle)
        intent.putExtra(Constants.KEY_REDIRECTION_VALUE, redirectValue)
        intent.putExtra(Constants.FROM_NOTIFICATION, Constants.IS_NOTIFICATION_SHOW)
        intent.putExtra(Constants.KEY_NOTIFICATION_ID, notificationId)
        intent.putExtra(Constants.KEY_NOTIFICATION_TITLE, title)
        intent.putExtra(Constants.KEY_NOTIFICATION_MESSAGE, body)
        intent.putExtra(Constants.KEY_IMAGE, notificationImg)

        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(this, Calendar.getInstance().timeInMillis.toInt(), intent,
                PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val importance = NotificationManager.IMPORTANCE_HIGH

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val notificationChannel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, resources.getString(R.string.app_name), importance)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.enableVibration(true)
                notificationManager.createNotificationChannel(notificationChannel)
            }

            notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(getNotificationIcon())
                    .setVibrate(vibrationArray)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setGroup(BuildConfig.APPLICATION_ID)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setChannelId(Constants.NOTIFICATION_CHANNEL_ID)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)


            if(!TextUtils.isEmpty(redirectTitle)){
                notification.setContentTitle(title)
            }
            if(notificationImg.isBlank().not()){
             notification.setStyle(NotificationCompat.BigPictureStyle()
                     .bigPicture(getBitmapFromURL(notificationImg))
                     .setBigContentTitle(title)
                     .setSummaryText(body))
            }

        } else {
            notification = NotificationCompat.Builder(this)
                    .setSmallIcon(getNotificationIcon())
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)

            if(!TextUtils.isEmpty(redirectTitle)){
                notification.setContentTitle(title)
            }

            if(notificationImg.isBlank().not()){
                notification.setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(getBitmapFromURL(notificationImg))
                        .setBigContentTitle(title)
                        .setSummaryText(body))
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.color = resources.getColor(R.color.black)
        }

        if(SavedPreferences.getInstance()?.getBooleanValue(Constants.IS_NOTIFICATION_SHOW)!!){
            notificationManager.notify(Calendar.getInstance().timeInMillis.toInt(), notification.build())

            /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                val summary = buildSummary(body)
                notificationManager.notify(0, summary!!.build())
            }*/
        }
    }

    private fun buildSummary(notificationBody: String): NotificationCompat.Builder? {
        val intent = Intent(this, SplashActivity::class.java)
        intent.putExtra(Constants.KEY_REDIRECTION_TYPE, "")
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(this, BuildConfig.APPLICATION_ID)
                .setContentText(notificationBody)
                .setSmallIcon(getNotificationIcon())
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(notificationBody))
                .setGroup(BuildConfig.APPLICATION_ID)
                .setGroupSummary(true)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    private fun getNotificationIcon(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            R.mipmap.tras_app_icon
        } else {
            R.mipmap.app_icon
        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Constants.notificationCounter--
    }

    private fun getBitmapFromURL(strURL: String): Bitmap? {
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

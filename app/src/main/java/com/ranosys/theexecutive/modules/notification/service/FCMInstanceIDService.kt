package com.ranosys.theexecutive.modules.notification.service

import android.util.Log

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences


class FCMInstanceIDService : FirebaseInstanceIdService() {

    // Called when registration token is refreshed i.e. when security of previous token is compromised
    // Not Called initially

    override fun onTokenRefresh() {
        super.onTokenRefresh()

        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.i(TAG, "FCM Registration Token: " + refreshedToken!!)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken)
    }

    private fun sendRegistrationToServer(token: String) {
        // Add custom implementation, as needed.
        SavedPreferences.getInstance()?.storeFcmId(Constants.USER_FCM_ID, token)
    }

    companion object {

        private val TAG = "RegIntentService"
    }
}

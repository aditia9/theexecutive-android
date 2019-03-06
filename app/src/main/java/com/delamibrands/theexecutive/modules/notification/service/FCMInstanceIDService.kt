package com.delamibrands.theexecutive.modules.notification.service

import AppLog
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.delamibrands.theexecutive.utils.Constants
import com.delamibrands.theexecutive.utils.SavedPreferences

/**
 * @Details A service class called on token refresh
 * @Author Ranosys Technologies
 * @Date 02,May,2018
 */
class FCMInstanceIDService : FirebaseInstanceIdService() {

    // Called when registration token is refreshed i.e. when security of previous token is compromised
    // Not Called initially
    override fun onTokenRefresh() {
        super.onTokenRefresh()

        val refreshedToken = FirebaseInstanceId.getInstance().token
        AppLog.d("FCM Registration Token: " , refreshedToken!!)
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken)
    }

    private fun sendRegistrationToServer(token: String) {
        // Add custom implementation, as needed.
        SavedPreferences.getInstance()?.saveStringValue(token, Constants.USER_FCM_ID)
    }
}

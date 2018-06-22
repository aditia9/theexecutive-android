package com.ranosys.theexecutive.modules.splash

import AppLog
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.TextUtils
import com.ranosys.theexecutive.BuildConfig
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.activities.DashBoardActivity
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.modules.myAccount.MyAccountDataClass
import com.ranosys.theexecutive.modules.notification.dataclasses.DeviceRegisterRequest
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.GlobalSingelton
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 * @Details Splash activity
 * @Author Ranosys Technologies
 * @Date 02,March,2018
 */
class SplashActivity : BaseActivity() {

    private val handler = Handler()
    private var canNavigateToHome: Boolean = false
    private var fromNotifcation: String? = null
    private var redirectType: String? = null
    private var notificationIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //check for auth token in SP if not get from assets
        if (TextUtils.isEmpty(SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY))) {
            val token: String = getAuthToken()
            SavedPreferences.getInstance()?.saveStringValue(token, Constants.ACCESS_TOKEN_KEY)
        }

        if (Utils.isConnectionAvailable(this)) {
            //call configuration API
            getConfigurationApi()
        } else {
            Utils.showNetworkErrorDialog(this)
        }

        //fetch device id
        getDeviceID()

        // get data from notification
        dataFromNotification()

        //register device on server
        registerDeviceOnServer()

        handler.postDelayed({
            kotlin.run {
                if (canNavigateToHome) moveToHome() else canNavigateToHome = true
            }
        }, Constants.SPLASH_TIMEOUT)

    }

    private fun dataFromNotification() {
        notificationIntent = intent
        val extras = notificationIntent?.extras
        notificationIntent?.extras?.run {
            fromNotifcation = extras?.getString(Constants.FROM_NOTIFICATION)
            redirectType = extras?.getString(Constants.KEY_REDIRECTION_TYPE)
        }
    }

    private fun getConfigurationApi() {
        AppRepository.getConfiguration(object : ApiCallback<ConfigurationResponse> {
            override fun onException(error: Throwable) {
                AppLog.d("Config Api : ${error.message}")
                showExitApplicationDialog(getString(R.string.common_error), pAction = { getConfigurationApi() }, nAction = { finish() })
            }

            override fun onError(errorMsg: String) {
                AppLog.e("Config Api : $errorMsg")
                showExitApplicationDialog(getString(R.string.common_error), pAction = { getConfigurationApi() }, nAction = { finish() })
            }

            override fun onSuccess(configuration: ConfigurationResponse?) {
                manageConfiguration(configuration)
            }

        })
    }

    private fun manageConfiguration(configuration: ConfigurationResponse?) {
        val ignore = SavedPreferences.getInstance()?.getStringValue(Constants.IGNORE)

        if (configuration?.maintenance == Constants.MAINTENENCE_OFF) {
            GlobalSingelton.instance?.configuration = configuration
            //check version
            if (configuration.version.toDouble().toInt() > BuildConfig.VERSION_NAME.toDouble().toInt()) {
                //force update
                AppLog.d("Config Api : FORCE UPDATE")
                showExitApplicationDialog(getString(R.string.force_update_msg), pText = getString(R.string.update), nText = getString(R.string.exit) ,pAction = {
                    redirectToPlaystore()
                }, nAction = {finish()})

            } else if (configuration.version.toFloat() > BuildConfig.VERSION_NAME.toFloat()&& ignore.isNullOrBlank()) {
                //soft update
                showExitApplicationDialog(getString(R.string.soft_update_msg), pText = getString(R.string.update), nText = getString(R.string.ignore), pAction = {
                    redirectToPlaystore()
                }, nAction = {
                    //call store api
                    SavedPreferences.getInstance()?.saveStringValue(getString(R.string.ignore), Constants.IGNORE)
                    getStoresApi()
                    //get cart id and count
                    getCartIdAndCount()
                })
            }else{
                //call store api
                getStoresApi()
                //get cart id and count
                getCartIdAndCount()
            }
        } else {
            //stop app with maintenance message
            AppLog.d("Config Api : MAINTENANCE MODE")
            showExitApplicationDialog(configuration?.message_maintenance ?: getString(R.string.maintenance_msg), nText = "",  pAction = { finish() })

        }
    }

    private fun redirectToPlaystore() {
        startActivity( Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.project.CK")))
        finish()
    }

    private fun getCartIdAndCount() {
        //if user logged in get his cart count and cart id
        val userToken = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val guestCardId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY) ?: ""

        if (userToken.isNullOrBlank().not()) {
            getCartIdForUser()
            // to get user info and save in global singleton
            getUserInformation()
        } else if (guestCardId.isNotBlank()) {
            getGuestCartCount(guestCardId)
        }
    }

    private fun getUserInformation() {
        AppRepository.getUserInfo(object: ApiCallback<MyAccountDataClass.UserInfoResponse> {
            override fun onException(error: Throwable) {
                AppLog.e("My Information API : ${error.message}")
            }

            override fun onError(errorMsg: String) {
                AppLog.e("My Information API : $errorMsg")
            }

            override fun onSuccess(t: MyAccountDataClass.UserInfoResponse?) {
                //update info saved at singleton
                GlobalSingelton.instance?.userInfo = t

                //save username and email in sharedpreference too because Global singleton will
                // not persist data when app got killed.
                SavedPreferences.getInstance()?.saveStringValue(t?.email, Constants.USER_EMAIL)
                SavedPreferences.getInstance()?.saveStringValue(t?.firstname, Constants.FIRST_NAME)
                SavedPreferences.getInstance()?.saveStringValue(t?.lastname, Constants.LAST_NAME)
            }
        })
    }

    private fun getStoresApi() {
        AppRepository.getStores(object : ApiCallback<ArrayList<StoreResponse>> {
            override fun onSuccess(stores: ArrayList<StoreResponse>?) {

                val it = stores?.iterator()
                while (it?.hasNext()!!) {
                    val integer = it.next()
                    if (integer.id == 0) {
                        it.remove()
                    }
                }

                GlobalSingelton.instance?.storeList = stores

                if (canNavigateToHome) moveToHome() else canNavigateToHome = true
            }

            override fun onException(error: Throwable) {
                AppLog.e(error.message!!)
                if (canNavigateToHome) moveToHome() else canNavigateToHome = true

            }

            override fun onError(errorMsg: String) {
                AppLog.e(errorMsg)
                if (canNavigateToHome) moveToHome() else canNavigateToHome = true
            }


        })
    }

    private fun showExitApplicationDialog(message: String, pText: String = getString(R.string.ok), pAction: () -> Unit = {}, nText: String = getString(R.string.cancel), nAction: () -> Unit = {}) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
                .setPositiveButton(pText) { dialog, _ ->
                    dialog.cancel()
                    pAction()
                }

                .setNegativeButton(nText) { dialog, _ ->
                    dialog.cancel()
                    nAction()
                }

        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.theme_black_color))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.theme_black_color))
    }

    private fun moveToHome() {
        val intent = Intent(this@SplashActivity, DashBoardActivity::class.java)
        if (!TextUtils.isEmpty(redirectType)) {
            intent.putExtras(notificationIntent)
        }
        startActivity(intent)
        finish()
    }

    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun getAuthToken(): String {
        var reader: BufferedReader? = null
        var token = ""
        try {
            reader = BufferedReader(InputStreamReader(assets.open(Constants.CONFIG_FILE_NAME)))
            token = reader.readLine()

        } catch (e: IOException) {
            if (BuildConfig.DEBUG)
                e.printStackTrace()
        } finally {
            if (null != reader) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    if (BuildConfig.DEBUG)
                        e.printStackTrace()
                }
            }
        }

        return token
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceID() {
        if (TextUtils.isEmpty(SavedPreferences.getInstance()?.getStringValue(Constants.ANDROID_DEVICE_ID_KEY))) {
            SavedPreferences.getInstance()?.saveStringValue(Settings.Secure.getString(contentResolver,
                    Settings.Secure.ANDROID_ID), Constants.ANDROID_DEVICE_ID_KEY)

        }
        AppLog.d("device id: "+SavedPreferences.getInstance()?.getStringValue(Constants.ANDROID_DEVICE_ID_KEY))

    }

    private fun getCartIdForUser() {
        val apiResponse = ApiResponse<String>()
        AppRepository.createUserCart(object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                AppLog.e("User Cart Id: ${error.message}")
            }

            override fun onError(errorMsg: String) {
                AppLog.e("User Cart Id: $errorMsg")
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                SavedPreferences.getInstance()?.saveStringValue(t, Constants.USER_CART_ID_KEY)
                getUserCartCount()
            }

        })
    }

    private fun getUserCartCount() {
        val apiResponse = ApiResponse<String>()
        AppRepository.cartCountUser(object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                AppLog.e("User Cart count: ${error.message}")
            }

            override fun onError(errorMsg: String) {
                AppLog.e("User Cart count: $errorMsg")
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                try {
                    Utils.updateCartCount(t?.toInt() ?: 0)
                } catch (e: NumberFormatException) {
                    AppLog.printStackTrace(e)
                }

            }

        })

    }

    private fun getGuestCartCount(cartId: String) {
        val apiResponse = ApiResponse<String>()
        AppRepository.cartCountGuest(cartId, object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                AppLog.e("Guest Cart count: ${error.message}")
            }

            override fun onError(errorMsg: String) {
                AppLog.e("Guest Cart count: $errorMsg")
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                try {
                    Utils.updateCartCount(t?.toInt() ?: 0)
                } catch (e: NumberFormatException) {
                    AppLog.printStackTrace(e)
                }
            }
        })

    }

    private fun registerDeviceOnServer() {
        val request = DeviceRegisterRequest(Constants.OS_TYPE,
                SavedPreferences.getInstance()?.getStringValue(Constants.USER_FCM_ID),
                SavedPreferences.getInstance()?.getStringValue(Constants.ANDROID_DEVICE_ID_KEY))

        AppRepository.registerDevice(request, object : ApiCallback<Boolean> {
            override fun onSuccess(t: Boolean?) {
                AppLog.d(t.toString())
            }

            override fun onException(error: Throwable) {
                AppLog.d(error.message!!)
            }

            override fun onError(errorMsg: String) {
                AppLog.d(errorMsg)
            }
        })
    }

}

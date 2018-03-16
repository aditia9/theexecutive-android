package com.ranosys.theexecutive.modules.splash

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.TextUtils
import com.ranosys.theexecutive.BuildConfig
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.activities.DashBoardActivity
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.GlobalSingelton
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class SplashActivity : BaseActivity() {

    private val SPLASH_TIMEOUT = 3000
    private val handler = Handler()
    private var canNavigateToHome: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //check for auth token in SP if not get from assets
        if(TextUtils.isEmpty(SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY))){
            val token: String = getAuthToken()
            SavedPreferences.getInstance()?.saveStringValue(token, Constants.ACCESS_TOKEN_KEY)
        }

        //call configuration API
        getConfigurationApi()

        //fetch device id
        getDeviceID()



        handler.postDelayed({
            kotlin.run {
                if(canNavigateToHome) moveToHome() else canNavigateToHome = true
            }
        }, SPLASH_TIMEOUT.toLong())

    }

    private fun getConfigurationApi() {
        AppRepository.getConfiguration(object: ApiCallback<ConfigurationResponse>{
            override fun onException(error: Throwable) {
                Utils.printLog("Config Api", "Error")
                getStoresApi()
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("Config Api", errorMsg)
                getStoresApi()
            }

            override fun onSuccess(configuration: ConfigurationResponse?) {
                manageConfiguration(configuration)
            }

        })
    }

    private fun manageConfiguration(configuration: ConfigurationResponse?) {
        if(configuration?.maintenance == Constants.MAINTENENCE_OFF){

            //call store api
            getStoresApi()

            //check version
            if(configuration.version.toFloat() >= BuildConfig.VERSION_CODE + 1){
                //force update
                Utils.printLog("Config Api", "Force Update")
                showExitApplicationDialog(getString(R.string.force_update_msg), {
                    //redirect to play store
                })
            }else if(configuration.version.toFloat() >= BuildConfig.VERSION_NAME.toFloat()){
                //soft update
                Utils.printLog("Config Api", "Soft Update")
            }
        }else{
            //stop app with maintenance message
            Utils.printLog("Config Api", "Maintance Mode")
            showExitApplicationDialog(getString(R.string.maintenence_msg), {finish()})

        }
    }

    private fun getStoresApi() {
        AppRepository.getStores(object: ApiCallback<ArrayList<StoreResponse>>{
            override fun onSuccess(stores: ArrayList<StoreResponse>?) {
                GlobalSingelton.instance?.storeList = stores

//                if(TextUtils.isEmpty(SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)) ||
//                        TextUtils.isEmpty(SavedPreferences.getInstance()?.getIntValue(Constants.SELECTED_STORE_ID_KEY).toString()) ||
//                        TextUtils.isEmpty(SavedPreferences.getInstance()?.getIntValue(Constants.SELECTED_WEBSITE_ID_KEY).toString())){
//                    for(store in stores!!){
//                        if(store.id == 1){
//                            SavedPreferences.getInstance()?.saveStringValue(store.code, Constants.SELECTED_STORE_CODE_KEY)
//                            SavedPreferences.getInstance()?.saveIntValue(store.id, Constants.SELECTED_STORE_ID_KEY)
//                            SavedPreferences.getInstance()?.saveIntValue(store.website_id, Constants.SELECTED_WEBSITE_ID_KEY)
//                            break
//                        }
//                    }
//                }

                if(canNavigateToHome) moveToHome() else canNavigateToHome = true
            }

            override fun onException(error: Throwable) {
                Utils.printLog("Store Api", "error")
                if(canNavigateToHome) moveToHome() else canNavigateToHome = true

            }

            override fun onError(errorMsg: String) {
                Utils.printLog("Store Api", errorMsg)
                if(canNavigateToHome) moveToHome() else canNavigateToHome = true
            }


        })
    }

    private fun showExitApplicationDialog(message: String, action:() -> Unit = {}) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
                .setPositiveButton(android.R.string.ok) {
                    dialog, id -> dialog.cancel()
                    action()}

        val alert = builder.create()
        alert.show()
    }


    private fun moveToHome(){
        val intent = Intent(this@SplashActivity, DashBoardActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun getAuthToken(): String{
        var reader: BufferedReader? = null
        var token = ""
        try {
            reader =  BufferedReader(InputStreamReader(assets.open(Constants.CONFIG_FILE_NAME)))
            token = reader.readLine()

        } catch (e: IOException) {
            if(BuildConfig.DEBUG)
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

    private fun getDeviceID() {
        if (TextUtils.isEmpty(SavedPreferences.getInstance()?.getStringValue(Constants.ANDROID_DEVICE_ID_KEY))) {
            SavedPreferences.getInstance()?.saveStringValue(Settings.Secure.getString(contentResolver,
                    Settings.Secure.ANDROID_ID), Constants.ANDROID_DEVICE_ID_KEY)
        }
    }
}

package com.ranosys.theexecutive

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.TextUtils
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.modules.splash.StoreResponse
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by Mohammad Sunny on 25/1/18.
 */
class SplashActivity : BaseActivity() {

    val SPLASH_TIMEOUT = 3000
    val handler = Handler()
    var canNavigateToHome: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //call configuration api
        getStoresApi()


        //fetch device id
        getDeviceID()

        //check for auth token in SP if not get from assets
        if(TextUtils.isEmpty(SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY))){
            val token: String = getAuthToken()
            SavedPreferences.getInstance()?.saveStringValue(token, Constants.ACCESS_TOKEN_KEY)
        }

        //check for selected language
        if(TextUtils.isEmpty(SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_KEY))){
            SavedPreferences.getInstance()?.saveStringValue(Constants.DEFAULT_STORE_CODE, Constants.SELECTED_STORE_KEY)
        }

        handler.postDelayed(Runnable {
            kotlin.run {
                if(canNavigateToHome){
                    moveToHome()
                }else{
                    canNavigateToHome = true
                }
            }
        }, SPLASH_TIMEOUT.toLong())

    }

    private fun getStoresApi() {
        AppRepository.getStores(object: ApiCallback<ArrayList<StoreResponse>>{
            override fun onSuccess(t: ArrayList<StoreResponse>?) {
                if(canNavigateToHome){
                    moveToHome()
                }else{
                    canNavigateToHome = true
                }
            }

            override fun onException(error: Throwable) {
                if(canNavigateToHome){
                    moveToHome()
                }else{
                    canNavigateToHome = true
                }
            }

            override fun onError(errorMsg: String) {
                if(canNavigateToHome){
                    moveToHome()
                }else{
                    canNavigateToHome = true
                }
            }


        })
    }


    private fun moveToHome(){
        val intent = Intent(this@SplashActivity, UserActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null);
    }

    fun getAuthToken(): String{
        var reader: BufferedReader? = null
        var token: String = ""
        try {
            reader =  BufferedReader(InputStreamReader(getAssets().open("config")))
            token = reader.readLine()



        } catch (e: IOException) {
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
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

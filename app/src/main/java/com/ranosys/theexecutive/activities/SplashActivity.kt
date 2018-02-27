package com.ranosys.theexecutive

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.modules.splash.AdminDataClass
import com.ranosys.theexecutive.modules.splash.StoreResponse
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences

/**
 * Created by Mohammad Sunny on 25/1/18.
 */
class SplashActivity : BaseActivity() {

    val SPLASH_TIMEOUT = 1000
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //call admin token api
        getAdminToken()


        //check for admin token in SP

//        handler.postDelayed({
//            kotlin.run {
//                moveToHome()
//            }
//        }, SPLASH_TIMEOUT.toLong())
    }

    private fun getAdminToken() {

        val adminTokenRequest = AdminDataClass("admin", "admin123")
        AppRepository.getAdminToken(adminTokenRequest, object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                //
            }

            override fun onError(errorMsg: String) {
                //
            }

            override fun onSuccess(adminToken: String?) {
                //save admin token in shared preference
                SavedPreferences.getInstance()?.saveStringValue(Constants.ADMIN_TOKEN_KEY, adminToken?: "")

                //call store api
                getStores()
            }
        })

    }

    private fun getStores() {
        val adminToken = SavedPreferences.getInstance()?.getStringValue(Constants.ADMIN_TOKEN_KEY)

        AppRepository.getStores(object : ApiCallback<StoreResponse>{
            override fun onException(error: Throwable) {
                //
            }

            override fun onError(errorMsg: String) {
                //
            }

            override fun onSuccess(storeList: StoreResponse?) {
                Log.e("lkl", "sfsfsssfsfs")
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
}

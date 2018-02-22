package com.ranosys.theexecutive

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.ranosys.rtp.IsPermissionGrantedInterface
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.utils.Utils

/**
 * Created by Mohammad Sunny on 25/1/18.
 */
class SplashActivity : BaseActivity() {

    val SPLASH_TIMEOUT = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            kotlin.run {
                if(Utils.isMarshmallowOrAbove!!){
                    // initialization of an Arraylist
                    val permissionList: MutableList<String> = ArrayList<String>()
                    permissionList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    getPermission(permissionList, object: IsPermissionGrantedInterface{
                        override fun Done(isAllPermissionGranted: Boolean) {
                            moveToHome()
                        }
                    })
                }else{
                    moveToHome()
                }
            }
        }, SPLASH_TIMEOUT.toLong())
    }
    fun moveToHome(){
        val intent = Intent(this@SplashActivity, UserActivity::class.java)
        startActivity(intent)
        finish()
    }
}

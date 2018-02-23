package com.ranosys.theexecutive.base

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import com.ranosys.rtp.RunTimePermissionActivity
import com.ranosys.theexecutive.utils.Utils

/**
 * Created by Mohammad Sunny on 24/1/18.
 */
open class BaseActivity: RunTimePermissionActivity(){

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        Utils.hideSoftKeypad(this)
        if(supportFragmentManager.backStackEntryCount > 1){
            supportFragmentManager.popBackStackImmediate()
        }else{
            this.finish()
        }
    }

    fun setScreenTitle(title: String){
        supportActionBar?.setTitle(title)
    }

    private fun changeStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(color)
        }
    }
}
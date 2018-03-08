package com.ranosys.theexecutive.base

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import com.ranosys.rtp.RunTimePermissionActivity
import com.ranosys.theexecutive.activities.ToolbarViewModel
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by Mohammad Sunny on 24/1/18.
 */
open class BaseActivity: RunTimePermissionActivity(){

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarViewModel = ViewModelProviders.of(this).get(ToolbarViewModel::class.java)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
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
        toolbarViewModel?.title?.set(title)
    }

    fun setLeftIcon(icon: Int){
        toolbarViewModel?.icon?.set(icon)
    }

    fun setLeftIconVisibility(isVisible: Boolean){
        toolbarViewModel?.isIconVisible?.set(isVisible)
    }

    private fun changeStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(color)
        }
    }

    fun hideToolBar(){
        supportActionBar?.hide()
    }

    companion object{
        var toolbarViewModel: ToolbarViewModel? = null
    }
}

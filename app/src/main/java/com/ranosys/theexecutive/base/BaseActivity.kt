package com.ranosys.theexecutive.base

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import com.ranosys.rtp.RunTimePermissionActivity
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.activities.ToolbarViewModel
import com.ranosys.theexecutive.modules.home.HomeFragment
import com.ranosys.theexecutive.utils.DialogOkCallback
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by Mohammad Sunny on 22/2/18.
 */
open class BaseActivity: RunTimePermissionActivity(){

    var toolbarViewModel: ToolbarViewModel? = null

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(Utils.isTablet(this)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        toolbarViewModel = ViewModelProviders.of(this).get(ToolbarViewModel::class.java)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onBackPressed() {
        Utils.hideSoftKeypad(this)
        if(supportFragmentManager.backStackEntryCount > 1){
            supportFragmentManager.popBackStackImmediate()
        }else{
            val fragment = FragmentUtils.getCurrentFragment(this@BaseActivity)
            fragment?.run {
                if (fragment is HomeFragment) {
                    when(HomeFragment.fragmentPosition){
                        0-> {
                            Utils.showDialog(this@BaseActivity, getString(R.string.close_app_text),
                                    getString(R.string.yes), getString(R.string.no), object : DialogOkCallback {
                                override fun setDone(done: Boolean) {
                                    finish()
                                }
                            })
                        }
                        1,2 -> {
                            fragment.viewpager.setCurrentItem(0, true)
                        }
                    }

                }
            }

        }
    }

    fun setShowLogo(showLogo: Boolean){
        toolbarViewModel?.showLogo?.set(showLogo)
    }

    fun setScreenTitle(title: String?){
        toolbarViewModel?.title?.set(title)
    }

    fun setTitleBackground(background: Int?){
        toolbarViewModel?.titleBackground?.set(background)
    }

    fun setSubTitle(subTitle: String?){
        toolbarViewModel?.subTitle?.set(subTitle)
    }

    fun setLeftIcon(icon: Int?){
        toolbarViewModel?.leftIcon?.set(icon)
    }

    fun setLeftIconVisibility(isVisible: Boolean){
        toolbarViewModel?.isLeftIconVisible?.set(isVisible)
    }

    fun setRightIcon(icon: Int?){
        toolbarViewModel?.rightIcon?.set(icon)
    }

    fun setRightIconVisibility(isVisible: Boolean){
        toolbarViewModel?.isRightIconVisible?.set(isVisible)
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

}

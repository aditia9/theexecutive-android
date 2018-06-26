package com.ranosys.theexecutive.base

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import com.ranosys.rtp.RunTimePermissionActivity
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.activities.ToolbarViewModel
import com.ranosys.theexecutive.modules.home.HomeFragment
import com.ranosys.theexecutive.modules.shoppingBag.ShoppingBagFragment
import com.ranosys.theexecutive.utils.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import android.annotation.TargetApi
import android.content.res.Configuration
import java.util.*


/**
 * @Details Base class for all activities
 * @Author Ranosys Technologies
 * @Date 22,Feb,2018
 */
open class BaseActivity: RunTimePermissionActivity(){

    var toolbarViewModel: ToolbarViewModel? = null
    private lateinit var baseViewModel: BaseViewModel

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // changeStatusBarColor(R.color.white)
        requestedOrientation = if(Utils.isTablet(this)){
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }else{
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        baseViewModel = ViewModelProviders.of(this).get(BaseViewModel::class.java)
        toolbarViewModel = ViewModelProviders.of(this).get(ToolbarViewModel::class.java)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        observeCartCount()
    }

    private fun observeCartCount() {
        GlobalSingelton.instance?.cartCount?.observe(this, Observer { count ->
            when(count){
                0 -> toolbarViewModel?.cartCount?.set("")
                in 1..99 -> toolbarViewModel?.cartCount?.set(count.toString())
                else -> toolbarViewModel?.cartCount?.set("99+")
            }
        })
    }

    override fun onBackPressed() {
        Utils.hideSoftKeypad(this)
        if(supportFragmentManager.backStackEntryCount > 1){
            supportFragmentManager.popBackStack()
        }else{
            val fragment = FragmentUtils.getCurrentFragment(this@BaseActivity)
            fragment?.run {
                if (fragment is HomeFragment) {
                    when(HomeFragment.fragmentPosition){
                        0-> {
                            Utils.showDialog(this@BaseActivity, getString(R.string.close_app_text),
                                    getString(R.string.yes), getString(R.string.no), object : DialogOkCallback {
                                override fun setDone(done: Boolean) {
                                    finishAndRemoveTask()
                                }
                            })
                        }
                        1,2 -> {
                            fragment.viewpager.setCurrentItem(0, true)
                        }
                    }

                }else{
                    finish()
                }
            }

        }
    }

    fun setShoppingBagFragment(){
        FragmentUtils.addFragment(this, ShoppingBagFragment(), null, ShoppingBagFragment::class.java.name, true)
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
            window.statusBarColor = ContextCompat.getColor(this,color)
        }
    }

    fun hideToolBar(){
        supportActionBar?.hide()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
    }

    private fun updateBaseContextLocale(context: Context): Context {
        var language = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY) // Helper method to get saved language from SharedPreferences
        var countryCode = ""
       when(language){
          Constants.COUNTRY_CODE_ID ->{
               countryCode = Constants.COUNTRY_CODE_IN
               language = Constants.COUNTRY_CODE_ID
          }

           Constants.DEFAULT_STORE_CODE ->{
               countryCode = Constants.COUNTRY_CODE_US
               language =  Constants.DEFAULT_STORE_CODE
           }
       }
        val locale = Locale(language, countryCode)
        Locale.setDefault(locale)
       return updateResourcesLocale(context, locale)

    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResourcesLocale(context: Context, locale: Locale): Context {
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            SavedPreferences.getInstance()?.setBooleanValue(Constants.ORIENTATION, true)
        } else {
            SavedPreferences.getInstance()?.setBooleanValue( Constants.ORIENTATION, false)
        }
    }
}

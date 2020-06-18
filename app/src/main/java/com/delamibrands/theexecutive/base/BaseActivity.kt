package com.delamibrands.theexecutive.base

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.activities.ToolbarViewModel
import com.delamibrands.theexecutive.modules.home.HomeFragment
import com.delamibrands.theexecutive.modules.shoppingBag.ShoppingBagFragment
import com.delamibrands.theexecutive.utils.*
import com.facebook.FacebookSdk.addLoggingBehavior
import com.facebook.FacebookSdk.setAutoLogAppEventsEnabled
import com.facebook.LoggingBehavior
import com.facebook.appevents.AppEventsLogger
import com.ranosys.rtp.RunTimePermissionActivity
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.io.IOException
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
        GlobalSingelton.instance?.cartCount?.observeForever {count ->
            when(count){
                0 -> toolbarViewModel?.cartCount?.set("")
                in 1..99 -> toolbarViewModel?.cartCount?.set(count.toString())
                else -> toolbarViewModel?.cartCount?.set("99+")
            }
        }
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

    private fun updateResourcesLocale(context: Context, locale: Locale): Context {
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            SavedPreferences.getInstance()?.setBooleanValue(Constants.ORIENTATION, true)
        } else {
            SavedPreferences.getInstance()?.setBooleanValue( Constants.ORIENTATION, false)
        }
    }

    fun getCountryJson(): String {
        var json: String? = null
        try {
            val iStream = this?.getAssets()?.open("countryCodes.json")
            val size = iStream?.available()
            val buffer = ByteArray(size ?: 0)
            iStream?.read(buffer)
            iStream?.close()
            json = String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }

        return json
    }
}

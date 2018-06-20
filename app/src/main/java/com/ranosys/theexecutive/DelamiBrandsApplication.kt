package com.ranosys.theexecutive

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
import io.fabric.sdk.android.Fabric

/**
 * @Details Application class
 * @Author Ranosys Technologies
 * @Date 02,March,2018
 */
class DelamiBrandsApplication : Application(){

    companion object {
        var samleApplication: DelamiBrandsApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        samleApplication = this
        SavedPreferences.init(this)
        Fabric.with(this, Crashlytics())
    }

    override fun attachBaseContext(base: Context?) {
        val lang = SavedPreferences.savedPreferences?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
        super.attachBaseContext(Utils.setLocale(base!!, lang ?: Constants.DEFAULT_STORE_CODE))
    }
}
package com.ranosys.theexecutive

import android.app.Application
import com.crashlytics.android.Crashlytics
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
        Fabric.with(this, Crashlytics())
    }
}
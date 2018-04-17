package com.ranosys.theexecutive

import android.app.Application
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

/**
 * Created by Mohammad Sunny on 24/1/18.
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
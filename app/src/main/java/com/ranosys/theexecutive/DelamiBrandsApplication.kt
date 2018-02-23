package com.ranosys.theexecutive

import android.app.Application
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration

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
        //Fabric.with(this, Crashlytics())
        initiateUIL()
    }

    private fun initiateUIL() {
        val config = ImageLoaderConfiguration.Builder(
                applicationContext)
                .memoryCacheSize(20000000)
                // 1.5 Mb
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(Md5FileNameGenerator())
                .build()
        ImageLoader.getInstance().init(config)
    }
}
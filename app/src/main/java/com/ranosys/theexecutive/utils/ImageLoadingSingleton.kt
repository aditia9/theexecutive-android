package com.ranosys.theexecutive.utils

import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageScaleType

/**
 * Created by ranosys on 24/1/18.
 */
class ImageLoadingSingleton private constructor(){
    companion object {

        var imageLoaderSingleton: ImageLoader? = null

        fun init() {
            imageLoaderSingleton = imageLoaderSingleton?: ImageLoader.getInstance()
        }

        var displayOption: DisplayImageOptions? = null
        get() {
            field =  field ?: DisplayImageOptions.Builder()
                    .showImageForEmptyUri(-1)
                    .showImageOnFail(-1)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .considerExifParams(true)
                    .build();
            return field
        }
        set(value) {
            field = value
        }
    }
}
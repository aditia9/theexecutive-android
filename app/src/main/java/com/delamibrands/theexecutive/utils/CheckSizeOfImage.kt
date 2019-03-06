package com.delamibrands.theexecutive.utils

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition


class CheckSizeOfImage() {
    fun getRatioOfImage(path: String?, pos: Int, context: Context, imageCallBack: ImageCallBack) {
        var ratioList: HashMap<Int, Double> = HashMap()
        if (TextUtils.isEmpty(path).not()) {
            GlideApp.with(context)
                    .asBitmap()
                    .load(GlobalSingelton.instance?.configuration?.category_media_url + path)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val ratio = resource.height.toDouble() / resource.width.toDouble()
                            ratioList[pos] = ratio
                            imageCallBack.callBackImage(ratioList)
                        }
                    })
        } else {
            ratioList[pos] = 0.0
            imageCallBack.callBackImage(ratioList)

        }
    }
}
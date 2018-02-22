package com.ranosys.theexecutive.base

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.view.View
import com.ranosys.theexecutive.utils.Utils

/**
 * Created by Mohammad Sunny on 24/1/18.
 */
open class BaseViewModel : BaseObservable(){

    val backButtonClicked = MutableLiveData<Boolean>()

    fun backButtonClicked(view: View) {
        Utils.hideSoftKeypad(view.context)
        backButtonClicked.setValue(true)
    }
    fun doNothing(v: View) {}
}
package com.ranosys.theexecutive.base

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.view.View
import com.ranosys.theexecutive.utils.Utils

/**
 * Created by Mohammad Sunny on 24/1/18.
 */
open class BaseViewModel(application : Application?) : AndroidViewModel(application){

    val backButtonClicked = MutableLiveData<Boolean>()

    fun backButtonClicked(view: View) {
        Utils.hideSoftKeypad(view.context)
        backButtonClicked.setValue(true)
    }

}
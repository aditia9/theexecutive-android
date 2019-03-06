package com.delamibrands.theexecutive.activities

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.delamibrands.theexecutive.base.BaseViewModel


/**
 * @Class A ViewModel for DashBoardActivity so that on configuration change, view doesn't load again.
 * @author Ranosys Technologies
 * @Date 09-Apr-2018
 */
class DashBoardViewModel(application: Application) : BaseViewModel(application) {

    private val isCreated: MutableLiveData<Boolean>? = MutableLiveData()

    fun manageFragments(): MutableLiveData<Boolean> {
        if(isCreated?.value == null){
            isCreated?.setValue(true)
        }else{
            isCreated.setValue(false)
        }
        return isCreated!!
    }
}
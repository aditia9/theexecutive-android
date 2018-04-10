package com.ranosys.theexecutive.activities

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.ranosys.theexecutive.base.BaseViewModel


/**
 * Created by Mohammad Sunny on 9/4/18.
 * @Class A ViewModel for DashBoardActivity so that on configuration change, view doesn't load again.
 * @author Ranosys Technologies
 * @Date 09-Apr-2018
 */
class DashBoardViewModel(application: Application) : BaseViewModel(application) {

    val isCreated: MutableLiveData<Boolean>? = MutableLiveData<Boolean>()

    fun manageFragments(): MutableLiveData<Boolean> {
        if(isCreated?.value == null){
            isCreated?.setValue(true)
        }else{
            isCreated.setValue(false)
        }
        return isCreated!!
    }
}
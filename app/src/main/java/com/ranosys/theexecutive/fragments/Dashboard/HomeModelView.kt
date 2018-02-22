package com.ranosys.theexecutive.fragments.Dashboard

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.ranosys.theexecutive.base.BaseViewModel

/**
 * Created by Mohammad Sunny on 2/2/18.
 */
class HomeModelView : BaseViewModel() {

    var userData: ObservableField<HomeDataClass.HomeUserData>? = ObservableField<HomeDataClass.HomeUserData>()

    var name: ObservableField<String>? = ObservableField()
    var buttonClicked = MutableLiveData<HomeDataClass.HomeUserData>()


    fun onItemClick(userData: HomeDataClass.HomeUserData?){
        buttonClicked.value = userData
    }

}
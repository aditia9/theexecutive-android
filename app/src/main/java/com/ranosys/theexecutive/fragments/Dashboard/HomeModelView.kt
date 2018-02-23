package com.ranosys.theexecutive.fragments.Dashboard

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.BindingAdapter
import android.databinding.ObservableField
import android.support.v7.widget.RecyclerView
import com.ranosys.theexecutive.base.BaseViewModel

/**
 * Created by Mohammad Sunny on 2/2/18.
 */
class HomeModelView(application: Application) : BaseViewModel(application) {

    companion object {
        //binding adapters
        @JvmStatic
        @BindingAdapter("app:onClickItem")
        fun onItemClick(view : RecyclerView, model : HomeModelView){
            model.onItemClick(model.userData?.get())
        }
    }

    var userData: ObservableField<HomeDataClass.HomeUserData>? = ObservableField<HomeDataClass.HomeUserData>()

    var name: ObservableField<String>? = ObservableField()
    var buttonClicked = MutableLiveData<HomeDataClass.HomeUserData>()


    fun onItemClick(userData: HomeDataClass.HomeUserData?){
        buttonClicked.value = userData
    }

}
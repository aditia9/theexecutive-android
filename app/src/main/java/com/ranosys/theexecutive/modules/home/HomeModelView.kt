package com.ranosys.theexecutive.modules.home

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel

/**
 * Created by Mohammad Sunny on 2/2/18.
 */
class HomeModelView(application: Application) : BaseViewModel(application) {

    var categoryList : ObservableArrayList<ChildrenData>? = ObservableArrayList<ChildrenData>()
    var mutualHomeResponse = MutableLiveData<ApiResponse<HomeResponseDataClass>>()
    var homeResponse : ObservableField<HomeResponseDataClass>? = ObservableField<HomeResponseDataClass>()

    fun getCategories(){
        val apiResponse = ApiResponse<HomeResponseDataClass>()
        AppRepository.getCategories(object : ApiCallback<HomeResponseDataClass>{
            override fun onException(error: Throwable) {
                mutualHomeResponse.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                mutualHomeResponse.value?.error = errorMsg
            }

            override fun onSuccess(t: HomeResponseDataClass?) {
                apiResponse.apiResponse = t
                mutualHomeResponse.value = apiResponse
            }

        })
    }

}
package com.ranosys.theexecutive.modules.category

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel

/**
 * Created by Mohammad Sunny on 2/2/18.
 */
class CategoryModelView(application: Application) : BaseViewModel(application) {

    var mutualHomeResponse = MutableLiveData<ApiResponse<CategoryResponseDataClass>>()
    var categoryResponse: ObservableField<CategoryResponseDataClass>? = ObservableField<CategoryResponseDataClass>()

    fun getCategories(){
        val apiResponse = ApiResponse<CategoryResponseDataClass>()
        AppRepository.getCategories(object : ApiCallback<CategoryResponseDataClass>{
            override fun onException(error: Throwable) {
                mutualHomeResponse.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                mutualHomeResponse.value?.error = errorMsg
            }

            override fun onSuccess(t: CategoryResponseDataClass?) {
                apiResponse.apiResponse = t
                mutualHomeResponse.value = apiResponse
            }

        })
    }

}
package com.ranosys.theexecutive.modules.myAccount

import AppLog
import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 27-Apr-2018
 */
class MyInformationViewModel(application: Application): BaseViewModel(application) {

    var userInfoApiResponse: MutableLiveData<ApiResponse<ObservableField<MyAccountDataClass.UserInfoResponse>>>? = null


    fun callUserInfoApi() {
        val apiResponse = ApiResponse<ObservableField<MyAccountDataClass.UserInfoResponse>>()
        AppRepository.getUserInfo(object: ApiCallback<MyAccountDataClass.UserInfoResponse>{
            override fun onException(error: Throwable) {
                AppLog.e("My Information API : ${error.message}")
                apiResponse.error = error.message

            }

            override fun onError(errorMsg: String) {
                AppLog.e("My Information API : ${errorMsg}")
                apiResponse.error = errorMsg
            }

            override fun onSuccess(t: MyAccountDataClass.UserInfoResponse?) {
                apiResponse.apiResponse?.set(t)
            }

        })
    }

}
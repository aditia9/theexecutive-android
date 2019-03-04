package com.delamibrands.theexecutive.modules.notification

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.delamibrands.theexecutive.api.ApiResponse
import com.delamibrands.theexecutive.api.AppRepository
import com.delamibrands.theexecutive.api.interfaces.ApiCallback
import com.delamibrands.theexecutive.base.BaseViewModel
import com.delamibrands.theexecutive.modules.notification.dataclasses.NotificationChangeStatusRequest
import com.delamibrands.theexecutive.modules.notification.dataclasses.NotificationListResponse

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 17-May-2018
 */
class NotificationViewModel(application: Application): BaseViewModel(application) {

    var notificationListResponse: MutableLiveData<ApiResponse<List<NotificationListResponse>>>? = MutableLiveData()
    var changeNotificationStatusResponse: MutableLiveData<ApiResponse<Boolean>>? = MutableLiveData()

    fun getNotificationList(){
        val apiResponse = ApiResponse<List<NotificationListResponse>>()
        AppRepository.getNotificationList(object : ApiCallback<List<NotificationListResponse>> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                notificationListResponse?.value = apiResponse

            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                notificationListResponse?.value = apiResponse
            }

            override fun onSuccess(t: List<NotificationListResponse>?) {
                apiResponse.apiResponse = t
                notificationListResponse?.value = apiResponse
            }
        })
    }

    fun changeNotificationStatus(request: NotificationChangeStatusRequest){
        val apiResponse = ApiResponse<Boolean>()
        AppRepository.changeNotificationStatus(request, object : ApiCallback<Boolean> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                changeNotificationStatusResponse?.value = apiResponse

            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                changeNotificationStatusResponse?.value = apiResponse
            }

            override fun onSuccess(t: Boolean?) {
                apiResponse.apiResponse = t
                changeNotificationStatusResponse?.value = apiResponse
            }
        })
    }


}
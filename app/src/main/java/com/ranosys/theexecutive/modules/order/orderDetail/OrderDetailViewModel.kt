package com.ranosys.theexecutive.modules.order.orderDetail

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel

/**
 * @Class An data class for Order Detail view model
 * @author Ranosys Technologies
 * @Date 21-May-2018
 */

class OrderDetailViewModel(application: Application) : BaseViewModel(application) {

    var orderDetailResponse = MutableLiveData<ApiResponse<OrderDetailResponse>>()
    var orderDetailObservable: ObservableField<OrderDetailResponse>? = ObservableField()


    fun getOrderList(orderId: String) {
        val apiResponse = ApiResponse<OrderDetailResponse>()

        AppRepository.getOrdersDetail(orderId = orderId, callBack = object : ApiCallback<OrderDetailResponse> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                orderDetailResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                orderDetailResponse.value = apiResponse
            }

            override fun onSuccess(t: OrderDetailResponse?) {
                apiResponse.apiResponse = t
                orderDetailResponse.value = apiResponse
            }
        })

    }

}
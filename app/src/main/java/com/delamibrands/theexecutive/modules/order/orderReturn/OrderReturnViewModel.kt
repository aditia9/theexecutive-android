package com.delamibrands.theexecutive.modules.order.orderReturn

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.delamibrands.theexecutive.api.ApiResponse
import com.delamibrands.theexecutive.api.AppRepository
import com.delamibrands.theexecutive.api.interfaces.ApiCallback
import com.delamibrands.theexecutive.base.BaseViewModel
import com.delamibrands.theexecutive.modules.order.orderDetail.OrderDetailResponse


/**
 * @Class An data class for Order return view model
 * @author Ranosys Technologies
 * @Date 24-May-2018
 */
class OrderReturnViewModel(application: Application) : BaseViewModel(application) {

    var orderReturnResponse = MutableLiveData<ApiResponse<OrderDetailResponse>>()
    var orderReturnResponseStatus = MutableLiveData<ApiResponse<String>>()
    var orderDetailObservable: ObservableField<OrderDetailResponse>? = ObservableField()

    fun getOrderList(orderId: String) {
        val apiResponse = ApiResponse<OrderDetailResponse>()

        AppRepository.getOrdersDetail(orderId = orderId, callBack = object : ApiCallback<OrderDetailResponse> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                orderReturnResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                orderReturnResponse.value = apiResponse
            }

            override fun onSuccess(t: OrderDetailResponse?) {
                apiResponse.apiResponse = t
                orderReturnResponse.value = apiResponse
            }
        })

    }


    fun returnProduct(orderReturnRequest: OrderReturnRequest) {
        val apiResponse = ApiResponse<String>()

        AppRepository.returnProduct(orderReturnRequest, callBack = object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                orderReturnResponseStatus.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                orderReturnResponseStatus.value = apiResponse
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                orderReturnResponseStatus.value = apiResponse

            }
        })

    }
}
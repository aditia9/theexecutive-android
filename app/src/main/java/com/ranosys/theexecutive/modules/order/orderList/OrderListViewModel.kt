package com.ranosys.theexecutive.modules.order.orderList

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel


class OrderListViewModel(application: Application) : BaseViewModel(application) {

    var mutualOrderListResponse = MutableLiveData<ApiResponse<List<OrderListResponse>>>()
    var orderListResponse: ObservableField<MutableList<OrderListResponse>>? = ObservableField()


    fun getOrderList() {
        val apiResponse = ApiResponse<List<OrderListResponse>>()

        AppRepository.getOrdersList(callBack = object : ApiCallback<List<OrderListResponse>> {
            override fun onException(error: Throwable) {
            apiResponse.error = error.message
                mutualOrderListResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualOrderListResponse.value = apiResponse
            }

            override fun onSuccess(t: List<OrderListResponse>?) {
               apiResponse.apiResponse = t
                mutualOrderListResponse.value = apiResponse
            }
        })

    }

}
package com.delamibrands.theexecutive.modules.order.orderList

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.delamibrands.theexecutive.api.ApiResponse
import com.delamibrands.theexecutive.api.AppRepository
import com.delamibrands.theexecutive.api.interfaces.ApiCallback
import com.delamibrands.theexecutive.base.BaseViewModel

/**
 * @Class An data class for Order List view model
 * @author Ranosys Technologies
 * @Date 21-May-2018
 */

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
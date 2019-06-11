package com.delamibrands.theexecutive.modules.checkout

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.api.AppRepository
import com.delamibrands.theexecutive.api.interfaces.ApiCallback
import com.delamibrands.theexecutive.base.BaseViewModel
import com.delamibrands.theexecutive.utils.Constants

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 31-May-2018
 */
class OrderResultViewModel(application: Application) : BaseViewModel(application) {



    val apiError: MutableLiveData<String> = MutableLiveData()
    val orderStatus: MutableLiveData<CheckoutDataClass.OrderStatusResponse> = MutableLiveData()
    var status : String = ""
    var statusImg : ObservableField<String> = ObservableField("")
    var orderId : ObservableField<String> = ObservableField("")
    var incrementalOrderId : ObservableField<String> = ObservableField("")
    var virtualAccountNumber : ObservableField<String> = ObservableField("")
    var statusMsg : ObservableField<String> = ObservableField("")
    var infoMsg : ObservableField<String> = ObservableField("")
    var btnAction : ObservableField<String> = ObservableField("")

    fun getOrderDetails() {

        //API call to get order status
        AppRepository.getOrderStatus(orderId.get(), object : ApiCallback<CheckoutDataClass.OrderStatusResponse> {
            override fun onException(error: Throwable) {
                apiError.value = error.message
            }

            override fun onError(errorMsg: String) {
                apiError.value = errorMsg
            }

            override fun onSuccess(t: CheckoutDataClass.OrderStatusResponse?) {
                orderStatus.value = t
                incrementalOrderId.set(t?.order_id)
                if(t?.virtual_account_number != null){
                    virtualAccountNumber.set(t.virtual_account_number.toString())
                }
            }

        })
    }
}
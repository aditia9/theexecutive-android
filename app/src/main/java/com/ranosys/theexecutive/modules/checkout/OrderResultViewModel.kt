package com.ranosys.theexecutive.modules.checkout

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.utils.Constants

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

        //API call to get order address
        AppRepository.getOrderStatus(orderId.get(), object : ApiCallback<CheckoutDataClass.OrderStatusResponse>{
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
                processOrderStatus()
            }

        })


    }

    private fun processOrderStatus() {
        var statusStr = ""
        var btnStr = ""
        var infoStr = ""

        if(orderStatus.value?.order_state == Constants.CANCEL_STATUS){
            status = Constants.CANCEL
        }

        when(status){
            Constants.SUCCESS -> {
                statusStr = getApplication<Application>().resources.getString(R.string.order_success_msg)
                btnStr = getApplication<Application>().resources.getString(R.string.order_success_btn_text)
                infoStr = getApplication<Application>().resources.getString(R.string.order_success_info)
            }
            Constants.CANCEL -> {
                statusStr = getApplication<Application>().resources.getString(R.string.order_cancel_msg)
                btnStr = getApplication<Application>().resources.getString(R.string.order_cancel_btn_text)
                infoStr = getApplication<Application>().resources.getString(R.string.order_cancel_info)
            }
            Constants.FAILURE -> {
                statusStr = getApplication<Application>().resources.getString(R.string.order_failure_msg)
                btnStr = getApplication<Application>().resources.getString(R.string.order_failure_btn_text)
                infoStr = getApplication<Application>().resources.getString(R.string.order_failure_info)
            }
        }

        statusMsg.set(statusStr)
        infoMsg.set(infoStr)
        btnAction.set(btnStr)
        statusImg.set(status)
    }

}
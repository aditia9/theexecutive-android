package com.ranosys.theexecutive.modules.checkout

import android.app.Application
import android.databinding.ObservableField
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.utils.Constants

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 31-May-2018
 */
class OrderResultViewModel(application: Application) : BaseViewModel(application) {



    var status : ObservableField<String> = ObservableField("")
    var orderId : ObservableField<String> = ObservableField("")
    var statusMsg : ObservableField<String> = ObservableField("")
    var infoMsg : ObservableField<String> = ObservableField("")
    var btnAction : ObservableField<String> = ObservableField("")

    fun getOrderDetails() {
        //API call to get order address

        var statusStr = ""
        var btnStr = ""
        var infoStr = ""

        when(status.get()){
            Constants.SUCCESS -> {
                statusStr = "order placed successfully"
                btnStr = "view order"
                infoStr = "Thank you for order. You will receive a confirmation email with detail of ypur order and link to track theYour oder has been placed successfully. Your oder has been placed successfully."
            }
            Constants.CANCEL -> {
                statusStr = "order cancled"
                btnStr = "continue shopping"
                infoStr = "Your order has been cancelled"
            }
            Constants.FAILURE -> {
                statusStr = "order failed"
                btnStr = "continue shopping"
                infoStr = "Your order has been failed"
            }
        }

        statusMsg.set(statusStr)
        infoMsg.set(infoStr)
        btnAction.set(btnStr)
    }

}
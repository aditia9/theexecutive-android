package com.ranosys.theexecutive.modules.checkout

import AppLog
import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.modules.myAccount.MyAccountDataClass
import com.ranosys.theexecutive.utils.GlobalSingelton
import com.ranosys.theexecutive.utils.Utils

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 10-May-2018
 */
class CheckoutViewModel(application: Application): BaseViewModel(application) {

    val CommanError: MutableLiveData<String> = MutableLiveData()
    val selectedAddress: MutableLiveData<MyAccountDataClass.Address> = MutableLiveData()
    val userInfo: MutableLiveData<ApiResponse<MyAccountDataClass.UserInfoResponse>> = MutableLiveData()

    fun getAddressApi() {
        AppRepository.getUserInfo(object: ApiCallback<MyAccountDataClass.UserInfoResponse> {
            override fun onException(error: Throwable) {
                AppLog.e("My Information API : ${error.message}")
                CommanError.value = error.message

            }

            override fun onError(errorMsg: String) {
                AppLog.e("My Information API : $errorMsg")
                CommanError.value = errorMsg
            }

            override fun onSuccess(t: MyAccountDataClass.UserInfoResponse?) {
                //update info saved at singleton
                GlobalSingelton.instance?.userInfo = t
                selectedAddress.value = Utils.getDefaultAddress()
            }
        })
    }


    fun getCartItemsApi() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
package com.ranosys.theexecutive.modules.checkout

import AppLog
import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.modules.myAccount.MyAccountDataClass
import com.ranosys.theexecutive.modules.shoppingBag.ShoppingBagResponse
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
    val shoppingBagItems: MutableLiveData<List<ShoppingBagResponse>> = MutableLiveData()
    val shippingMethodList: MutableLiveData<List<CheckoutDataClass.GetShippingMethodsResponse>> = MutableLiveData()
    var country: MutableLiveData<String> = MutableLiveData()

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
                country.value = selectedAddress.value?.country_id?.let { Utils.getCountryName(it) }
            }
        })
    }


    fun getCartItemsApi() {
        AppRepository.getCartOfUser(callBack = object : ApiCallback<List<ShoppingBagResponse>> {
            override fun onSuccess(t: List<ShoppingBagResponse>?) {
                shoppingBagItems.value = t
            }

            override fun onException(error: Throwable) {
                CommanError.value = error.message
            }

            override fun onError(errorMsg: String) {
                CommanError.value = errorMsg
            }
        })
    }

    fun getShippingMethodsApi(addressId: String){

        val request = CheckoutDataClass.GetShippingMethodsRequest(addressId)
        AppRepository.getShippingMethods(request, object : ApiCallback<List<CheckoutDataClass.GetShippingMethodsResponse>>{

            override fun onSuccess(t: List<CheckoutDataClass.GetShippingMethodsResponse>?) {
                shippingMethodList.value = t
            }

            override fun onException(error: Throwable) {
                CommanError.value = error.message
            }

            override fun onError(errorMsg: String) {
                CommanError.value = errorMsg
            }


        })
    }
}
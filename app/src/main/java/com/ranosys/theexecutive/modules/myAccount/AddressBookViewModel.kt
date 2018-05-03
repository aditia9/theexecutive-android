package com.ranosys.theexecutive.modules.myAccount

import AppLog
import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.utils.GlobalSingelton

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 01-May-2018
 */
class AddressBookViewModel(application: Application): BaseViewModel(application) {
    val addressList: MutableLiveData<ApiResponse<MutableList<MyAccountDataClass.Address>>> = MutableLiveData()
    var removeAddressApiResponse : MutableLiveData<ApiResponse<MyAccountDataClass.UserInfoResponse>> = MutableLiveData()

    fun getAddressList() {

        val apiResponse = ApiResponse<MutableList<MyAccountDataClass.Address>>()
        AppRepository.getUserInfo(object: ApiCallback<MyAccountDataClass.UserInfoResponse> {
            override fun onException(error: Throwable) {
                AppLog.e("My Information API : ${error.message}")
                apiResponse.error = error.message
                addressList.value = apiResponse

            }

            override fun onError(errorMsg: String) {
                AppLog.e("My Information API : $errorMsg")
                apiResponse.error = errorMsg
                addressList.value = apiResponse
            }

            override fun onSuccess(t: MyAccountDataClass.UserInfoResponse?) {
                //update info saved at singleton
                GlobalSingelton.instance?.userInfo = t
                val defaultAdd = t?.addresses?.single { it.id == t.default_shipping }
                apiResponse.apiResponse = t?.addresses?.toMutableList()
                addressList.value = apiResponse
            }
        })
    }

    fun removeAddress(address: MyAccountDataClass.Address?) {
        var addList = addressList.value?.apiResponse?.toMutableList()
        addList?.remove(address)

        var updatedInfo = GlobalSingelton.instance?.userInfo?.copy(addresses = addList)

        updatedInfo?.let {

            val request = MyAccountDataClass.UpdateInfoRequest(customer = updatedInfo)

            val apiResponse = ApiResponse<MyAccountDataClass.UserInfoResponse>()
            AppRepository.updateUserInfo(request, object: ApiCallback<MyAccountDataClass.UserInfoResponse>{
                override fun onException(error: Throwable) {
                    AppLog.e("Update Information API : ${error.message}")
                    apiResponse.error = error.message
                    removeAddressApiResponse.value = apiResponse
                }

                override fun onError(errorMsg: String) {
                    AppLog.e("Update Information API : $errorMsg")
                    apiResponse.error = errorMsg
                    removeAddressApiResponse.value = apiResponse
                }

                override fun onSuccess(t: MyAccountDataClass.UserInfoResponse?) {
                    //update info saved at singleton
                    GlobalSingelton.instance?.userInfo = t

                    apiResponse.apiResponse = t
                    removeAddressApiResponse.value = apiResponse
                }
            })
        }


    }
}
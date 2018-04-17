package com.ranosys.theexecutive.base

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences

/**
 * Created by Mohammad Sunny on 21/2/18.
 */
open class BaseViewModel(application : Application?) : AndroidViewModel(application){

    var userCartIdResponse: MutableLiveData<ApiResponse<String>>? = MutableLiveData()
    var guestCartIdResponse: MutableLiveData<ApiResponse<String>>? = MutableLiveData()
    var userCartCountResponse: MutableLiveData<ApiResponse<String>>? = MutableLiveData()
    var guestCartCountResponse: MutableLiveData<ApiResponse<String>>? = MutableLiveData()


    fun getCartIdForUser(userToken: String?){
        val apiResponse = ApiResponse<String>()
        AppRepository.createUserCart(object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                userCartIdResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                userCartIdResponse?.value?.error = errorMsg
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                SavedPreferences.getInstance()?.saveStringValue(t, Constants.USER_CART_ID_KEY)
                userCartIdResponse?.value = apiResponse
            }

        })

    }

    fun getUserCartCount() {
        val apiResponse = ApiResponse<String>()
        AppRepository.cartCountUser(object : ApiCallback<String>{
            override fun onException(error: Throwable) {
                userCartCountResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                userCartCountResponse?.value?.error = errorMsg
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                userCartCountResponse?.value = apiResponse
            }

        })

    }

    fun getGuestCartCount(cartId: String) {
        val apiResponse = ApiResponse<String>()
        AppRepository.cartCountGuest(cartId, object : ApiCallback<String>{
            override fun onException(error: Throwable) {
                guestCartCountResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                guestCartCountResponse?.value?.error = errorMsg
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                guestCartCountResponse?.value = apiResponse
            }
        })
    }

    fun getCartIdForGuest() {
        val apiResponse = ApiResponse<String>()
        AppRepository.createGuestCart(object : ApiCallback<String>{
            override fun onException(error: Throwable) {
                guestCartIdResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                guestCartIdResponse?.value?.error = errorMsg
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                guestCartIdResponse?.value = apiResponse

            }

        })
     //   SavedPreferences.getInstance()?.saveStringValue(cartId, Constants.GUEST_CART_ID_KEY)
    }
}
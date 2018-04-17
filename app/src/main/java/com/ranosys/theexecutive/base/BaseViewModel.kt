package com.ranosys.theexecutive.base

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils

/**
 * Created by Mohammad Sunny on 21/2/18.
 */
open class BaseViewModel(application : Application?) : AndroidViewModel(application){

    fun getCartIdForUser(userToken: String?): String {
        var cartId = ""
        AppRepository.createUserCart(object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                Utils.printLog("get user cart Api", "error")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("get user cart Api", "error")
            }

            override fun onSuccess(t: String?) {
                cartId = t ?: ""
            }

        })

        SavedPreferences.getInstance()?.saveStringValue(cartId, Constants.USER_CART_ID_KEY)
        return cartId
    }

    fun getUserCartCount(): String {
        var cartCount = "0"
        AppRepository.cartCountUser(object : ApiCallback<String>{
            override fun onException(error: Throwable) {
                Utils.printLog("cart count user Api", "error")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("cart count user Api", "error")
            }

            override fun onSuccess(count: String?) {
               cartCount = count ?: "0"
            }

        })

        return cartCount
    }

    fun getGuestCartCount(cartId: String): String {
        var cartCount = "0"
        AppRepository.cartCountGuest(cartId, object : ApiCallback<String>{
            override fun onException(error: Throwable) {
                Utils.printLog("cart count user Api", "error")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("cart count user Api", "error")
            }

            override fun onSuccess(count: String?) {
                cartCount = count ?: "0"
            }

        })

        return cartCount
    }

    fun getCartIdForGuest(): String {
        var cartId = ""
        AppRepository.createGuestCart(object : ApiCallback<String>{
            override fun onException(error: Throwable) {
                Utils.printLog("get guest cart Api", "error")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("get guest cart Api", "error")
            }

            override fun onSuccess(t: String?) {
                cartId = t?:""

            }

        })

        SavedPreferences.getInstance()?.saveStringValue(cartId, Constants.GUEST_CART_ID_KEY)
        return cartId
    }
}
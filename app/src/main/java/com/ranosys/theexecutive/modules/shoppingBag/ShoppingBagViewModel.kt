package com.ranosys.theexecutive.modules.shoppingBag

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel


class ShoppingBagViewModel(application: Application) : BaseViewModel(application) {

    var mutualShoppingBaglistResponse = MutableLiveData<ApiResponse<List<ShoppingBagResponse>>>()
    var mutualShoppingBagItemResponse = MutableLiveData<ApiResponse<ShoppingBagResponse>>()
    var mutualDeleteItemResponse = MutableLiveData<ApiResponse<String>>()
    var shoppingBagListResponse: ObservableField<MutableList<ShoppingBagResponse>>? = ObservableField()

    fun getShoppingBagForUser() {
        val apiResponse = ApiResponse<List<ShoppingBagResponse>>()
        AppRepository.getCartOfUser(callBack = object : ApiCallback<List<ShoppingBagResponse>> {
            override fun onSuccess(t: List<ShoppingBagResponse>?) {
                apiResponse.apiResponse = t
                mutualShoppingBaglistResponse.value = apiResponse
            }

            override fun onException(error: Throwable) {
                mutualShoppingBaglistResponse.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                mutualShoppingBaglistResponse.value?.error = errorMsg
            }
        })
    }


    fun getShoppingBagForGuestUser(cartId: String) {
        val apiResponse = ApiResponse<List<ShoppingBagResponse>>()
        AppRepository.getCartOfGuest(callBack = object : ApiCallback<List<ShoppingBagResponse>> {
            override fun onSuccess(t: List<ShoppingBagResponse>?) {
                apiResponse.apiResponse = t
                mutualShoppingBaglistResponse.value = apiResponse
            }

            override fun onException(error: Throwable) {
                mutualShoppingBaglistResponse.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                mutualShoppingBaglistResponse.value?.error = errorMsg
            }

        }, cartId = cartId)
    }


    fun deleteItemFromShoppingBagUser(itemId: Int?) {
        val apiResponse = ApiResponse<String>()
        AppRepository.deleteFromShoppingBagItemUser(itemId, callBack = object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualDeleteItemResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualDeleteItemResponse.value = apiResponse
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                mutualDeleteItemResponse.value = apiResponse
            }

        })
    }

    fun deleteItemFromShoppingBagGuest(itemId: Int?, cartId: String) {
        val apiResponse = ApiResponse<String>()
        AppRepository.deleteFromShoppingBagItemGuestUser(cartId = cartId, itemId = itemId, callBack = object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualDeleteItemResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualDeleteItemResponse.value = apiResponse
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                mutualDeleteItemResponse.value = apiResponse
            }

        })
    }


    fun updateItemFromShoppingBagUser(shoppingBagQtyUpdateRequest: ShoppingBagQtyUpdateRequest) {
        val apiResponse = ApiResponse<ShoppingBagResponse>()
        AppRepository.updateFromShoppingBagItemUser(shoppingBagQtyUpdateRequest, callBack = object : ApiCallback<ShoppingBagResponse> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualShoppingBagItemResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualShoppingBagItemResponse.value = apiResponse
            }

            override fun onSuccess(t: ShoppingBagResponse?) {
                apiResponse.apiResponse = t
                mutualShoppingBagItemResponse.value = apiResponse
            }

        })
    }

    fun updateItemFromShoppingBagGuest(shoppingBagQtyUpdateRequest: ShoppingBagQtyUpdateRequest) {
        val apiResponse = ApiResponse<ShoppingBagResponse>()
        AppRepository.updateFromShoppingBagItemGuestUser(shoppingBagQtyUpdateRequest, callBack = object : ApiCallback<ShoppingBagResponse> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualShoppingBagItemResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualShoppingBagItemResponse.value = apiResponse
            }

            override fun onSuccess(t: ShoppingBagResponse?) {
                apiResponse.apiResponse = t
                mutualShoppingBagItemResponse.value = apiResponse
            }

        })
    }


    fun moveItemFromCart(itemId: Int?) {
        val apiResponse = ApiResponse<String>()
        AppRepository.moveItemFromCart(itemId = itemId, callBack = object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualDeleteItemResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualDeleteItemResponse.value = apiResponse
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                mutualDeleteItemResponse.value = apiResponse
            }

        })
    }


    fun applyCouponCodeForUser(promoCode: String?) {
        val apiResponse = ApiResponse<String>()
        AppRepository.applyCouponCodeForUser(promoCode, callBack = object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualDeleteItemResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualDeleteItemResponse.value = apiResponse
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                mutualDeleteItemResponse.value = apiResponse
            }

        })
    }


    fun applyCouponCodeForGuestUser(promoCode: String?, cartId: String) {
        val apiResponse = ApiResponse<String>()
        AppRepository.applyCouponCodeForGuestUser(couponCode = promoCode, cartId = cartId, callBack = object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualDeleteItemResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualDeleteItemResponse.value = apiResponse
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                mutualDeleteItemResponse.value = apiResponse
            }

        })
    }
}
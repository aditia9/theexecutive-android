package com.ranosys.theexecutive.modules.shoppingBag

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
/**
 * @Class An data class for Shopping bag view model
 * @author Ranosys Technologies
 * @Date 15-May-2018
 */

class ShoppingBagViewModel(application: Application) : BaseViewModel(application) {

    var mutualShoppingBagListResponse = MutableLiveData<ApiResponse<ShoppingCartResponse>>()
    var mutualShoppingBagItemResponse = MutableLiveData<ApiResponse<ShoppingBagQtyUpdateRequest>>()
    var mutualTotalResponse = MutableLiveData<ApiResponse<TotalResponse>>()
    var mutualDeleteItemResponse = MutableLiveData<ApiResponse<String>>()
    var mutualPromoCodeResponse = MutableLiveData<ApiResponse<String>>()
    var mutualPromoCodeDeleteResponse = MutableLiveData<ApiResponse<String>>()
    var shoppingBagListResponse: ObservableField<ShoppingCartResponse>? = ObservableField()

    fun getShoppingBagForUser() {
        val apiResponse = ApiResponse<ShoppingCartResponse>()
        AppRepository.getCartOfUser(callBack = object : ApiCallback<ShoppingCartResponse> {
            override fun onSuccess(t: ShoppingCartResponse?) {
                apiResponse.apiResponse = t
                mutualShoppingBagListResponse.value = apiResponse
            }

            override fun onException(error: Throwable) {
                mutualShoppingBagListResponse.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                mutualShoppingBagListResponse.value?.error = errorMsg
            }
        })
    }


    fun getShoppingBagForGuestUser(cartId: String) {
        val apiResponse = ApiResponse<ShoppingCartResponse>()
        AppRepository.getCartOfGuest(callBack = object : ApiCallback<ShoppingCartResponse> {
            override fun onSuccess(t: ShoppingCartResponse?) {
                apiResponse.apiResponse = t
                mutualShoppingBagListResponse.value = apiResponse
            }

            override fun onException(error: Throwable) {
                mutualShoppingBagListResponse.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                mutualShoppingBagListResponse.value?.error = errorMsg
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
        val apiResponse = ApiResponse<ShoppingBagQtyUpdateRequest>()
        AppRepository.updateFromShoppingBagItemUser(shoppingBagQtyUpdateRequest, callBack = object : ApiCallback<ShoppingBagQtyUpdateRequest> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualShoppingBagItemResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualShoppingBagItemResponse.value = apiResponse
            }

            override fun onSuccess(t: ShoppingBagQtyUpdateRequest?) {
                apiResponse.apiResponse = t
                mutualShoppingBagItemResponse.value = apiResponse
            }

        })
    }

    fun updateItemFromShoppingBagGuest(shoppingBagQtyUpdateRequest: ShoppingBagQtyUpdateRequest) {
        val apiResponse = ApiResponse<ShoppingBagQtyUpdateRequest>()
        AppRepository.updateFromShoppingBagItemGuestUser(shoppingBagQtyUpdateRequest, callBack = object : ApiCallback<ShoppingBagQtyUpdateRequest> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualShoppingBagItemResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualShoppingBagItemResponse.value = apiResponse
            }

            override fun onSuccess(t: ShoppingBagQtyUpdateRequest?) {
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

    fun getCouponCodeForUser() {
        val apiResponse = ApiResponse<String>()
        AppRepository.getCouponCodeForUser(callBack = object : ApiCallback<Any> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualPromoCodeResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualPromoCodeResponse.value = apiResponse
            }

            override fun onSuccess(t: Any?) {
                if (t is String) {
                    apiResponse.apiResponse = "" + t.toString()
                } else {
                    apiResponse.apiResponse = ""
                }
                mutualPromoCodeResponse.value = apiResponse
            }

        })
    }


    fun getCouponCodeForGuestUser(cartId: String) {
        val apiResponse = ApiResponse<String>()
        AppRepository.getCouponCodeForGuestUser(cartId = cartId, callBack = object : ApiCallback<Any> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualPromoCodeResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualPromoCodeResponse.value = apiResponse
            }

            override fun onSuccess(t: Any?) {
                if (t is String) {
                    apiResponse.apiResponse = "" + t.toString()
                } else {
                    apiResponse.apiResponse = ""
                }
                mutualPromoCodeResponse.value = apiResponse
            }

        })
    }


    fun getTotalForUser() {
        val apiResponse = ApiResponse<TotalResponse>()
        AppRepository.getTotalForUser(callBack = object : ApiCallback<TotalResponse> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualTotalResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualTotalResponse.value = apiResponse
            }

            override fun onSuccess(t: TotalResponse?) {
                apiResponse.apiResponse = t
                mutualTotalResponse.value = apiResponse
            }

        })
    }


    fun getTotalForGuestUser(cartId: String) {
        val apiResponse = ApiResponse<TotalResponse>()
        AppRepository.getTotalForGuestUser(cartId = cartId, callBack = object : ApiCallback<TotalResponse> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualTotalResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualTotalResponse.value = apiResponse
            }

            override fun onSuccess(t: TotalResponse?) {
                apiResponse.apiResponse = t
                mutualTotalResponse.value = apiResponse
            }
        })
    }


    fun deleteCouponCodeForUser() {
        val apiResponse = ApiResponse<String>()
        AppRepository.deleteCouponCodeForUser(callBack = object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualPromoCodeDeleteResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualPromoCodeDeleteResponse.value = apiResponse
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                mutualPromoCodeDeleteResponse.value = apiResponse
            }

        })
    }

    fun deleteCouponCodeForGuestUser(cartId: String) {
        val apiResponse = ApiResponse<String>()
        AppRepository.deleteCouponCodeForGuestUser(cartId = cartId, callBack = object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualPromoCodeDeleteResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualPromoCodeDeleteResponse.value = apiResponse
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                mutualPromoCodeDeleteResponse.value = apiResponse
            }

        })
    }
}
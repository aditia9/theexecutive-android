package com.delamibrands.theexecutive.modules.shoppingBag

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.delamibrands.theexecutive.api.ApiResponse
import com.delamibrands.theexecutive.api.AppRepository
import com.delamibrands.theexecutive.api.interfaces.ApiCallback
import com.delamibrands.theexecutive.base.BaseViewModel
import com.delamibrands.theexecutive.utils.Constants
import com.delamibrands.theexecutive.utils.SavedPreferences

/**
 * @Class An data class for Shopping bag view model
 * @author Ranosys Technologies
 * @Date 15-May-2018
 */

class ShoppingBagViewModel(application: Application) : BaseViewModel(application) {

    var mutualShoppingBagListResponse = MutableLiveData<ApiResponse<List<ShoppingBagResponse>>>()
    var mutualShoppingBagItemResponse = MutableLiveData<ApiResponse<ShoppingBagQtyUpdateRequest>>()
    var mutualTotalResponse = MutableLiveData<ApiResponse<TotalResponse>>()
    var mutualDeleteItemResponse = MutableLiveData<ApiResponse<String>>()
    var mutualMoveToWishlistResponse = MutableLiveData<ApiResponse<String>>()
    var mutualPromoCodeResponse = MutableLiveData<ApiResponse<String>>()
    var mutualApplyPromoCodeResponse = MutableLiveData<ApiResponse<String>>()
    var mutualPromoCodeDeleteResponse = MutableLiveData<ApiResponse<String>>()
    var shoppingBagListResponse: ObservableField<MutableList<ShoppingBagResponse>>? = ObservableField()
    var guestCartIdResponse: MutableLiveData<ApiResponse<String>>? = MutableLiveData()
    var userCartIdResponse: MutableLiveData<ApiResponse<String>>? = MutableLiveData()

    fun getShoppingBagForUser() {
        val apiResponse = ApiResponse<List<ShoppingBagResponse>>()
        AppRepository.getCartOfUser(callBack = object : ApiCallback<List<ShoppingBagResponse>> {
            override fun onSuccess(t: List<ShoppingBagResponse>?) {
                apiResponse.apiResponse = t
                mutualShoppingBagListResponse.value = apiResponse
            }

            override fun onException(error: Throwable) {
                apiResponse.throwable = error
                mutualShoppingBagListResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualShoppingBagListResponse.value = apiResponse
            }
        })
    }


    fun getShoppingBagForGuestUser(cartId: String) {
        val apiResponse = ApiResponse<List<ShoppingBagResponse>>()
        AppRepository.getCartOfGuest(callBack = object : ApiCallback<List<ShoppingBagResponse>> {
            override fun onSuccess(t: List<ShoppingBagResponse>?) {
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
                mutualMoveToWishlistResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualMoveToWishlistResponse.value = apiResponse
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                mutualMoveToWishlistResponse.value = apiResponse
            }

        })
    }


    fun applyCouponCodeForUser(promoCode: String?) {
        val apiResponse = ApiResponse<String>()
        AppRepository.applyCouponCodeForUser(promoCode, callBack = object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualApplyPromoCodeResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualApplyPromoCodeResponse.value = apiResponse
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                mutualApplyPromoCodeResponse.value = apiResponse
            }

        })
    }


    fun applyCouponCodeForGuestUser(promoCode: String?, cartId: String) {
        val apiResponse = ApiResponse<String>()
        AppRepository.applyCouponCodeForGuestUser(couponCode = promoCode, cartId = cartId, callBack = object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualApplyPromoCodeResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualApplyPromoCodeResponse.value = apiResponse
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                mutualApplyPromoCodeResponse.value = apiResponse
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

    fun getCartIdForGuest() {
        val apiResponse = ApiResponse<String>()
        AppRepository.createGuestCart(object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                guestCartIdResponse?.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                guestCartIdResponse?.value = apiResponse
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                SavedPreferences.getInstance()?.saveStringValue(t, Constants.GUEST_CART_ID_KEY)
                guestCartIdResponse?.value = apiResponse
            }

        })
    }


    fun getCartIdForUser(){
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
}
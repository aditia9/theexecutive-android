package com.ranosys.theexecutive.modules.wishlist

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel

/**
 * @Details View model for wishlist screen
 * @Author Ranosys Technologies
 * @Date 02,May,2018
 */
class WishlistViewModel(application: Application) : BaseViewModel(application) {

    var mutualWishlistResponse = MutableLiveData<ApiResponse<WishlistResponse>>()
    var mutualDeleteItemResponse = MutableLiveData<ApiResponse<String>>()
    var mutualAddToBagItemResponse = MutableLiveData<ApiResponse<String>>()
    var wishlistResponse: ObservableField<WishlistResponse>? = ObservableField()

    fun getWishlist(){
        val apiResponse = ApiResponse<WishlistResponse>()
        AppRepository.getWishlist(callBack = object : ApiCallback<WishlistResponse> {
            override fun onException(error: Throwable) {
                mutualWishlistResponse.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                mutualWishlistResponse.value?.error = errorMsg
            }

            override fun onSuccess(t: WishlistResponse?) {
                apiResponse.apiResponse = t
                mutualWishlistResponse.value = apiResponse
            }

        })
    }

    fun deleteItemFromWishlist(itemId : Int?){
        val apiResponse = ApiResponse<String>()
        AppRepository.deleteWishlistItem(itemId, callBack = object : ApiCallback<String> {
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

    fun addToBagWishlistItem(itemId : Int?){
        val apiResponse = ApiResponse<String>()
        val request = MoveToBagRequest(id = itemId.toString(), qty = "1")
        AppRepository.addToBagWishlistItem(request, callBack = object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                mutualAddToBagItemResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                mutualAddToBagItemResponse.value = apiResponse
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                mutualAddToBagItemResponse.value = apiResponse
            }

        })
    }


}
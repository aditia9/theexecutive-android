package com.ranosys.theexecutive.modules.productDetail

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.view.View
import com.google.gson.JsonObject
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.modules.productDetail.dataClassess.AddToCartRequest
import com.ranosys.theexecutive.modules.productDetail.dataClassess.AddToCartResponse
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ChildProductsResponse
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ProductOptionsResponse
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences

/**
 * @Class ViewModel for product item.
 * @author Ranosys Technologies
 * @Date 06-Apr-2018
 */
class ProductItemViewModel(application: Application) : BaseViewModel(application){

    var productItem :  ProductListingDataClass.Item? = null
    var productChildrenResponse: MutableLiveData<ApiResponse<List<ChildProductsResponse>>>? = MutableLiveData()
    var productOptionResponse: MutableLiveData<ApiResponse<List<ProductOptionsResponse>>>? = MutableLiveData()
    var addToWIshListResponse: MutableLiveData<ApiResponse<String>>? = MutableLiveData()
    var addToCartResponse: MutableLiveData<ApiResponse<AddToCartResponse>>? = MutableLiveData()
    var urlOne : ObservableField<String> = ObservableField()
    var urlTwo : ObservableField<String> = ObservableField()
    var userCartIdResponse: MutableLiveData<ApiResponse<String>>? = MutableLiveData()
    var guestCartIdResponse: MutableLiveData<ApiResponse<String>>? = MutableLiveData()
    var userCartCountResponse: MutableLiveData<ApiResponse<String>>? = MutableLiveData()
    var guestCartCountResponse: MutableLiveData<ApiResponse<String>>? = MutableLiveData()
    var productDetailResponse: MutableLiveData<ApiResponse<ProductListingDataClass.Item>>? = MutableLiveData()

    var clickedAddBtnId: MutableLiveData<Int>? = null
        get() {
            field =  field ?: MutableLiveData()
            return field
        }

    fun btnClicked(view: View) {
        when (view.id) {
            R.id.btn_add_to_bag -> {
                clickedAddBtnId?.value = R.id.btn_add_to_bag
            }
            R.id.tv_composition_and_care -> {
                clickedAddBtnId?.value = R.id.tv_composition_and_care
            }
            R.id.tv_size_guideline -> {
                clickedAddBtnId?.value = R.id.tv_size_guideline
            }
            R.id.tv_shipping -> {
                clickedAddBtnId?.value = R.id.tv_shipping
            }
            R.id.tv_return -> {
                clickedAddBtnId?.value = R.id.tv_return
            }
            R.id.tv_share -> {
                clickedAddBtnId?.value = R.id.tv_share
            }
            R.id.tv_buying_guidelinie -> {
                clickedAddBtnId?.value = R.id.tv_buying_guidelinie
            }
            R.id.tv_chat -> {
                clickedAddBtnId?.value = R.id.tv_chat
            }
            R.id.tv_wishlist -> {
                clickedAddBtnId?.value = R.id.tv_wishlist
            }
        }
    }

    fun getProductChildren(productSku : String?){
        val apiResponse = ApiResponse<List<ChildProductsResponse>>()
        AppRepository.getProductChildren(productSku, object : ApiCallback<List<ChildProductsResponse>> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                productChildrenResponse?.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                productChildrenResponse?.value = apiResponse
            }

            override fun onSuccess(t: List<ChildProductsResponse>?) {
                apiResponse.apiResponse = t
                productChildrenResponse?.value = apiResponse
            }
        })
    }

    fun getProductOptions(attributeId : String?, label : String?){
        val apiResponse = ApiResponse<List<ProductOptionsResponse>>()
        AppRepository.getProductOptions(attributeId, object : ApiCallback<List<ProductOptionsResponse>> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                productOptionResponse?.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                productOptionResponse?.value = apiResponse
            }

            override fun onSuccess(t: List<ProductOptionsResponse>?) {
                t?.get(0)?.label = label
                apiResponse.apiResponse = t
                productOptionResponse?.value = apiResponse
            }
        })
    }

    fun callAddToWishListApi(colorAttr : String?, colorValue : String?, sizeAttr : String?, sizeValue : String?){
        val apiResponse = ApiResponse<String>()

        val jsonObject = JsonObject()
        val jsonOptionObject = JsonObject().apply {
            addProperty(colorAttr, colorValue)
            addProperty(sizeAttr, sizeValue)
        }
        jsonObject.run {
            addProperty(Constants.PRODUCT_SKU, productItem?.sku)
            add(Constants.OPTIONS ,jsonOptionObject)
        }

        AppRepository.addToWishList(jsonObject, object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                addToWIshListResponse?.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                addToWIshListResponse?.value = apiResponse
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                addToWIshListResponse?.value = apiResponse
            }
        })

    }

    fun addToUserCart(addToCartRequest: AddToCartRequest) {
        val apiResponse = ApiResponse<AddToCartResponse>()
        AppRepository.addToCartUser(addToCartRequest, object: ApiCallback<AddToCartResponse>{
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                addToCartResponse?.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                addToCartResponse?.value = apiResponse
            }

            override fun onSuccess(t: AddToCartResponse?) {
                apiResponse.apiResponse = t
                addToCartResponse?.value = apiResponse
            }

        })
    }

    fun addToGuestCart(addToCartRequest: AddToCartRequest) {
        val apiResponse = ApiResponse<AddToCartResponse>()
        AppRepository.addToCartGuest(addToCartRequest.cartItem?.quote_id!!, addToCartRequest, object: ApiCallback<AddToCartResponse>{
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                addToCartResponse?.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                addToCartResponse?.value = apiResponse
            }

            override fun onSuccess(t: AddToCartResponse?) {
                apiResponse.apiResponse = t
                addToCartResponse?.value = apiResponse
            }

        })
    }

    fun getCartIdForUser() {
        val apiResponse = ApiResponse<String>()
        AppRepository.createUserCart(object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                userCartIdResponse?.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                userCartIdResponse?.value = apiResponse
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
                apiResponse.error = error.message
                userCartCountResponse?.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                userCartCountResponse?.value = apiResponse
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
                apiResponse.error = error.message
                guestCartCountResponse?.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                guestCartCountResponse?.value = apiResponse
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

    fun getProductDetail(productSku : String?){
        val apiResponse = ApiResponse<ProductListingDataClass.Item>()
        AppRepository.getProductDetail(productSku, object : ApiCallback<ProductListingDataClass.Item> {
            override fun onException(error: Throwable) {
                productDetailResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                productDetailResponse?.value?.error = errorMsg
            }

            override fun onSuccess(t: ProductListingDataClass.Item?) {
                apiResponse.apiResponse = t
                productDetailResponse?.value = apiResponse
            }
        })
    }
}
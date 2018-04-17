package com.ranosys.theexecutive.modules.productDetail

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.view.View
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.modules.productDetail.dataClassess.*
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass
import com.ranosys.theexecutive.utils.Utils

/**
 * @Class ViewModel for product item.
 * @author Ranosys Technologies
 * @Date 06-Apr-2018
 */
class ProductItemViewModel(application: Application) : BaseViewModel(application){

    var productItem :  ProductListingDataClass.Item? = null
    var productChildrenResponse: MutableLiveData<ApiResponse<List<ChildProductsResponse>>>? = MutableLiveData()
    var productOptionResponse: MutableLiveData<ApiResponse<List<ProductOptionsResponse>>>? = MutableLiveData()
    var staticPagesUrlResponse: MutableLiveData<ApiResponse<StaticPagesUrlResponse>>? = MutableLiveData()
    var addToWIshListResponse: MutableLiveData<ApiResponse<String>>? = MutableLiveData()
    var addToCartSuccess: MutableLiveData<String>? = MutableLiveData()
    var addToCartFailure: MutableLiveData<String>? = MutableLiveData()
    var staticPages : StaticPagesUrlResponse? = null
    var url_one : ObservableField<String> = ObservableField()
    var url_two : ObservableField<String> = ObservableField()

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
        AppRepository.getProductChildern(productSku, object : ApiCallback<List<ChildProductsResponse>> {
            override fun onException(error: Throwable) {
                productChildrenResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                productChildrenResponse?.value?.error = errorMsg
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
                productOptionResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                productOptionResponse?.value?.error = errorMsg
            }

            override fun onSuccess(t: List<ProductOptionsResponse>?) {
                t?.get(0)?.label = label
                apiResponse.apiResponse = t
                productOptionResponse?.value = apiResponse
            }
        })
    }

    fun getStaticPagesUrl(){
        val apiResponse = ApiResponse<StaticPagesUrlResponse>()
        AppRepository.getStaticPagesUrl(object : ApiCallback<StaticPagesUrlResponse> {
            override fun onException(error: Throwable) {
                staticPagesUrlResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                staticPagesUrlResponse?.value?.error = errorMsg
            }

            override fun onSuccess(t: StaticPagesUrlResponse?) {
                apiResponse.apiResponse = t
                staticPagesUrlResponse?.value = apiResponse
            }
        })
    }

    fun callAddToWishListApi(){
        val apiResponse = ApiResponse<String>()

        //prepare request
        val requestMap: MutableMap<String, Int> = mutableMapOf()

        requestMap["productId"] = productItem?.id!!
        //further add options

        AppRepository.addToWishList(requestMap, object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                addToWIshListResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                addToWIshListResponse?.value?.error = errorMsg
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                addToWIshListResponse?.value = apiResponse
            }
        })

    }

    fun addToUserCart(addTOCartRequest: AddTOCartRequest) {
        AppRepository.addToCartUser(addTOCartRequest, object: ApiCallback<AddTOCartResponse>{
            override fun onException(error: Throwable) {
                Utils.printLog("add to cart Api", "error")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("add to cart Api", "error")
            }

            override fun onSuccess(t: AddTOCartResponse?) {
                //TODO - get and update cart count
            }

        })
    }

    fun addToGuestCart(guestCartId: String, addTOCartRequest: AddTOCartRequest) {

        AppRepository.addToCartGuest(guestCartId, addTOCartRequest, object: ApiCallback<AddTOCartResponse>{
            override fun onException(error: Throwable) {
                Utils.printLog("add to cart Api", "error")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("add to cart Api", "error")
            }

            override fun onSuccess(t: AddTOCartResponse?) {
                //TODO - get and update cart count
            }

        })
    }
}
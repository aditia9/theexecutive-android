package com.ranosys.theexecutive.modules.productDetail

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ChildProductsResponse
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ProductDetailResponse
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ProductOptionsResponse

/**
 * Created by Mohammad Sunny on 5/4/18.
 */
class ProductDetailViewModel(application: Application): BaseViewModel(application) {

    var productDetailResponse: MutableLiveData<ApiResponse<ProductDetailResponse>>? = MutableLiveData()
    var productChildrenResponse: MutableLiveData<ApiResponse<ChildProductsResponse>>? = MutableLiveData()
    var productOptionResponse: MutableLiveData<ApiResponse<List<ProductOptionsResponse>>>? = MutableLiveData()


    fun getProductDetail(productSku : String?){
        val apiResponse = ApiResponse<ProductDetailResponse>()
        AppRepository.getProductDetail(productSku, object : ApiCallback<ProductDetailResponse>{
            override fun onException(error: Throwable) {
                productDetailResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                productDetailResponse?.value?.error = errorMsg
            }

            override fun onSuccess(t: ProductDetailResponse?) {
                apiResponse.apiResponse = t
                productDetailResponse?.value = apiResponse
            }
        })
    }

    fun getProductChildren(productSku : String?){
        val apiResponse = ApiResponse<ChildProductsResponse>()
        AppRepository.getProductChildern(productSku, object : ApiCallback<ChildProductsResponse>{
            override fun onException(error: Throwable) {
                productChildrenResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                productChildrenResponse?.value?.error = errorMsg
            }

            override fun onSuccess(t: ChildProductsResponse?) {
                apiResponse.apiResponse = t
                productChildrenResponse?.value = apiResponse
            }
        })
    }

    fun getProductOptions(attributeId : String?){
        val apiResponse = ApiResponse<List<ProductOptionsResponse>>()
        AppRepository.getProductOptions(attributeId, object : ApiCallback<List<ProductOptionsResponse>>{
            override fun onException(error: Throwable) {
                productOptionResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                productOptionResponse?.value?.error = errorMsg
            }

            override fun onSuccess(t: List<ProductOptionsResponse>?) {
                apiResponse.apiResponse = t
                productOptionResponse?.value = apiResponse
            }
        })
    }

}
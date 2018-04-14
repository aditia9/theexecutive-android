package com.ranosys.theexecutive.modules.productDetail

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ProductDetailResponse
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass

/**
 * Created by Mohammad Sunny on 5/4/18.
 */
class ProductDetailViewModel(application: Application): BaseViewModel(application) {

    var productList :  ObservableField<List<ProductListingDataClass.Item>>? = ObservableField()
    var productDetailResponse: MutableLiveData<ApiResponse<ProductDetailResponse>>? = MutableLiveData()

    fun getProductDetail(productSku : String?){
        val apiResponse = ApiResponse<ProductDetailResponse>()
        AppRepository.getProductDetail(productSku, object : ApiCallback<ProductDetailResponse> {
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

}
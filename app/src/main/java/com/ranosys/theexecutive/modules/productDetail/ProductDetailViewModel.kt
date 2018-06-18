package com.ranosys.theexecutive.modules.productDetail

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.modules.productDetail.dataClassess.StaticPagesUrlResponse
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass

/**
 * Created by Mohammad Sunny on 5/4/18.
 */
class ProductDetailViewModel(application: Application): BaseViewModel(application) {

    var productList :  ObservableField<List<ProductListingDataClass.Item>>? = ObservableField()
    var productDetailResponse: MutableLiveData<ApiResponse<ProductListingDataClass.Item>>? = MutableLiveData()
    var staticPagesUrlResponse: MutableLiveData<ApiResponse<StaticPagesUrlResponse>>? = MutableLiveData()
    var staticPages : StaticPagesUrlResponse? = null
    var productName: String? = ""

    fun getProductDetail(productSku : String?){
        val apiResponse = ApiResponse<ProductListingDataClass.Item>()
        AppRepository.getProductDetail(productSku, object : ApiCallback<ProductListingDataClass.Item> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                productDetailResponse?.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                productDetailResponse?.value = apiResponse
            }

            override fun onSuccess(t: ProductListingDataClass.Item?) {
                apiResponse.apiResponse = t
                productDetailResponse?.value = apiResponse
            }
        })
    }

    fun getStaticPagesUrl(){
        val apiResponse = ApiResponse<StaticPagesUrlResponse>()
        AppRepository.getStaticPagesUrl(object : ApiCallback<StaticPagesUrlResponse> {
            override fun onException(error: Throwable) {
                apiResponse.error = error.message
                staticPagesUrlResponse?.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                apiResponse.error = errorMsg
                staticPagesUrlResponse?.value = apiResponse
            }

            override fun onSuccess(t: StaticPagesUrlResponse?) {
                apiResponse.apiResponse = t
                staticPagesUrlResponse?.value = apiResponse
            }
        })
    }

}
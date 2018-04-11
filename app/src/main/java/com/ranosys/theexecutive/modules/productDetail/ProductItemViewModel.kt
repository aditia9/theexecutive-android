package com.ranosys.theexecutive.modules.productDetail

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.view.View
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ChildProductsResponse
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ProductOptionsResponse

/**
 * @Class ViewModel for product item.
 * @author Ranosys Technologies
 * @Date 06-Apr-2018
 */
class ProductItemViewModel(application: Application) : BaseViewModel(application){

    var productChildrenResponse: MutableLiveData<ApiResponse<ChildProductsResponse>>? = MutableLiveData()
    var productOptionResponse: MutableLiveData<ApiResponse<List<ProductOptionsResponse>>>? = MutableLiveData()

    var clickedAddBtnId: MutableLiveData<ViewClass>? = null
        get() {
            field =  field ?: MutableLiveData()
            return field
        }

    fun btnClicked(view: View) {
        when (view.id) {
            R.id.btn_add_to_bag -> {
                clickedAddBtnId?.value = ViewClass(R.id.btn_add_to_bag, view.tag as Int)
            }
        }
    }

   data class ViewClass( var id : Int,var tag : Int)





    fun getProductChildren(productSku : String?){
        val apiResponse = ApiResponse<ChildProductsResponse>()
        AppRepository.getProductChildern(productSku, object : ApiCallback<ChildProductsResponse> {
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
        AppRepository.getProductOptions(attributeId, object : ApiCallback<List<ProductOptionsResponse>> {
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
package com.ranosys.theexecutive.modules.productListing

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.modules.login.LoginDataClass
import com.ranosys.theexecutive.utils.Utils

/**
 * Created by nikhil on 20/3/18.
 */
class ProductListingViewModel(application: Application): BaseViewModel(application) {

    var partialProductList: MutableLiveData<ArrayList<ProductListingDataClass.DummyResponse>> = MutableLiveData()
    var isLoading: Boolean = false
    var totalProductCount: Int = 0


    fun getSortOptions() {}

    fun getFilterOptions() {}

    fun getProductListing(sku: String) {
        isLoading = true
        //dummy data
        var response: ArrayList<ProductListingDataClass.DummyResponse> = ArrayList()

        response.add(ProductListingDataClass.DummyResponse())
        response.add(ProductListingDataClass.DummyResponse())
        response.add(ProductListingDataClass.DummyResponse())
        response.add(ProductListingDataClass.DummyResponse())
        response.add(ProductListingDataClass.DummyResponse())
        response.add(ProductListingDataClass.DummyResponse())
        response.add(ProductListingDataClass.DummyResponse())
        response.add(ProductListingDataClass.DummyResponse())
        response.add(ProductListingDataClass.DummyResponse())
        response.add(ProductListingDataClass.DummyResponse())

        // when data recieved
        isLoading = false
    }


}
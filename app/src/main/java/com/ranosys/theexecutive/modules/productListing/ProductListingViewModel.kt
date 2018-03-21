package com.ranosys.theexecutive.modules.productListing

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.ranosys.theexecutive.base.BaseViewModel

/**
 * Created by nikhil on 20/3/18.
 */
class ProductListingViewModel(application: Application): BaseViewModel(application) {

    var productList: MutableLiveData<ArrayList<ProductListingDataClass.DummyResponse>> = MutableLiveData()


    fun getSortOptions() {}

    fun getFilterOptions() {}

    fun getProductListing(sku: String) {
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

        productList.value = response
    }


}
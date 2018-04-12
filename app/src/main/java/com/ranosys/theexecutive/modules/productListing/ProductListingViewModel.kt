package com.ranosys.theexecutive.modules.productListing

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.Utils

/**
 * Created by nikhil on 20/3/18.
 */
class ProductListingViewModel(application: Application): BaseViewModel(application) {

    var maskedProductList: MutableLiveData<ArrayList<ProductListingDataClass.ProductMaskedResponse>> = MutableLiveData()
    var isLoading: Boolean = false
    var totalProductCount: Int = 0

    var filterOptionList: MutableLiveData<MutableList<ProductListingDataClass.Filter>>? = MutableLiveData<MutableList<ProductListingDataClass.Filter>>()
    var priceFilter: MutableLiveData<ProductListingDataClass.Filter> = MutableLiveData()
    var selectedFilterMap = hashMapOf<String, String>()
    var selectedPriceRange = ProductListingDataClass.PriceRange()

    var productListResponse: ProductListingDataClass.ProductListingResponse? = null

    fun getSortOptions() {
        AppRepository.sortOptionApi(object : ApiCallback<ProductListingDataClass.SortOptionResponse>{
            override fun onException(error: Throwable) {
                Utils.printLog("Sort option api", error.message?: "exception")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("Sort option api", message = errorMsg)
            }

            override fun onSuccess(sortOptions: ProductListingDataClass.SortOptionResponse?) {
                //TODO - save sort options
            }

        })
    }

    fun getFilterOptions(categoryId: Int?) {
        AppRepository.filterOptionApi(categoryId!!, object : ApiCallback<ProductListingDataClass.FilterOptionsResponse>{
            override fun onException(error: Throwable) {
                Utils.printLog("Filter option api", error.message?: "exception")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("Filter option api", errorMsg)
            }

            override fun onSuccess(filterOptions: ProductListingDataClass.FilterOptionsResponse?) {

                if (filterOptions?.total_count!! > 0) {
                    filterOptions.run {
                        for (filter in filterOptions.filters) {
                            selectedFilterMap.put(filter.name, "")
                        }
                    }

                    priceFilter.value = filterOptions.filters.filter { option -> option.name == Constants.FILTER_PRICE_LABEL }?.get(0)
                }
                filterOptionList?.value = filterOptions.filters.toMutableList()

            }
        })
    }

    fun getProductListing(sku: String) {
        isLoading = true

        AppRepository.getProductList(prepareProductListingRequest(sku), object: ApiCallback<ProductListingDataClass.ProductListingResponse>{
            override fun onException(error: Throwable) {
                Utils.printLog("product listing", error.message?: "exception")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("product listing", errorMsg)            }

            override fun onSuccess(response: ProductListingDataClass.ProductListingResponse?) {

                isLoading = false

                if(null == productListResponse){
                    productListResponse = response

                }else{
                    productListResponse?.items?.addAll(response?.items as ArrayList)
                }
                totalProductCount = productListResponse?.total_count ?: 0

                val maskedResponse: ArrayList<ProductListingDataClass.ProductMaskedResponse> = ArrayList()
                productListResponse?.items.let {
                    for(product in productListResponse!!.items){
                        val sku = product.sku
                        val name = product.name
                        val productType = product.type_id
                        var price = 0.0
                        var specialPrice = 0.0
                        if(productType == Constants.FILTER_CONFIGURABLE_LABEL){
                            price = product.extension_attributes.regular_price
                            specialPrice = product.extension_attributes.final_price
                        }else{
                            price = product.price
                            val attributes = product.custom_attributes.filter { it.attribute_code == Constants.FILTER_SPECIAL_PRICE_LABEL }.toList()
                            if(attributes.isNotEmpty()){
                                specialPrice = attributes.get(0).value.toString().toDouble()
                            }
                        }

                        val type = ""
                        val discount = (((price - specialPrice).div(price)).times(100)).toInt()
                        var imgUrl = ""
                        if(product.media_gallery_entries.isNotEmpty())   imgUrl = product.media_gallery_entries[0].label.toString()

                        val product = ProductListingDataClass.ProductMaskedResponse(
                                sku = sku,
                                name = name,
                                normalPrice = price.toString(),
                                specialPrice = specialPrice.toString(),
                                type = type,
                                discountPer = discount,
                                imageUrl = imgUrl)

                        maskedResponse.add(product)

                    }
                }

                var list = maskedProductList.value
                if(null == list){
                    list = arrayListOf()
                }
                list.addAll(maskedResponse)

                maskedProductList.value = list
            }
        })
    }

    private fun prepareProductListingRequest(sku: String): Map<String, String> {
        val requestMap: MutableMap<String, String> = mutableMapOf()

        requestMap.put(Constants.REQUEST_ID_LABEL, "81")
        requestMap.put(Constants.REQUEST_PAGE_LIMIT_LABEL, Constants.LIST_PAGE_ITEM_COUNT.toString())
        val page = (maskedProductList.value?.size)?.div(10)?.plus(1) ?: 1
        requestMap.put(Constants.REQUEST_PAGE_LABEL, page.toString()) // pageCout
        //requestMap.put("product_list_order",) sort option
        //requestMap.put("product_list_dir","asc/desc) sort option

        if(selectedPriceRange.min.isNotBlank() && selectedPriceRange.max.isNotBlank()){
            selectedFilterMap.put(Constants.FILTER_PRICE_LABEL, selectedPriceRange.min + "-" + selectedPriceRange.max)
        }

        for((key, value) in selectedFilterMap){
            requestMap.put(key, value)
        }

        return requestMap
    }


}
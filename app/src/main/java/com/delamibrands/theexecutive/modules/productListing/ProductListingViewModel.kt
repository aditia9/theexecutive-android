package com.delamibrands.theexecutive.modules.productListing

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.delamibrands.theexecutive.api.AppRepository
import com.delamibrands.theexecutive.api.interfaces.ApiCallback
import com.delamibrands.theexecutive.base.BaseViewModel
import com.delamibrands.theexecutive.modules.productDetail.dataClassess.ProductOptionsResponse
import com.delamibrands.theexecutive.utils.Constants
import com.delamibrands.theexecutive.utils.GlobalSingelton
import com.delamibrands.theexecutive.utils.Utils

/**
 * Created by nikhil on 20/3/18.
 */
class ProductListingViewModel(application: Application): BaseViewModel(application) {

    var productList: MutableLiveData<MutableList<ProductListingDataClass.Item>> = MutableLiveData()
    var isLoading: Boolean = false
    var isFiltered: Boolean = false
    var isSorted: Boolean = false
    var lastSearchQuery: String = ""


    var sortOptionList: MutableLiveData<MutableList<ProductListingDataClass.SortOptionResponse>>? = MutableLiveData()
    var filterOptionList: MutableLiveData<MutableList<ProductListingDataClass.Filter>>? = MutableLiveData()
    var priceFilter: MutableLiveData<ProductListingDataClass.Filter> = MutableLiveData()
    var noProductAvailable: MutableLiveData<Int> = MutableLiveData()
    var selectedFilterMap = hashMapOf<String, String>()
    var selectedPriceRange = ProductListingDataClass.PriceRange()
    var selectedSortOption = ProductListingDataClass.SortOptionResponse("", "")
    var apiFailureResponse: MutableLiveData<String>? = MutableLiveData()
    var productListResponse: ProductListingDataClass.ProductListingResponse? = null

    fun getSortOptions(type: String) {
        AppRepository.sortOptionApi(type, object : ApiCallback<ArrayList<ProductListingDataClass.SortOptionResponse>> {
            override fun onSuccess(sortOptions: ArrayList<ProductListingDataClass.SortOptionResponse>?) {
                val tempOptions: ArrayList<ProductListingDataClass.SortOptionResponse> = ArrayList()
                sortOptions?.let {
                    for (option in it){
                        if (option.attribute_code == Constants.FILTER_PRICE_KEY){
                            tempOptions.add(ProductListingDataClass.SortOptionResponse(
                                    attribute_code = option.attribute_code,
                                    attribute_name = option.attribute_name + Constants.LOW_TO_HIGH))

                            tempOptions.add(ProductListingDataClass.SortOptionResponse(
                                    attribute_code = option.attribute_code,
                                    attribute_name = option.attribute_name + Constants.HIGH_TO_LOW))

                            continue
                        }

                        tempOptions.add(option)
                    }
                }

                sortOptionList?.value = tempOptions
            }

            override fun onException(error: Throwable) {
                Utils.printLog("Sort option api", error.message?: "exception")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("Sort option api", message = errorMsg)
            }


        })
    }

    fun getFilterOptions(categoryId: Int?) {
        AppRepository.filterOptionApi(categoryId ?: Constants.UNIVERSAL_CAT_ID, object : ApiCallback<ProductListingDataClass.FilterOptionsResponse> {
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
                            selectedFilterMap.put(filter.code, "")
                        }
                    }

                    priceFilter.value = filterOptions.filters.filter { option -> option.code == Constants.FILTER_PRICE_KEY }[0]
                }
                filterOptionList?.value = filterOptions.filters.toMutableList()
            }
        })
    }

    fun getSearchFilterOptions(query: String) {
        AppRepository.searchFilterOptionApi(query, object : ApiCallback<ProductListingDataClass.FilterOptionsResponse>{
            override fun onException(error: Throwable) {
                Utils.printLog("search Filter option api", error.message?: "exception")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("search Filter option api", errorMsg)
            }

            override fun onSuccess(filterOptions: ProductListingDataClass.FilterOptionsResponse?) {

                if (filterOptions?.filters?.isNotEmpty()!!) {
                    filterOptions.run {
                        for (filter in filterOptions.filters) {
                            selectedFilterMap.put(filter.code, "")
                        }
                    }

                   val localPriceFilter = filterOptions.filters.filter { option -> option.code == Constants.FILTER_PRICE_KEY }
                    if(localPriceFilter.isNotEmpty())
                    priceFilter.value = localPriceFilter[0]
                    else
                        priceFilter.value = null
                }
                filterOptionList?.value = filterOptions.filters.toMutableList()
            }
        })
    }

    fun getProductListing(catId: Int?, query: String = "", fromSearch:Boolean  = false, fromPagination: Boolean = false) {
        isLoading = true

        if(fromSearch && fromPagination.not()){
            clearExistingList()
        }

        AppRepository.getProductList(prepareProductListingRequest(catId ?: Constants.UNIVERSAL_CAT_ID, query,fromSearch), fromSearch,  object: ApiCallback<ProductListingDataClass.ProductListingResponse> {
            override fun onException(error: Throwable) {
                apiFailureResponse?.value = error.message
                Utils.printLog("product listing", error.message?: "exception")
            }

            override fun onError(errorMsg: String) {
                apiFailureResponse?.value = errorMsg
                Utils.printLog("product listing", errorMsg)
            }

            override fun onSuccess(response: ProductListingDataClass.ProductListingResponse?) {

                isLoading = false
                if(null == productListResponse){
                    productListResponse = response

                }else{
                    productListResponse?.items?.addAll(response?.items as ArrayList)
                    productListResponse?.total_count = response?.total_count ?: 0
                }

                productList.value = productListResponse?.items
                noProductAvailable.value = productListResponse?.items?.size
                saveOptionsResponse(productListResponse)
            }
        })
    }

    private fun saveOptionsResponse(response : ProductListingDataClass.ProductListingResponse?){
        response?.items?.forEach {
            if(it.type_id == Constants.CONFIGURABLE) {
                if(null == GlobalSingelton.instance?.colorList || null == GlobalSingelton.instance?.sizeList)
                    it.extension_attributes.configurable_product_options.forEach {
                        when(it.label){
                            Constants.COLOR_ -> {
                                getProductOptions(it.attribute_id, Constants.COLOR_)
                            }
                            Constants.SIZE_ -> {
                                getProductOptions(it.attribute_id, Constants.SIZE_)
                            }
                        }
                    }
                return
            }
        }
    }

    private fun getProductOptions(attributeId : String?, label : String?){
        AppRepository.getProductOptions(attributeId, object : ApiCallback<List<ProductOptionsResponse>> {
            override fun onException(error: Throwable) {
            }

            override fun onError(errorMsg: String) {
            }

            override fun onSuccess(t: List<ProductOptionsResponse>?) {
                when(label){
                    Constants.COLOR_ -> {
                        GlobalSingelton.instance?.colorList = t
                    }
                    Constants.SIZE_ -> {
                        GlobalSingelton.instance?.sizeList = t
                    }
                }
            }
        })
    }

    fun clearExistingList() {
        productListResponse?.total_count = 0
        productListResponse?.items?.clear()
        productList.value?.clear()
    }

    private fun prepareProductListingRequest(catId: Int, query: String, fromSearch: Boolean): Map<String, String> {
        val requestMap: MutableMap<String, String> = mutableMapOf()

        if(fromSearch){
            requestMap.put(Constants.REQUEST_SEARCH_LABEL, query)
        }else {
            requestMap.put(Constants.REQUEST_ID_LABEL, catId.toString())
        }

        if(selectedSortOption.attribute_code.isNotBlank()){
            requestMap.put(Constants.SORT_OPTION_LABEL, selectedSortOption.attribute_code)
            val dir = when{
                selectedSortOption.attribute_name.contains(Constants.HIGH_TO_LOW, true) -> Constants.DESC
                selectedSortOption.attribute_name.contains(Constants.LOW_TO_HIGH, true) -> Constants.ASC
                else -> ""
            }

            if(dir.isNotBlank()){
                requestMap.put(Constants.SORT_OPTION_DIR, dir)
            }
        }

        if(selectedPriceRange.min.isNotBlank() && selectedPriceRange.max.isNotBlank()){
            selectedFilterMap.put(Constants.FILTER_PRICE_KEY, selectedPriceRange.min.replace(".","") + "-" + selectedPriceRange.max.replace(".",""))
        }

        for((key, value) in selectedFilterMap){
            if(value.isNotBlank()){
                requestMap.put(key, value)
            }
        }


        requestMap.put(Constants.REQUEST_PAGE_LIMIT_LABEL, Constants.LIST_PAGE_ITEM_COUNT.toString())
        val page = (productList.value?.size)?.div(Constants.LIST_PAGE_ITEM_COUNT)?.plus(1) ?: 1
        requestMap.put(Constants.REQUEST_PAGE_LABEL, page.toString())

        return requestMap
    }


}
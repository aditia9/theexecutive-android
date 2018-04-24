package com.ranosys.theexecutive.modules.productListing

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.Utils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by nikhil on 20/3/18.
 */
class ProductListingViewModel(application: Application): BaseViewModel(application) {

    var maskedProductList: MutableLiveData<ArrayList<ProductListingDataClass.ProductMaskedResponse>> = MutableLiveData()
    var isLoading: Boolean = false
    var isFiltered: Boolean = false
    var isSorted: Boolean = false
    var totalProductCount: Int = 0
    var lastSearchQuery: String = ""


    var sortOptionList: MutableLiveData<MutableList<ProductListingDataClass.SortOptionResponse>>? = MutableLiveData()
    var filterOptionList: MutableLiveData<MutableList<ProductListingDataClass.Filter>>? = MutableLiveData()
    var priceFilter: MutableLiveData<ProductListingDataClass.Filter> = MutableLiveData()
    var noProductAvailable: MutableLiveData<Int> = MutableLiveData()
    var selectedFilterMap = hashMapOf<String, String>()
    var selectedPriceRange = ProductListingDataClass.PriceRange()
    var selectedSortOption = ProductListingDataClass.SortOptionResponse("", "")

    var productListResponse: ProductListingDataClass.ProductListingResponse? = null

    fun getSortOptions() {
        AppRepository.sortOptionApi(object : ApiCallback<ArrayList<ProductListingDataClass.SortOptionResponse>>{
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
        AppRepository.filterOptionApi(categoryId ?: Constants.UNIVERSAL_CAT_ID, object : ApiCallback<ProductListingDataClass.FilterOptionsResponse>{
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
                            selectedFilterMap[filter.code] = ""
                        }
                    }

                    priceFilter.value = filterOptions.filters.filter { option -> option.name == Constants.FILTER_PRICE_LABEL }[0]
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

        AppRepository.getProductList(prepareProductListingRequest(catId ?: Constants.UNIVERSAL_CAT_ID, query,fromSearch), fromSearch,  object: ApiCallback<ProductListingDataClass.ProductListingResponse>{
            override fun onException(error: Throwable) {
                Utils.printLog("product listing", error.message?: "exception")
                noProductAvailable.value = 0
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("product listing", errorMsg)
                noProductAvailable.value = 0
            }

            override fun onSuccess(response: ProductListingDataClass.ProductListingResponse?) {

                isLoading = false
                if(null == productListResponse){
                    productListResponse = response

                }else{
                    productListResponse?.items?.addAll(response?.items as ArrayList)
                    productListResponse?.total_count = response?.total_count ?: 0
                }
                totalProductCount = productListResponse?.total_count ?: 0

                if(totalProductCount > 0){

                    val maskedResponse: ArrayList<ProductListingDataClass.ProductMaskedResponse> = ArrayList()
                    productListResponse?.items.let {
                        for(product in productListResponse!!.items){
                            val sku = product.sku
                            val name = product.name
                            val productType = product.type_id
                            var price: Double
                            var specialPrice = 0.0
                            if(productType == Constants.FILTER_CONFIGURABLE_LABEL){
                                price = product.extension_attributes.regular_price
                                specialPrice = product.extension_attributes.final_price
                            }else{
                                price = product.price
                                val attributes = product.custom_attributes.filter { it.attribute_code == Constants.FILTER_SPECIAL_PRICE_LABEL }.toList()
                                if(attributes.isNotEmpty()) {
                                    specialPrice = attributes[0].value.toString().toDouble()
                                }
                            }


                            var toDate = ""
                            var fromDate = ""
                            var attributes = product.custom_attributes.filter { it.attribute_code == Constants.NEW_FROM_DATE_LABEL }.toList()
                            if(attributes.isNotEmpty()){
                                fromDate = attributes.single().value.toString()
                            }

                            attributes = product.custom_attributes.filter { it.attribute_code == Constants.NEW_TO_DATE_LABEL }.toList()
                            if(attributes.isNotEmpty()){
                                toDate = attributes.single().value.toString()
                            }
                            val type = if(toDate.isNotBlank() && fromDate.isNotBlank()) isNewProduct(fromDate, toDate) else ""

                            val discount = (((price - specialPrice).div(price)).times(100)).toInt()
                            var imgUrl = ""
                            if(product.media_gallery_entries?.isNotEmpty()!!)   imgUrl = product.media_gallery_entries[0].file.toString()

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

                    maskedProductList.value = maskedResponse
                }
                noProductAvailable.value = totalProductCount
            }
        })
    }

    fun clearExistingList() {
        productListResponse?.total_count = 0
        productListResponse?.items?.clear()
        maskedProductList.value?.clear()
    }

    private fun isNewProduct(fromDate: String, toDate: String): String {

        val sdf = SimpleDateFormat(Constants.YY_MM__DD_DATE_FORMAT)
        val d = Date()
        val currentDate= sdf.format(d)
        val cDate=sdf.parse(currentDate)
        val sDtate=sdf.parse(fromDate)
        val eDate=sdf.parse(toDate)

        return if(!(cDate < sDtate || cDate > eDate)) {
            Constants.NEW_TAG
        } else ""
    }

    private fun prepareProductListingRequest(catId: Int, query: String, fromSearch: Boolean): Map<String, String> {
        val requestMap: MutableMap<String, String> = mutableMapOf()

        if(fromSearch){
            requestMap[Constants.REQUEST_SEARCH_LABEL] = query
        }

        requestMap[Constants.REQUEST_ID_LABEL] = catId.toString()
        requestMap[Constants.REQUEST_PAGE_LIMIT_LABEL] = Constants.LIST_PAGE_ITEM_COUNT.toString()
        val page = (maskedProductList.value?.size)?.div(10)?.plus(1) ?: 1
        requestMap[Constants.REQUEST_PAGE_LABEL] = page.toString()

        if(selectedSortOption.attribute_code.isNotBlank()){
            requestMap[Constants.SORT_OPTION_LABEL] = selectedSortOption.attribute_code
            val dir = when{
                selectedSortOption.attribute_name.contains(Constants.HIGH_TO_LOW, true) -> Constants.DESC
                selectedSortOption.attribute_name.contains(Constants.LOW_TO_HIGH, true) -> Constants.ASC
                else -> ""
            }

            if(dir.isNotBlank()){
                requestMap[Constants.SORT_OPTION_DIR] = dir
            }
        }

        if(selectedPriceRange.min.isNotBlank() && selectedPriceRange.max.isNotBlank()){
            selectedFilterMap[Constants.FILTER_PRICE_KEY] = selectedPriceRange.min.toFloat().toInt().toString() + "-" + selectedPriceRange.max.toFloat().toInt().toString()
        }

        for((key, value) in selectedFilterMap){
            if(value.isNotBlank()){
                requestMap[key] = value
            }
        }

        return requestMap
    }


}
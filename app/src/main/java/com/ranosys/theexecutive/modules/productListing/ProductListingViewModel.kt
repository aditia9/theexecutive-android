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

/**
 * Created by nikhil on 20/3/18.
 */
class ProductListingViewModel(application: Application): BaseViewModel(application) {

    var maskedProductList: MutableLiveData<ArrayList<ProductListingDataClass.ProductMaskedResponse>> = MutableLiveData()
    var isLoading: Boolean = false
    var isFiltered: Boolean = false
    var isSorted: Boolean = false
    var totalProductCount: Int = 0


    var sortOptionList: MutableLiveData<MutableList<ProductListingDataClass.SortOptionResponse>>? = MutableLiveData<MutableList<ProductListingDataClass.SortOptionResponse>>()
    var filterOptionList: MutableLiveData<MutableList<ProductListingDataClass.Filter>>? = MutableLiveData<MutableList<ProductListingDataClass.Filter>>()
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

    fun getProductListing(catId: String) {
        isLoading = true

        AppRepository.getProductList(prepareProductListingRequest(catId), object: ApiCallback<ProductListingDataClass.ProductListingResponse>{
            override fun onException(error: Throwable) {
                Utils.printLog("product listing", error.message?: "exception")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("product listing", errorMsg)
            }

            override fun onSuccess(response: ProductListingDataClass.ProductListingResponse?) {

                isLoading = false
                productListResponse = response

                totalProductCount = productListResponse?.total_count ?: 0

                if(totalProductCount > 0){

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
                            if(product.media_gallery_entries.isNotEmpty())   imgUrl = product.media_gallery_entries[0].file

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

                noProductAvailable.value = totalProductCount

            }
        })
    }

    private fun isNewProduct(fromDate: String, toDate: String): String {

        val sdf = SimpleDateFormat(Constants.YY_MM__DD_DATE_FORMAT)
        val d = Date()
        var currentDate= sdf.format(d)
        var cDate=sdf.parse(currentDate)
        var sDtate=sdf.parse(fromDate)
        var eDate=sdf.parse(toDate)

        if(cDate.compareTo(sDtate) >= 0 && cDate.compareTo(eDate) <= 0) return Constants.NEW_TAG else  return ""
    }

    private fun prepareProductListingRequest(catId: String): Map<String, String> {
        val requestMap: MutableMap<String, String> = mutableMapOf()

        requestMap.put(Constants.REQUEST_ID_LABEL, catId)
        requestMap.put(Constants.REQUEST_PAGE_LIMIT_LABEL, Constants.LIST_PAGE_ITEM_COUNT.toString())
        val page = (maskedProductList.value?.size)?.div(10)?.plus(1) ?: 1
        requestMap.put(Constants.REQUEST_PAGE_LABEL, page.toString())

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
            selectedFilterMap.put(Constants.FILTER_PRICE_LABEL, selectedPriceRange.min + "-" + selectedPriceRange.max)
        }

        for((key, value) in selectedFilterMap){
            requestMap.put(key, value)
        }

        return requestMap
    }


}
package com.ranosys.theexecutive.modules.productListing

/**
 * Created by nikhil on 20/3/18.
 */
class ProductListingDataClass {
    class ProductListingRequest()
    class ProductListingResponse()

    class DummyResponse(var sku: String = "0",
                        var name: String = "Demo Longggg Naaaaame",
                        var normalPrice: String = "6.9",
                        var specialPrice: String = "2,42.900",
                        var type: String = "New",
                        var discountPer: Int = 40,
                        var collectionTag: String = "Chinese Collections",
                        var imageUrl: String = "http://fashionbombdaily.com/wp-content/uploads/2015/08/bomb-product-of-the-day-zara-mini-city-bag-fbd2.jpg")


    data class SortOptionResponse(
            val attribute_code: String,
            val attribute_name: String
    )


}
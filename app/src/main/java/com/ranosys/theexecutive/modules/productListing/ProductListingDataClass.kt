package com.ranosys.theexecutive.modules.productListing

/**
 * Created by nikhil on 20/3/18.
 */
class ProductListingDataClass {
    class ProductListingRequest()
    class ProductListingResponse()

    class DummyResponse(var sku: String = "0",
                        var name: String = "Demo Name",
                        var normalPrice: String = "50000$",
                        var specialPrice: String = "1000000000000$",
                        var type: String = "New",
                        var discountPer: Int = 40,
                        var collectionTag: String = "Chinese Collections",
                        var imageUrl: String = "https://www.planwallpaper.com/static/images/8ccb4ec4225b290726ae9be975220ff4.jpg")


}
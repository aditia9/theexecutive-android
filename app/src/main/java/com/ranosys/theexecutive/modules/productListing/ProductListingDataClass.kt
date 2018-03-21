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
                        var imageUrl: String = "http://www.thoroughbredbonusscheme.co.nz/images/category_9/Online%20Sale%20Hot%20products%20Zara%20Black%20Printed%20Slides%20Mens%20Footwear%20R40JEdtLkigj_5.jpeg")


}
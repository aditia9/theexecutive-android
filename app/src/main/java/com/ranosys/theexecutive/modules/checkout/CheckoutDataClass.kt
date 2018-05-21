package com.ranosys.theexecutive.modules.checkout

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 21-May-2018
 */
class CheckoutDataClass {


    data class GetShippingMethodsRequest(val addressId: String)


    data class GetShippingMethodsResponse(
            val carrier_code: String,
            val method_code: String,
            val carrier_title: String,
            val method_title: String,
            val amount: Int,
            val base_amount: Int,
            val available: Boolean,
            val error_message: String,
            val price_excl_tax: Int,
            val price_incl_tax: Int
    )
}
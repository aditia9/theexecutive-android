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
            val price_incl_tax: Int,
            var isSelected: Boolean = false
    )


    data class GetPaymentMethodsRequest(
            val addressInformation: AddressInformation
    )

    data class AddressInformation(
            val shipping_address: ShippingAddress,
            val billing_address: ShippingAddress,
            val shipping_carrier_code: String,
            val shipping_method_code: String
    )

    data class ShippingAddress(
            val customer_id: String,
            val region: String,
            val region_id: String,
            val region_code: String,
            val country_id: String,
            val street: List<String?>,
            val postcode: String,
            val city: String,
            val firstname: String,
            val lastname: String,
            val telephone: String
    )


    data class PaymentMethodResponse(
            val payment_methods: List<PaymentMethod>,
            val totals: Totals
    )

    data class PaymentMethod(
            val code: String,
            val title: String
    )

    data class Totals(
            val total_segments: List<TotalSegment>
    )

    data class TotalSegment(
            val code: String,
            val title: String,
            val value: Int
    )

}
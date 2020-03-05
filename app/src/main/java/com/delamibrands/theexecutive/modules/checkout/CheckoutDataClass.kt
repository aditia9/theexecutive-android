package com.delamibrands.theexecutive.modules.checkout

import com.delamibrands.theexecutive.modules.myAccount.MyAccountDataClass

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 21-May-2018
 */
class CheckoutDataClass {



    data class UserInfoNselectedShippingResponse(
            val customer: MyAccountDataClass.UserInfoResponse,
            val extension_attributes: ExtensionAttributes
    )

    data class ExtensionAttributes(
            val shipping_assignments: List<ShippingAssignment>
    )

    data class ShippingAssignment(
            val shipping: Shipping
    )

    data class Shipping(
            val address: CheckoutDataClass.Address,
            val method: String? = ""
    )

    data class Address(
            var id: String? = "",
            var customer_id: String? = "",
            val region: String?,
            val region_id: String?,
            val country_id: String?,
            val street: List<String?>?,
            var telephone: String?,
            val postcode: String?,
            val city: String?,
            val prefix: String? = "",
            val firstname: String? = "",
            val lastname: String? = "",
            val customer_address_id: String? = "",
            var default_shipping: Boolean? = null,
            var default_billing: Boolean? = null
    )

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
            val telephone: String,
            val customer_address_id: String?

    )


    data class PaymentMethodResponse(
            val payment_methods: List<PaymentMethod>,
            val totals: Totals
    )

    data class PaymentMethod(
            val code: String,
            val title: String,
            var isSelected: Boolean = false
    )

    data class Totals(
            val total_segments: List<TotalSegment>
    )

    data class TotalSegment(
            val code: String,
            val title: String,
            val value: String
    )


    data class PlaceOrderRequest(
            val paymentMethod: PlaceOrderPaymentMethod
    )

    data class PlaceOrderPaymentMethod(
            val method: String
    )


    data class OrderStatusResponse(
            val order_id: String,
            val shipping_method: String,
            val grand_total: String,
            val payment_method: String,
            val virtual_account_number: Any,
            val base_currency_code: String,
            val status_code: String,
            val status_label: String,
            val order_state: String
    )

    data class OrderCommentRequest(
    val statusHistory: StatusHistory
)

    data class StatusHistory(
    val comment: String,
    val created_at: String? = null,
    val entity_id: Int? = null,
    val entity_name: String? = null,
    val extension_attributes: ExtensionAttributes? = null,
    val is_customer_notified: Int? = null,
    val is_visible_on_front: Int? = null,
    val parent_id: Int? = null,
    val status: String? = null
)


}
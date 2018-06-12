package com.ranosys.theexecutive.modules.order.orderDetail

data class OrderDetailResponse(
        val grand_total: Int,
        val shipping_incl_tax: Int,
        val subtotal_incl_tax: Int,
        val items: List<Item>,
        val billing_address: BillingAddress,
        val extension_attributes: ExtensionAttributesReturnto,
        val payment : Payment
)


data class ExtensionAttributesReturnto(
        val returnto_address: ReturntoAddress,
        val virtual_account_number : String,
        val payment_method : String,
        val formatted_shipping_address : FormattedShippingAddress
)


data class FormattedShippingAddress(
        val extension_attributes: ExtensionAttributesCountry
)

data class ExtensionAttributesCountry(
        val country_name: String
)


data class ReturntoAddress(
        val returnto_name: String,
        val returnto_address: String,
        val returnto_contact: String
)

data class BillingAddress(
        val address_type: String,
        val city: String,
        val country_id: String,
        val customer_address_id: Int,
        val email: String,
        val entity_id: Int,
        val firstname: String,
        val lastname: String,
        val parent_id: Int,
        val postcode: String,
        val prefix: String,
        val region: String,
        val region_code: String,
        val region_id: Int,
        val street: List<String>,
        val telephone: String
)

data class Item(
        val amount_refunded: Int,
        val base_amount_refunded: Int,
        val base_discount_amount: Int,
        val base_discount_invoiced: Int,
        val base_discount_tax_compensation_amount: Int,
        val base_original_price: Int,
        val base_price: Int,
        val base_price_incl_tax: Int,
        val base_row_invoiced: Int,
        val base_row_total: Int,
        val base_row_total_incl_tax: Int,
        val base_tax_amount: Int,
        val base_tax_invoiced: Int,
        var created_at: String,
        val discount_amount: Int,
        val discount_invoiced: Int,
        val discount_percent: Int,
        val free_shipping: Int,
        val discount_tax_compensation_amount: Int,
        val is_qty_decimal: Int,
        val is_virtual: Int,
        val item_id: Int,
        val name: String,
        val no_discount: Int,
        val order_id: Int,
        val original_price: Int,
        val price: Int,
        val price_incl_tax: Int,
        val product_id: Int,
        val product_type: String,
        val qty_canceled: Int,
        val qty_invoiced: Int,
        val qty_ordered: Int,
        val qty_refunded: Int,
        val qty_shipped: Int,
        val quote_item_id: Int,
        val row_invoiced: Int,
        val row_total: Int,
        val row_total_incl_tax: Int,
        val row_weight: Int,
        val sku: String,
        val store_id: Int,
        val tax_amount: Int,
        val tax_invoiced: Int,
        val tax_percent: Int,
        val updated_at: String,
        val weight: Int,
        val extension_attributes: ExtensionAttributes? = null,
        val parent_item_id: Int,
        val parent_item: ParentItem,
        var request_qty: Int = 0,
        var request_reason: String,
        var request_return: Boolean
)

data class ExtensionAttributes(
        val options: List<Option>? = null,
        val image: String
)

data class Option(
        val label: String,
        val value: String,
        val option_id: Int,
        val option_value: String
)

data class Payment(
        val account_status: Any,
        val additional_information: List<String>,
        val amount_ordered: Int,
        val base_amount_ordered: Int,
        val base_shipping_amount: Int,
        val cc_last4: Any,
        val entity_id: Int,
        val method: String,
        val parent_id: Int,
        val shipping_amount: Int
)

data class ParentItem(
        val amount_refunded: Int,
        val base_amount_refunded: Int,
        val base_discount_amount: Int,
        val base_discount_invoiced: Int,
        val base_discount_tax_compensation_amount: Int,
        val base_original_price: Int,
        val base_price: Int,
        val base_price_incl_tax: Int,
        val base_row_invoiced: Int,
        val base_row_total: Int,
        val base_row_total_incl_tax: Int,
        val base_tax_amount: Int,
        val base_tax_invoiced: Int,
        val created_at: String,
        val discount_amount: Int,
        val discount_invoiced: Int,
        val discount_percent: Int,
        val free_shipping: Int,
        val discount_tax_compensation_amount: Int,
        val is_qty_decimal: Int,
        val is_virtual: Int,
        val item_id: Int,
        val name: String,
        val no_discount: Int,
        val order_id: Int,
        val original_price: Int,
        val price: Int,
        val price_incl_tax: Int,
        val product_id: Int,
        val product_type: String,
        val qty_canceled: Int,
        val qty_invoiced: Int,
        val qty_ordered: Int,
        val qty_refunded: Int,
        val qty_shipped: Int,
        val quote_item_id: Int,
        val row_invoiced: Int,
        val row_total: Int,
        val row_total_incl_tax: Int,
        val row_weight: Int,
        val sku: String,
        val store_id: Int,
        val tax_amount: Int,
        val tax_invoiced: Int,
        val tax_percent: Int,
        val updated_at: String,
        val weight: Int,
        val extension_attributes: ExtensionAttributes
)
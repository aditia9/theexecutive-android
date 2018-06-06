package com.ranosys.theexecutive.modules.shoppingBag


data class ShoppingBagResponse(
        val item_id: Int,
        val sku: String,
        var qty: Int,
        val name: String,
        val price: Int,
        val product_type: String,
        val quote_id: String,
        val product_option: ProductOptionShoppingBag,
        val extension_attributes: ExtensionAttributes
)

data class ProductOptionShoppingBag(
        val extension_attributes: ExtensionAttributesProductOptions
)

data class ExtensionAttributesProductOptions(
        val configurable_item_options: List<ConfigurableItemOption>
)

data class ConfigurableItemOption(
        val option_id: String,
        val option_value: Int,
        val extension_attributes: ExtensionAttributesConfigurableItemOption
)

data class ExtensionAttributesConfigurableItemOption(
        val attribute_label: String,
        val option_label: String
)

data class ExtensionAttributes(
        val regular_price: Int,
        val image: String,
        val stock_item: StockItem
)

data class StockItem(
        val item_id: Int,
        val product_id: Int,
        val stock_id: Int,
        val qty: Int,
        val is_in_stock: Boolean,
        val is_qty_decimal: Boolean,
        val show_default_notification_message: Boolean,
        val use_config_min_qty: Boolean,
        val min_qty: Int,
        val use_config_min_sale_qty: Int,
        val min_sale_qty: Int,
        val use_config_max_sale_qty: Boolean,
        val max_sale_qty: Int,
        val use_config_backorders: Boolean,
        val backorders: Int,
        val use_config_notify_stock_qty: Boolean,
        val notify_stock_qty: Int,
        val use_config_qty_increments: Boolean,
        val qty_increments: Int,
        val use_config_enable_qty_inc: Boolean,
        val enable_qty_increments: Boolean,
        val use_config_manage_stock: Boolean,
        val manage_stock: Boolean,
        val low_stock_date: Any,
        val is_decimal_divided: Boolean,
        val stock_status_changed_auto: Int
)


data class TotalResponse(
        val grand_total: Int,
        val base_grand_total: Int,
        val subtotal: Int,
        val base_subtotal: Int,
        val discount_amount: Int,
        val base_discount_amount: Int,
        val subtotal_with_discount: Int,
        val base_subtotal_with_discount: Int,
        val shipping_amount: Int,
        val base_shipping_amount: Int,
        val shipping_discount_amount: Int,
        val base_shipping_discount_amount: Int,
        val tax_amount: Int,
        val base_tax_amount: Int,
        val weee_tax_applied_amount: Any,
        val shipping_tax_amount: Int,
        val base_shipping_tax_amount: Int,
        val subtotal_incl_tax: Int,
        val shipping_incl_tax: Int,
        val base_shipping_incl_tax: Int,
        val base_currency_code: String,
        val quote_currency_code: String,
        val items_qty: Int,
        val items: List<ItemTotal>,
        val total_segments: List<TotalSegment>
)

data class TotalSegment(
        val code: String,
        val title: String,
        val value: Int,
        val extension_attributes: ExtensionAttributesTotalSegment,
        val area: String
)

data class ExtensionAttributesTotalSegment(
        val tax_grandtotal_details: List<Any>
)

data class ItemTotal(
        val item_id: Int,
        val price: Int,
        val base_price: Int,
        val qty: Int,
        val row_total: Int,
        val base_row_total: Int,
        val row_total_with_discount: Int,
        val tax_amount: Int,
        val base_tax_amount: Int,
        val tax_percent: Int,
        val discount_amount: Int,
        val base_discount_amount: Int,
        val discount_percent: Int,
        val price_incl_tax: Int,
        val base_price_incl_tax: Int,
        val row_total_incl_tax: Int,
        val base_row_total_incl_tax: Int,
        val options: String,
        val weee_tax_applied_amount: Any,
        val weee_tax_applied: Any,
        val name: String
)

data class ShoppingBagQtyUpdateRequest(
        val cartItem: CartItem
)

data class CartItem(
        val item_id: String,
        val qty: String,
        val quote_id: String?
)

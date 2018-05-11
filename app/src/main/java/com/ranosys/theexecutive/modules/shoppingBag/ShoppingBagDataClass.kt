package com.ranosys.theexecutive.modules.shoppingBag

data class ShoppingBagResponse(
        val item_id: Int,
        val sku: String,
        var qty: Int,
        val name: String,
        val price: Int,
        val product_type: String,
        val quote_id: String,
        val product_option: ProductOption
)

data class ProductOption(
        val extension_attributes: ExtensionAttributes
)

data class ExtensionAttributes(
        val configurable_item_options: List<ConfigurableItemOption>
)

data class ConfigurableItemOption(
        val option_id: String,
        val option_value: Int
)

data class ShoppingBagQtyUpdateRequest(
    val cartItem: CartItem
)

data class CartItem(
    val item_id: String,
    val qty: String,
    val quote_id: String
)
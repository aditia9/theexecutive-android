package com.delamibrands.theexecutive.modules.productDetail.dataClassess

/**
 * @Details data class
 * @Author Ranosys Technologies
 * @Date 17-Apr-2018
 */

data class AddToCartRequest(
        val cartItem: CartItem? = null
)

data class CartItem(
        val sku: String? = "",
        val qty: Int = 0,
        val quote_id: String? = "",
        val product_option: ProductOption? = null,
        val extension_attributes: ExtensionAttributes? = null
)

data class ProductOption(
        val extension_attributes: CartExtensionAttributes? = null
)

data class CartExtensionAttributes(
		val Configurable_item_options: MutableList<ConfigurableItemOption>
)



data class ConfigurableItemOption(
        val option_id: String? = "",
        val option_value: String? = ""
)


data class AddToCartResponse(
		val item_id: Int,
		val sku: String,
		val qty: Int,
		val name: String,
		val product_type: String,
		val quote_id: String,
		val product_option: ProductOption
)

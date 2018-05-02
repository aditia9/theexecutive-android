package com.ranosys.theexecutive.modules.wishlist

/**
 * @Details data class for wishlist response
 * @Author Ranosys Technologies
 * @Date 02,May,2018
 */

data class WishlistResponse(
		val id: Int,
		val name: String,
		val items_count: Int,
		val items: List<Item>?
)

data class Item(
		val id: Int,
		val product_id: Int,
		val qty: Int,
		val sku: String,
		val name: String,
		val image: String,
		val regular_price: Int,
		val final_price: Int,
		val options: List<Any>
)
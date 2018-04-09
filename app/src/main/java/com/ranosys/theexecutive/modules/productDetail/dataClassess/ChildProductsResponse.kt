package com.ranosys.theexecutive.modules.productDetail.dataClassess

/**
 * Created by Mohammad Sunny on 9/4/18.
 */

data class ChildProductsResponse(
		val id: Int,
		val sku: String,
		val name: String,
		val attribute_set_id: Int,
		val price: Int,
		val status: Int,
		val type_id: String,
		val created_at: String,
		val updated_at: String,
		val weight: Int,
		val product_links: List<Any>,
		val tier_prices: List<Any>,
		val custom_attributes: List<CustomAttributes>
)

data class CustomAttributes(
		val attribute_code: String,
		val value: String
)
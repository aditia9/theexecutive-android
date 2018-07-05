package com.ranosys.theexecutive.modules.productDetail.dataClassess

data class ExtensionAttributes(
		val website_ids: List<Int>,
		val category_links: List<CategoryLink>,
		val configurable_product_options: List<ConfigurableProductOption>?,
		val configurable_product_links: List<Int>?
)

data class ConfigurableProductOption(
		val id: Int,
		val attribute_id: String,
		val label: String,
		val position: Int,
		val values: List<Value>,
		val product_id: Int
)

data class Value(
		val value_index: Int
)

data class CategoryLink(
		val position: Int,
		val category_id: String
)

data class CustomAttribute(
		val attribute_code: String,
		val value: Any
)

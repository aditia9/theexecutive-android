package com.ranosys.theexecutive.modules.productDetail.dataClassess

/**
 * Created by Mohammad Sunny on 4/4/18.
 */
data class ProductDetailResponse(
		val id: Int,
		val sku: String?,
		val name: String,
		val attribute_set_id: Int,
		val price: Int,
		val status: Int,
		val visibility: Int,
		val type_id: String,
		val created_at: String,
		val updated_at: String,
		val weight: Double,
		val extension_attributes: ExtensionAttributes?,
		val product_links: List<Any>?,
		val options: List<Any>?,
		val media_gallery_entries: List<MediaGalleryEntry>?,
		val tier_prices: List<Any>?,
		val custom_attributes: List<CustomAttribute>?
)

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

data class MediaGalleryEntry(
		val id: Int,
		val media_type: String,
		val label: String,
		val position: Int,
		val disabled: Boolean,
		val types: List<String>,
		val file: String
)
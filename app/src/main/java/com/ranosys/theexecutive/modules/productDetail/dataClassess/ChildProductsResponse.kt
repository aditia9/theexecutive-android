package com.ranosys.theexecutive.modules.productDetail.dataClassess

import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass

/**
 * Created by Mohammad Sunny on 9/4/18.
 */

/*
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
*/

data class ChildProductsResponse(
		val id: Int,
		val sku: String,
		val name: String,
		val attribute_set_id: Int,
		val price: Float,
		val status: Int,
		val visibility: Int,
		val type_id: String,
		val created_at: String,
		val updated_at: String,
		val weight: Int,
		val extension_attributes: ExtensionAttributesChild,
		val product_links: List<Any>,
		val options: List<Any>,
		val media_gallery_entries: MutableList<ProductListingDataClass.MediaGalleryEntry>,
		val tier_prices: List<Any>,
		val custom_attributes: List<CustomAttributeChild>
)

data class ExtensionAttributesChild(
		val website_ids: List<Int>,
		val category_links: List<CategoryLinkChild>,
		val stock_item: StockItem
)

data class CategoryLinkChild(
		val position: Int,
		val category_id: String
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


data class CustomAttributeChild(
		val attribute_code: String,
		val value: Any
)

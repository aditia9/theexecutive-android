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
		val items: MutableList<Item>?
)

data class Item(
		val id: Int,
		val product_id: Int,
		val type_id: String,
		val qty: Int,
		val sku: String,
		val name: String,
		val image: String,
		val regular_price: Int,
		val final_price: Int,
		val stock_item: StockItem?,
		val options: List<Option?>
)

data class Option(
		val label: String,
		val value: String,
		val option_id: Int,
		val option_value: Int
)

data class StockItem(
		val item_id: Int,
		val product_id: Int,
		val stock_id: Int,
		val qty: Int,
		val is_in_stock: Boolean = true,
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


data class MoveToBagRequest(
		val id: String,
		val qty: String
)
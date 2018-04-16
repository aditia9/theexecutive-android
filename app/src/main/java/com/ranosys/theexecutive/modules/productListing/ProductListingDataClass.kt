package com.ranosys.theexecutive.modules.productListing

import android.databinding.ObservableField

/**
 * @Class An data class collection fro product listing
 * @author Ranosys Technologies
 * @Date 20-Mar-2018
 */

class ProductListingDataClass {

    class ProductMaskedResponse(var sku: String = "0",
                                var name: String = "Demo Longggg Naaaaame",
                                var normalPrice: String = "6.9",
                                var specialPrice: String = "2,42.900",
                                var type: String = "New",
                                var discountPer: Int = 40,
                                var collectionTag: String = "Chinese Collections",
                                var imageUrl: String = "http://fashionbombdaily.com/wp-content/uploads/2015/08/bomb-product-of-the-day-zara-mini-city-bag-fbd2.jpg")



    data class SortOptionResponse(
            val attribute_code: String,
            val attribute_name: String
    )


    data class FilterOptionsResponse(
            val total_count: Int,
            val filters: List<Filter>,
            val active_filters: List<Any>
    )

    data class Filter(
            val name: String,
            val code: String,
            val options: List<FilterChildOption>
    )

    data class FilterChildOption(
            val label: String,
            val code: String,
            val value: String){
        var _isSelected: ObservableField<Boolean>
        init {
            _isSelected = ObservableField<Boolean>()
        }
    }



    data class PriceRange(var min: String = "",
                          var max: String = "")


    data class ProductListingResponse(
            var total_count: Int,
            var items: MutableList<Item>
    )

    data class Item(
            val id: Int,
            val sku: String,
            val name: String,
            val attribute_set_id: Int,
            val price: Double,
            val status: Int,
            val visibility: Int,
            val type_id: String,
            val created_at: String,
            val updated_at: String,
            val weight: Double,
            val extension_attributes: ExtensionAttributes,
            val product_links: List<ProductLinks?>?,
            val options: List<Any>,
            val media_gallery_entries: MutableList<MediaGalleryEntry>?,
            val tier_prices: List<Any>,
            val custom_attributes: List<CustomAttribute>
    )

    data class ExtensionAttributes(
            val website_ids: List<Int>,
            val category_links: List<CategoryLink>,
            val stock_item: StockItem?,
            val configurable_product_options: List<ConfigurableProductOption>,
            val configurable_product_links: List<Int>,
            val regular_price: Double,
            val final_price: Double
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

    data class MediaGalleryEntry(
            val id: Int,
            val media_type: String,
            val label: Any,
            val position: Int,
            val disabled: Boolean,
            val types: List<String>,
            val file: String
    )

    data class CustomAttribute(
            val attribute_code: String,
            val value: Any
    )


    data class ProductLinks(
            val sku: String,
            val link_type: String,
            val linked_product_sku: String,
            val linked_product_type: String,
            val position: Int,
            val extension_attributes: ProductExtensionAttributes
    )

    data class ProductExtensionAttributes (

        var linked_product_name : String,
        var linked_product_image : String,
        var linked_product_regularprice : Int,
        var linked_product_finalprice  : Int)


}
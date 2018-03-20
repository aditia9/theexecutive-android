package com.ranosys.theexecutive.modules.category

/**
 * Created by Mohammad Sunny on 5/2/18.
 */
data class CategoryResponseDataClass(
		val id: Int? = 0,
		val parent_id: Int? = 0,
		val name: String? = "",
		val is_active: Boolean? = false,
		val position: Int? = 0,
		val level: Int? = 0,
		val product_count: Int? = 0,
		val children_data: ArrayList<ChildrenData>
)

data class ChildrenData(
		val id: Int? = 0,
		val parent_id: Int? = 0,
		val name: String? = "",
		val is_active: Boolean? = false,
		val position: Int? = 0,
		val level: Int? = 0,
		val product_count: Int? = 0,
		val children_data: ArrayList<ChildrenData>
)


data class CategoryDataResponse(
		val id: Int,
		val parent_id: Int,
		val name: String,
		val is_active: Boolean,
		val position: Int,
		val level: Int,
		val children: String,
		val created_at: String,
		val updated_at: String,
		val path: String,
		val available_sort_by: List<Any>,
		val include_in_menu: Boolean,
		val custom_attributes: List<CustomAttribute>
)

data class CustomAttribute(
		val attribute_code: String,
		val value: String
)
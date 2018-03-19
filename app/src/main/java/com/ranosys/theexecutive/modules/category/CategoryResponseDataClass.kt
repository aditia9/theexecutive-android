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
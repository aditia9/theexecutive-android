package com.ranosys.theexecutive.modules.order.orderReturn


/**
 * @Class An data class for Order return data class
 * @author Ranosys Technologies
 * @Date 24-May-2018
 */
data class OrderReturnRequest(
        val rmaData: RmaData
)

data class RmaData(
        var orderId: String,
        val items: MutableList<Item>
)

data class Item(
        var item_id: Int,
        val reason: String,
        val qty: Int
)
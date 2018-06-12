package com.ranosys.theexecutive.modules.order.orderList

/**
 * @Class An data class for Order List Response
 * @author Ranosys Technologies
 * @Date 23-May-2018
 */
data class OrderListResponse(
    val id: String,
    val amount: String,
    val status: String,
    val date: String,
    val image: String,
    val is_refundable: Boolean,
    val payment_method: String
)
package com.ranosys.theexecutive.modules.order


data class OrderListResponse(
    val id: String,
    val amount: String,
    val status: String,
    val date: String,
    val image: String
)
package com.ranosys.theexecutive.modules.notification


data class NotificationListResponse(val id: Int,
                                    val title: String,
                                    val imgUrl: String,
                                    val description: String,
                                    val isRead: Boolean
)
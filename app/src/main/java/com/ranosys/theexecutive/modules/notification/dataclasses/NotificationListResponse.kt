package com.ranosys.theexecutive.modules.notification.dataclasses

/**
 * @Details Data class for notification list response
 * @Author Ranosys Technologies
 * @Date 22,May,2018
 */
data class NotificationListResponse(val id: Int,
                                    val notification_id: Int,
                                    val title: String,
                                    val description: String,
                                    val type: String,
                                    val type_id: String,
                                    val redirection_title: String,
                                    val isRead: Boolean = false,
                                    val store_id: Int,
                                    val sent_date: String,
                                    val imgUrl: String
)

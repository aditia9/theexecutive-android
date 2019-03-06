package com.delamibrands.theexecutive.modules.notification.dataclasses

/**
 * @Details Data class for notification change status request
 * @Author Ranosys Technologies
 * @Date 22,May,2018
 */
class NotificationChangeStatusRequest(val notification_id: String?,
                                      val device_id: String?,
                                      val customer_token: String?)


class DeviceRegisterRequest(val device_type: String?,
                                      val registration_id: String?,
                                      val device_id: String?)
package com.ranosys.theexecutive.modules.myAccount

import com.ranosys.theexecutive.modules.register.RegisterDataClass

/**
 * Created by nikhil on 22/3/18.
 */
class MyAccountDataClass {

    data class MyAccountOption(val title: String, val icon: Int)

    data class NewsletterSubscriptionRequest(val email: String)


    data class UserInfoResponse(
            val id: Int,
            val group_id: Int,
            val default_billing: String,
            val default_shipping: String,
            val created_at: String,
            val updated_at: String,
            val created_in: String,
            val dob: String,
            val email: String,
            val firstname: String,
            val lastname: String,
            val prefix: String,
            val gender: Int,
            val store_id: Int,
            val website_id: Int,
            val addresses: List<RegisterDataClass.Address>,
            val disable_auto_group_change: Int
    )

}
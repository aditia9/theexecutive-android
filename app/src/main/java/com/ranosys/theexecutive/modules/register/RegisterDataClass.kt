package com.ranosys.theexecutive.modules.register

/**
 * Created by Mohammad Sunny on 31/1/18.
 */
class RegisterDataClass {

    data class Country(
            val id: String = "",
            val two_letter_abbreviation: String = "",
            val three_letter_abbreviation: String = "",
            val full_name_locale: String = "",
            val full_name_english: String = "",
            val available_regions: List<State> = listOf()
    ){
        override fun toString(): String {
            return full_name_locale
        }
    }

    data class State(
            val id: String = "",
            val code: String = "",
            val name: String = ""
    ){
        override fun toString(): String {
            return name
        }
    }


    data class City(
            val name: String = "",
            val value: Int = 0
    ){
        override fun toString(): String {
            return name
        }
    }


    data class RegisterRequest(
            val customer: Customer,
            val password: String
    )

    data class Customer(
            val group_id: Int,
            val confirmation: String,
            val dob: String,
            val email: String,
            val prefix: String ="",
            val firstname: String,
            val lastname: String,
            val gender: Int,
            val store_id: Int,
            val website_id: Int,
            val extension_attributes: ExtensionAttributes,
            val addresses: List<Address>
    )

    data class ExtensionAttributes(
            val is_subscribed: Boolean
    )

    data class Address(
            val region: Region,
            val region_id: String,
            val country_id: String,
            val street: List<String>,
            var telephone: String,
            val postcode: String,
            val city: String,
            val prefix: String = "",
            val firstname: String,
            val lastname: String,
            val default_shipping: Boolean,
            val default_billing: Boolean
    )

    data class Region(
            var region_code: String,
            val region: String,
            val region_id: Int
    )


    data class RegistrationResponse(
            val id: Int,
            val group_id: Int,
            val default_billing: String,
            val default_shipping: String,
            val confirmation: String,
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
            val addresses: List<Address>,
            val disable_auto_group_change: Int
    )
}
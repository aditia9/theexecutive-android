package com.ranosys.theexecutive.modules.register

/**
 * Created by Mohammad Sunny on 31/1/18.
 */
class RegisterDataClass {
    data class RegisterRequest(var name: String, var mobile: String,
                               var email: String, var city: String,
                               var state: String, var gender: String,
                               var password: String)

    data class RegisterResponse(var isSuccess: Boolean)

    data class Country(
            val id: String = "1",
            val two_letter_abbreviation: String = "",
            val three_letter_abbreviation: String = "",
            val full_name_locale: String = "india",
            val full_name_english: String = "",
            val available_regions: List<State>?
    ){
        override fun toString(): String {
            return full_name_locale
        }
    }

    data class State(
            val id: String = "",
            val code: String = "123",
            val name: String = "raj"
    ){
        override fun toString(): String {
            return name
        }
    }


    data class City(
            val name: String,
            val value: Int
    )
}
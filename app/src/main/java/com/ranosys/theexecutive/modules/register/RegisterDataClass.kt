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
}
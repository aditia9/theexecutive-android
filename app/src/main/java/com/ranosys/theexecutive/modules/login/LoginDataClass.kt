package com.ranosys.theexecutive.modules.login

/**
 * Created by Mohammad Sunny on 25/1/18.
 */
class LoginDataClass {

    data class LoginRequest(var username: String,
                            var password: String)

    data class IsEmailAvailableRequest(var customerEmail: String,
                                       var websiteId: Int)

    data class SocialLoginData(var firstName: String,
                               var latsName: String,
                               var email: String,
                               var gender: String,
                               var type: String,
                               var token: String)


    data class SocialLoginRequest(
            val email: String,
            val type: String,
            val token: String
    )
}


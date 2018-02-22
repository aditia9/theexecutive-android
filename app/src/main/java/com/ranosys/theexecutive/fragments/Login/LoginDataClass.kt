package com.ranosys.theexecutive.fragments.Login

/**
 * Created by Mohammad Sunny on 25/1/18.
 */
class LoginDataClass {

    data class LoginRequest(var email: String,
                            var password: String,
                            var registrationId : String,
                            var deviceId : String,
                            var deviceType : String)

    data class LoginResponse(val accessToken : String,
                             val refreshToken : String,
                             val isFirstLogin : Boolean,
                             val error : String,
                             val message : String,
                             val isSubscriptionPurchased : Boolean,
                             val invitationCode : String,
                             val isCodeUsed : Boolean,
                             val referralUrl : String)
}


package com.ranosys.theexecutive.api.interfaces

import com.ranosys.theexecutive.modules.login.LoginDataClass
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by Mohammad Sunny on 25/1/18.
 */
interface ApiService {

    interface LoginService {
        @POST("user/login/email")
        @Headers(ApiConstants.API_HEADER)
        fun getLoginData(@Body loginRequest: LoginDataClass.LoginRequest?): Call<LoginDataClass.LoginResponse>
    }
}
package com.ranosys.theexecutive.api.interfaces

import com.ranosys.theexecutive.fragments.Login.LoginDataClass
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by Mohammad Sunny on 25/1/18.
 */
interface apiService {

    interface LoginService {
        @POST("api/v1/user/login/email")
        @Headers("Content-Type: application/json", "X-Requested-With: XMLHttpRequest", "Cache-Control: no-cache")
        fun getLoginData(@Body loginRequest: LoginDataClass.LoginRequest?): Call<LoginDataClass.LoginResponse>
    }
}
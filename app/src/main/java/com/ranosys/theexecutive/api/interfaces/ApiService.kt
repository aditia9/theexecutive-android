package com.ranosys.theexecutive.api.interfaces

import com.ranosys.theexecutive.api.ApiConstants
import com.ranosys.theexecutive.modules.login.LoginDataClass
import com.ranosys.theexecutive.modules.splash.AdminDataClass
import com.ranosys.theexecutive.modules.splash.StoreResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by Mohammad Sunny on 25/1/18.
 */
interface ApiService {

    interface LoginService {
        @POST("user/login/email")
        @Headers(ApiConstants.API_HEADER)
        fun getLoginData(@Body loginRequest: LoginDataClass.LoginRequest?): Call<LoginDataClass.LoginResponse>
    }

    interface AdminTokenService {
        @POST("rest/all/V1/integration/admin/token")
        @Headers("Content-Type: application/json", "X-Requested-With: XMLHttpRequest", "Cache-Control: no-cache")
        fun getAdminToken(@Body adminTokenRequest: AdminDataClass): Call<String>
    }

    interface StoresService {
        @POST("rest/all/V1/store/storeViews")
        @Headers("Content-Type: application/json", "X-Requested-With: XMLHttpRequest", "Cache-Control: no-cache")
        fun getStores(): Call<StoreResponse>
    }
}
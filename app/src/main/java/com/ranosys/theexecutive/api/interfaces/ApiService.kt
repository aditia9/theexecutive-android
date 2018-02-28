package com.ranosys.theexecutive.api.interfaces

import com.ranosys.theexecutive.api.ApiConstants
import com.ranosys.theexecutive.modules.login.LoginDataClass
import com.ranosys.theexecutive.modules.splash.AdminDataClass
import com.ranosys.theexecutive.modules.splash.StoreResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Mohammad Sunny on 25/1/18.
 */
interface ApiService {

    interface LoginService {
        @POST("user/login/email")
        fun getLoginData(@Body loginRequest: LoginDataClass.LoginRequest?): Call<LoginDataClass.LoginResponse>
    }

    interface AdminTokenService {
        @POST("rest/all/V1/integration/admin/token")
        @Headers("Content-Type: application/json", "X-Requested-With: XMLHttpRequest", "Cache-Control: no-cache")
        fun getAdminToken(@Body adminTokenRequest: AdminDataClass): Call<String>
    }

    interface StoresService {
        @GET("rest/all/V1/store/storeViews")
        @Headers(ApiConstants.CONTENT_TYPE,
            ApiConstants.X_REQUESTED_WITH,
            ApiConstants.CACHE_CONTROL)
        fun getStores(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?): Call<StoreResponse>
    }
}
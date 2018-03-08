package com.ranosys.theexecutive.api.interfaces

import com.ranosys.theexecutive.api.ApiConstants
import com.ranosys.theexecutive.modules.forgot_password.ForgotPasswordDataClass
import com.ranosys.theexecutive.modules.login.LoginDataClass
import com.ranosys.theexecutive.modules.splash.ConfigurationResponse
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

//    interface AdminTokenService {
//        @POST("rest/all/V1/integration/admin/token")
//        @Headers("Content-Type: application/json", "X-Requested-With: XMLHttpRequest", "Cache-Control: no-cache")
//        fun getAdminToken(@Body adminTokenRequest: AdminDataClass): Call<String>
//    }

    interface StoresService {
        @GET("rest/all/V1/store/storeViews")
        @Headers(ApiConstants.CONTENT_TYPE,
            ApiConstants.X_REQUESTED_WITH,
            ApiConstants.CACHE_CONTROL)
        fun getStores(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?): Call<ArrayList<StoreResponse>>
    }

    interface ConfigurationService {
        @GET("rest/{store_code}/V1/mobileappversionapi/configuration/androivd")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getConfiguration(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String): Call<ConfigurationResponse>
    }

    interface ForgotPasswordService {
        @PUT("rest/{store_code}/V1/customers/password")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun forgotPasswordApi(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String, @Body requets: ForgotPasswordDataClass.ForgotPasswordRequest): Call<Boolean>
    }
}
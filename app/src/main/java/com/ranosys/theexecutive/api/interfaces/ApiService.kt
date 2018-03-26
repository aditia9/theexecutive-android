package com.ranosys.theexecutive.api.interfaces

import com.ranosys.theexecutive.api.ApiConstants
import com.ranosys.theexecutive.modules.category.AllCategoryDataResponse
import com.ranosys.theexecutive.modules.category.CategoryDataResponse
import com.ranosys.theexecutive.modules.category.CategoryResponseDataClass
import com.ranosys.theexecutive.modules.category.PromotionsResponseDataClass
import com.ranosys.theexecutive.modules.forgot_password.ForgotPasswordDataClass
import com.ranosys.theexecutive.modules.login.LoginDataClass
import com.ranosys.theexecutive.modules.splash.ConfigurationResponse
import com.ranosys.theexecutive.modules.splash.StoreResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Mohammad Sunny on 21/2/18.
 */
interface ApiService {

    interface LoginService {
        @POST("rest/{store_code}/V1/integration/customer/token")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getLoginData(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String, @Body loginRequest: LoginDataClass.LoginRequest?): Call<String>
    }

    interface StoresService {
        @GET("rest/all/V1/store/storeViews")
        @Headers(ApiConstants.CONTENT_TYPE,
            ApiConstants.X_REQUESTED_WITH,
            ApiConstants.CACHE_CONTROL)
        fun getStores(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?): Call<ArrayList<StoreResponse>>
    }

    interface ConfigurationService {
        @GET("rest/{store_code}/V1/mobileappversionapi/configuration/android")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getConfiguration(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String): Call<ConfigurationResponse>
    }

    interface IsEmailAvailableService {
        @POST("rest/{store_code}/V1/customers/isEmailAvailable")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun isEmailAvailableApi(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String, @Body request: LoginDataClass.IsEmailAvailableRequest?): Call<Boolean>
    }

    interface SocialLoginService {
        @POST("rest/{store_code}/V1/customer/sociallogin/token")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun socialLogin(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode : String, @Body request: LoginDataClass.SocialLoginRequest?): Call<String>
    }

    interface ForgotPasswordService {
        @PUT("rest/{store_code}/V1/customers/password")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun forgotPasswordApi(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String, @Body requets: ForgotPasswordDataClass.ForgotPasswordRequest): Call<Boolean>
    }

    interface PromotionService{
        @GET("rest/{store_code}/V1/homepromotions/list")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getPromotions(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode : String): Call<List<PromotionsResponseDataClass>>

    }

    interface CategoryService {
        @GET("rest/{store_code}/V1/categories")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getCategories(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode : String): Call<CategoryResponseDataClass>

        @GET("rest/{store_code}/V1/categories/{category_id}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getCategoryData(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode : String, @Path("category_id") categoryId : String): Call<CategoryDataResponse>

        @GET("rest/{store_code}/V1/categories/list")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getAllCategoryData(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode : String, @QueryMap queryMap : HashMap<String,String>?): Call<AllCategoryDataResponse>


    }
}
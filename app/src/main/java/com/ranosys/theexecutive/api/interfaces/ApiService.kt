package com.ranosys.theexecutive.api.interfaces

import com.ranosys.theexecutive.api.ApiConstants
import com.ranosys.theexecutive.modules.category.AllCategoryDataResponse
import com.ranosys.theexecutive.modules.category.CategoryDataResponse
import com.ranosys.theexecutive.modules.category.CategoryResponseDataClass
import com.ranosys.theexecutive.modules.category.PromotionsResponseDataClass
import com.ranosys.theexecutive.modules.forgotPassword.ForgotPasswordDataClass
import com.ranosys.theexecutive.modules.login.LoginDataClass
import com.ranosys.theexecutive.modules.myAccount.MyAccountDataClass
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ChildProductsResponse
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ProductDetailResponse
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ProductOptionsResponse
import com.ranosys.theexecutive.modules.productDetail.dataClassess.StaticPagesUrlResponse
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass
import com.ranosys.theexecutive.modules.register.RegisterDataClass
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

    interface RegistrationService {
        @POST("rest/{store_code}/V1/customers")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun registration(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String, @Body registrationRequest: RegisterDataClass.RegisterRequest?): Call<RegisterDataClass.RegistrationResponse>
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
        @GET("rest/{store_code}/V1/categoriescustom")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getCategories(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode : String): Call<CategoryResponseDataClass>

        @GET("rest/{store_code}/V1/categories/{categoryId}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getCategoryData(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode : String, @Path("categoryId") categoryId : String): Call<CategoryDataResponse>

        @GET("rest/{store_code}/V1/categories/list")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getAllCategoryData(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode : String, @QueryMap queryMap : HashMap<String,String>?): Call<AllCategoryDataResponse>


    }

    interface CountryListService {
        @GET("rest/{store_code}/V1/directory/countries")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun countryList(@Path("store_code") storeCode:String): Call<List<RegisterDataClass.Country>>
    }

    interface CityListService {
        @GET("rest/{store_code}/V1/custom/cities/{state_code}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun cityList(@Path("store_code") storeCode:String, @Path("state_code") stateCode:String): Call<List<RegisterDataClass.City>>
    }

    interface NewsLetterSubscription {
        @POST("rest/{store_code}/V1/newsletter/subscribe")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun newsLetterSuscribe(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String, @Body request: MyAccountDataClass.NewsletterSubscriptionRequest?): Call<String>
    }

    interface SortOptionService {
        @GET("rest/{store_code}/V1/attributes/sort")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getSortOptions(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String): Call<java.util.ArrayList<ProductListingDataClass.SortOptionResponse>>
    }

    interface FilterOptionService {
        @GET("rest/{store_code}/V1/layerednavigation/filters")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getFilterOptions(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String, @Query("id")categoryId: Int): Call<ProductListingDataClass.FilterOptionsResponse>
    }

    interface ProductListingService {
        @GET("rest/{store_code}/V1/{list_from}/")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getProductList(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String, @Path("list_from", encoded = true) listFrom:String, @QueryMap requestMap: Map<String, String>): Call<ProductListingDataClass.ProductListingResponse>
    }

    interface ProductDetailService{
        @GET("rest/{store_code}/V1/products/{product_sku}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getProductDetail(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String?, @Path("product_sku") productSku : String?): Call<ProductDetailResponse>

        @GET("rest/{store_code}/V1/configurable-products/{product_sku}/children")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getProductChildren(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String?, @Path("product_sku") productSku : String?): Call<ChildProductsResponse>

        @GET("rest/{store_code}/V1/products/attributes/{attribute_id}/options")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getProductOptions(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String?, @Path("attribute_id") attributeId : String?): Call<List<ProductOptionsResponse>>

        @GET("rest/{store_code}/V1/productcontent/url")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getStaticPagesUrl(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String?): Call<StaticPagesUrlResponse>


        @POST("rest/{store_code}/V1/wishlist/mine/addproduct")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun addToWishList(@Header(ApiConstants.AUTHORIZATION_KEY) userToken:String?, @Path("store_code") storeCode:String, @Body request: Map<String, Int>): Call<String>

    }
}
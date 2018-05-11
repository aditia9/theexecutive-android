package com.ranosys.theexecutive.api.interfaces

import com.google.gson.JsonObject
import com.ranosys.theexecutive.api.ApiConstants
import com.ranosys.theexecutive.modules.category.AllCategoryDataResponse
import com.ranosys.theexecutive.modules.category.CategoryDataResponse
import com.ranosys.theexecutive.modules.category.CategoryResponseDataClass
import com.ranosys.theexecutive.modules.category.PromotionsResponseDataClass
import com.ranosys.theexecutive.modules.changePassword.ChangePasswordDataClass
import com.ranosys.theexecutive.modules.forgotPassword.ForgotPasswordDataClass
import com.ranosys.theexecutive.modules.login.LoginDataClass
import com.ranosys.theexecutive.modules.myAccount.MyAccountDataClass
import com.ranosys.theexecutive.modules.productDetail.dataClassess.*
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass
import com.ranosys.theexecutive.modules.register.RegisterDataClass
import com.ranosys.theexecutive.modules.shoppingBag.ShoppingBagQtyUpdateRequest
import com.ranosys.theexecutive.modules.shoppingBag.ShoppingBagResponse
import com.ranosys.theexecutive.modules.splash.ConfigurationResponse
import com.ranosys.theexecutive.modules.splash.StoreResponse
import com.ranosys.theexecutive.modules.wishlist.WishlistResponse
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


    interface ChangePasswordService {
        @PUT("rest/{store_code}/V1/customers/me/password")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun changePasswordApi(@Header(ApiConstants.AUTHORIZATION_KEY) userToken:String?, @Path("store_code") storeCode:String, @Body requets: ChangePasswordDataClass): Call<Boolean>
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
        @GET("rest/{store_code}/V1/attributes/sort/")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getSortOptions(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String, @Query("type")type: String): Call<java.util.ArrayList<ProductListingDataClass.SortOptionResponse>>
    }

    interface FilterOptionService {
        @GET("rest/{store_code}/V1/layerednavigation/filters")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getFilterOptions(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String, @Query("id")categoryId: Int): Call<ProductListingDataClass.FilterOptionsResponse>

        @GET("rest/{store_code}/V1/layerednavigation/searchfilters/")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getSearchFilters(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String, @Query("q")searchQuery: String): Call<ProductListingDataClass.FilterOptionsResponse>
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
        fun getProductDetail(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String?, @Path("product_sku") productSku : String?): Call<ProductListingDataClass.Item>

        @GET("rest/{store_code}/V1/configurable-products/{product_sku}/children")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getProductChildren(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String?, @Path("product_sku") productSku : String?): Call<List<ChildProductsResponse>>

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
                ApiConstants.CACHE_CONTROL)
        fun addToWishList(@Header(ApiConstants.AUTHORIZATION_KEY) userToken:String?, @Path("store_code") storeCode:String, @Body request: JsonObject): Call<String>

    }

    interface CartService {

        @POST("rest/{store_code}/V1/guest-carts")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun createGuestCart(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken:String?, @Path("store_code") storeCode:String): Call<String>

        @POST("rest/{store_code}/V1/carts/mine")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun createUserCart(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode:String): Call<String>

        @GET("rest/{store_code}/V1/cart/mine/count")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun cartCountUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode:String): Call<String>

        @GET("rest/{store_code}/V1/guest-carts/{cart_id}/items/count")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun cartCountGuest(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode:String, @Path("cart_id") cartId:String): Call<String>

        @POST("rest/{store_code}/V1/carts/mine/items")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun addTOCartUser(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode:String, @Body request: AddToCartRequest): Call<AddToCartResponse>

        @POST("rest/{store_code}/V1/guest-carts/{cart_id}/items")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun addTOCartGuest(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode:String, @Path("cart_id") cartId: String,  @Body request: AddToCartRequest): Call<AddToCartResponse>


        @GET("rest/{store_code}/V1/carts/mine/items")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getCartOfUser(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode:String): Call<List<ShoppingBagResponse>>

        @GET("rest/{store_code}/V1/guest-carts/{cart_id}/items")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getCartOfGuest(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode:String, @Path("cart_id") cartId: String): Call<List<ShoppingBagResponse>>


        @DELETE("rest/{store_code}/V1/carts/mine/guest-carts/{cartId}/items/{itemId}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun deleteItemFromShoppingBagGuestUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("cartId")  cartId: String?, @Path("itemId") itemId: Int?): Call<String>

        @DELETE("rest/{store_code}/V1/carts/mine/items/{itemId}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun deleteItemFromShoppingBagUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("itemId") itemId: Int?): Call<String>

        @PUT("rest/{store_code}/V1/wishlist/mine/movefromcart/{itemId}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun moveItemFromCart(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("itemId") itemId: Int?): Call<String>

        @PUT("rest/{store_code}/V1/carts/mine/items/{itemId}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun updateItemFromShoppingBagUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("itemId") itemId: Int?, @Body request: ShoppingBagQtyUpdateRequest): Call<ShoppingBagResponse>


        @PUT("rest/{store_code}/V1/carts/mine/guest-carts/{cartId}/items/{itemId}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun updateItemFromShoppingBagGuestUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("cartId")  cartId: String?, @Path("itemId") itemId: Int?, @Body request: ShoppingBagQtyUpdateRequest): Call<ShoppingBagResponse>


        @PUT("rest/{store_code}/V1/carts/mine/coupons/{couponCode}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun applyCouponCodeForUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("couponCode") couponCode: String?): Call<String>


        @PUT("rest/{store_code}/V1/guest-carts/{cartId}/coupons/{couponCode}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun applyCouponCodeForGuestUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("couponCode")  cartId: String?,  @Path("couponCode") couponCode: String?): Call<String>

    }

    interface MyAccount {
        @GET("rest/{store_code}/V1/customers/me")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getUserInfo(@Header(ApiConstants.AUTHORIZATION_KEY) userToken:String?, @Path("store_code") storeCode:String): Call<MyAccountDataClass.UserInfoResponse>

        @PUT("rest/{store_code}/V1/customers/me")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun updateUserInfo(@Header(ApiConstants.AUTHORIZATION_KEY) userToken:String?, @Path("store_code") storeCode:String, @Body request: MyAccountDataClass.UpdateInfoRequest): Call<MyAccountDataClass.UserInfoResponse>
    }

    interface WishlistService {

        @GET("rest/{store_code}/V1/wishlist/mine/info/")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getWishlist(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String): Call<WishlistResponse>

        @DELETE("rest/{store_code}/V1/wishlist/mine/item/{item_id}/delete")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun deleteWishlistItem(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("item_id") itemId: Int?): Call<String>

        @POST("rest/{store_code}/V1/wishlist/mine/item/{item_id}/addtocart")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun addToBagWishlistItem(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("item_id") itemId: Int?): Call<String>

    }
}
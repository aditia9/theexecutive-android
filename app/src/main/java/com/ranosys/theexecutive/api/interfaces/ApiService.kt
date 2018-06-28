package com.ranosys.theexecutive.api.interfaces

import com.google.gson.JsonObject
import com.ranosys.theexecutive.api.ApiConstants
import com.ranosys.theexecutive.modules.bankTransfer.Recipients
import com.ranosys.theexecutive.modules.bankTransfer.TransferMethodsDataClass
import com.ranosys.theexecutive.modules.category.AllCategoryDataResponse
import com.ranosys.theexecutive.modules.category.CategoryDataResponse
import com.ranosys.theexecutive.modules.category.CategoryResponseDataClass
import com.ranosys.theexecutive.modules.category.PromotionsResponseDataClass
import com.ranosys.theexecutive.modules.changePassword.ChangePasswordDataClass
import com.ranosys.theexecutive.modules.checkout.CheckoutDataClass
import com.ranosys.theexecutive.modules.forgotPassword.ForgotPasswordDataClass
import com.ranosys.theexecutive.modules.login.LoginDataClass
import com.ranosys.theexecutive.modules.myAccount.MyAccountDataClass
import com.ranosys.theexecutive.modules.notification.dataclasses.DeviceRegisterRequest
import com.ranosys.theexecutive.modules.notification.dataclasses.NotificationChangeStatusRequest
import com.ranosys.theexecutive.modules.notification.dataclasses.NotificationListResponse
import com.ranosys.theexecutive.modules.order.orderDetail.OrderDetailResponse
import com.ranosys.theexecutive.modules.order.orderList.OrderListResponse
import com.ranosys.theexecutive.modules.order.orderReturn.OrderReturnRequest
import com.ranosys.theexecutive.modules.productDetail.dataClassess.*
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass
import com.ranosys.theexecutive.modules.register.RegisterDataClass
import com.ranosys.theexecutive.modules.shoppingBag.ShoppingBagQtyUpdateRequest
import com.ranosys.theexecutive.modules.shoppingBag.ShoppingBagResponse
import com.ranosys.theexecutive.modules.shoppingBag.TotalResponse
import com.ranosys.theexecutive.modules.splash.ConfigurationResponse
import com.ranosys.theexecutive.modules.splash.StoreResponse
import com.ranosys.theexecutive.modules.wishlist.MoveToBagRequest
import com.ranosys.theexecutive.modules.wishlist.WishlistResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        fun getLoginData(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Body loginRequest: LoginDataClass.LoginRequest?,  @QueryMap queryMap: HashMap<String, String>?,  @Query("___store") addOnStoreCode : String): Call<String>
    }

    interface RegistrationService {
        @POST("rest/{store_code}/V1/customers")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun registration(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Body registrationRequest: RegisterDataClass.RegisterRequest?, @Query("___store") addOnStoreCode : String): Call<RegisterDataClass.RegistrationResponse>
    }

    interface StoresService {
        @GET("rest/all/V1/store/storeViews")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getStores(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?): Call<ArrayList<StoreResponse>>
    }

    interface ConfigurationService {
        @GET("rest/{store_code}/V1/mobileappversionapi/configuration/android")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getConfiguration(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<ConfigurationResponse>
    }

    interface IsEmailAvailableService {
        @POST("rest/{store_code}/V1/customers/isEmailAvailable")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun isEmailAvailableApi(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Body request: LoginDataClass.IsEmailAvailableRequest?, @Query("___store") addOnStoreCode : String): Call<Boolean>
    }

    interface SocialLoginService {
        @POST("rest/{store_code}/V1/customer/sociallogin/token")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun socialLogin(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Body request: LoginDataClass.SocialLoginRequest?, @QueryMap queryMap: HashMap<String, String>, @Query("___store") addOnStoreCode : String): Call<String>
    }

    interface ForgotPasswordService {
        @PUT("rest/{store_code}/V1/customers/password")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun forgotPasswordApi(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Body requets: ForgotPasswordDataClass.ForgotPasswordRequest, @Query("___store") addOnStoreCode : String): Call<Boolean>
    }


    interface ChangePasswordService {
        @PUT("rest/{store_code}/V1/customers/me/password")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun changePasswordApi(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Body requets: ChangePasswordDataClass, @Query("___store") addOnStoreCode : String): Call<Boolean>
    }

    interface PromotionService {
        @GET("rest/{store_code}/V1/homepromotions/list")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getPromotions(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<List<PromotionsResponseDataClass>>

    }

    interface CategoryService {
        @GET("rest/{store_code}/V1/categoriescustom")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getCategories(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<CategoryResponseDataClass>

        @GET("rest/{store_code}/V1/categories/{categoryId}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getCategoryData(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Path("categoryId") categoryId: String, @Query("___store") addOnStoreCode : String): Call<CategoryDataResponse>

        @GET("rest/{store_code}/V1/categories/list")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getAllCategoryData(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @QueryMap queryMap: HashMap<String, String>?, @Query("___store") addOnStoreCode : String): Call<AllCategoryDataResponse>


    }

    interface CountryListService {
        @GET("rest/{store_code}/V1/directory/countries")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun countryList(@Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<List<RegisterDataClass.Country>>
    }

    interface CityListService {
        @GET("rest/{store_code}/V1/custom/cities/{state_code}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun cityList(@Path("store_code") storeCode: String, @Path("state_code") stateCode: String, @Query("___store") addOnStoreCode : String): Call<List<RegisterDataClass.City>>
    }

    interface NewsLetterSubscription {
        @POST("rest/{store_code}/V1/newsletter/subscribe")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun newsLetterSuscribe(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Body request: MyAccountDataClass.NewsletterSubscriptionRequest?, @Query("___store") addOnStoreCode : String): Call<String>
    }

    interface SortOptionService {
        @GET("rest/{store_code}/V1/attributes/sort/")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getSortOptions(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Query("type") type: String, @Query("___store") addOnStoreCode : String): Call<java.util.ArrayList<ProductListingDataClass.SortOptionResponse>>
    }

    interface FilterOptionService {
        @GET("rest/{store_code}/V1/layerednavigation/filters")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getFilterOptions(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Query("id") categoryId: Int, @Query("___store") addOnStoreCode : String): Call<ProductListingDataClass.FilterOptionsResponse>

        @GET("rest/{store_code}/V1/layerednavigation/searchfilters/")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getSearchFilters(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Query("q") searchQuery: String, @Query("___store") addOnStoreCode : String): Call<ProductListingDataClass.FilterOptionsResponse>
    }

    interface ProductListingService {
        @GET("rest/{store_code}/V1/{list_from}/")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getProductList(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Path("list_from", encoded = true) listFrom: String, @QueryMap requestMap: Map<String, String>, @Query("___store") addOnStoreCode : String): Call<ProductListingDataClass.ProductListingResponse>
    }

    interface ProductDetailService {
        @GET("rest/{store_code}/V1/products/{product_sku}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getProductDetail(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String?, @Path("product_sku") productSku: String?, @Query("___store") addOnStoreCode : String): Call<ProductListingDataClass.Item>

        @GET("rest/{store_code}/V1/configurable-products/{product_sku}/children")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getProductChildren(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String?, @Path("product_sku") productSku: String?, @Query("___store") addOnStoreCode : String): Call<List<ChildProductsResponse>>

        @GET("rest/{store_code}/V1/products/attributes/{attribute_id}/options")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getProductOptions(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String?, @Path("attribute_id") attributeId: String?, @Query("___store") addOnStoreCode : String): Call<List<ProductOptionsResponse>>

        @GET("rest/{store_code}/V1/productcontent/url")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getStaticPagesUrl(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String?, @Query("___store") addOnStoreCode : String): Call<StaticPagesUrlResponse>


        @POST("rest/{store_code}/V1/wishlist/mine/addproduct")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.CACHE_CONTROL)
        fun addToWishList(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Body request: JsonObject, @Query("___store") addOnStoreCode : String): Call<String>

    }

    interface CartService {

        @POST("rest/{store_code}/V1/guest-carts")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun createGuestCart(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<String>

        @POST("rest/{store_code}/V1/carts/mine")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun createUserCart(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<String>

        @GET("rest/{store_code}/V1/cart/mine/count")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun cartCountUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<String>

        @GET("rest/{store_code}/V1/guest-carts/{cart_id}/items/count")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun cartCountGuest(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Path("cart_id") cartId: String, @Query("___store") addOnStoreCode : String): Call<String>

        @POST("rest/{store_code}/V1/carts/mine/items")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun addTOCartUser(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Body request: AddToCartRequest, @Query("___store") addOnStoreCode : String): Call<AddToCartResponse>

        @POST("rest/{store_code}/V1/guest-carts/{cart_id}/items?from_mobile=1")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun addTOCartGuest(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode:String, @Path("cart_id") cartId: String,  @Body request: AddToCartRequest, @Query("___store") addOnStoreCode : String): Call<AddToCartResponse>

        @GET("rest/{store_code}/V1/cart/mine/mergecart/{guest_cart_id}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun mergeCart(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode:String, @Path("guest_cart_id") guestCartId:String, @Query("___store") addOnStoreCode : String): Call<String>

        @GET("rest/{store_code}/V1/carts/mine/items?from_mobile=1")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getCartOfUser(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<List<ShoppingBagResponse>>

        @GET("rest/{store_code}/V1/guest-carts/{cart_id}/items?from_mobile=1")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getCartOfGuest(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode: String, @Path("cart_id") cartId: String, @Query("___store") addOnStoreCode : String): Call<List<ShoppingBagResponse>>


        @DELETE("rest/{store_code}/V1/guest-carts/{cartId}/items/{itemId}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun deleteItemFromShoppingBagGuestUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("cartId") cartId: String?, @Path("itemId") itemId: Int?, @Query("___store") addOnStoreCode : String): Call<String>

        @DELETE("rest/{store_code}/V1/carts/mine/items/{itemId}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun deleteItemFromShoppingBagUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("itemId") itemId: Int?, @Query("___store") addOnStoreCode : String): Call<String>

        @PUT("rest/{store_code}/V1/wishlist/mine/movefromcart/{itemId}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun moveItemFromCart(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("itemId") itemId: Int?, @Query("___store") addOnStoreCode : String): Call<String>

        @PUT("rest/{store_code}/V1/carts/mine/items/{itemId}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun updateItemFromShoppingBagUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("itemId") itemId: Int?, @Body request: ShoppingBagQtyUpdateRequest, @Query("___store") addOnStoreCode : String): Call<ShoppingBagQtyUpdateRequest>


        @PUT("rest/{store_code}/V1/guest-carts/{cartId}/items/{itemId}")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun updateItemFromShoppingBagGuestUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("cartId") cartId: String?, @Path("itemId") itemId: Int?, @Body request: ShoppingBagQtyUpdateRequest, @Query("___store") addOnStoreCode : String): Call<ShoppingBagQtyUpdateRequest>


        @PUT("rest/{store_code}/V1/carts/mine/coupons/{couponCode}?from_mobile=1")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun applyCouponCodeForUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("couponCode") couponCode: String?, @Query("___store") addOnStoreCode : String): Call<String>


        @PUT("rest/{store_code}/V1/guest-carts/{cartId}/coupons/{couponCode}?from_mobile=1")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun applyCouponCodeForGuestUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("cartId") cartId: String?, @Path("couponCode") couponCode: String?, @Query("___store") addOnStoreCode : String): Call<String>


        @GET("rest/{store_code}/V1/carts/mine/coupons")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getCouponCodeForUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<Any>


        @GET("rest/{store_code}/V1/guest-carts/{cartId}/coupons")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getCouponCodeForGuestUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("cartId") cartId: String?, @Query("___store") addOnStoreCode : String): Call<Any>


        @DELETE("rest/{store_code}/V1/carts/mine/coupons")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun deleteCouponCodeForUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<String>


        @DELETE("rest/{store_code}/V1/guest-carts/{cartId}/coupons")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun deleteCouponCodeForGuestUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("cartId") cartId: String?, @Query("___store") addOnStoreCode : String): Call<String>

        @GET("rest/{store_code}/V1/carts/mine/totals?from_mobile=1")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getTotalForUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<TotalResponse>


        @GET("rest/{store_code}/V1/guest-carts/{cartId}/totals?from_mobile=1")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getTotalForGuestUser(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("cartId") cartId: String?, @Query("___store") addOnStoreCode : String): Call<TotalResponse>


    }

    interface MyOrdersService {
        @GET("rest/{store_code}/V1/orders/mine")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getMyOrderList(@Header(ApiConstants.AUTHORIZATION_KEY) userToken:String?, @Path("store_code") storeCode:String, @Query("___store") addOnStoreCode : String): Call<List<OrderListResponse>>

        @GET("rest/{store_code}/V1/order/{order_id}/mine")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getOrderDetail(@Header(ApiConstants.AUTHORIZATION_KEY) userToken:String?, @Path("store_code") storeCode:String, @Path("order_id") orderId:String ,@QueryMap queryMap: HashMap<String, String>?, @Query("___store") addOnStoreCode : String): Call<OrderDetailResponse>


        @POST("rest/{store_code}/V1/rma/productreturn")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun returnProduct(@Header(ApiConstants.AUTHORIZATION_KEY) adminToken: String?, @Path("store_code") storeCode:String,  @Body request: OrderReturnRequest, @Query("___store") addOnStoreCode : String): Call<String>

    }

    interface MyAccountService {
        @GET("rest/{store_code}/V1/customers/me")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getUserInfo(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<MyAccountDataClass.UserInfoResponse>

        @PUT("rest/{store_code}/V1/customers/me")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun updateUserInfo(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Body request: MyAccountDataClass.UpdateInfoRequest, @Query("___store") addOnStoreCode : String): Call<MyAccountDataClass.UserInfoResponse>
    }

    interface WishlistService {

        @GET("rest/{store_code}/V1/wishlist/mine/info/")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getWishlist(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<WishlistResponse>

        @DELETE("rest/{store_code}/V1/wishlist/mine/item/{item_id}/delete")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun deleteWishlistItem(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("item_id") itemId: Int?, @Query("___store") addOnStoreCode : String): Call<String>

        @POST("rest/{store_code}/V1/wishlist/mine/item/{item_id}/addtocart")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun addToBagWishlistItem(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("item_id") itemId: String, @Body request: MoveToBagRequest , @Query("___store") addOnStoreCode : String): Call<String>

    }

    interface NotificationService {

        @POST("rest/{store_code}/V1/notification/mine/list")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getNotificationList(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Body request: DeviceRegisterRequest, @Query("___store") addOnStoreCode : String): Call<List<NotificationListResponse>>

        @POST("rest/{store_code}/V1/notification/changestatus")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun changeNotificationStatus(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Body request: NotificationChangeStatusRequest, @Query("___store") addOnStoreCode : String): Call<Boolean>


        @POST("rest/{store_code}/V1/notification/registerdevice")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun registerDevice(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Body request: DeviceRegisterRequest, @Query("___store") addOnStoreCode : String): Call<Boolean>


        @POST("rest/{store_code}/V1/notification/mine/logout")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun logoutNotification(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Body request: NotificationChangeStatusRequest, @Query("___store") addOnStoreCode : String): Call<Boolean>

    }


    interface BankTransfer{

        @GET("rest/{store_code}/V1/banktransfer/transfermethods")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getBankTransferMethod(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<List<TransferMethodsDataClass>>


        @GET("rest/{store_code}/V1/banktransfer/recipients")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getRecipient(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<List<Recipients>>

        @Multipart
        @POST("rest/{store_code}/V1/banktransfer/submit")
        @Headers(ApiConstants.CACHE_CONTROL)
       fun submitBankTransfer(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Part file: MultipartBody.Part, @Part("name") name: RequestBody, @Part("email_submitter") email_submitter: RequestBody, @Part("orderid") orderid: RequestBody,@Part("bank_name") bank_name: RequestBody,@Part("holder_account") holder_account: RequestBody,@Part("amount") amount: RequestBody,@Part("recipient") recipient: RequestBody, @Part("method") method: RequestBody,@Part("date") date: RequestBody, @Query("___store") addOnStoreCode : String): Call<String>
    }

    interface CheckoutService {

        @GET("rest/{store_code}/V1/carts/mine/totals?fields=total_segments")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getTotalAmounts(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<CheckoutDataClass.Totals>

        @POST("rest/{store_code}/V1/carts/mine/payment-information")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun placeOrder(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Body request: CheckoutDataClass.PlaceOrderRequest, @Query("___store") addOnStoreCode : String): Call<String>

        @POST("rest/{store_code}/V1/carts/mine/estimate-shipping-methods-by-address-id")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getShippingMethods(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Body request: CheckoutDataClass.GetShippingMethodsRequest, @Query("___store") addOnStoreCode : String): Call<List<CheckoutDataClass.GetShippingMethodsResponse>>

        @POST("rest/{store_code}/V1/carts/mine/shipping-information?fields=payment_methods,totals[total_segments]")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getPaymentMethods(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Body request: CheckoutDataClass.GetPaymentMethodsRequest, @Query("___store") addOnStoreCode : String): Call<CheckoutDataClass.PaymentMethodResponse>

        @GET("rest/{store_code}/V1/carts/mine?fields=customer,extension_attributes")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getUserInfoNSelectedShipping(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Query("___store") addOnStoreCode : String): Call<CheckoutDataClass.UserInfoNselectedShippingResponse>

        @GET("rest/{store_code}/V1/order/mine/{order_id}/information")
        @Headers(ApiConstants.CONTENT_TYPE,
                ApiConstants.X_REQUESTED_WITH,
                ApiConstants.CACHE_CONTROL)
        fun getOrderStatus(@Header(ApiConstants.AUTHORIZATION_KEY) userToken: String?, @Path("store_code") storeCode: String, @Path("order_id") orderId: String, @Query("___store") addOnStoreCode : String): Call<CheckoutDataClass.OrderStatusResponse>
    }


}
package com.ranosys.theexecutive.api

import android.text.TextUtils
import com.google.gson.JsonObject
import com.ranosys.theexecutive.BuildConfig
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.api.interfaces.ApiService
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
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


/**
 * @Details Repository class for api calling
 * @Author Ranosys Technologies
 * @Date 23,Feb,2018
 */
object AppRepository {

    private fun parseError(response: Response<Any>?, callBack: ApiCallback<Any>) {
        try {
            val jobError = JSONObject(response?.errorBody()?.string())
            val errorBody = jobError.getString(Constants.MESSAGE)
            callBack.onError(errorBody)

        } catch (e: JSONException) {
            callBack.onException(Throwable(Constants.ERROR))
            if (BuildConfig.DEBUG)
                e.printStackTrace()
        } catch (e: IOException) {
            callBack.onException(Throwable(Constants.ERROR))
            if (BuildConfig.DEBUG)
                e.printStackTrace()
        }
    }

    fun getStores(callBack: ApiCallback<ArrayList<StoreResponse>>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val callPost = retrofit?.create<ApiService.StoresService>(ApiService.StoresService::class.java)?.getStores(ApiConstants.BEARER + adminToken)

        callPost?.enqueue(object : Callback<ArrayList<StoreResponse>> {
            override fun onResponse(call: Call<ArrayList<StoreResponse>>?, response: Response<ArrayList<StoreResponse>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<ArrayList<StoreResponse>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getConfiguration(callBack: ApiCallback<ConfigurationResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode = Constants.ALL
        val callGet = retrofit?.create<ApiService.ConfigurationService>(ApiService.ConfigurationService::class.java)?.getConfiguration(ApiConstants.BEARER + adminToken, storeCode)

        callGet?.enqueue(object : Callback<ConfigurationResponse> {
            override fun onResponse(call: Call<ConfigurationResponse>?, response: Response<ConfigurationResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<ConfigurationResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }


    fun login(loginRequest: LoginDataClass.LoginRequest?, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.LoginService>(ApiService.LoginService::class.java)?.getLoginData(ApiConstants.BEARER + adminToken, storeCode, loginRequest)

        callPost?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    if (response.code() == Constants.ERROR_CODE_401) {
                        val errorBody = Constants.INVALID_CREDENTIALS
                        callBack.onError(errorBody)
                    } else {
                        parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                    }
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getPromotions(callBack: ApiCallback<List<PromotionsResponseDataClass>>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.PromotionService>(ApiService.PromotionService::class.java)?.getPromotions(ApiConstants.BEARER + adminToken, storeCode)

        callGet?.enqueue(object : Callback<List<PromotionsResponseDataClass>> {
            override fun onResponse(call: Call<List<PromotionsResponseDataClass>>?, response: Response<List<PromotionsResponseDataClass>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<List<PromotionsResponseDataClass>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getCategories(callBack: ApiCallback<CategoryResponseDataClass>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.CategoryService>(ApiService.CategoryService::class.java)?.getCategories(ApiConstants.BEARER + adminToken, storeCode)

        callGet?.enqueue(object : Callback<CategoryResponseDataClass> {
            override fun onResponse(call: Call<CategoryResponseDataClass>?, response: Response<CategoryResponseDataClass>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<CategoryResponseDataClass>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun isEmailAvailable(request: LoginDataClass.IsEmailAvailableRequest?, callBack: ApiCallback<Boolean>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.IsEmailAvailableService>(ApiService.IsEmailAvailableService::class.java)?.isEmailAvailableApi(ApiConstants.BEARER + adminToken, storeCode = storeCode, request = request)

        callPost?.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())

                }

            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callBack.onError(Constants.ERROR)

            }
        })
    }

    fun socialLogin(request: LoginDataClass.SocialLoginRequest?, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.SocialLoginService>(ApiService.SocialLoginService::class.java)?.socialLogin(ApiConstants.BEARER + adminToken, storeCode, request = request)

        callPost?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())

                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)

            }
        })
    }

    fun forgotPassword(request: ForgotPasswordDataClass.ForgotPasswordRequest, callBack: ApiCallback<Boolean>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPut = retrofit?.create<ApiService.ForgotPasswordService>(ApiService.ForgotPasswordService::class.java)?.forgotPasswordApi(ApiConstants.BEARER + adminToken, storeCode, request)

        callPut?.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                if (!response!!.isSuccessful) {
                    if (response.code() == Constants.ERROR_CODE_404) {
                        val errorBody = Constants.NO_USER_EXIST
                        callBack.onError(errorBody)
                    } else {

                        parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                    }

                } else {
                    callBack.onSuccess(response.body())

                }

            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callBack.onError(Constants.ERROR)

            }
        })
    }


    fun changePassword(request: ChangePasswordDataClass, callBack: ApiCallback<Boolean>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPut = retrofit?.create<ApiService.ChangePasswordService>(ApiService.ChangePasswordService::class.java)?.changePasswordApi(ApiConstants.BEARER + userToken, storeCode, request)

        callPut?.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                if (!response!!.isSuccessful) {
                    if (response.code() == Constants.ERROR_CODE_404) {
                        val errorBody = Constants.NO_USER_EXIST
                        callBack.onError(errorBody)
                    } else {

                        parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                    }

                } else {
                    callBack.onSuccess(response.body())

                }

            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callBack.onError(Constants.ERROR)

            }
        })
    }

    fun getCountryList(callBack: ApiCallback<List<RegisterDataClass.Country>>) {
        val retrofit = ApiClient.retrofit
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.CountryListService>(ApiService.CountryListService::class.java)?.countryList(storeCode)

        callGet?.enqueue(object : Callback<List<RegisterDataClass.Country>> {
            override fun onResponse(call: Call<List<RegisterDataClass.Country>>?, response: Response<List<RegisterDataClass.Country>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<List<RegisterDataClass.Country>>, t: Throwable) {
                callBack.onError(Constants.ERROR)

            }
        })
    }

    fun getCityList(stateCode: String, callBack: ApiCallback<List<RegisterDataClass.City>>) {
        val retrofit = ApiClient.retrofit
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.CityListService>(ApiService.CityListService::class.java)?.cityList(storeCode, stateCode)

        callGet?.enqueue(object : Callback<List<RegisterDataClass.City>> {
            override fun onResponse(call: Call<List<RegisterDataClass.City>>?, response: Response<List<RegisterDataClass.City>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<List<RegisterDataClass.City>>, t: Throwable) {
                callBack.onError(Constants.ERROR)

            }
        })
    }

    fun registrationApi(registrationRequest: RegisterDataClass.RegisterRequest, callBack: ApiCallback<RegisterDataClass.RegistrationResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.RegistrationService>(ApiService.RegistrationService::class.java)?.registration(ApiConstants.BEARER + adminToken, storeCode, registrationRequest)

        callPost?.enqueue(object : Callback<RegisterDataClass.RegistrationResponse> {
            override fun onResponse(call: Call<RegisterDataClass.RegistrationResponse>?, response: Response<RegisterDataClass.RegistrationResponse>?) {
                if (!response!!.isSuccessful) {
                    if (response.code() == Constants.ERROR_CODE_400) {
                        val errorBody = Constants.ALREADY_REGISTERED
                        callBack.onError(errorBody)

                    } else {
                        parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                    }
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<RegisterDataClass.RegistrationResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun subscribeNewsletterApi(request: MyAccountDataClass.NewsletterSubscriptionRequest, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.NewsLetterSubscription>(ApiService.NewsLetterSubscription::class.java)?.newsLetterSuscribe(ApiConstants.BEARER + adminToken, storeCode, request)

        callPost?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    if (response.code() == Constants.ERROR_CODE_404) {
                        val errorBody = Constants.NO_USER_EXIST
                        callBack.onError(errorBody)
                    } else {

                        parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                    }
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun sortOptionApi(type: String, callBack: ApiCallback<java.util.ArrayList<ProductListingDataClass.SortOptionResponse>>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.SortOptionService>(ApiService.SortOptionService::class.java)?.getSortOptions(ApiConstants.BEARER + adminToken, storeCode, type)

        callGet?.enqueue(object : Callback<java.util.ArrayList<ProductListingDataClass.SortOptionResponse>> {
            override fun onResponse(call: Call<java.util.ArrayList<ProductListingDataClass.SortOptionResponse>>?, response: Response<java.util.ArrayList<ProductListingDataClass.SortOptionResponse>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<java.util.ArrayList<ProductListingDataClass.SortOptionResponse>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun filterOptionApi(categoryId: Int, callBack: ApiCallback<ProductListingDataClass.FilterOptionsResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.FilterOptionService>(ApiService.FilterOptionService::class.java)?.getFilterOptions(ApiConstants.BEARER + adminToken, storeCode, categoryId = categoryId)

        callGet?.enqueue(object : Callback<ProductListingDataClass.FilterOptionsResponse> {
            override fun onResponse(call: Call<ProductListingDataClass.FilterOptionsResponse>?, response: Response<ProductListingDataClass.FilterOptionsResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<ProductListingDataClass.FilterOptionsResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun searchFilterOptionApi(query: String, callBack: ApiCallback<ProductListingDataClass.FilterOptionsResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.FilterOptionService>(ApiService.FilterOptionService::class.java)?.getSearchFilters(ApiConstants.BEARER + adminToken, storeCode, searchQuery = query)

        callGet?.enqueue(object : Callback<ProductListingDataClass.FilterOptionsResponse> {
            override fun onResponse(call: Call<ProductListingDataClass.FilterOptionsResponse>?, response: Response<ProductListingDataClass.FilterOptionsResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<ProductListingDataClass.FilterOptionsResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getProductList(requestMap: Map<String, String>, fromSearch: Boolean, callBack: ApiCallback<ProductListingDataClass.ProductListingResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val listFrom = if (fromSearch) "catalogsearch/list" else "productslist"
        val callGet = retrofit?.create<ApiService.ProductListingService>(ApiService.ProductListingService::class.java)?.getProductList(ApiConstants.BEARER + adminToken, storeCode, listFrom, requestMap)

        callGet?.enqueue(object : Callback<ProductListingDataClass.ProductListingResponse> {
            override fun onResponse(call: Call<ProductListingDataClass.ProductListingResponse>?, response: Response<ProductListingDataClass.ProductListingResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<ProductListingDataClass.ProductListingResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getProductDetail(productSku: String?, callBack: ApiCallback<ProductListingDataClass.Item>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.ProductDetailService>(ApiService.ProductDetailService::class.java)?.getProductDetail(ApiConstants.BEARER + adminToken, storeCode, productSku)

        callGet?.enqueue(object : Callback<ProductListingDataClass.Item> {
            override fun onResponse(call: Call<ProductListingDataClass.Item>?, response: Response<ProductListingDataClass.Item>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<ProductListingDataClass.Item>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getProductChildren(productSku: String?, callBack: ApiCallback<List<ChildProductsResponse>>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.ProductDetailService>(ApiService.ProductDetailService::class.java)?.getProductChildren(ApiConstants.BEARER + adminToken, storeCode, productSku)

        callGet?.enqueue(object : Callback<List<ChildProductsResponse>> {
            override fun onResponse(call: Call<List<ChildProductsResponse>>?, response: Response<List<ChildProductsResponse>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<List<ChildProductsResponse>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getProductOptions(attributeId: String?, callBack: ApiCallback<List<ProductOptionsResponse>>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.ProductDetailService>(ApiService.ProductDetailService::class.java)?.getProductOptions(ApiConstants.BEARER + adminToken, storeCode, attributeId)

        callGet?.enqueue(object : Callback<List<ProductOptionsResponse>> {
            override fun onResponse(call: Call<List<ProductOptionsResponse>>?, response: Response<List<ProductOptionsResponse>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<List<ProductOptionsResponse>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getStaticPagesUrl(callBack: ApiCallback<StaticPagesUrlResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.ProductDetailService>(ApiService.ProductDetailService::class.java)?.getStaticPagesUrl(ApiConstants.BEARER + adminToken, storeCode)

        callGet?.enqueue(object : Callback<StaticPagesUrlResponse> {
            override fun onResponse(call: Call<StaticPagesUrlResponse>?, response: Response<StaticPagesUrlResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<StaticPagesUrlResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun addToWishList(requestMap: JsonObject, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.ProductDetailService>(ApiService.ProductDetailService::class.java)?.addToWishList(ApiConstants.BEARER + userToken, storeCode, requestMap)

        callPost?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun createGuestCart(callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.createGuestCart(ApiConstants.BEARER + adminToken, storeCode)

        callPost?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun createUserCart(callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.createUserCart(ApiConstants.BEARER + userToken, storeCode)

        callPost?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun cartCountUser(callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.cartCountUser(ApiConstants.BEARER + userToken, storeCode)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun cartCountGuest(cartId: String, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.cartCountGuest(ApiConstants.BEARER + adminToken, storeCode, cartId)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun addToCartGuest(cartId: String, request: AddToCartRequest, callBack: ApiCallback<AddToCartResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.addTOCartGuest(ApiConstants.BEARER + adminToken, storeCode, cartId, request)

        callPost?.enqueue(object : Callback<AddToCartResponse> {
            override fun onResponse(call: Call<AddToCartResponse>?, response: Response<AddToCartResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<AddToCartResponse
                    >, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun addToCartUser(request: AddToCartRequest, callBack: ApiCallback<AddToCartResponse>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.addTOCartUser(ApiConstants.BEARER + userToken, storeCode, request)

        callPost?.enqueue(object : Callback<AddToCartResponse> {
            override fun onResponse(call: Call<AddToCartResponse>?, response: Response<AddToCartResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<AddToCartResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getCartOfUser(callBack: ApiCallback<List<ShoppingBagResponse>>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE

        val callPost = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.getCartOfUser(ApiConstants.BEARER + userToken, storeCode)

        callPost?.enqueue(object : Callback<List<ShoppingBagResponse>> {
            override fun onResponse(call: Call<List<ShoppingBagResponse>>?, response: Response<List<ShoppingBagResponse>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<List<ShoppingBagResponse>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getCartOfGuest(cartId: String ,callBack: ApiCallback<List<ShoppingBagResponse>>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE

        val callPost = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.getCartOfGuest((ApiConstants.BEARER + adminToken), storeCode, cartId)

        callPost?.enqueue(object : Callback<List<ShoppingBagResponse>> {
            override fun onResponse(call: Call<List<ShoppingBagResponse>>?, response: Response<List<ShoppingBagResponse>>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<List<ShoppingBagResponse>>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun searchFilterApi(searchQuery: String, callBack: ApiCallback<ProductListingDataClass.FilterOptionsResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.FilterOptionService>(ApiService.FilterOptionService::class.java)?.getSearchFilters(ApiConstants.BEARER + adminToken, storeCode, searchQuery = searchQuery)

        callGet?.enqueue(object : Callback<ProductListingDataClass.FilterOptionsResponse> {
            override fun onResponse(call: Call<ProductListingDataClass.FilterOptionsResponse>?, response: Response<ProductListingDataClass.FilterOptionsResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<ProductListingDataClass.FilterOptionsResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getUserInfo(callBack: ApiCallback<MyAccountDataClass.UserInfoResponse>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.MyAccount>(ApiService.MyAccount::class.java)?.getUserInfo(ApiConstants.BEARER + userToken,  storeCode)

        callGet?.enqueue(object : Callback<MyAccountDataClass.UserInfoResponse> {
            override fun onResponse(call: Call<MyAccountDataClass.UserInfoResponse>?, response: Response<MyAccountDataClass.UserInfoResponse>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<MyAccountDataClass.UserInfoResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun updateUserInfo(request: MyAccountDataClass.UpdateInfoRequest, callBack: ApiCallback<MyAccountDataClass.UserInfoResponse>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val callPut = retrofit?.create<ApiService.MyAccount>(ApiService.MyAccount::class.java)?.updateUserInfo(ApiConstants.BEARER + userToken,  storeCode, request)

        callPut?.enqueue(object : Callback<MyAccountDataClass.UserInfoResponse> {
            override fun onResponse(call: Call<MyAccountDataClass.UserInfoResponse>?, response: Response<MyAccountDataClass.UserInfoResponse>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<MyAccountDataClass.UserInfoResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun getWishlist(callBack: ApiCallback<WishlistResponse>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.WishlistService>(ApiService.WishlistService::class.java)?.getWishlist(ApiConstants.BEARER + userToken,  storeCode)

        callGet?.enqueue(object : Callback<WishlistResponse> {
            override fun onResponse(call: Call<WishlistResponse>?, response: Response<WishlistResponse>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun deleteWishlistItem(itemId : Int?, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.WishlistService>(ApiService.WishlistService::class.java)?.deleteWishlistItem(ApiConstants.BEARER + userToken,  storeCode, itemId)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }


    fun deleteFromShoppingBagItemGuestUser(cartId : String?, itemId : Int?, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.deleteItemFromShoppingBagGuestUser(ApiConstants.BEARER + userToken,  storeCode, cartId, itemId)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun updateFromShoppingBagItemGuestUser(shoppingBagQtyUpdateRequest : ShoppingBagQtyUpdateRequest, callBack: ApiCallback<ShoppingBagResponse>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.updateItemFromShoppingBagGuestUser(ApiConstants.BEARER + userToken,  storeCode, shoppingBagQtyUpdateRequest.cartItem.quote_id,shoppingBagQtyUpdateRequest.cartItem.item_id.toInt(), shoppingBagQtyUpdateRequest)

        callGet?.enqueue(object : Callback<ShoppingBagResponse> {
            override fun onResponse(call: Call<ShoppingBagResponse>?, response: Response<ShoppingBagResponse>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<ShoppingBagResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }


    fun updateFromShoppingBagItemUser( shoppingBagQtyUpdateRequest : ShoppingBagQtyUpdateRequest, callBack: ApiCallback<ShoppingBagResponse>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.updateItemFromShoppingBagUser(ApiConstants.BEARER + userToken,  storeCode, shoppingBagQtyUpdateRequest.cartItem.item_id.toInt()
                , shoppingBagQtyUpdateRequest )

        callGet?.enqueue(object : Callback<ShoppingBagResponse> {
            override fun onResponse(call: Call<ShoppingBagResponse>?, response: Response<ShoppingBagResponse>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<ShoppingBagResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun deleteFromShoppingBagItemUser( itemId : Int?, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.deleteItemFromShoppingBagUser(ApiConstants.BEARER + userToken,  storeCode, itemId)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun moveItemFromCart( itemId : Int?, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE

        val callGet = retrofit?.create<ApiService.CartService>(ApiService.CartService::class.java)?.moveItemFromCart(ApiConstants.BEARER + userToken,  storeCode, itemId)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun addToBagWishlistItem(itemId : Int?, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.WishlistService>(ApiService.WishlistService::class.java)?.addToBagWishlistItem(ApiConstants.BEARER + userToken,  storeCode, itemId)

        callGet?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

}
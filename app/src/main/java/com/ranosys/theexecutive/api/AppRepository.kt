package com.ranosys.theexecutive.api

import com.ranosys.theexecutive.BuildConfig
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.api.interfaces.ApiService
import com.ranosys.theexecutive.modules.category.AllCategoryDataResponse
import com.ranosys.theexecutive.modules.category.CategoryDataResponse
import com.ranosys.theexecutive.modules.category.CategoryResponseDataClass
import com.ranosys.theexecutive.modules.forgot_password.ForgotPasswordDataClass
import com.ranosys.theexecutive.modules.login.LoginDataClass
import com.ranosys.theexecutive.modules.splash.ConfigurationResponse
import com.ranosys.theexecutive.modules.splash.StoreResponse
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


/**
 * Created by Mohammad Sunny on 23/2/18.
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
                Utils.printLog("Login:", "Failed")

            }
        })
    }

    fun getConfiguration(callBack: ApiCallback<ConfigurationResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        //val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val storeCode = Constants.ALL
        val callGet = retrofit?.create<ApiService.ConfigurationService>(ApiService.ConfigurationService::class.java)?.getConfiguration(ApiConstants.BEARER + adminToken,  storeCode)

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
                Utils.printLog("Login:", "Failed")

            }
        })
    }


    fun login(loginRequest: LoginDataClass.LoginRequest?, callBack: ApiCallback<String>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.LoginService>(ApiService.LoginService::class.java)?.getLoginData(ApiConstants.BEARER + adminToken,  storeCode, loginRequest)

        callPost?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.onError(Constants.ERROR)
                Utils.printLog("Login:","Failed")
            }
        })
    }

    fun getCategories(callBack: ApiCallback<CategoryResponseDataClass>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
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

    fun getCategoryData(categoryId :  String, callBack: ApiCallback<CategoryDataResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.CategoryService>(ApiService.CategoryService::class.java)?.getCategoryData(ApiConstants.BEARER + adminToken, storeCode, categoryId)

        callGet?.enqueue(object : Callback<CategoryDataResponse> {
            override fun onResponse(call: Call<CategoryDataResponse>?, response: Response<CategoryDataResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<CategoryDataResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }


    fun getAllCategoryData(queryMap :  HashMap<String,String>?, callBack: ApiCallback<AllCategoryDataResponse>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val callGet = retrofit?.create<ApiService.CategoryService>(ApiService.CategoryService::class.java)?.getAllCategoryData(ApiConstants.BEARER + adminToken, storeCode, queryMap)

        callGet?.enqueue(object : Callback<AllCategoryDataResponse> {
            override fun onResponse(call: Call<AllCategoryDataResponse>?, response: Response<AllCategoryDataResponse>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())
                }

            }

            override fun onFailure(call: Call<AllCategoryDataResponse>, t: Throwable) {
                callBack.onError(Constants.ERROR)
            }
        })
    }

    fun isEmailAvailable(request: LoginDataClass.IsEmailAvailableRequest?, callBack: ApiCallback<Boolean>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
        val callPost = retrofit?.create<ApiService.IsEmailAvailableService>(ApiService.IsEmailAvailableService::class.java)?.isEmailAvailableApi(ApiConstants.BEARER + adminToken,  storeCode = storeCode, request = request)

        callPost?.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                if(!response!!.isSuccessful){
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                }else{
                    callBack.onSuccess(response.body())

                }

            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callBack.onError(Constants.ERROR)
                Utils.printLog("Is Email Available:","Failed")

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
                Utils.printLog("Is Email Available:", "Failed")

            }
        })
    }

    fun forgotPassword(request: ForgotPasswordDataClass.ForgotPasswordRequest, callBack: ApiCallback<Boolean>) {
        val retrofit = ApiClient.retrofit
        val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
        val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                ?: Constants.DEFAULT_STORE_CODE
        val callPut = retrofit?.create<ApiService.ForgotPasswordService>(ApiService.ForgotPasswordService::class.java!!)?.forgotPasswordApi(ApiConstants.BEARER + adminToken, storeCode, request)

        callPut?.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                if (!response!!.isSuccessful) {
                    parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                } else {
                    callBack.onSuccess(response.body())

                }

            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callBack.onError(Constants.ERROR)
                Utils.printLog("Login:", "Failed")

            }
        })
    }


}
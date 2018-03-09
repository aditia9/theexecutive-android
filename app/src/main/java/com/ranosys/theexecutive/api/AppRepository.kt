package com.ranosys.theexecutive.api

import android.util.Log
import com.ranosys.theexecutive.BuildConfig
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.api.interfaces.ApiService
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
 * Created by Mohammad Sunny on 25/1/18.
 */
class AppRepository private constructor(){

    companion object {

        private fun parseError(response: Response<Any>?, callBack: ApiCallback<Any>) {
            try {
                val jobError = JSONObject(response?.errorBody()?.string())
                var errorBody = jobError.getString(Constants.MESSAGE)
                callBack.onError(errorBody)

            } catch (e: JSONException) {
                callBack.onException(Throwable(Constants.ERROR))
                if (BuildConfig.DEBUG)
                    e.printStackTrace()
            } catch (e:IOException) {
                callBack.onException(Throwable(Constants.ERROR))
                if (BuildConfig.DEBUG)
                    e.printStackTrace()
            }
        }

        fun getStores(callBack: ApiCallback<ArrayList<StoreResponse>>) {
            val retrofit = ApiClient.retrofit
            val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
            val callPost = retrofit?.create<ApiService.StoresService>(ApiService.StoresService::class.java!!)?.getStores(ApiConstants.BEARER + adminToken)

            callPost?.enqueue(object : Callback<ArrayList<StoreResponse>> {
                override fun onResponse(call: Call<ArrayList<StoreResponse>>?, response: Response<ArrayList<StoreResponse>>?) {
                    if(!response!!.isSuccessful){
                        parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                    }else{
                        callBack.onSuccess(response.body())

                    }

                }

                override fun onFailure(call: Call<ArrayList<StoreResponse>>, t: Throwable) {
                    callBack.onError(Constants.ERROR)
                    Utils.printLog("Login:","Failed")

                }
            })
        }

        fun getConfiguration(callBack: ApiCallback<ConfigurationResponse>) {
            val retrofit = ApiClient.retrofit
            val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
            //val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
            val storeCode: String = "all"
            val callGet = retrofit?.create<ApiService.ConfigurationService>(ApiService.ConfigurationService::class.java!!)?.getConfiguration(ApiConstants.BEARER + adminToken,  storeCode)

            callGet?.enqueue(object : Callback<ConfigurationResponse> {
                override fun onResponse(call: Call<ConfigurationResponse>?, response: Response<ConfigurationResponse>?) {
                    if(!response!!.isSuccessful){
                        parseError(response as Response<Any>, callBack as ApiCallback<Any>)

                    }else{
                        callBack.onSuccess(response.body())

                    }

                }

                override fun onFailure(call: Call<ConfigurationResponse>, t: Throwable) {
                    callBack.onError(Constants.ERROR)
                    Utils.printLog("Login:","Failed")

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
                    }else{
                        callBack.onSuccess(response.body())

                    }

                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    callBack.onError(Constants.ERROR)
                    Utils.printLog("Login:","Failed")

                }
            })
        }

        fun isEmailAvailable(request: LoginDataClass.IsEmailAvailableRequest?, callBack: ApiCallback<Boolean>) {
            val retrofit = ApiClient.retrofit
            val adminToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.ACCESS_TOKEN_KEY)
            val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
            val callPost = retrofit?.create<ApiService.IsEmailAvailableService>(ApiService.IsEmailAvailableService::class.java)?.isEmailAvailableApi(ApiConstants.BEARER + adminToken,  storeCode, request = request)

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
            val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?:Constants.DEFAULT_STORE_CODE
            val callPost = retrofit?.create<ApiService.SocialLoginService>(ApiService.SocialLoginService::class.java)?.socialLogin(ApiConstants.BEARER + adminToken,  storeCode, request = request)

            callPost?.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>?, response: Response<String>?) {
                    if(!response!!.isSuccessful){
                        parseError(response as Response<Any>, callBack as ApiCallback<Any>)
                    }else{
                        callBack.onSuccess(response.body())

                    }

                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    callBack.onError(Constants.ERROR)
                    Utils.printLog("Is Email Available:","Failed")

                }
            })
        }

    }



}
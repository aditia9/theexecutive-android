package com.ranosys.theexecutive.api

import android.util.Log
import com.ranosys.theexecutive.BuildConfig
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.api.interfaces.ApiService
import com.ranosys.theexecutive.modules.login.LoginDataClass
import com.ranosys.theexecutive.modules.splash.AdminDataClass
import com.ranosys.theexecutive.modules.splash.Store
import com.ranosys.theexecutive.modules.splash.StoreResponse
import com.ranosys.theexecutive.utils.Constants
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

        fun getStores(callBack: ApiCallback<StoreResponse>) {
            val retrofit = ApiClient.retrofit
            val callPost = retrofit?.create<ApiService.StoresService>(ApiService.StoresService::class.java!!)?.getStores()

            callPost?.enqueue(object : Callback<StoreResponse> {
                override fun onResponse(call: Call<StoreResponse>?, response: Response<StoreResponse>?) {
                    if(!response!!.isSuccessful){
                        try {
                            val jobError = JSONObject(response.errorBody()?.string())
                            var errorBody = jobError.getString("errorCode")
                            callBack.onError(errorBody)
//                            apiResponse.apiResponse = response.body()
//                            apiResponse.error = errorBody
//                            dataLogin.value = apiResponse

                        } catch (e: JSONException) {
//                            apiResponse.throwable = Throwable("Error")
//                            dataLogin.value = apiResponse
                            callBack.onException(Throwable("Error"))
                            if (BuildConfig.DEBUG)
                                e.printStackTrace();
                        } catch (e:IOException) {
//                            apiResponse.throwable = Throwable("Error")
//                            dataLogin.value = apiResponse
                            callBack.onException(Throwable("Error"))
                            if (BuildConfig.DEBUG)
                                e.printStackTrace();
                        }
                    }else{
//                        apiResponse.apiResponse = response.body()
//                        dataLogin.value = apiResponse
//                        printLog("Login:",""+dataLogin.value?.apiResponse?.accessToken)
                        callBack.onSuccess(response.body())

                    }

                }

                override fun onFailure(call: Call<StoreResponse>, t: Throwable) {
//                    dataLogin.value = null
                    callBack.onError("Error")
                    printLog("Login:","Failed")

                }
            })
        }

        fun getAdminToken(adminTokenRequest : AdminDataClass, callBack: ApiCallback<String>) {
            val retrofit = ApiClient.retrofit
            val callPost = retrofit?.create<ApiService.AdminTokenService>(ApiService.AdminTokenService::class.java!!)?.getAdminToken(adminTokenRequest)

            callPost?.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>?, response: Response<String>?) {
                    if(!response!!.isSuccessful){
                        try {
                            val jobError = JSONObject(response.errorBody()?.string())
                            var errorBody = jobError.getString("errorCode")
                            callBack.onError(errorBody)
//                            apiResponse.apiResponse = response.body()
//                            apiResponse.error = errorBody
//                            dataLogin.value = apiResponse

                        } catch (e: JSONException) {
//                            apiResponse.throwable = Throwable("Error")
//                            dataLogin.value = apiResponse
                            callBack.onException(Throwable("Error"))
                            if (BuildConfig.DEBUG)
                                e.printStackTrace();
                        } catch (e:IOException) {
//                            apiResponse.throwable = Throwable("Error")
//                            dataLogin.value = apiResponse
                            callBack.onException(Throwable("Error"))
                            if (BuildConfig.DEBUG)
                                e.printStackTrace();
                        }
                    }else{
//                        apiResponse.apiResponse = response.body()
//                        dataLogin.value = apiResponse
//                        printLog("Login:",""+dataLogin.value?.apiResponse?.accessToken)
                        callBack.onSuccess(response.body())

                    }

                }

                override fun onFailure(call: Call<String>, t: Throwable) {
//                    dataLogin.value = null
                    callBack.onError("Error")
                    printLog("Login:","Failed")

                }
            })
        }


        fun login(loginRequest: LoginDataClass.LoginRequest?, callBack: ApiCallback<LoginDataClass.LoginResponse>) {
            //val apiResponse = ApiResponse<LoginDataClass.LoginResponse>()
            val retrofit = ApiClient.retrofit
            val callPost = retrofit?.create<ApiService.LoginService>(ApiService.LoginService::class.java!!)?.getLoginData(loginRequest)

            callPost?.enqueue(object : Callback<LoginDataClass.LoginResponse> {
                override fun onResponse(call: Call<LoginDataClass.LoginResponse>?, response: Response<LoginDataClass.LoginResponse>?) {
                    if(!response!!.isSuccessful){
                        try {
                            val jobError = JSONObject(response.errorBody()?.string())
                            var errorBody = jobError.getString("errorCode")
                            callBack.onError(errorBody)
//                            apiResponse.apiResponse = response.body()
//                            apiResponse.error = errorBody
//                            dataLogin.value = apiResponse

                        } catch (e: JSONException) {
//                            apiResponse.throwable = Throwable("Error")
//                            dataLogin.value = apiResponse
                            callBack.onException(Throwable("Error"))
                            if (BuildConfig.DEBUG)
                                e.printStackTrace();
                        } catch (e:IOException) {
//                            apiResponse.throwable = Throwable("Error")
//                            dataLogin.value = apiResponse
                            callBack.onException(Throwable("Error"))
                            if (BuildConfig.DEBUG)
                                e.printStackTrace();
                        }
                    }else{
//                        apiResponse.apiResponse = response.body()
//                        dataLogin.value = apiResponse
//                        printLog("Login:",""+dataLogin.value?.apiResponse?.accessToken)
                        callBack.onSuccess(response.body())

                    }

                }

                override fun onFailure(call: Call<LoginDataClass.LoginResponse>, t: Throwable) {
//                    dataLogin.value = null
                    callBack.onError("Error")
                    printLog("Login:","Failed")

                }
            })
        }

        fun printLog(TAG:String, message: String): Unit{
            if(BuildConfig.DEBUG){
                Log.e(TAG, message)
            }
        }

    }



}
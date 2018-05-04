package com.ranosys.theexecutive.modules.login

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.ObservableField
import android.text.TextUtils
import android.view.View
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils

class LoginViewModel(application: Application) : BaseViewModel(application){

    val emailError = ObservableField<String>()
    val passwordError = ObservableField<String>()
    var email = ObservableField<String>()
    var password = ObservableField<String>()

    var apiFailureResponse: MutableLiveData<String>? = MutableLiveData()
    var apiSuccessResponse: MutableLiveData<String>? = MutableLiveData()
    var isEmailNotAvailable: MutableLiveData<LoginDataClass.SocialLoginData>? = MutableLiveData()
    var userCartIdResponse: MutableLiveData<ApiResponse<String>>? = MutableLiveData()
    var userCartCountResponse: MutableLiveData<ApiResponse<String>>? = MutableLiveData()


    var clickedBtnId: MutableLiveData<Int>? = null
        get() {
            field =  field ?: MutableLiveData()
            return field
        }

    fun btnClicked(view: View){
        when(view.id){
            R.id.btn_login ->{
                clickedBtnId?.value = R.id.btn_login
            }

            R.id.btn_register ->{

                clickedBtnId?.value = R.id.btn_register
            }

            R.id.btn_fb_login ->{

                clickedBtnId?.value = R.id.btn_fb_login
            }

            R.id.btn_gmail_login ->{

                clickedBtnId?.value = R.id.btn_gmail_login
            }

            R.id.tv_forgot_password ->{

                clickedBtnId?.value = R.id.tv_forgot_password
            }

            R.id.btn_register ->{

                clickedBtnId?.value = R.id.btn_register
            }
        }
    }


    fun login(){
        val loginRequest = LoginDataClass.LoginRequest(email.get().toString(), password.get().toString())

        AppRepository.login(loginRequest, object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                Utils.printLog("Login Api", "error")
                apiFailureResponse?.value = Constants.UNKNOWN_ERROR

            }

            override fun onError(errorMsg: String) {
                Utils.printLog("Login Api", "error")
                apiFailureResponse?.value = errorMsg
            }

            override fun onSuccess(userToken: String?) {
                //save customer token
                SavedPreferences.getInstance()?.saveStringValue(userToken!!, Constants.USER_ACCESS_TOKEN_KEY)
                apiSuccessResponse?.value = userToken

            }
        })
    }


    fun validateData(context: Context): Boolean {
        var isValid = true

        if (TextUtils.isEmpty(email.get())) {
            emailError.set(context.getString(R.string.empty_email))
            isValid = false
        } else if (!Utils.isValidEmail(email.get())) {
            emailError.set(context.getString(R.string.provide_valid_email))
            isValid = false
        }

        if (TextUtils.isEmpty(password.get())) {
            passwordError.set(context.getString(R.string.empty_password))
            isValid = false
        }

        return isValid
    }

    fun isEmailAvailableApi(userData: LoginDataClass.SocialLoginData) {
        val request = LoginDataClass.IsEmailAvailableRequest(userData.email, SavedPreferences.getInstance()?.getIntValue(Constants.SELECTED_WEBSITE_ID_KEY)?:  1)
        AppRepository.isEmailAvailable(request, object : ApiCallback<Boolean>{
            override fun onException(error: Throwable) {
                apiFailureResponse?.value = Constants.UNKNOWN_ERROR
            }

            override fun onError(errorMsg: String) {
                apiFailureResponse?.value = errorMsg
            }

            override fun onSuccess(available: Boolean?) {
                if(available!!.not()){
                    callSocialLoginApi(userData)
                }else{
                    isEmailNotAvailable?.value = userData
                }
            }

        })
    }

    private fun callSocialLoginApi(userData: LoginDataClass.SocialLoginData) {
        val request = LoginDataClass.SocialLoginRequest(userData.email, userData.type, userData.token)
        AppRepository.socialLogin(request, object: ApiCallback<String>{
            override fun onException(error: Throwable) {
                apiFailureResponse?.value = Constants.UNKNOWN_ERROR
            }

            override fun onError(errorMsg: String) {
                apiFailureResponse?.value = errorMsg
            }

            override fun onSuccess(userToken: String?) {
                SavedPreferences.getInstance()?.saveStringValue(userToken!!, Constants.USER_ACCESS_TOKEN_KEY)
                email.set(userData.email)
                apiSuccessResponse?.value = userToken

            }

        })
    }

    fun onEmailTextChanged() {
        emailError.set("")
    }

    fun onPasswordTextChanged() {
        passwordError.set("")
    }

    fun getCartIdForUser(userToken: String?){
        val apiResponse = ApiResponse<String>()
        AppRepository.createUserCart(object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                userCartIdResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                userCartIdResponse?.value?.error = errorMsg
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                SavedPreferences.getInstance()?.saveStringValue(t, Constants.USER_CART_ID_KEY)
                userCartIdResponse?.value = apiResponse
            }

        })
    }

    fun getUserCartCount() {
        val apiResponse = ApiResponse<String>()
        AppRepository.cartCountUser(object : ApiCallback<String>{
            override fun onException(error: Throwable) {
                userCartCountResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                userCartCountResponse?.value?.error = errorMsg
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                userCartCountResponse?.value = apiResponse
            }

        })

    }

}


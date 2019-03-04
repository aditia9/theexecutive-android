package com.delamibrands.theexecutive.modules.login

import AppLog
import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.ObservableField
import android.text.TextUtils
import android.view.View
import com.delamibrands.theexecutive.DelamiBrandsApplication
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.api.ApiResponse
import com.delamibrands.theexecutive.api.AppRepository
import com.delamibrands.theexecutive.api.interfaces.ApiCallback
import com.delamibrands.theexecutive.base.BaseViewModel
import com.delamibrands.theexecutive.modules.myAccount.MyAccountDataClass
import com.delamibrands.theexecutive.utils.Constants
import com.delamibrands.theexecutive.utils.GlobalSingelton
import com.delamibrands.theexecutive.utils.SavedPreferences
import com.delamibrands.theexecutive.utils.Utils

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
    var loginRequiredPrompt: Boolean = false


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

            }

            override fun onError(errorMsg: String) {
                Utils.printLog("Login Api", "error")
                apiFailureResponse?.value = errorMsg
            }

            override fun onErrorCode(errorCode: Int) {
                apiFailureResponse?.value = errorCode.toString()
            }
            override fun onSuccess(userToken: String?) {
                //save customer token
                SavedPreferences.getInstance()?.saveStringValue(userToken!!, Constants.USER_ACCESS_TOKEN_KEY)

                //get users complete info
                getUserInformation()

                //merge user and guest cart
                val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY)?: ""
                if(guestCartId.isNotBlank()){
                    mergeCart(guestCartId)
                }else{
                    apiSuccessResponse?.value = userToken
                }

            }
        })
    }

    private fun getUserInformation() {
        AppRepository.getUserInfo(object: ApiCallback<MyAccountDataClass.UserInfoResponse> {
            override fun onException(error: Throwable) {
                AppLog.e("My Information API : ${error.message}")
            }

            override fun onError(errorMsg: String) {
                AppLog.e("My Information API : $errorMsg")
            }

            override fun onSuccess(t: MyAccountDataClass.UserInfoResponse?) {
                //update info saved at singleton
                GlobalSingelton.instance?.userInfo = t

                //save username and email in sharedpreference too because Global singleton will
                // not persist data when app got killed.
                SavedPreferences.getInstance()?.saveStringValue(t?.email, Constants.USER_EMAIL)
                SavedPreferences.getInstance()?.saveStringValue(t?.firstname, Constants.FIRST_NAME)
                SavedPreferences.getInstance()?.saveStringValue(t?.lastname, Constants.LAST_NAME)
                Utils.setUpZendeskChat()
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
        AppRepository.isEmailAvailable(request, object : ApiCallback<Boolean> {
            override fun onException(error: Throwable) {
                apiFailureResponse?.value = DelamiBrandsApplication.samleApplication?.getString(R.string.something_went_wrong_error)
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
        val request = LoginDataClass.SocialLoginRequest(userData.email, userData.type, userData.token, SavedPreferences.getInstance()?.getStringValue(Constants.USER_FCM_ID), Constants.OS_TYPE,
                SavedPreferences.getInstance()?.getStringValue(Constants.ANDROID_DEVICE_ID_KEY))
        AppRepository.socialLogin(request, object: ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiFailureResponse?.value = DelamiBrandsApplication.samleApplication?.getString(R.string.something_went_wrong_error)
            }

            override fun onError(errorMsg: String) {
                apiFailureResponse?.value = errorMsg
            }

            override fun onSuccess(userToken: String?) {
                SavedPreferences.getInstance()?.saveStringValue(userToken!!, Constants.USER_ACCESS_TOKEN_KEY)
                email.set(userData.email)

                //get users complete info
                getUserInformation()

                val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY)?: ""
                if(guestCartId.isNotBlank()){
                    mergeCart(guestCartId)
                }else{
                    apiSuccessResponse?.value = userToken
                }

            }

        })
    }

    private fun mergeCart(guestCartId: String) {
        AppRepository.cartMergeApi(guestCartId, object: ApiCallback<String> {
            override fun onException(error: Throwable) {
                AppLog.d("cart merge api : ${error.message}")
            }

            override fun onError(errorMsg: String) {
                AppLog.d("cart merge api : $errorMsg")
            }

            override fun onSuccess(t: String?) {
                //delete guest cart id
                SavedPreferences.getInstance()?.saveStringValue("", Constants.GUEST_CART_ID_KEY)
                apiSuccessResponse?.value = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
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
                getUserCartCount()
                SavedPreferences.getInstance()?.saveStringValue(t, Constants.USER_CART_ID_KEY)
                userCartIdResponse?.value = apiResponse
            }

        })
    }

    fun getUserCartCount() {
        val apiResponse = ApiResponse<String>()
        AppRepository.cartCountUser(object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                userCartCountResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                userCartCountResponse?.value?.error = errorMsg
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                userCartCountResponse?.value = apiResponse
                Utils.updateCartCount(t!!.toInt())
            }

        })
    }
}


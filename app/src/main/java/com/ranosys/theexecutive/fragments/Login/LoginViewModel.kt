package com.ranosys.theexecutive.fragments.Login

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.ObservableField
import android.text.TextUtils
import android.view.View
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.utils.Utils

/**
 * Created by Mohammad Sunny on 25/1/18.
 */
class LoginViewModel(application: Application) : BaseViewModel(application){

    var login = ObservableField<LoginDataClass.LoginResponse>()
    var loginObservable: LiveData<LoginDataClass.LoginResponse>? = null
    var loginRequest: LoginDataClass.LoginRequest? = null
    var email: ObservableField<String>? = ObservableField<String>()
    var password :ObservableField<String>? = ObservableField<String>()
    val emailError = ObservableField<String>()
    val passwordError = ObservableField<String>()
    val isButtonClicked = MutableLiveData<Int>()
    val isCreateAcctClicked = MutableLiveData<Int>()
    var mutualresponse = MutableLiveData<ApiResponse<LoginDataClass.LoginResponse>>()

    init {
        this.loginRequest = loginRequest
    }

    fun onLoginClick(view: View) {
        if (isDataValid(view.context)) {
            isButtonClicked.setValue(view.id)
        }
    }
    fun onRegisterClick(view: View){
        isCreateAcctClicked.value = view.id
    }

    fun login(){

        loginRequest?.registrationId = "fksjflkslfksaofishfslkfgjlkjjljlkjl34"
        loginRequest?.deviceType = "DEVICE_ANDROID"
        loginRequest?.deviceId = "123456"
        loginRequest?.email = email?.get().toString()
        loginRequest?.password = password?.get().toString()

        AppRepository.login(loginRequest, mutualresponse)
    }

    fun isDataValid(context: Context): Boolean {
        var isValid = true
        if (TextUtils.isEmpty(email?.get())) {
            emailError.set(context.getString(R.string.empty_email))
            isValid = false
        } else if (!Utils.isValidEmail(email?.get())) {
            emailError.set(context.getString(R.string.provide_valid_email))
            isValid = false
        }
        if (TextUtils.isEmpty(password?.get())) {
            passwordError.set(context.getString(R.string.empty_password))
            isValid = false
        }

        return isValid
    }


    fun getObservableProject(): LiveData<LoginDataClass.LoginResponse>? {
        return loginObservable
    }

    fun onEmailTextChanged(text: CharSequence) {
        emailError.set("")
    }

    fun onPasswordTextChanged(text: CharSequence) {
        passwordError.set("")
    }

}


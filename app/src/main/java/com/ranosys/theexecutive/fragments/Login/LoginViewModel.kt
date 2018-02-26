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
import com.ranosys.theexecutive.api.interfaces.ApiCallback
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
    var clickedBtnId: MutableLiveData<Int>? = null
        get() {
            field =  field ?: MutableLiveData()
            return field
        }
    var mutualresponse = MutableLiveData<ApiResponse<LoginDataClass.LoginResponse>>()

    init {
        this.loginRequest = loginRequest
    }

    fun onRegisterClick(view: View){
        when(view.id){
            R.id.tv_forgot_password ->{

                if (!TextUtils.isEmpty(email?.get())) {
                    if (Utils.isValidEmail(email?.get())) {
                        clickedBtnId?.value = view.id
                    } else {
                        emailError?.set(view.context.getString(R.string.error_invalid_email))
                    }
                } else {
                    emailError?.set(view.context.getString(R.string.error_empty_email_id))
                }
            }
            else ->{
                clickedBtnId?.value = view.id
            }

        }

    }

    fun login(){

        loginRequest?.registrationId = "fksjflkslfksaofishfslkfgjlkjjljlkjl34"
        loginRequest?.deviceType = "DEVICE_ANDROID"
        loginRequest?.deviceId = "123456"
        loginRequest?.email = email?.get().toString()
        loginRequest?.password = password?.get().toString()

        if(isDataValid(getApplication())){
            AppRepository.login(loginRequest, object : ApiCallback<LoginDataClass.LoginResponse> {
                override fun onException(error: Throwable) {
                    mutualresponse.value?.throwable = error
                }

                override fun onError(errorMsg: String) {
                    mutualresponse.value?.error = errorMsg
                }

                override fun onSuccess(t: LoginDataClass.LoginResponse?) {
                    mutualresponse.value?.apiResponse = t
                }
            })
        }

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

}


package com.ranosys.theexecutive.modules.login

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
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils

/**
 * Created by Mohammad Sunny on 25/1/18.
 */
class LoginViewModel(application: Application) : BaseViewModel(application){

    val emailError = ObservableField<String>()
    val passwordError = ObservableField<String>()
    var email = ObservableField<String>()
    var password = ObservableField<String>()

    var apiFailureResponse: MutableLiveData<String>? = MutableLiveData()
    var apiSuccessResponse: MutableLiveData<String>? = MutableLiveData()

    var clickedBtnId: MutableLiveData<Int>? = null
        get() {
            field =  field ?: MutableLiveData()
            return field
        }

    fun btnClicked(view: View){
        when(view.id){
            R.id.btn_login ->{

                //validate data
                if(validateData(getApplication())){
                    clickedBtnId?.value = R.id.btn_login
                }
            }
        }
    }


    fun login(){

        val loginRequest = LoginDataClass.LoginRequest(email.get().toString(), password.get().toString())




        AppRepository.login(loginRequest, object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                Utils.printLog("Login Api", "error")
                apiFailureResponse?.value = "Something went wrong"

            }

            override fun onError(errorMsg: String) {
                Utils.printLog("Login Api", "error")
                apiFailureResponse?.value = errorMsg
            }

            override fun onSuccess(accessToken: String?) {
                //save customer token
                SavedPreferences.getInstance()?.saveStringValue(accessToken!!, Constants.USER_ACCESS_TOKEN_KEY)
                apiSuccessResponse?.value = accessToken

            }
        })


    }

    fun validateData(context: Context): Boolean {
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
        //add password regex validation

        return isValid
    }

}


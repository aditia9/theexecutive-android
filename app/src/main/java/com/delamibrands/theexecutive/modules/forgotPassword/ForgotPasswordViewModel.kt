package com.delamibrands.theexecutive.modules.forgotPassword

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.ObservableField
import android.text.TextUtils
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.api.AppRepository
import com.delamibrands.theexecutive.api.interfaces.ApiCallback
import com.delamibrands.theexecutive.base.BaseViewModel
import com.delamibrands.theexecutive.utils.Constants
import com.delamibrands.theexecutive.utils.SavedPreferences
import com.delamibrands.theexecutive.utils.Utils

/**
 * Created by nikhil on 8/3/18.
 */
class ForgotPasswordViewModel(application: Application): BaseViewModel(application) {
    var email: ObservableField<String> = ObservableField()
    val emailError = ObservableField<String>()
    var apiSuccessResponse: MutableLiveData<Boolean>? = MutableLiveData()
    var apiFailureResponse: MutableLiveData<String>? = MutableLiveData()


    fun callForgetPasswordApi(){
        val websiteId = SavedPreferences.getInstance()?.getIntValue(Constants.SELECTED_WEBSITE_ID_KEY)
        val request = ForgotPasswordDataClass.ForgotPasswordRequest(email = email.get(), websiteId = websiteId)

        AppRepository.forgotPassword(request, object: ApiCallback<Boolean> {
            override fun onException(error: Throwable) {
                apiFailureResponse?.value = error.message
            }

            override fun onError(errorMsg: String) {
                apiFailureResponse?.value = errorMsg
            }

            override fun onErrorCode(errorCode: Int) {
                apiFailureResponse?.value = errorCode.toString()
            }

            override fun onSuccess(linkSent: Boolean?) {
                apiSuccessResponse?.value = linkSent
            }

        })
    }

    fun validateData(context: Context): Boolean {

        if (TextUtils.isEmpty(email.get())) {
            emailError.set(context.getString(R.string.empty_email))
            return false
        } else if (!Utils.isValidEmail(email.get())) {
            emailError.set(context.getString(R.string.provide_valid_email))
            return false
        }

        return true
    }

    fun onEmailTextChanged() {
        emailError.set("")
    }
}
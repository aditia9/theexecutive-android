package com.ranosys.theexecutive.modules.forgot_password

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.ObservableField
import android.text.TextUtils
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils

/**
 * Created by nikhil on 8/3/18.
 */
class ForgotPasswordViewModel(application: Application): BaseViewModel(application) {
    var email: ObservableField<String> = ObservableField<String>()
    val emailError = ObservableField<String>()
    var apiSuccessResponse: MutableLiveData<String>? = MutableLiveData()
    var apiFailureResponse: MutableLiveData<String>? = MutableLiveData()


    fun callForgetPasswordApi(){

        if(validateData(getApplication())){
            //showLoading()
            val websiteId = SavedPreferences.getInstance()?.getIntValue(Constants.SELECTED_WEBSITE_ID_KEY)
            val request = ForgotPasswordDataClass.ForgotPasswordRequest(email = email.get(), websiteId = websiteId)

            AppRepository.forgotPassword(request, object: ApiCallback<Boolean>{
                override fun onException(error: Throwable) {
                    Utils.printLog("Forgot password Api", "error")
                    apiFailureResponse?.value = "Something went wrong"
                }

                override fun onError(errorMsg: String) {
                    Utils.printLog("Forgot password Api", "error")
                    apiFailureResponse?.value = errorMsg
                }

                override fun onSuccess(linkSent: Boolean?) {
                    //show toast to of success
                    if(linkSent!!) apiSuccessResponse?.value = "link sent" else apiSuccessResponse?.value = "link not sent"
                }

            })
        }


    }

    private fun validateData(context: Context): Boolean {

        var isValid = true

        if (TextUtils.isEmpty(email.get())) {
            emailError.set(context.getString(R.string.empty_email))
            isValid = false
        } else if (!Utils.isValidEmail(email.get())) {
            emailError.set(context.getString(R.string.provide_valid_email))
            isValid = false
        }

        return isValid
    }

    fun onEmailTextChanged() {
        emailError.set("")
    }
}
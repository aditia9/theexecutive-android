package com.delamibrands.theexecutive.modules.newsLetter

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.ObservableField
import android.text.TextUtils
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.api.AppRepository
import com.delamibrands.theexecutive.api.interfaces.ApiCallback
import com.delamibrands.theexecutive.base.BaseViewModel
import com.delamibrands.theexecutive.modules.myAccount.MyAccountDataClass
import com.delamibrands.theexecutive.utils.Utils

/**
 * Created by nikhil on 23/3/18.
 */
class NewsLetterViewModel(application: Application): BaseViewModel(application) {

    var email: ObservableField<String> = ObservableField()
    val emailError = ObservableField<String>()
    var apiSuccessResponse: MutableLiveData<String>? = MutableLiveData()
    var apiFailureResponse: MutableLiveData<String>? = MutableLiveData()



    fun callNewsLetterSubscribeApi(){
        AppRepository.subscribeNewsletterApi(MyAccountDataClass.NewsletterSubscriptionRequest(email.get()), object: ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiFailureResponse?.value = error.message
            }

            override fun onError(errorMsg: String) {
                apiFailureResponse?.value = errorMsg
            }

            override fun onErrorCode(errorCode: Int) {
                apiFailureResponse?.value = errorCode.toString()
            }
            override fun onSuccess(message: String?) {
                apiSuccessResponse?.value = message
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
package com.ranosys.theexecutive.modules.myAccount

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.ObservableField
import android.text.TextUtils
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.utils.Utils

/**
 * Created by nikhil on 23/3/18.
 */
class NewsLetterViewModel(application: Application): BaseViewModel(application) {

    var email: ObservableField<String> = ObservableField<String>()
    val emailError = ObservableField<String>()
    var apiSuccessResponse: MutableLiveData<String>? = MutableLiveData()
    var apiFailureResponse: MutableLiveData<String>? = MutableLiveData()



    fun callNewsLetterSubscribeApi(){
        //TODO - implement news letter api
        validateData(getApplication())

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
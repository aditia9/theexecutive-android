package com.delamibrands.theexecutive.modules.changePassword

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.ObservableField
import android.support.design.widget.TextInputEditText
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.api.AppRepository
import com.delamibrands.theexecutive.api.interfaces.ApiCallback
import com.delamibrands.theexecutive.base.BaseViewModel
import com.delamibrands.theexecutive.utils.Utils


class ChangePasswordViewModel(application: Application) : BaseViewModel(application) {

    var newPassword: ObservableField<String> = ObservableField()
    var currentPassword: ObservableField<String> = ObservableField()
    var confirmPassword: ObservableField<String> = ObservableField()
    val currentPasswordError = ObservableField<String>()
    val newPasswordError = ObservableField<String>()
    val confirmPasswordError = ObservableField<String>()
    var apiSuccessResponse: MutableLiveData<Boolean>? = MutableLiveData()
    var apiFailureResponse: MutableLiveData<String>? = MutableLiveData()


    fun callChangePasswordApi() {

        if (validateData(getApplication())) {

            val request = ChangePasswordDataClass(currentPassword = currentPassword.get(), newPassword = newPassword.get())

            AppRepository.changePassword(request, object : ApiCallback<Boolean> {
                override fun onException(error: Throwable) {
                    apiFailureResponse?.value = error.message
                }

                override fun onError(errorMsg: String) {
                    apiFailureResponse?.value = errorMsg
                }

                override fun onErrorCode(errorCode: Int) {
                    apiFailureResponse?.value = errorCode.toString()
                }
                override fun onSuccess(t: Boolean?) {
                    apiSuccessResponse?.value = t
                }
            })
        }
    }

    fun onTextChanged(et: TextInputEditText) {
        when (et.id) {
            R.id.et_new_password -> newPasswordError.set("")
            R.id.et_current_password -> currentPasswordError.set("")
            R.id.et_confirm_new_password -> confirmPasswordError.set("")
        }
    }

    fun validateData(context: Context): Boolean {

        var isValid = true

        if (currentPassword.get().isNullOrBlank()) {
            currentPasswordError.set(context.getString(R.string.empty_current_password))
            isValid = false
        }

        if (newPassword.get().isNullOrBlank()) {
            newPasswordError.set(context.getString(R.string.empty_new_password))
            isValid = false
        }

        if (newPassword.get().isNullOrBlank().not() && !Utils.isValidPassword(newPassword.get())) {
            newPasswordError.set(context.getString(R.string.valid_password_error))
            isValid = false
        } else if ((newPassword.get().isNullOrBlank().not() &&  confirmPassword.get().isNullOrBlank())) {
            confirmPasswordError.set(context.getString(R.string.empty_confirm_password))
            isValid = false
        } else if (confirmPassword.get().isNullOrBlank().not() && newPassword.get().isNullOrBlank().not()) {
            if ((confirmPassword.get() == newPassword.get()).not()) {
                confirmPasswordError.set(context.getString(R.string.confirm_password_error))
                isValid = false
            }
        }
        return isValid
    }
}
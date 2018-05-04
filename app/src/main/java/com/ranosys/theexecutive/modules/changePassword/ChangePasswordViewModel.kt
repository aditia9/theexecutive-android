package com.ranosys.theexecutive.modules.changePassword

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.ObservableField
import android.support.design.widget.TextInputEditText
import android.text.TextUtils
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.utils.Utils


class ChangePasswordViewModel(application: Application) : BaseViewModel(application) {
    var currentPassword: ObservableField<String> = ObservableField()
    var newPassword: ObservableField<String> = ObservableField()
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

                override fun onSuccess(t: Boolean?) {
                    apiSuccessResponse?.value = t
                }
            })
        }
    }

    fun onTextChanged(et: TextInputEditText) {
        when (et.id) {
            R.id.et_new_password -> newPasswordError.set("")
            R.id.et_confirm_new_password -> confirmPasswordError.set("")
            R.id.et_current_password -> currentPasswordError.set("")
        }
    }

    fun validateData(context: Context): Boolean {

        var isValid = true

        if ((newPassword.get() == confirmPassword.get()).not()) {
            confirmPasswordError.set(context.getString(R.string.confirm_password_error))
            isValid = false
        }

        if (TextUtils.isEmpty(currentPassword.get())) {
            currentPasswordError.set(context.getString(R.string.empty_current_password))
            isValid = false
        }

        if (TextUtils.isEmpty(newPassword.get())) {
            newPasswordError.set(context.getString(R.string.empty_new_password))
            isValid = false
        } else if (TextUtils.isEmpty(confirmPassword.get())) {
            confirmPasswordError.set(context.getString(R.string.empty_confirm_password))
            isValid = false
        }

        if (!TextUtils.isEmpty(newPassword.get()) && !Utils.isValidPassword(newPassword.get())) {
            newPasswordError.set(context.getString(R.string.valid_password_error))
            isValid = false
        } else if (!TextUtils.isEmpty(confirmPassword.get()) && !Utils.isValidPassword(confirmPassword.get())) {
            confirmPasswordError.set(context.getString(R.string.valid_password_error))
            isValid = false
        } else if (!TextUtils.isEmpty(confirmPassword.get()) && !TextUtils.isEmpty(newPassword.get())) {
            if ((confirmPassword.get() == newPassword.get()).not()) {
                confirmPasswordError.set(context.getString(R.string.confirm_password_error))
                isValid = false
            }
        }

        if (!TextUtils.isEmpty(currentPassword.get()) && (!TextUtils.isEmpty(newPassword.get()))) {
            if (currentPassword.get() == newPassword.get()) {
                newPasswordError.set(context.getString(R.string.same_password_error))
                isValid = false
            }
        }

        return isValid
    }
}
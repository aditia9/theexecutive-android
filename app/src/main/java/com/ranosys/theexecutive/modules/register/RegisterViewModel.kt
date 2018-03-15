package com.ranosys.theexecutive.modules.register

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.ObservableField
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.RadioGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.utils.Utils

/**
 * Created by Mohammad Sunny on 31/1/18.
 */
class RegisterViewModel(application: Application): BaseViewModel(application) {
    var registerRequest: RegisterDataClass.RegisterRequest? = null
    var name: ObservableField<String>? = ObservableField()
    var mobile: ObservableField<String>? = ObservableField()
    var emailId: ObservableField<String>? = ObservableField()
    var gender: ObservableField<String>? = ObservableField()
    var state: ObservableField<String>? = ObservableField()
    var city: ObservableField<String>? = ObservableField()
    var password: ObservableField<String>? = ObservableField()
    val emailError = ObservableField<String>()
    val passwordError = ObservableField<String>()
    val nameError = ObservableField<String>()
    val mobileError = ObservableField<String>()
    val cityError = ObservableField<String>()
    val stateError = ObservableField<String>()
    var buttonClicked = MutableLiveData<Int>()
    var mutualresponse = MutableLiveData<ApiResponse<RegisterDataClass.RegisterResponse>>()

    init {
        this.registerRequest = registerRequest
        this.gender?.set("Male")
    }

    fun onSignupClick(view: View) {
        if(isValidData(view.context))
        buttonClicked.value = view.id
        this.registerRequest = RegisterDataClass.RegisterRequest(
                name?.get().toString(), mobile?.get().toString(),
                emailId?.get().toString(),city?.get().toString(),
                state?.get().toString(), gender?.get().toString(),
                password?.get().toString()
        )
    }

    private fun isValidData(context: Context): Boolean {
        var isValid = true
        if (TextUtils.isEmpty(emailId?.get())) {
            emailError.set(context.getString(R.string.empty_email))
            isValid = false
        } else if (!Utils.isValidEmail(emailId?.get())) {
            emailError.set(context.getString(R.string.provide_valid_email))
            isValid = false
        }
        if (TextUtils.isEmpty(password?.get())) {
            passwordError.set(context.getString(R.string.empty_password))
            isValid = false
        }
        if (TextUtils.isEmpty(name?.get())){
            nameError.set(context.getString(R.string.name_error))
            isValid = false
        }
        if (TextUtils.isEmpty(mobile?.get())){
            mobileError.set(context.getString(R.string.mobile_error))
            isValid = false
        }
        if (TextUtils.isEmpty(city?.get())){
            cityError.set(context.getString(R.string.city_error))
            isValid = false
        }
        if (TextUtils.isEmpty(state?.get())){
            stateError.set(context.getString(R.string.state_error))
            isValid = false
        }
        return isValid
    }

    fun onRadioClick(radioGroup: RadioGroup, id: Int) {
        when (id) {
//            R.id.radio_male -> gender?.set("Male")
//            R.id.radio_female -> gender?.set("Female")
        }
        Log.e("Gender", gender?.get())
    }

    fun onStateSelected(parent: AdapterView<Adapter>, v: View, position: Int, id: Long) {

    }
}

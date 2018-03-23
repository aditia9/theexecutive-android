package com.ranosys.theexecutive.modules.register

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.ObservableField
import android.text.TextUtils
import android.view.View
import android.widget.RadioGroup
import android.widget.Spinner
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.utils.Utils

/**
 * Created by Mohammad Sunny on 31/1/18.
 */
class RegisterViewModel(application: Application): BaseViewModel(application) {
    var registerRequest: RegisterDataClass.RegisterRequest? = null
    var firstName: ObservableField<String> = ObservableField()
    var firstNameError: ObservableField<String> = ObservableField()
    var lastName: ObservableField<String> = ObservableField()
    var lastNameError: ObservableField<String> = ObservableField()
    var emailAddress: ObservableField<String> = ObservableField()
    var emailAddressError: ObservableField<String> = ObservableField()
    var mobileNumber: ObservableField<String> = ObservableField()
    var mobileNumberError: ObservableField<String> = ObservableField()
    var dob: ObservableField<String> = ObservableField()
    var dobError: ObservableField<String> = ObservableField()
    var streetAddress1: ObservableField<String> = ObservableField()
    var streetAddress1Error: ObservableField<String> = ObservableField()
    var streetAddress2: ObservableField<String> = ObservableField()
    var streetAddress2Error: ObservableField<String> = ObservableField()
    var state: ObservableField<String> = ObservableField()
    var city: ObservableField<String> = ObservableField()
    var country: ObservableField<String> = ObservableField()
    var selectedcountry: ObservableField<RegisterDataClass.Country> = ObservableField()
    var postalCode: ObservableField<String> = ObservableField()
    var postalCodeError: ObservableField<String> = ObservableField()
    var password: ObservableField<String> = ObservableField()
    val passwordError = ObservableField<String>()
    var confirmPassword: ObservableField<String> = ObservableField()
    val confirmPasswordError = ObservableField<String>()
    var mutualresponse = MutableLiveData<ApiResponse<RegisterDataClass.RegisterResponse>>()

    var countryList :MutableList<RegisterDataClass.Country> = mutableListOf<RegisterDataClass.Country>()

    init {
        countryList.add(RegisterDataClass.Country(available_regions = null))
    }



    private fun isValidData(context: Context): Boolean {
        var isValid = true

        if (TextUtils.isEmpty(firstName.get())){
            firstNameError.set(context.getString(R.string.first_name_error))
            isValid = false
        }

        if (TextUtils.isEmpty(lastName.get())){
            lastNameError.set(context.getString(R.string.last_name_error))
            isValid = false
        }

        if (TextUtils.isEmpty(emailAddress.get())) {
            emailAddressError.set(context.getString(R.string.empty_email))
            isValid = false
        } else if (!Utils.isValidEmail(emailAddress.get())) {
            emailAddressError.set(context.getString(R.string.provide_valid_email))
            isValid = false
        }

        if (TextUtils.isEmpty(dob.get())){
            dobError.set(context.getString(R.string.dob_error))
            isValid = false
        }

        if (TextUtils.isEmpty(postalCode.get())){
            postalCodeError.set(context.getString(R.string.postal_error))
            isValid = false
        }

        if (TextUtils.isEmpty(password.get())) {
            passwordError.set(context.getString(R.string.empty_password))
            isValid = false
        }else if(!Utils.isValidPassword(password.get())){
            passwordError.set(context.getString(R.string.valid_password_error))
            isValid = false
        }

        if((password.get() == confirmPassword.get()).not()){
            confirmPasswordError.set(context.getString(R.string.confirm_password_error))
            isValid = false
        }

        return isValid
    }

    fun onCountrySelection(countrySpinner: View, position: Int){
        selectedcountry.set(countryList[position])
        //TODO - update data set for province spinner
    }

    fun onStateSelection(stateSpinner: Spinner){
        state.set(stateSpinner.selectedItem.toString())
        //TODO - call respective city for city api spinner
        val stateCode = 543 // will be replaced by actual code of selected state
        callCityApi(stateCode)
    }

    private fun callCityApi(stateCode: Int) {
        AppRepository.getCityList(stateCode, object : ApiCallback<List<RegisterDataClass.City>>{
            override fun onException(error: Throwable) {
                Utils.printLog("City API", "error")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("City API", "error")
            }

            override fun onSuccess(t: List<RegisterDataClass.City>?) {
                //TODO- set value in city spinner
            }

        })
    }

    fun onRadioClick(radioGroup: RadioGroup, id: Int) {
        when (id) {
//            R.id.radio_male -> gender?.set("Male")
//            R.id.radio_female -> gender?.set("Female")
        }

    }



    fun callCountryApi() {
        AppRepository.getCountryList(object : ApiCallback<List<RegisterDataClass.Country>>{
            override fun onException(error: Throwable) {
                Utils.printLog("Country API", "error")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("Country API", "error")
                var countries = ArrayList<RegisterDataClass.Country>()
                countries.add(RegisterDataClass.Country(full_name_locale = "india", available_regions = null))
                countries.add(RegisterDataClass.Country(full_name_locale = "pak",available_regions = null))
                countries.add(RegisterDataClass.Country(full_name_locale = "shrilanks",available_regions = null))
                countries.add(RegisterDataClass.Country(full_name_locale = "bhutan",available_regions = null))
                countries.add(RegisterDataClass.Country(full_name_locale = "usa",available_regions = null))
                countries.add(RegisterDataClass.Country(full_name_locale = "uk",available_regions = null))
                countries.add(RegisterDataClass.Country(full_name_locale = "Australia",available_regions = null))
                countries.add(RegisterDataClass.Country(full_name_locale = "indinesia",available_regions = null))

                countryList.addAll(countries as ArrayList<RegisterDataClass.Country>)
            }

            override fun onSuccess(countries: List<RegisterDataClass.Country>?) {
                //TODO - set value in country spinner
                countryList.addAll(countries as ArrayList<RegisterDataClass.Country>)
            }
        })
    }


    fun callRegisterApi() {
        if(isValidData(getApplication())){
            //TODO - call register api
        }
    }
}

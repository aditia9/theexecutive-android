package com.ranosys.theexecutive.modules.register

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.ObservableField
import android.support.design.widget.TextInputEditText
import android.text.TextUtils
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.modules.login.LoginDataClass
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils

/**
 * Created by Mohammad Sunny on 31/1/18.
 */
class RegisterViewModel(application: Application): BaseViewModel(application) {
    var firstName: ObservableField<String> = ObservableField()
    var firstNameError: ObservableField<String> = ObservableField()
    var lastName: ObservableField<String> = ObservableField()
    var lastNameError: ObservableField<String> = ObservableField()
    var emailAddress: ObservableField<String> = ObservableField()
    var emailAddressError: ObservableField<String> = ObservableField()
    var mobileNumber: ObservableField<String> = ObservableField()
    var mobileNumberError: ObservableField<String> = ObservableField()
    var selectedGender: ObservableField<Int> = ObservableField(MALE)
    var dob: ObservableField<String> = ObservableField()
    var dobError: ObservableField<String> = ObservableField()
    var streetAddress1: ObservableField<String> = ObservableField()
    var streetAddress1Error: ObservableField<String> = ObservableField()
    var streetAddress2: ObservableField<String> = ObservableField()
    var streetAddress2Error: ObservableField<String> = ObservableField()
    var selectedCity: ObservableField<RegisterDataClass.City> = ObservableField()
    var selectedcountry: ObservableField<RegisterDataClass.Country> = ObservableField()
    var selectedState: ObservableField<RegisterDataClass.State> = ObservableField()
    var postalCode: ObservableField<String> = ObservableField()
    var postalCodeError: ObservableField<String> = ObservableField()
    var password: ObservableField<String> = ObservableField()
    val passwordError = ObservableField<String>()
    var confirmPassword: ObservableField<String> = ObservableField()
    val confirmPasswordError = ObservableField<String>()

    var countryList :MutableList<RegisterDataClass.Country> = mutableListOf<RegisterDataClass.Country>()
    var stateList :MutableList<RegisterDataClass.State> = mutableListOf<RegisterDataClass.State>()
    var cityList :MutableList<RegisterDataClass.City> = mutableListOf<RegisterDataClass.City>()

    var isSocialLogin: Boolean = false

    var apiFailureResponse: MutableLiveData<String>? = MutableLiveData()

    companion object {
        const val MALE = 1
        const val FEMALE = 2
    }

    init {
        countryList.add(RegisterDataClass.Country(full_name_locale = "Country", available_regions = null))
        stateList.add(RegisterDataClass.State(name = "State"))
        cityList.add(RegisterDataClass.City(name = "City"))
    }


    fun onCountrySelection(countrySpinner: View, position: Int){
        if(position > 0){
            selectedcountry.set(countryList[position])
            stateList.clear()
            selectedcountry.get().available_regions?.run{
                stateList.addAll(selectedcountry.get().available_regions as ArrayList)
            }
        }else{
            stateList.clear()
            stateList.add(RegisterDataClass.State(name = "City"))
        }
    }

    fun onStateSelection(stateSpinner: View, position: Int){
        if(position > 0){
            selectedState.set(stateList[position])
            callCityApi(selectedState.get().id)
        }
    }

    fun onCitySelection(citySpinner: View, position: Int){
        if(position > 0){
            selectedCity.set(cityList[position])
        }
    }

    private fun callCityApi(stateCode: String) {
        AppRepository.getCityList("543", object : ApiCallback<List<RegisterDataClass.City>>{
            override fun onException(error: Throwable) {
                Utils.printLog("City API", "error")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("City API", "error")
                Toast.makeText(getApplication<Application>(), errorMsg, Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(cities: List<RegisterDataClass.City>?) {
                //TODO- set value in city spinner
                cities?.run {
                    if(cities.isNotEmpty()){
                        cityList.clear()
                        cityList.addAll(cities)
                    }
                }
            }

        })
    }

    fun callCountryApi() {
        AppRepository.getCountryList(object : ApiCallback<List<RegisterDataClass.Country>>{
            override fun onException(error: Throwable) {
                Utils.printLog("Country API", "error")
            }

            override fun onError(errorMsg: String) {
                Utils.printLog("Country API", "error")
            }

            override fun onSuccess(countries: List<RegisterDataClass.Country>?) {
                //TODO - set value in country spinner
                countryList.addAll(countries as ArrayList<RegisterDataClass.Country>)
            }
        })
    }



    fun callRegisterApi() {
        if(isValidData(getApplication())){
            //TODO - create register request
            val address = RegisterDataClass.Address(region_id = 1, //region_id = selectedState?.get().id as Int,
                    firstname = firstName.get(),
                    lastname = lastName.get(),
                    telephone = mobileNumber.get(),
                    city = "test",//  city = selectedCity.get().name,
                    postcode = postalCode.get(),
                    default_billing = true,
                    default_shipping = true,
                    country_id = "1",
                    //country_id = selectedcountry.get().id,
                    street = listOf(streetAddress1.get(), streetAddress2.get()),
                    region = RegisterDataClass.Region(region_code = "1", //region_code = selectedState.get().code,
                            region_id = 1,
                            //region_id = selectedState.get().id as Int,
                            region = "1")
                            //region = selectedState.get().name)

            )

            val customer = RegisterDataClass.Customer(
                    group_id = Constants.DEFAULT_GROUP_ID,
                    confirmation = if(isSocialLogin) Constants.SOCIAL_LOGIN_CONFIRMATION else "",
                    gender = selectedGender.get(),
                    email = emailAddress.get(),
                    firstname = firstName.get(),
                    lastname = lastName.get(),
                    store_id = SavedPreferences.getInstance()?.getIntValue(Constants.STORE_ID_KEY)!!,
                    website_id = SavedPreferences.getInstance()?.getIntValue(Constants.SELECTED_WEBSITE_ID_KEY)!!,
                    dob = "22-01-1993",
                    //dob = dob.get(),
                    addresses = listOf(address))

            val password = password.get()
            val registerRequest = RegisterDataClass.RegisterRequest(customer, password)

            //TODO - call register API
            AppRepository.registrationApi(registerRequest, object: ApiCallback<String>{
                override fun onException(error: Throwable) {
                    Utils.printLog("Registration API", "error")
                }

                override fun onError(errorMsg: String) {
                    Utils.printLog("Registration API", "error")
                }

                override fun onSuccess(t: String?) {
                    Utils.printLog("Registration API", "Success")
                    if(isSocialLogin){
                        //TODO -  if social registration Login user and redirect to home screen
                        callLoginApi()
                    }else{
                        //TODO  - redirect to Login screen with message

                    }
                }

            })
        }
    }


    private fun callLoginApi() {
        val loginRequest = LoginDataClass.LoginRequest(emailAddress.get().toString(), password.get().toString())

        AppRepository.login(loginRequest, object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                Utils.printLog("Login Api", "error")
                apiFailureResponse?.value = Constants.UNKNOWN_ERROR

            }

            override fun onError(errorMsg: String) {
                Utils.printLog("Login Api", "error")
                apiFailureResponse?.value = errorMsg
            }

            override fun onSuccess(userToken: String?) {
                //save customer token
                SavedPreferences.getInstance()?.saveStringValue(userToken!!, Constants.USER_ACCESS_TOKEN_KEY)
                //TODO - Move to Home screen

            }
        })
    }

    fun onGenderSelection(view: RadioGroup, id:Int){
        when(id){
            R.id.rb_male -> selectedGender.set(MALE)
            R.id.rb_female -> selectedGender.set(FEMALE)
        }
    }

    fun onTextChanged(et: TextInputEditText){
        when(et.id){
            R.id.et_first_name -> firstNameError.set("")
            R.id.et_last_name -> lastNameError.set("")
            R.id.et_email_address -> emailAddressError.set("")
            R.id.et_mobile_number -> mobileNumberError.set("")
            R.id.et_address_1 -> streetAddress1Error.set("")
            R.id.et_postcode -> postalCodeError.set("")
            R.id.et_password -> passwordError.set("")
            R.id.et_confirm_password -> confirmPasswordError.set("")
        }
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

        if (TextUtils.isEmpty(mobileNumber.get())){
            mobileNumberError.set(context.getString(R.string.mobile_error))
            isValid = false
        }

//        if (TextUtils.isEmpty(dob.get())){
//            dobError.set(context.getString(R.string.dob_error))
//            isValid = false
//        }

        if (TextUtils.isEmpty(streetAddress1.get()) && TextUtils.isEmpty(streetAddress2.get())){
            streetAddress1Error.set(context.getString(R.string.street_address_1))
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

}

package com.ranosys.theexecutive.modules.register

import AppLog
import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.ObservableField
import android.support.design.widget.TextInputEditText
import android.text.TextUtils
import android.view.View
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.modules.login.LoginDataClass
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Mohammad Sunny on 31/1/18.
 */
class RegisterViewModel(application: Application): BaseViewModel(application) {
    var firstName: ObservableField<String> = ObservableField()
    var lastName: ObservableField<String> = ObservableField()
    var firstNameError: ObservableField<String> = ObservableField()
    var emailAddressError: ObservableField<String> = ObservableField()
    var lastNameError: ObservableField<String> = ObservableField()
    val passwordError = ObservableField<String>()
    val confirmPasswordError = ObservableField<String>()
    var mobileNumberError: ObservableField<String> = ObservableField()
    var streetAddress1Error: ObservableField<String> = ObservableField()
    var postalCodeError: ObservableField<String> = ObservableField()
    var emailAddress: ObservableField<String> = ObservableField()
    var mobileNumber: ObservableField<String> = ObservableField()
    var countryCode: ObservableField<String> = ObservableField()
    var selectedGender: ObservableField<Int> = ObservableField(MALE)
    var dob: ObservableField<Date> = ObservableField()
    var dobError: ObservableField<String> = ObservableField()
    var streetAddress1: ObservableField<String> = ObservableField()
    var streetAddress2: ObservableField<String> = ObservableField()
    var postalCode: ObservableField<String> = ObservableField()
    var password: ObservableField<String> = ObservableField()
    var confirmPassword: ObservableField<String> = ObservableField()
    val isSubscribed = ObservableField<Boolean>(false)

    var countryList :MutableList<RegisterDataClass.Country> = mutableListOf<RegisterDataClass.Country>()
    var stateList :MutableList<RegisterDataClass.State> = mutableListOf<RegisterDataClass.State>()
    var cityList :MutableList<RegisterDataClass.City> = mutableListOf<RegisterDataClass.City>()
    var selectedcountry: ObservableField<RegisterDataClass.Country> = ObservableField()
    var selectedState: ObservableField<RegisterDataClass.State> = ObservableField()
    var selectedCity: ObservableField<RegisterDataClass.City> = ObservableField()
    val countryHint:RegisterDataClass.Country = RegisterDataClass.Country(full_name_locale = Constants.COUNTRY_LABEL)
    val stateHint:RegisterDataClass.State = RegisterDataClass.State(name = Constants.STATE_LABEL)
    val cityHint:RegisterDataClass.City = RegisterDataClass.City(name = Constants.CITY_LABEL)

    var isSocialLogin: Boolean = false

    var apiFailureResponse: MutableLiveData<String>? = MutableLiveData()
    var apiSocialRegResponse: MutableLiveData<String>? = MutableLiveData()
    var apiDirectRegSuccessResponse: MutableLiveData<RegisterDataClass.RegistrationResponse>? = MutableLiveData()
    var userCartIdResponse: MutableLiveData<ApiResponse<String>>? = MutableLiveData()
    var userCartCountResponse: MutableLiveData<ApiResponse<String>>? = MutableLiveData()

    companion object {
        const val MALE = 1
        const val FEMALE = 2

        //error tags
        val ERROR_TAG = "error"
        val COUNTRY_API_TAG = "Country Api"
        val CITY_API_TAG = "City Api"
        val REGISTRATION_API_TAG = "Registration Api"
        val LOGIN_API_TAG = "Login Api"
    }

    init {
        countryList.add(countryHint)
        stateList.add(stateHint)
        cityList.add(cityHint)

        selectedcountry.set(countryHint)
        selectedState.set(stateHint)
        selectedCity.set(cityHint)
    }


    fun callCountryApi() {
        AppRepository.getCountryList(object : ApiCallback<List<RegisterDataClass.Country>>{
            override fun onException(error: Throwable) {
                Utils.printLog(COUNTRY_API_TAG, error.message!!)
            }

            override fun onError(errorMsg: String) {
                Utils.printLog(COUNTRY_API_TAG, errorMsg)
            }

            override fun onSuccess(countries: List<RegisterDataClass.Country>?) {
                if(null != countries && countries.isNotEmpty()){
                    countryList.addAll(countries as ArrayList<RegisterDataClass.Country>)
                }
            }
        })
    }

    fun onCountrySelection(countrySpinner: View, position: Int, stateSpinner: Spinner){
        if(countryHint != countryList[position]){
            selectedcountry.set(countryList[position])
            if(selectedcountry.get().available_regions.isNotEmpty()){
                stateList.addAll(selectedcountry.get().available_regions as ArrayList)

            }else{
                Utils.printLog(ERROR_TAG, getApplication<Application>().getString(R.string.no_state_available_error))
            }

        }else{
            stateList.clear()
            stateList.add(stateHint)
            selectedState.set(stateHint)
            selectedCity.set(cityHint)
        }
        stateSpinner.setSelection(0)
    }

    fun onStateSelection(stateSpinner: View, position: Int, citySpinner: Spinner){
        if(stateHint != stateList[position]){
            selectedState.set(stateList[position])
            callCityApi(selectedState.get().id)
        }else{
            cityList.clear()
            cityList.add(cityHint)
            selectedCity.set(cityHint)
        }
        citySpinner.setSelection(0)
    }

    fun onCitySelection(citySpinner: View, position: Int){
        if(cityHint != cityList[position]){
            selectedCity.set(cityList[position])
        }
    }

    fun onCountryCodeSelection(countryCodeSpinner: View, position: Int){
        countryCode.set((countryCodeSpinner as Spinner).selectedItem.toString())
    }

    private fun callCityApi(stateCode: String) {
        AppRepository.getCityList(stateCode, object : ApiCallback<List<RegisterDataClass.City>>{
            override fun onException(error: Throwable) {
                Utils.printLog(CITY_API_TAG, ERROR_TAG)
            }

            override fun onError(errorMsg: String) {
                Utils.printLog(CITY_API_TAG, ERROR_TAG)
                Toast.makeText(getApplication<Application>(), errorMsg, Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(cities: List<RegisterDataClass.City>?) {
                cities?.run {
                    if(cities.isNotEmpty()){
                        cityList.addAll(cities)
                    }else{
                        Utils.printLog(ERROR_TAG, getApplication<Application>().getString(R.string.no_cities_available_error))
                    }
                }
            }

        })
    }


    fun callRegisterApi() {
        if(isValidData(getApplication())){
            val address = RegisterDataClass.Address(region_id = selectedState.get().id,
                    firstname = firstName.get(),
                    lastname = lastName.get(),
                    telephone = "${countryCode.get()}-${ mobileNumber.get()}",
                    city =      selectedCity.get().name ,
                    postcode = postalCode.get(),
                    default_billing = true,
                    default_shipping = true,
                    country_id = selectedcountry.get().id,
                    street = listOf(streetAddress1.get(), streetAddress2.get()?:""),
                    region = RegisterDataClass.Region(region_code = selectedState.get().code,
                            region_id = if(!TextUtils.isEmpty(selectedState.get().id)) selectedState.get().id.toInt() else 1,
                            region = selectedState.get().name)

            )

            val customer = RegisterDataClass.Customer(
                    group_id = Constants.DEFAULT_GROUP_ID,
                    confirmation = if(isSocialLogin) Constants.SOCIAL_LOGIN_CONFIRMATION else "",
                    gender = selectedGender.get(),
                    email = emailAddress.get(),
                    firstname = firstName.get(),
                    lastname = lastName.get(),
                    store_id = SavedPreferences.getInstance()?.getIntValue(Constants.SELECTED_STORE_ID_KEY)!!,
                    website_id = SavedPreferences.getInstance()?.getIntValue(Constants.SELECTED_WEBSITE_ID_KEY)!!,
                    dob = SimpleDateFormat(Constants.YY_MM__DD_DATE_FORMAT).format(dob.get()),
                    addresses = listOf(address),
                    extension_attributes = RegisterDataClass.ExtensionAttributes(is_subscribed = isSubscribed.get() ))

            val password = password.get()
            val registerRequest = RegisterDataClass.RegisterRequest(customer, password)

            AppRepository.registrationApi(registerRequest, object: ApiCallback<RegisterDataClass.RegistrationResponse>{
                override fun onException(error: Throwable) {
                    apiFailureResponse?.value = error.message
                }

                override fun onError(errorMsg: String) {
                    apiFailureResponse?.value = errorMsg
                }

                override fun onSuccess(response: RegisterDataClass.RegistrationResponse?) {
                    if(isSocialLogin) callLoginApi() else apiDirectRegSuccessResponse?.value = response
                }

            })
        }
    }


    private fun callLoginApi() {
        val loginRequest = LoginDataClass.LoginRequest(emailAddress.get().toString(), password.get().toString(), SavedPreferences.getInstance()?.getStringValue(Constants.USER_FCM_ID), Constants.OS_TYPE,
                SavedPreferences.getInstance()?.getStringValue(Constants.ANDROID_DEVICE_ID_KEY))

        AppRepository.login(loginRequest, object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                apiFailureResponse?.value = Constants.UNKNOWN_ERROR

            }

            override fun onError(errorMsg: String) {
                apiFailureResponse?.value = errorMsg
            }

            override fun onSuccess(userToken: String?) {
                SavedPreferences.getInstance()?.saveStringValue(emailAddress.get(), Constants.USER_EMAIL)
                SavedPreferences.getInstance()?.saveStringValue(userToken!!, Constants.USER_ACCESS_TOKEN_KEY)
                SavedPreferences.getInstance()?.saveStringValue(userToken!!, Constants.USER_ACCESS_TOKEN_KEY)

                val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY)?: ""
                if(guestCartId.isNotBlank()){
                    mergeCart(guestCartId)
                }else{
                    apiSocialRegResponse?.value = userToken
                }

            }
        })
    }

    private fun mergeCart(guestCartId: String) {
        AppRepository.cartMergeApi(guestCartId, object: ApiCallback<String>{
            override fun onException(error: Throwable) {
                AppLog.d("cart merge api : ${error.message}")
            }

            override fun onError(errorMsg: String) {
                AppLog.d("cart merge api : ${errorMsg}")
            }

            override fun onSuccess(t: String?) {
                //delete guest cart id
                SavedPreferences.getInstance()?.saveStringValue("", Constants.GUEST_CART_ID_KEY)
                apiSocialRegResponse?.value = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
            }

        })
    }

    fun getCartIdForUser(userToken: String?){
        val apiResponse = ApiResponse<String>()
        AppRepository.createUserCart(object : ApiCallback<String> {
            override fun onException(error: Throwable) {
                userCartIdResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                userCartIdResponse?.value?.error = errorMsg
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                SavedPreferences.getInstance()?.saveStringValue(t, Constants.USER_CART_ID_KEY)
                userCartIdResponse?.value = apiResponse
            }

        })
    }

    fun getUserCartCount() {
        val apiResponse = ApiResponse<String>()
        AppRepository.cartCountUser(object : ApiCallback<String>{
            override fun onException(error: Throwable) {
                userCartCountResponse?.value?.throwable = error
            }

            override fun onError(errorMsg: String) {
                userCartCountResponse?.value?.error = errorMsg
            }

            override fun onSuccess(t: String?) {
                apiResponse.apiResponse = t
                userCartCountResponse?.value = apiResponse
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
            R.id.et_dob -> dobError.set("")
        }
    }

    fun isValidData(context: Context): Boolean {
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
        }else if(!Utils.isValidMobile(mobileNumber.get())){
            mobileNumberError.set(context.getString(R.string.valid_mobile_error))
            isValid = false
        }

        if (TextUtils.isEmpty(dob.get()?.toString())){
            dobError.set(context.getString(R.string.dob_error))
            isValid = false
        }


        if (TextUtils.isEmpty(streetAddress1.get())){
            streetAddress1Error.set(context.getString(R.string.street_address_error))
            isValid = false
        }

        if(selectedcountry.get() == countryHint){
            Toast.makeText(context, context.getString(R.string.empty_country_error), Toast.LENGTH_SHORT ).show()
            isValid = false
        }else if(selectedState.get() == stateHint){
            Toast.makeText(context, context.getString(R.string.empty_state_error), Toast.LENGTH_SHORT ).show()
            isValid = false
        }else if(selectedCity.get() == cityHint){
            Toast.makeText(context, context.getString(R.string.empty_city_error), Toast.LENGTH_SHORT ).show()
            isValid = false
        }

        if (TextUtils.isEmpty(postalCode.get())){
            postalCodeError.set(context.getString(R.string.postal_error))
            isValid = false
        }else if(!Utils.isValidPincode(postalCode.get())){
            postalCodeError.set(context.getString(R.string.valid_postal_error))
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

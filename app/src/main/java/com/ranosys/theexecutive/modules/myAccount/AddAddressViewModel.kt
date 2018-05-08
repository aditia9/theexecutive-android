package com.ranosys.theexecutive.modules.myAccount

import AppLog
import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.support.design.widget.TextInputEditText
import android.text.TextUtils
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.modules.register.RegisterDataClass
import com.ranosys.theexecutive.modules.register.RegisterViewModel
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.GlobalSingelton
import com.ranosys.theexecutive.utils.Utils
import java.util.*

/**
 * @Details view model for add address
 * @Author Ranosys Technologies
 * @Date 07-May-2018
 */
class AddAddressViewModel(application: Application): BaseViewModel(application) {

    var countryListApiResponse : MutableLiveData<ApiResponse<MutableList<RegisterDataClass.Country>>> = MutableLiveData()
    var countryList : ObservableArrayList<RegisterDataClass.Country> = ObservableArrayList()
    var stateList : ObservableArrayList<RegisterDataClass.State> = ObservableArrayList()
    var cityList : ObservableArrayList<RegisterDataClass.City> = ObservableArrayList()
    var maskedAddress: MyAccountDataClass.MaskedUserInfo? = null
    var firstNameError: ObservableField<String> = ObservableField()
    var lastNameError: ObservableField<String> = ObservableField()
    var mobileNumberError: ObservableField<String> = ObservableField()
    var streetAddress1Error: ObservableField<String> = ObservableField()
    var postalCodeError: ObservableField<String> = ObservableField()
    var updateAddressApiResponse : MutableLiveData<ApiResponse<MyAccountDataClass.UserInfoResponse>> = MutableLiveData()

    private val cityHint: RegisterDataClass.City = RegisterDataClass.City(name = Constants.CITY_LABEL)



    fun callCountryApi() {
        val apiResponse = ApiResponse<MutableList<RegisterDataClass.Country>>()
        AppRepository.getCountryList(object : ApiCallback<List<RegisterDataClass.Country>> {
            override fun onException(error: Throwable) {
                AppLog.e(RegisterViewModel.COUNTRY_API_TAG, error.message!!)
                apiResponse.error = error.message
                countryListApiResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                AppLog.e(RegisterViewModel.COUNTRY_API_TAG, errorMsg)
                apiResponse.error = errorMsg
                countryListApiResponse.value = apiResponse
            }

            override fun onSuccess(countries: List<RegisterDataClass.Country>?) {
                if(null != countries && countries.isNotEmpty()){

                    apiResponse.apiResponse = countries.toMutableList()
                    countryList.addAll(countries.toMutableList())

                    if(countryList.isNotEmpty()){
                        stateList.addAll(apiResponse.apiResponse!![0].available_regions)
                        if(stateList.isNotEmpty()){
                            callCityApi(stateList[0].id)
                        }
                    }

                    countryListApiResponse.value = apiResponse

                }
            }
        })
    }

    fun onCountrySelection(position: Int){
        stateList.clear()
        stateList.addAll(countryList[position]?.available_regions as ArrayList<RegisterDataClass.State>)
    }


    fun onStateSelection(position: Int){
        cityList.clear()
        cityList.add(cityHint)
        callCityApi(stateList[position].id)
    }


    fun prepareMaskedAddress(){

        maskedAddress = MyAccountDataClass.MaskedUserInfo(
                _id = "",
                _firstName = "",
                _lastName = "",
                _email = "",
                _country = "",
                _city = "",
                _state = "",
                _streedAdd1 = "",
                _streedAdd2 = "",
                _mobile = "",
                _postalCode = "",
                _countryCode = ""
        )

    }

    private fun callCityApi(stateCode: String) {
        AppRepository.getCityList(stateCode, object : ApiCallback<List<RegisterDataClass.City>> {
            override fun onException(error: Throwable) {
                Utils.printLog(RegisterViewModel.CITY_API_TAG, RegisterViewModel.ERROR_TAG)
            }

            override fun onError(errorMsg: String) {
                Utils.printLog(RegisterViewModel.CITY_API_TAG, RegisterViewModel.ERROR_TAG)
                Toast.makeText(getApplication(), errorMsg, Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(cities: List<RegisterDataClass.City>?) {
                cities?.run {
                    cityList.clear()
                    cityList.addAll(cities.toMutableList())
                }
            }

        })
    }


    fun onTextChanged(et: TextInputEditText){
        when(et.id){
            R.id.et_first_name -> firstNameError.set("")
            R.id.et_last_name -> lastNameError.set("")
            R.id.et_mobile_number -> mobileNumberError.set("")
            R.id.et_address_1 -> streetAddress1Error.set("")
            R.id.et_zip_code -> postalCodeError.set("")
        }
    }

    fun isValidData(context: Context): Boolean {
        var isValid = true

        if (TextUtils.isEmpty(maskedAddress?.firstName)){
            firstNameError.set(context.getString(R.string.first_name_error))
            isValid = false
        }

        if (TextUtils.isEmpty(maskedAddress?.lastName)){
            lastNameError.set(context.getString(R.string.last_name_error))
            isValid = false
        }


        if (TextUtils.isEmpty(maskedAddress?.mobile)){
            mobileNumberError.set(context.getString(R.string.mobile_error))
            isValid = false
        }else if(!Utils.isValidMobile(maskedAddress?.mobile ?: "")){
            mobileNumberError.set(context.getString(R.string.valid_mobile_error))
            isValid = false
        }

        if (TextUtils.isEmpty(maskedAddress?.streedAdd1) && TextUtils.isEmpty(maskedAddress?.streedAdd2)){
            streetAddress1Error.set(context.getString(R.string.street_address_error))
            isValid = false
        }

        if (TextUtils.isEmpty(maskedAddress?.postalCode)){
            postalCodeError.set(context.getString(R.string.postal_error))
            isValid = false
        }

        return isValid
    }

    fun addAddress() {

        val userInfo = GlobalSingelton.instance?.userInfo?.copy()
        val mobile = "${maskedAddress?.countryCode}-${maskedAddress?.mobile}"
        val selectedState = stateList.single { it.name == maskedAddress?.state }

        val newAddress = userInfo?.addresses!![0].copy(
                id = null,
                firstname = maskedAddress?.firstName,
                lastname = maskedAddress?.lastName,
                street = listOf(maskedAddress?.streedAdd1,maskedAddress?.streedAdd2) as List<String>,
                telephone = mobile,
                country_id = Utils.getCountryId(maskedAddress?.country),
                city = maskedAddress?.city,
                postcode = maskedAddress?.postalCode,
                region_id = if(selectedState.id.isBlank().not()) selectedState.id else "",
                region = RegisterDataClass.Region(region_code = selectedState.code,
                        region_id = if(!TextUtils.isEmpty(selectedState.id)) selectedState.id.toInt() else 1,
                        region = selectedState.name),

                default_billing = null,
                default_shipping = null

        )

        userInfo.addresses?.add(newAddress)

        val editAddressRequest = MyAccountDataClass.UpdateInfoRequest(
                customer = userInfo
        )

        val apiResponse = ApiResponse<MyAccountDataClass.UserInfoResponse>()
        AppRepository.updateUserInfo(editAddressRequest, object: ApiCallback<MyAccountDataClass.UserInfoResponse> {
            override fun onException(error: Throwable) {
                AppLog.e("Update Information API : ${error.message}")
                apiResponse.error = error.message
                updateAddressApiResponse.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                AppLog.e("Update Information API : $errorMsg")
                apiResponse.error = errorMsg
                updateAddressApiResponse.value = apiResponse
            }

            override fun onSuccess(t: MyAccountDataClass.UserInfoResponse?) {
                //update info saved at singleton
                GlobalSingelton.instance?.userInfo = t

                apiResponse.apiResponse = t
                updateAddressApiResponse.value = apiResponse
            }
        })
    }


}
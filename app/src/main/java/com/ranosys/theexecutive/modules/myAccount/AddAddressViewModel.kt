package com.ranosys.theexecutive.modules.myAccount

import AppLog
import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.widget.Spinner
import android.widget.Toast
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.modules.register.RegisterDataClass
import com.ranosys.theexecutive.modules.register.RegisterViewModel
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.GlobalSingelton
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
import java.util.*

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 03-May-2018
 */
class AddAddressViewModel(application: Application): BaseViewModel(application) {

    var countryList :MutableLiveData<ApiResponse<MutableList<RegisterDataClass.Country>>> = MutableLiveData()
    var countryListApiResponse :ApiResponse<MutableList<RegisterDataClass.Country>>? = null
    var stateList :MutableList<RegisterDataClass.State> = mutableListOf()
    var cityList :MutableList<RegisterDataClass.City> = mutableListOf()
    var maskedAddress: MyAccountDataClass.MaskedUserInfo? = null

    var selectedCountry: ObservableField<String> = ObservableField()
    var selectedCity: ObservableField<String> = ObservableField()
    var selectedState: ObservableField<String> = ObservableField()

    fun callCountryApi() {
        var apiResponse = ApiResponse<MutableList<RegisterDataClass.Country>>()
        AppRepository.getCountryList(object : ApiCallback<List<RegisterDataClass.Country>> {
            override fun onException(error: Throwable) {
                AppLog.e(RegisterViewModel.COUNTRY_API_TAG, error.message!!)
                apiResponse.error = error.message
                countryList.value = apiResponse
            }

            override fun onError(errorMsg: String) {
                AppLog.e(RegisterViewModel.COUNTRY_API_TAG, errorMsg)
                apiResponse.error = errorMsg
                countryList.value = apiResponse
            }

            override fun onSuccess(countries: List<RegisterDataClass.Country>?) {
                if(null != countries && countries.isNotEmpty()){
                    apiResponse.apiResponse = countries.toMutableList()
                    countryListApiResponse = apiResponse
                    stateList = apiResponse.apiResponse!!.single { it.full_name_english == maskedAddress?.country }.available_regions.toMutableList()

                    val temp = stateList.filter { it.name == maskedAddress?.state }
                    if(temp.isNotEmpty()){
                        callCityApi(temp[0].id)
                    }

                }
            }
        })
    }

    fun onCountrySelection(position: Int, stateSpinner: Spinner){
        AppLog.e(RegisterViewModel.COUNTRY_API_TAG, "sdfsdfsdfdfgds")
        stateList.clear()
        stateList.addAll(countryList.value?.apiResponse?.get(position)?.available_regions as ArrayList<RegisterDataClass.State>)
        stateSpinner.setSelection(0)
    }


    fun onStateSelection(position: Int){
        AppLog.e(RegisterViewModel.COUNTRY_API_TAG, "dsfdsfsdfsf")
        cityList.clear()
        callCityApi(stateList[position].id)
    }

    fun onCitySelection(){
        AppLog.e(RegisterViewModel.COUNTRY_API_TAG,"sfsfsfsfs")
        //callCityApi(stateList[position].id)
    }

    fun prepareMaskedAddress(address: MyAccountDataClass.Address?){
        var countryCode = ""
        var mobileNo = ""
        if(address?.telephone?.contains("-") == true){
            countryCode = address.telephone.split("-")[0]
            mobileNo = address.telephone.split("-")[1]
        }else{
            mobileNo = address?.telephone ?: ""
        }
        val country = GlobalSingelton.instance?.storeList?.single { it.code.toString() == address?.country_id}
        maskedAddress =  MyAccountDataClass.MaskedUserInfo(
                _id = address?.id,
                _firstName = address?.firstname,
                _lastName = address?.lastname,
                _email = SavedPreferences.getInstance()?.getStringValue(Constants.USER_EMAIL),
                _country = country?.name,
                _city = address?.city,
                _state = address?.region?.region,
                _streedAdd1 = address?.street?.get(0),
                _streedAdd2 = address?.street?.get(1),
                _mobile = mobileNo,
                _postalCode = address?.postcode,
                _countryCode = countryCode
        )

    }

    private fun callCityApi(stateCode: String) {
        AppRepository.getCityList(stateCode, object : ApiCallback<List<RegisterDataClass.City>>{
            override fun onException(error: Throwable) {
                Utils.printLog(RegisterViewModel.CITY_API_TAG, RegisterViewModel.ERROR_TAG)
            }

            override fun onError(errorMsg: String) {
                Utils.printLog(RegisterViewModel.CITY_API_TAG, RegisterViewModel.ERROR_TAG)
                Toast.makeText(getApplication<Application>(), errorMsg, Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(cities: List<RegisterDataClass.City>?) {
                cities?.run {
                    cityList = cities.toMutableList()
                    countryList.value = countryListApiResponse
                }
            }

        })
    }

}
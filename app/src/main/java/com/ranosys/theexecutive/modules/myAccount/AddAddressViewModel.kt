package com.ranosys.theexecutive.modules.myAccount

import AppLog
import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableArrayList
import android.view.View
import android.widget.Spinner
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.modules.register.RegisterDataClass
import com.ranosys.theexecutive.modules.register.RegisterViewModel
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.GlobalSingelton
import com.ranosys.theexecutive.utils.SavedPreferences
import java.util.*

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 03-May-2018
 */
class AddAddressViewModel(application: Application): BaseViewModel(application) {

    var countryList :MutableLiveData<ApiResponse<MutableList<RegisterDataClass.Country>>> = MutableLiveData()
    var stateList :ObservableArrayList<RegisterDataClass.State> = ObservableArrayList()
    var maskedAddress: MyAccountDataClass.MaskedUserInfo? = null

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
                    countryList.value = apiResponse
                }
            }
        })
    }

    fun onCountrySelection(countrySpinner: View, position: Int, stateSpinner: Spinner){
        stateList.clear()
        stateList.addAll(countryList.value?.apiResponse?.get(position)?.available_regions as ArrayList<RegisterDataClass.State>)
        stateSpinner.setSelection(0)
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
}
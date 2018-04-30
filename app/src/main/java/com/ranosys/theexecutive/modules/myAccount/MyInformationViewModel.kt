package com.ranosys.theexecutive.modules.myAccount

import AppLog
import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.ObservableField
import android.support.design.widget.TextInputEditText
import android.text.TextUtils
import android.view.View
import android.widget.Spinner
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.utils.GlobalSingelton
import com.ranosys.theexecutive.utils.Utils

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 27-Apr-2018
 */
class MyInformationViewModel(application: Application): BaseViewModel(application) {

    var maskedUserInfo : ObservableField<MyAccountDataClass.MaskedUserInfo> = ObservableField()
    var userInfoApiResponse : MutableLiveData<ApiResponse<String>> = MutableLiveData()
    var infoUpdated = false
    private val mobileNumberError: ObservableField<String> = ObservableField()
    var countryCode: ObservableField<String> = ObservableField()

    fun callUserInfoApi() {
        val apiResponse = ApiResponse<String>()
        AppRepository.getUserInfo(object: ApiCallback<MyAccountDataClass.UserInfoResponse>{
            override fun onException(error: Throwable) {
                AppLog.e("My Information API : ${error.message}")
                apiResponse.error = error.message
                userInfoApiResponse.value = apiResponse

            }

            override fun onError(errorMsg: String) {
                AppLog.e("My Information API : ${errorMsg}")
                apiResponse.error = errorMsg
                userInfoApiResponse.value = apiResponse
            }

            override fun onSuccess(t: MyAccountDataClass.UserInfoResponse?) {
                //update info saved at singleton
                GlobalSingelton.instance?.userInfo = t

                val defaultAdd = t?.addresses?.single { it.id == t.default_shipping }
                val country = GlobalSingelton.instance?.storeList?.single { it.code.toString() == defaultAdd?.country_id}
                var userInfo = MyAccountDataClass.MaskedUserInfo(
                        _id = t?.id.toString(),
                        _firstName = t?.firstname,
                        _lastName = t?.lastname,
                        _email = t?.email,
                        _country = country?.name,
                        _city = defaultAdd?.city,
                        _state = defaultAdd?.region?.region,
                        _streedAdd1 = defaultAdd?.street?.get(0),
                        _streedAdd2 = defaultAdd?.street?.get(1),
                        _mobile = defaultAdd?.telephone,
                        _postalCode = defaultAdd?.postcode,
                        _countryCode = defaultAdd?.telephone?.split("-")?.get(0)
                )

                apiResponse.apiResponse = userInfo._email
                maskedUserInfo.set(userInfo)
                userInfoApiResponse.value = apiResponse
            }

        })
    }


    fun onCountryCodeSelection(countryCodeSpinner: View, position: Int){
        countryCode.set((countryCodeSpinner as Spinner).selectedItem.toString())
        if(infoUpdated.not()){
            infoUpdated = true
        }
    }

    fun onTextChanged(et: TextInputEditText){
        when(et.id){
            R.id.et_mobile_number -> mobileNumberError.set("")
        }
    }


    fun isValidData(context: Context): Boolean {

        if (TextUtils.isEmpty(maskedUserInfo.get()._mobile)){
            mobileNumberError.set(context.getString(R.string.mobile_error))
            return false
        }else if(!Utils.isValidMobile(maskedUserInfo.get()._mobile!!)){
            mobileNumberError.set(context.getString(R.string.valid_mobile_error))
            return false
        }

        return true
    }

    fun UpdateUserInfo() {
        //TODO - prepare update info
    }

}
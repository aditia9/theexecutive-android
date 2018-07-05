package com.ranosys.theexecutive.modules.checkout

import AppLog
import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.ranosys.theexecutive.DelamiBrandsApplication
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.modules.myAccount.MyAccountDataClass
import com.ranosys.theexecutive.modules.shoppingBag.ShoppingBagResponse
import com.ranosys.theexecutive.utils.GlobalSingelton
import com.ranosys.theexecutive.utils.Utils

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 10-May-2018
 */
class CheckoutViewModel(application: Application): BaseViewModel(application) {

    val commonError: MutableLiveData<String> = MutableLiveData()
    val selectedAddress: MutableLiveData<MyAccountDataClass.Address> = MutableLiveData()
    var selectedPaymentMethod: CheckoutDataClass.PaymentMethod? = null
    var selectedShippingMethod: CheckoutDataClass.GetShippingMethodsResponse? = null
    val shoppingBagItems: MutableLiveData<List<ShoppingBagResponse>> = MutableLiveData()
    val orderId: MutableLiveData<String> = MutableLiveData()
    val shippingMethodList: MutableLiveData<List<CheckoutDataClass.GetShippingMethodsResponse>> = MutableLiveData()
    val paymentMethodList: MutableLiveData<List<CheckoutDataClass.PaymentMethod>> = MutableLiveData()
    val totalAmounts: MutableLiveData<List<CheckoutDataClass.TotalSegment>> = MutableLiveData()
    var country: MutableLiveData<String> = MutableLiveData()
    var totalSegmentVisible : Boolean = false
    var oldShippingMethodCode : String = ""


    fun getAddressApi() {
        AppRepository.getUserInfoNSelectedShipping(object: ApiCallback<CheckoutDataClass.UserInfoNselectedShippingResponse> {
            override fun onException(error: Throwable) {
                AppLog.e("My Information API : ${error.message}")
                commonError.value = error.message

            }

            override fun onError(errorMsg: String) {
                AppLog.e("My Information API : $errorMsg")
                commonError.value = errorMsg
            }

            override fun onSuccess(t: CheckoutDataClass.UserInfoNselectedShippingResponse?) {
                //update info saved at singleton
                GlobalSingelton.instance?.userInfo = t?.customer
                //selectedAddress.value = Utils.getDefaultAddress()
                analyseSelectedShippingMethod(t)
            }
        })
    }

    private fun analyseSelectedShippingMethod(t: CheckoutDataClass.UserInfoNselectedShippingResponse?) {
        var previousShippingMethod = ""
        var previousAddressId = ""
        if(t?.extension_attributes!!.shipping_assignments.isNotEmpty()){
            previousShippingMethod = t.extension_attributes.shipping_assignments[0].shipping.method ?: ""
            previousAddressId = t.extension_attributes.shipping_assignments[0].shipping.address.customer_address_id ?: ""
        }

        if(previousShippingMethod.isEmpty()){
            selectedAddress.value = Utils.getDefaultAddress()
            country.value = selectedAddress.value?.country_id?.let { Utils.getCountryName(it) }
            oldShippingMethodCode = ""

        }else{
            if(previousAddressId.isEmpty().not()){
                selectedAddress.value = Utils.getAddressFromId(previousAddressId)
                country.value = selectedAddress.value?.country_id?.let { Utils.getCountryName(it) }
                oldShippingMethodCode = previousShippingMethod ?: ""
            }else{
                selectedAddress.value = Utils.getDefaultAddress()
                country.value = selectedAddress.value?.country_id?.let { Utils.getCountryName(it) }
                oldShippingMethodCode = ""
            }

        }
    }


    fun getCartItemsApi() {
        AppRepository.getCartOfUser(callBack = object : ApiCallback<List<ShoppingBagResponse>> {
            override fun onSuccess(t: List<ShoppingBagResponse>?) {
                shoppingBagItems.value = t
            }

            override fun onException(error: Throwable) {
                commonError.value = error.message
            }

            override fun onError(errorMsg: String) {
                commonError.value = errorMsg
            }
        })
    }

    fun getShippingMethodsApi(addressId: String){

        if (addressId.isNotEmpty()){
            val request = CheckoutDataClass.GetShippingMethodsRequest(addressId)
            AppRepository.getShippingMethods(request, object : ApiCallback<List<CheckoutDataClass.GetShippingMethodsResponse>>{

                override fun onSuccess(t: List<CheckoutDataClass.GetShippingMethodsResponse>?) {
                    shippingMethodList.value = t
                }

                override fun onException(error: Throwable) {
                    commonError.value = error.message
                }

                override fun onError(errorMsg: String) {
                    commonError.value = errorMsg
                }


            })
        }else{
            commonError.value = DelamiBrandsApplication.samleApplication?.getString(R.string.something_went_wrong_error)
        }

    }

    fun getPaymentMethods(shippingMethod: CheckoutDataClass.GetShippingMethodsResponse) {
        val request = preparePaymentMethodRequest(shippingMethod)

        AppRepository.getPaymentMethods(request, object: ApiCallback<CheckoutDataClass.PaymentMethodResponse>{
            override fun onException(error: Throwable) {
                commonError.value = error.message
            }

            override fun onError(errorMsg: String) {
                commonError.value = errorMsg
            }

            override fun onSuccess(t: CheckoutDataClass.PaymentMethodResponse?) {
                paymentMethodList.value = t?.payment_methods
                totalAmounts.value = t?.totals?.total_segments
            }

        })
    }

    private fun preparePaymentMethodRequest(shippingMethod: CheckoutDataClass.GetShippingMethodsResponse): CheckoutDataClass.GetPaymentMethodsRequest {
        val selectedAddress = selectedAddress.value
        val requestAddress =  CheckoutDataClass.ShippingAddress(
                customer_id = selectedAddress?.customer_id!!,
                firstname = selectedAddress.firstname!!,
                lastname = selectedAddress.lastname!!,
                telephone = selectedAddress.telephone!!,
                country_id = selectedAddress.country_id!!,
                city = selectedAddress.city!!,
                postcode = selectedAddress.postcode!!,
                region_id = selectedAddress.region_id!!,
                region_code = selectedAddress.region?.region_code!!,
                region = selectedAddress.region.region,
                street = selectedAddress.street!!,
                customer_address_id = selectedAddress.id
        )

        val addressInformation = CheckoutDataClass.AddressInformation(
                shipping_address = requestAddress,
                billing_address = requestAddress,
                shipping_carrier_code = shippingMethod.carrier_code,
                shipping_method_code = shippingMethod.method_code
        )

        return CheckoutDataClass.GetPaymentMethodsRequest(addressInformation)

    }

    fun placeOrderApi(paymentMethod: CheckoutDataClass.PaymentMethod?) {
        val paymentMode = CheckoutDataClass.PlaceOrderPaymentMethod(
                method = paymentMethod?.code!!
        )
        val request = CheckoutDataClass.PlaceOrderRequest(
                paymentMethod = paymentMode
        )

        AppRepository.placeOrder(request, object: ApiCallback<String>{
            override fun onException(error: Throwable) {
                commonError.value = error.message
            }

            override fun onError(errorMsg: String) {
                commonError.value = errorMsg
            }

            override fun onSuccess(t: String?) {
                orderId.value = t
            }

        })
    }

    fun getTotalAmountsApi() {
        AppRepository.getTotalAmounts(callBack = object : ApiCallback<CheckoutDataClass.Totals> {
            override fun onException(error: Throwable) {
                commonError.value = error.message
            }

            override fun onError(errorMsg: String) {
                commonError.value = errorMsg
            }

            override fun onSuccess(t: CheckoutDataClass.Totals?) {
                totalAmounts.value = t?.total_segments
            }

        })
    }
}
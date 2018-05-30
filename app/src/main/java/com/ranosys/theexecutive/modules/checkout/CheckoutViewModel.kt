package com.ranosys.theexecutive.modules.checkout

import AppLog
import android.app.Application
import android.arch.lifecycle.MutableLiveData
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


    fun getAddressApi() {
        AppRepository.getUserInfo(object: ApiCallback<MyAccountDataClass.UserInfoResponse> {
            override fun onException(error: Throwable) {
                AppLog.e("My Information API : ${error.message}")
                commonError.value = "user info: ${error.message}"

            }

            override fun onError(errorMsg: String) {
                AppLog.e("My Information API : $errorMsg")
                commonError.value = "user info: $errorMsg"
            }

            override fun onSuccess(t: MyAccountDataClass.UserInfoResponse?) {
                //update info saved at singleton
                GlobalSingelton.instance?.userInfo = t
                selectedAddress.value = Utils.getDefaultAddress()
                country.value = selectedAddress.value?.country_id?.let { Utils.getCountryName(it) }

                //analyseSelectedShippingMethod(t)
            }
        })
    }

//    private fun analyseSelectedShippingMethod(t: CheckoutDataClass.UserInfoNselectedShippingResponse?) {
//        t?.extension_attributes!!.shipping_assignments[0].shipping.method
//    }


    fun getCartItemsApi() {
        AppRepository.getCartOfUser(callBack = object : ApiCallback<List<ShoppingBagResponse>> {
            override fun onSuccess(t: List<ShoppingBagResponse>?) {
                shoppingBagItems.value = t
            }

            override fun onException(error: Throwable) {
                commonError.value = "cart items: ${error.message}"
            }

            override fun onError(errorMsg: String) {
                commonError.value = "cart items: $errorMsg"
            }
        })
    }

    fun getShippingMethodsApi(addressId: String){

        val request = CheckoutDataClass.GetShippingMethodsRequest(addressId)
        AppRepository.getShippingMethods(request, object : ApiCallback<List<CheckoutDataClass.GetShippingMethodsResponse>>{

            override fun onSuccess(t: List<CheckoutDataClass.GetShippingMethodsResponse>?) {
                shippingMethodList.value = t
            }

            override fun onException(error: Throwable) {
                commonError.value = "shipping methods: ${error.message}"
            }

            override fun onError(errorMsg: String) {
                commonError.value = "shipping methods: $errorMsg"
            }


        })
    }

    fun getPaymentMethods(shippingMethod: CheckoutDataClass.GetShippingMethodsResponse) {
        val request = preparePaymentMethodRequest(shippingMethod)

        AppRepository.getPaymentMethods(request, object: ApiCallback<CheckoutDataClass.PaymentMethodResponse>{
            override fun onException(error: Throwable) {
                commonError.value = "payment methods: ${error.message}"
            }

            override fun onError(errorMsg: String) {
                commonError.value = "payment methods: $errorMsg"
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
                street = selectedAddress.street!!
        )

        val addressInformation = CheckoutDataClass.AddressInformation(
                shipping_address = requestAddress,
                billing_address = requestAddress,
                shipping_carrier_code = shippingMethod.carrier_code,
                shipping_method_code = shippingMethod.method_code
        )

        val request = CheckoutDataClass.GetPaymentMethodsRequest(addressInformation)
        return request

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
                commonError.value = "place order: ${error.message}"
            }

            override fun onError(errorMsg: String) {
                commonError.value = "place order: $errorMsg"
            }

            override fun onSuccess(t: String?) {
                orderId.value = t
            }

        })
    }

    fun getTotalAmountsApi() {
        AppRepository.getTotalAmounts(callBack = object : ApiCallback<CheckoutDataClass.Totals> {
            override fun onException(error: Throwable) {
                commonError.value = "total amount: ${error.message}"
            }

            override fun onError(errorMsg: String) {
                commonError.value = "total amount: $errorMsg"
            }

            override fun onSuccess(t: CheckoutDataClass.Totals?) {
                totalAmounts.value = t?.total_segments
            }

        })
    }
}
package com.delamibrands.theexecutive.modules.myAccount

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.delamibrands.theexecutive.BR
import com.delamibrands.theexecutive.modules.register.RegisterDataClass

/**
 * Created by nikhil on 22/3/18.
 */
class MyAccountDataClass {

    data class MyAccountOption(val title: String, val icon: Int)

    data class NewsletterSubscriptionRequest(val email: String)


    data class NotificationCountRequest(
            val deviceId: String
    )

    data class UserInfoResponse(
            val id: Int?,
            private val group_id: Int?,
            var default_billing: String?,
            var default_shipping: String?,
            private val created_at: String?,
            private val updated_at: String?,
            private val created_in: String?,
            private val dob: String?,
            val email: String?,
            val firstname: String?,
            val lastname: String?,
            private var prefix: String?,
            val gender: Int?,
            private val store_id: Int?,
            private val website_id: Int?,
            var addresses: MutableList<Address>?,
            private val disable_auto_group_change: Int?
    ){
        fun copy(): UserInfoResponse{

            val addressList = mutableListOf<Address>()
            this.addresses?.let {
                for(address in this.addresses!!){
                    addressList.add(address.copy(region = address.region?.copy(), street = address.street?.toMutableList()))
                }
            }

            return UserInfoResponse(
                    id = this.id,
                    group_id =  this.group_id,
                    default_billing = this.default_billing,
                    default_shipping = this.default_shipping,
                    created_at = this.created_at,
                    updated_at = this.updated_at,
                    created_in = this.created_in,
                    dob = this.dob,
                    email = this.email,
                    firstname = this.firstname,
                    lastname = this.lastname,
                    prefix = this.prefix,
                    gender = this.gender,
                    store_id = this.store_id,
                    website_id = this.website_id,
                    disable_auto_group_change = this.disable_auto_group_change,
                    addresses = addressList
            )
        }
    }

    data class Address(
            var id: String? = "",
            var customer_id: String? = "",
            val region: RegisterDataClass.Region?,
            val region_id: String?,
            val country_id: String?,
            val street: List<String?>?,
            var telephone: String?,
            val postcode: String?,
            val city: String?,
            val prefix: String? = "",
            val firstname: String? = "",
            val lastname: String? = "",
            var default_shipping: Boolean? = null,
            var default_billing: Boolean? = null
    )

    data class MaskedUserInfo(
            var _firstName: String?,
            var _lastName: String?,
            var _email: String?,
            var _mobile: String?,
            var _countryCode: String? = "",
            var _streedAdd1: String?,
            var _streedAdd2: String?,
            var _country: String?,
            var _state: String?,
            var _city: String?,
            var _postalCode: String?,
            val _id: String?
    ): BaseObservable(){

        var firstName : String?
            @Bindable get() = _firstName
            set(value) {
                _firstName = value
                notifyPropertyChanged(BR.firstName)
            }


        var lastName : String?
            @Bindable get() = _lastName
            set(value) {
                _lastName = value
                notifyPropertyChanged(BR.lastName)
            }

        var email : String?
            @Bindable get() = _email
            set(value) {
                _email = value
                notifyPropertyChanged(BR.email)
            }

        var mobile : String?
            @Bindable get() = _mobile
            set(value) {
                _mobile = value
                notifyPropertyChanged(BR.mobile)
            }

        var countryCode : String?
            @Bindable get() = _countryCode
            set(value) {
                _countryCode = value
                notifyPropertyChanged(BR.countryCode)
            }

        var streedAdd1 : String?
            @Bindable get() = _streedAdd1
            set(value) {
                _streedAdd1 = value
                notifyPropertyChanged(BR.streedAdd1)
            }

        var streedAdd2 : String?
            @Bindable get() = _streedAdd2
            set(value) {
                _streedAdd2 = value
                notifyPropertyChanged(BR.streedAdd2)
            }

        var country : String?
            @Bindable get() = _country
            set(value) {
                _country = value
                notifyPropertyChanged(BR.country)
            }

        var state : String?
            @Bindable get() = _state
            set(value) {
                _state = value
                notifyPropertyChanged(BR.state)
            }

        var city : String?
            @Bindable get() = _city
            set(value) {
                _city = value
                notifyPropertyChanged(BR.city)
            }

        var postalCode : String?
            @Bindable get() = _postalCode
            set(value) {
                _postalCode = value
                notifyPropertyChanged(BR.postalCode)
            }

        val id : String?
            @Bindable get() = _id

    }


    data class UpdateInfoRequest(
            var customer: UserInfoResponse
    )

}
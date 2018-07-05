package com.ranosys.theexecutive.utils

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.ranosys.theexecutive.modules.myAccount.MyAccountDataClass
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ProductOptionsResponse
import com.ranosys.theexecutive.modules.productDetail.dataClassess.StaticPagesUrlResponse
import com.ranosys.theexecutive.modules.splash.ConfigurationResponse
import com.ranosys.theexecutive.modules.splash.StoreResponse

/**
 * Created by Mohammad Sunny on 21/2/18.
 */
class GlobalSingelton private constructor(){

    var storeList: List<StoreResponse>? = null
    var colorList: List<ProductOptionsResponse>? = null
    var sizeList: List<ProductOptionsResponse>? = null
    var staticPagesResponse: StaticPagesUrlResponse? = null
    var configuration: ConfigurationResponse? = null
    var userInfo: MyAccountDataClass.UserInfoResponse? = null
    var paymentInitiated: Boolean = false
    var orderId: String = ""
    var notificationCount: ObservableField<Int> = ObservableField(0)

    var cartCount: MutableLiveData<Int> = MutableLiveData()

    companion object {
        var instance: GlobalSingelton? = null
            get() {
                field = field ?: GlobalSingelton()
                return field
            }
    }


}
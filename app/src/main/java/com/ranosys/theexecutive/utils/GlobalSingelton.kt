package com.ranosys.theexecutive.utils

import com.ranosys.theexecutive.modules.register.RegisterDataClass
import com.ranosys.theexecutive.modules.splash.ConfigurationResponse
import com.ranosys.theexecutive.modules.splash.StoreResponse

/**
 * Created by Mohammad Sunny on 24/1/18.
 */
class GlobalSingelton private constructor(){

    var userData: RegisterDataClass.RegisterRequest? = null
    var storeList: ArrayList<StoreResponse>? = null

    companion object {
        var instance: GlobalSingelton? = null
            get() {
                field = field ?: GlobalSingelton()
                return field
            }
            set(value) {
                field = value
            }
    }


}
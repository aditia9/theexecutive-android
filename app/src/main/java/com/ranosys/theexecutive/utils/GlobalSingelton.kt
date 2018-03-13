package com.ranosys.theexecutive.utils

import com.ranosys.theexecutive.modules.splash.StoreResponse

/**
 * Created by Mohammad Sunny on 21/2/18.
 */
class GlobalSingelton private constructor(){

    var storeList: List<StoreResponse>? = null

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
package com.ranosys.theexecutive.utils

import com.ranosys.theexecutive.modules.register.RegisterDataClass

/**
 * Created by Mohammad Sunny on 24/1/18.
 */
class GlobalSingelton private constructor(){

    var userData: RegisterDataClass.RegisterRequest? = null

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
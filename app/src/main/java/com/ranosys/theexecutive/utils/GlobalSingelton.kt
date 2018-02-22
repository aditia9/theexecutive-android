package com.ranosys.theexecutive.utils

import com.google.firebase.auth.FirebaseUser
import com.ranosys.theexecutive.fragments.Register.RegisterDataClass

/**
 * Created by Mohammad Sunny on 24/1/18.
 */
class GlobalSingelton private constructor(){

    var firebaseUser: FirebaseUser? = null
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
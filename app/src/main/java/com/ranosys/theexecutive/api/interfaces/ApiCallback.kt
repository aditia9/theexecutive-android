package com.ranosys.theexecutive.api.interfaces

/**
 * Created by Vikash Kumar Bijarniya on 29/1/18.
 */
interface ApiCallback<T>{
    fun onException(error : Throwable)

    fun onError(errorMsg : String)

    fun onSuccess(t : T)
}
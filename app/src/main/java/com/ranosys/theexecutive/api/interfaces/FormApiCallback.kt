package com.ranosys.theexecutive.api.interfaces

/**
 * Created by Mohammad Sunny on 29/1/18.
 */
interface FormApiCallback<T> {

    fun onException(error : Throwable)

    fun onError(errorMap : Map<String, String> )

    fun onSuccess(t : T)

}
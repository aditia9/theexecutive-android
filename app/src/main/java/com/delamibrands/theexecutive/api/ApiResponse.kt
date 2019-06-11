package com.delamibrands.theexecutive.api

/**
 * Created by Mohammad Sunny on 24/1/18.
 */
class ApiResponse<T> {

    var error: String? = null
    var throwable: Throwable? = null
    var apiResponse: T? = null

}
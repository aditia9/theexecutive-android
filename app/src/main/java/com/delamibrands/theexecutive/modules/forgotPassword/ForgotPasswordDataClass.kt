package com.delamibrands.theexecutive.modules.forgotPassword

import com.delamibrands.theexecutive.utils.Constants

/**
 * Created by nikhil on 8/3/18.
 */
class ForgotPasswordDataClass {
    data class ForgotPasswordRequest(var email: String?,
                                     var websiteId : Int?,
                                     var template: String = Constants.FORGOT_PASSWORD_REQUEST_PARAM_TEMPLATE)
}
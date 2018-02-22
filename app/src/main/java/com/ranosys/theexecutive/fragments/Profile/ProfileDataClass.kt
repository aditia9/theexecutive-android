package com.ranosys.theexecutive.fragments.Profile

/**
 * Created by Vikash Kumar Bijarniya on 6/2/18.
 */
class ProfileDataClass{
    data class UserProfile (var name: String? = null, var mobile: String? = null,
                           var blood : String? = null,
                           var email: String? = null, var city: String? = null,
                           var state: String? = null, var gender: String? = null)
}

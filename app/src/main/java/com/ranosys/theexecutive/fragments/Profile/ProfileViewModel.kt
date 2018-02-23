package com.ranosys.theexecutive.fragments.Profile

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.utils.SavedPreferences

/**
 * Created by Vikash Kumar Bijarniya on 6/2/18.
 */
class ProfileViewModel(application: Application, database: FirebaseDatabase) : BaseViewModel(application) {

    var userProfile: ProfileDataClass.UserProfile? = null
    var name: ObservableField<String>? = ObservableField()
    var mobile: ObservableField<String>? = ObservableField()
    var emailId: ObservableField<String>? = ObservableField()
    var blood : ObservableField<String>?= ObservableField()
    var gender: ObservableField<String>? = ObservableField()
    var state: ObservableField<String>? = ObservableField()
    var city: ObservableField<String>? = ObservableField()
    var password: ObservableField<String>? = ObservableField()
    val userData = MutableLiveData<ProfileDataClass.UserProfile>()
    var database: FirebaseDatabase? = null
    var savedPreferences: SavedPreferences? = null

    init {
        this.userProfile = userProfile
        this.database = database
    }

     fun getFromServer() {
        val rootReference = database?.getReference()
        savedPreferences = SavedPreferences.getInstance()
        var userMailId = savedPreferences?.getUserEmail()
        val userPath = userMailId?.replace("[-+.^:,@#]".toRegex(),"")
        var finalref = rootReference?.child(userPath)?.child("HOMEDATA")
        finalref?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot?) {
                var user: ProfileDataClass.UserProfile = data?.getValue(ProfileDataClass.UserProfile::class.java)!!
                userData.value = user
            }
            override fun onCancelled(errdata: DatabaseError?) {
                Log.e("Error", errdata?.message)
            }
        })
    }
    fun saveUserProfile(){
        val rootReference = database?.getReference()
        userProfile = ProfileDataClass.UserProfile(name?.get(), mobile?.get(), blood?.get(),
                emailId?.get(), city?.get(), state?.get(), gender?.get())
        val userMail = emailId?.get()?.replace("[-+.^:,@#]".toRegex(),"")
        var finalref = rootReference?.child(userMail)?.child("HOMEDATA")
        finalref?.setValue(userProfile)
    }


}
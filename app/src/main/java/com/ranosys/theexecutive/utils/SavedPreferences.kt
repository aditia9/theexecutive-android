package com.ranosys.theexecutive.utils

import android.content.Context
import android.content.SharedPreferences
import com.ranosys.theexecutive.DelamiBrandsApplication






/**
 * Created by Vikash Kumar Bijarniya on 5/2/18.
 */
class SavedPreferences private constructor(){

    var sharedPreferences: SharedPreferences? = null
    var USER_EMAIL_KEY = "userEmail"
    var IS_LOGIN_KEY = "isLogin"


    companion object {

        val user_pref = "APP_DATA"
        var savedPreferences: SavedPreferences? = null

        private fun init(context: Context) {
            savedPreferences = SavedPreferences()
            savedPreferences?.sharedPreferences = context.getSharedPreferences(user_pref, Context.MODE_PRIVATE)
        }


        fun getInstance(): SavedPreferences? {
            if (savedPreferences == null) {
                init(DelamiBrandsApplication.samleApplication?.applicationContext!!)
            }
            return savedPreferences
        }

    }

    fun saveStringValue(value: String, key: String) {
        val editor = getEditor()
        editor?.putString(key, value)
        editor?.commit()
    }

    fun getStringValue(key: String): String? {
        return sharedPreferences?.getString(key, "")
    }

    fun saveIntValue(value: Int, key: String) {
        val editor = getEditor()
        editor?.putInt(key, value)
        editor?.apply()
    }

    fun getIntValue(key: String): Int? {
        return sharedPreferences?.getInt(key, 0)
    }

    fun removeValue(key: String) {
        val editor = getEditor()
        editor?.clear()
        editor?.remove(key)
    }

    fun storeUserEmail(app_name: String) {
        val editor = sharedPreferences?.edit()
        editor?.putString(USER_EMAIL_KEY, app_name)
        editor?.apply()
    }

    fun getUserEmail(): String? {
        var app_name: String? = null
        app_name = getStringValue(USER_EMAIL_KEY)
        return app_name
    }

    private fun getEditor(): SharedPreferences.Editor? {
        return sharedPreferences?.edit()
    }

    fun setIsLogin(islogin: Boolean) {
        val editor = sharedPreferences?.edit()
        editor?.putBoolean(IS_LOGIN_KEY, islogin)
        editor?.apply()
    }
    fun getIsLogin():Boolean?{
        var islogin: Boolean?
        islogin = getBooleanValue(IS_LOGIN_KEY)
        return islogin
    }

    private fun getBooleanValue(key: String): Boolean? {
        return sharedPreferences?.getBoolean(key, false)
    }


}
package com.ranosys.theexecutive.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.ranosys.theexecutive.BuildConfig
import com.ranosys.theexecutive.R
import java.util.regex.Pattern


/**
 * Created by ranosys on 25/1/18.
 */
open class Utils {

    companion object {

        fun printLog(TAG:String, message: String): Unit{
            if(BuildConfig.DEBUG){
                Log.e(TAG, message)
            }
        }

        val isMarshmallowOrAbove: Boolean?
            get() {
                return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            }

        fun isValidEmail(email: String?): Boolean {
            val p = Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$")
            val m = p.matcher(email)
            return m.matches()
        }

        fun isConnectionAvailable(context: Context): Boolean{
            try {
                val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                val mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                return if (wifi.isAvailable && wifi.isConnected) {
                    true
                } else if (mobile.isAvailable && mobile.isConnected) {
                    true
                } else {
                    false
                }
            } catch (ex: Exception) {
                print(ex.stackTrace)
            }
            return false
        }

        fun showDialog(context: Context?):Dialog{
            val progressDialog = Dialog(context)
            if (progressDialog.window != null) {
                progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            progressDialog.show()
            progressDialog.setContentView(R.layout.progress_dialog)
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)
            return progressDialog

        }

        /**
         * To hide the soft key pad if open
         */
        fun hideSoftKeypad(context: Context) {
            val activity = context as Activity
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.currentFocus?.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

}
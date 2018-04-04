package com.ranosys.theexecutive.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.TextView
import com.ranosys.theexecutive.BuildConfig
import com.ranosys.theexecutive.R
import java.util.regex.Pattern


/**
 * Created by Mohammad Sunny on 21/2/18.
 */
object Utils {

    fun printLog(TAG:String, message: String){
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

    fun isValidPassword(password: String): Boolean {

        val p = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$")
        val m = p.matcher(password)
        return m.matches()
    }

    fun isValidMobile(mobile: String): Boolean {
       if(mobile.length >= 8 && mobile.length <=16){
           return true
       }
        return false

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

    fun showProgressDialog(context: Context?):Dialog{
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

    fun showDialog(context: Context?, title: String?, positiveMessage: String?, negativeMessage: String?, dialogOkCallback: DialogOkCallback?) {
        if (context != null) {
            val dialog : Dialog? = Dialog(context)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setContentView(R.layout.alert_dialog)
            dialog?.findViewById<TextView>(R.id.tv_title)?.text = title
            if(TextUtils.isEmpty(negativeMessage).not()){
                dialog?.findViewById<TextView>(R.id.tv_no)?.visibility = View.VISIBLE
                dialog?.findViewById<TextView>(R.id.tv_no)?.text = negativeMessage
                dialog?.findViewById<TextView>(R.id.tv_no)?.setOnClickListener { v ->
                    dialog.dismiss()
                }
            }
            else{
                dialog?.findViewById<TextView>(R.id.tv_no)?.visibility = View.GONE
            }
            dialog?.findViewById<TextView>(R.id.tv_yes)?.text = positiveMessage
            dialog?.findViewById<TextView>(R.id.tv_yes)?.setOnClickListener { v ->
                dialog.dismiss()
                dialogOkCallback?.setDone(true)
            }
            if ((context as Activity).isFinishing.not()) {
                dialog?.show()
                dialog?.window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                dialog?.setCancelable(false)
            }

        }

    }

    fun showNetworkErrorDialog(context: Context){
        Utils.showDialog(context, context.getString(R.string.network_err_text),context.getString(android.R.string.ok), "", object : DialogOkCallback{
            override fun setDone(done: Boolean) {

            }
        })
    }

}
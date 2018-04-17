package com.ranosys.theexecutive.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.ranosys.theexecutive.BuildConfig
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.modules.home.HomeFragment
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
        showDialog(context, context.getString(R.string.network_err_text),context.getString(android.R.string.ok), "", object : DialogOkCallback{
            override fun setDone(done: Boolean) {

            }
        })
    }

    fun logout(context: Context){
        SavedPreferences.getInstance()?.saveStringValue("", Constants.USER_ACCESS_TOKEN_KEY)
        Toast.makeText(context, context.getString(R.string.logout_success_message), Toast.LENGTH_SHORT).show()
        FragmentUtils.addFragment(context, HomeFragment(), null, HomeFragment::class.java.name, false)
    }

    fun isTablet(context: Context): Boolean {
        val xlarge = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === 4
        val large = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }

    fun openCmsPage(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }

    fun compareDrawable(context: Context, d1: Drawable, d2: Drawable): Boolean{
        return (d1 as BitmapDrawable).bitmap.equals((d2 as BitmapDrawable).bitmap)
    }

    fun getDeviceWidth(context: Context) : Int{
        val displayMetrics = DisplayMetrics()
        (context as BaseActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun getDeviceHeight(context: Context?) : Int{
        val displayMetrics = DisplayMetrics()
        (context as BaseActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun convertDpIntoPx(context: Context?, dp : Float) : Int{
        val r = context?.resources
        val px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, r?.displayMetrics))
        return px
    }

    fun setImageViewHeight(context: Context?, imageView : ImageView?, percentage : Int?){
        val height = getDeviceHeight(context)
        val removeHeight = height.times(percentage!!).div(100)
        imageView?.layoutParams?.height = height - removeHeight
    }

    fun setImageViewHeightWrtDeviceWidth(context: Context, imageView: ImageView, times: Double){
        val width = getDeviceWidth(context)
        val height = width.times(times)
        imageView.layoutParams?.height = height.toInt()
    }

    fun setViewHeightWrtDeviceHeight(context: Context, view: View, times: Double){
        val width = getDeviceHeight(context)
        val height = width.times(times)
        view.layoutParams?.height = height.toInt()
    }

    fun setViewHeightWrtDeviceWidth(context: Context, view: View, times: Double){
        val width = getDeviceWidth(context)
        val height = width.times(times)
        view.layoutParams?.height = height.toInt()
    }


    fun openPages(context: Context, url: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }

    fun shareUrl(context: Context, url: String?) {
        val intent = Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing Product links");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        context.startActivity(Intent.createChooser(intent, "Share Product"));
    }

    fun updateCartCount(count: Int) {
        GlobalSingelton.instance?.cartCount?.value = count
    }
}
package com.ranosys.theexecutive.utils

import AppLog
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.ranosys.theexecutive.BuildConfig
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.modules.home.HomeFragment
import com.ranosys.theexecutive.modules.myAccount.MyAccountDataClass
import com.zopim.android.sdk.api.ZopimChat
import com.zopim.android.sdk.model.VisitorInfo
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
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

    fun isUserLoggedIn(): Boolean {
        val userToken = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
         return (userToken.isNullOrEmpty().not() && userToken.isNullOrBlank().not())
    }


    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {

        val p = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+\$).{8,}\$")
        val m = p.matcher(password)
        return m.matches()
    }

    fun isValidMobile(mobile: String): Boolean {
        if(mobile.length in 8..16){
            return true
        }
        return false

    }

    fun isValidPincode(pincode: String): Boolean {
        if(pincode.length == 5){
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
            } else mobile.isAvailable && mobile.isConnected
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
            var errorTitle = title
            if(title == Constants.ERROR){
                errorTitle = context.getString(R.string.common_error)
            }
            val dialog : Dialog? = Dialog(context)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setContentView(R.layout.alert_dialog)
            dialog?.findViewById<TextView>(R.id.tv_title)?.text = errorTitle
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

    fun showNetworkErrorDialog(context: Context, action: () -> Unit = {}){
        showDialog(context, context.getString(R.string.network_err_text),context.getString(R.string.ok), "", object : DialogOkCallback{
            override fun setDone(done: Boolean) {
                action()
            }
        })
    }

    fun showErrorDialog(context: Context, error : String){
        showDialog(context, error, context.getString(R.string.ok), "", object : DialogOkCallback{
            override fun setDone(done: Boolean) {

            }
        })
    }

    fun logout(context: Context, mGoogleSignInClient: GoogleSignInClient){
        //fb and g mail logout
        LoginManager.getInstance().logOut()
        mGoogleSignInClient.signOut()
        SavedPreferences.getInstance()?.saveStringValue("", Constants.USER_ACCESS_TOKEN_KEY)
        SavedPreferences.getInstance()?.saveStringValue("", Constants.USER_EMAIL)
        SavedPreferences.getInstance()?.saveStringValue("", Constants.FIRST_NAME)
        SavedPreferences.getInstance()?.saveStringValue("", Constants.LAST_NAME)
        updateCartCount(0)
        SavedPreferences.getInstance()?.saveStringValue("",Constants.USER_CART_ID_KEY)
        GlobalSingelton.instance?.userInfo = null
        GlobalSingelton.instance?.notificationCount?.set(0)

        //remove user specific notifications
        removeUserNotification(context)

        FragmentUtils.addFragment(context, HomeFragment(), null, HomeFragment::class.java.name, false)

    }

    private fun removeUserNotification(context: Context) {
        val sp = SavedPreferences.getInstance()
        val ids = sp?.getStringValue(Constants.NOTIFICATION_ID_LIST)
        val notifyManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifyManager.cancelAll()
        sp?.saveStringValue("", Constants.NOTIFICATION_ID_LIST)
        /*if (!TextUtils.isEmpty(ids)) {
            try {
                val jsonArray = JSONArray(ids)
                if (jsonArray.length() > 0) {
                    val notifyManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    //notifyManager.cancelAll()
                    for (i in 0 until jsonArray.length()) {
                        notifyManager.cancel(jsonArray.getInt(i))
                    }
                }
                sp?.saveStringValue("", Constants.NOTIFICATION_ID_LIST)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

    }

    fun isTablet(context: Context): Boolean {
        val xlarge = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === 4
        val large = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }


    fun compareDrawable(context: Context, d1: Drawable, d2: Drawable): Boolean{
        return (d1 as BitmapDrawable).bitmap == (d2 as BitmapDrawable).bitmap
    }

    fun getDeviceWidth(context: Context) : Int{
        val displayMetrics = DisplayMetrics()
        (context as BaseActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

     private fun getDeviceHeight(context: Context?) : Int{
        val displayMetrics = DisplayMetrics()
        (context as BaseActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun convertDpIntoPx(context: Context?, dp : Float) : Int{
        val r = context?.resources
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, r?.displayMetrics))
    }

    fun setImageViewHeightWrtDeviceWidth(context: Context, imageView: ImageView, times: Double, widthMargin: Int = 0, column: Int = 1){
        val width = (getDeviceWidth(context) - convertDpIntoPx(context, widthMargin.toFloat())) / column
        val height = width.times(times)
        imageView.layoutParams?.height = height.toInt()
        imageView.layoutParams?.width = width.toInt()
    }

    fun setViewHeightWrtDeviceWidth(context: Context, view: View, times: Double){
        val width = getDeviceWidth(context)
        val height = width.times(times)
        view.layoutParams?.height = height.toInt()
    }

    fun shareUrl(context: Context, url: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing Product links")
        intent.putExtra(Intent.EXTRA_TEXT, url)
        context.startActivity(Intent.createChooser(intent, "Share Product"))
    }

    fun updateCartCount(count: Int) {
        GlobalSingelton.instance?.cartCount?.value = count
    }

    fun getFromattedPrice(price: String): String {
        var newPrice = ""
        try {
            val numberFormatter = NumberFormat.getNumberInstance(Locale.US)
            newPrice = if (price.isNotBlank()) {
                val p = price.toDouble()
                numberFormatter.format(p).replace(",", ".")
            } else {
                price
            }
        }catch (e : NumberFormatException){
            AppLog.printStackTrace(e)
        }
        return newPrice

    }


    fun getStringFromFormattedPrice(price: String): String {
        var newPrice = ""
        try {
            newPrice = price.replace(",", "")
        }catch (e : NumberFormatException){
            AppLog.printStackTrace(e)
        }
        return newPrice
    }


    fun setUpZendeskChat() {
        val isLogin = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        if(!TextUtils.isEmpty(isLogin)) {
            val email = SavedPreferences.getInstance()?.getStringValue(Constants.USER_EMAIL)
            val name = SavedPreferences.getInstance()?.getStringValue(Constants.FIRST_NAME) + " " + SavedPreferences.getInstance()?.getStringValue(Constants.LAST_NAME)
            val visitorInfo = VisitorInfo.Builder()
                    .email(email)
                    .name(name)
                    .build()

            // visitor info can be set at any point when that information becomes available
            ZopimChat.setVisitorInfo(visitorInfo)
        }else{
            val visitorInfo = VisitorInfo.Builder()
                    .email("")
                    .build()
            ZopimChat.setVisitorInfo(visitorInfo)
        }
        ZopimChat.init(Constants.ZENDESK_CHAT)
    }

    fun getCountryName(id: String): String{
        return GlobalSingelton.instance?.storeList?.single { it.code == id }.let { it?.name } ?: ""

    }

    fun getCountryId(name: String?): String{
        return GlobalSingelton.instance?.storeList?.single { it.name == name }.let { it?.code } ?: ""

    }

    fun getDefaultAddress(): MyAccountDataClass.Address?{
        val info = GlobalSingelton.instance?.userInfo
        return if(info?.default_shipping.isNullOrBlank().not()){
            info?.addresses?.single { it?.id == info.default_shipping }
        }else{
            null
        }

    }

    fun getAddressFromId(addId: String): MyAccountDataClass.Address?{
        val info = GlobalSingelton.instance?.userInfo
        return info?.addresses?.single { it.id == addId }

    }

    fun getDisplayPrice(configurePrice: String, configureSpecialPrice: String, currency: String): SpannableStringBuilder {
        return if(configurePrice.toDouble() > configureSpecialPrice.toDouble() && !configureSpecialPrice.equals(Constants.ZERO)){
            val normalP = "$currency\u00A0" + Utils.getFromattedPrice(configurePrice)
            val specialP = "$currency\u00A0" + Utils.getFromattedPrice(configureSpecialPrice)
            val displayPrice = "$normalP $specialP"
            SpannableStringBuilder(displayPrice).apply {
                setSpan(StrikethroughSpan(), 0, normalP.length, 0)
                setSpan(ForegroundColorSpan(Color.RED), normalP.length, displayPrice.length, 0)
                setSpan(RelativeSizeSpan(1.1f), normalP.length, displayPrice.length, 0)
            }
        }else{
            val normalP = "$currency\u00A0" + Utils.getFromattedPrice(configurePrice)
            SpannableStringBuilder(normalP)
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    fun isAppIsInBackground(context : Context) : Boolean{
        var isInBackground = true
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess.equals(context.packageName)) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo.get(0).topActivity
            if (componentInfo.packageName.equals(context.packageName)) {
                isInBackground = false
            }
        }

        return isInBackground
    }


    fun getDateFormat(strDate : String): String {
        var format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val newDate = format.parse(strDate)
        format = SimpleDateFormat("dd-MM-yyyy")
        return format.format(newDate)
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateTimeFormat(strDate : String, ctx : Context): String {
        var format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        format.timeZone = TimeZone.getTimeZone("GMT")
        val newDate = format.parse(strDate)
        format = SimpleDateFormat("dd-MM-yyyy, hh:mm a")

        val d = Date()
        val systemDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val systemTimeString = systemDateFormat.format(d.time)
        val compareDateString = systemDateFormat.format(newDate.time)

        val systemCurrentTimeString = systemDateFormat.parse(systemTimeString)
        val compareTimeString = systemDateFormat.parse(compareDateString)

        val text = StringBuffer("")
        return if(systemCurrentTimeString.compareTo(compareTimeString) == 0){
            format = SimpleDateFormat(" hh:mm a")
            text.append(ctx.getString(R.string.today) + ""+format.format(newDate)).toString()
        }else{
            format.format(newDate)
        }
    }


}
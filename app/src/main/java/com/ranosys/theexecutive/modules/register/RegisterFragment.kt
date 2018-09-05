package com.ranosys.theexecutive.modules.register

import AppLog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentRegisterBinding
import com.ranosys.theexecutive.modules.home.HomeFragment
import com.ranosys.theexecutive.utils.*
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_register.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Mohammad Sunny on 12/3/18.
 */
class RegisterFragment: BaseFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var  registerViewModel: RegisterViewModel
    private var  isFromSocialLogin: Boolean = false
    private var  socialLoginFirstName: String = ""
    private var  socialLoginLastName: String = ""
    private var  socialLoginEmail: String = ""

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.create_account), 0, "", R.drawable.back, true, 0, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = arguments
        data?.run {
            isFromSocialLogin = (data.get(Constants.FROM_SOCIAL_LOGIN)) as Boolean
            socialLoginFirstName = data.get(Constants.FROM_SOCIAL_LOGIN_FIRST_NAME).toString()
            socialLoginLastName = data.get(Constants.FROM_SOCIAL_LOGIN_LAST_NAME).toString()
            socialLoginEmail = data.get(Constants.FROM_SOCIAL_LOGIN_EMAIL).toString()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentRegisterBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)

        registerViewModel.countryHint = RegisterDataClass.Country(full_name_locale = getString(R.string.country))
        registerViewModel.stateHint = RegisterDataClass.State(name = getString(R.string.state_label))
        registerViewModel.cityHint = RegisterDataClass.City(name = getString(R.string.city))
        registerViewModel.initSpinnerHints()

        mViewDataBinding?.registerViewModel =  registerViewModel

        registerViewModel.isSocialLogin = isFromSocialLogin
        registerViewModel.firstName.set(socialLoginFirstName)
        registerViewModel.lastName.set(socialLoginLastName)
        registerViewModel.emailAddress.set(socialLoginEmail)
        registerViewModel.callCountryApi()

        observeApiFailure()
        observeApiSuccess()

        return mViewDataBinding?.root

    }

    private fun observeApiSuccess() {
        registerViewModel.apiDirectRegSuccessResponse?.observe(this, android.arch.lifecycle.Observer { response ->
            hideLoading()
            Utils.showDialog(activity as Context, getString(R.string.verify_email_message), context?.getString(R.string.ok), "", object: DialogOkCallback{
                override fun setDone(done: Boolean) {
                    //FragmentUtils.addFragment(activity as Context, LoginFragment(), null, LoginFragment::class.java.name, false)
                    activity?.onBackPressed()
                }

            } )
        })

        registerViewModel.apiSocialRegResponse?.observe(this, android.arch.lifecycle.Observer { token ->
            if(!TextUtils.isEmpty(token)){
                hideLoading()
                Toast.makeText(activity, getString(R.string.register_successfull), Toast.LENGTH_SHORT).show()


                //send locan broadcast on successfull login
                // Create intent with action
                val loginIntent = Intent("LOGIN")
                LocalBroadcastManager.getInstance(activity as Context).sendBroadcast(loginIntent)

                //api to get cart id
                registerViewModel.getCartIdForUser(token)
                SavedPreferences.getInstance()?.saveStringValue(token, Constants.USER_ACCESS_TOKEN_KEY)
                SavedPreferences.getInstance()?.saveStringValue(registerViewModel.emailAddress.get(), Constants.USER_EMAIL)
                FragmentUtils.addFragment(activity as Context, HomeFragment(), null, HomeFragment::class.java.name, false)
            }
        })

        registerViewModel.userCartIdResponse?.observe(this, android.arch.lifecycle.Observer {response ->
            val userCartId = response?.apiResponse ?: response?.error
            if(userCartId is String){
                registerViewModel.getUserCartCount()
            }
            else {
                Toast.makeText(activity, getString(R.string.common_error), Toast.LENGTH_LONG).show()
            }
        })

        registerViewModel.userCartCountResponse?.observe(this, android.arch.lifecycle.Observer { response ->
            val userCount = response?.apiResponse
            if(userCount is String){
                try {
                    Utils.updateCartCount(userCount.toInt())
                }catch (e : NumberFormatException){
                    AppLog.printStackTrace(e)
                }
            }
            else {
                Toast.makeText(activity, getString(R.string.common_error), Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun observeApiFailure() {
        registerViewModel.apiFailureResponse?.observe(this, android.arch.lifecycle.Observer { errorMsg ->
            hideLoading()
            var msg = errorMsg
            if(errorMsg == Constants.ERROR_CODE_400.toString()){
                msg = getString(R.string.error_user_already_exist)
            }else if(errorMsg == Constants.ERROR_CODE_401.toString()){
                msg = getString(R.string.error_invalid_login_credential)

            }
            Utils.showDialog(activity as Context, msg, context?.getString(R.string.ok), "", null)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       et_country_code.setOnTouchListener { v, event ->
            spinner_country_code.performClick()
        }
        //country spinner
        val gson = Gson()
        val countries = gson.fromJson((activity as BaseActivity).getCountryJson(), RegisterDataClass.CountryCodeList:: class.java)

        val country = CountryAdapter(activity!!, countries.countryList)
        spinner_country_code.adapter = country
        spinner_country_code.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val code: String = countries.countryList[position].dial_code
                et_country_code.setText(code)
                registerViewModel.countryCode.set(code)
            }
        }


        tv_subscribe_msg.text = GlobalSingelton.instance?.configuration?.subscription_message

        if(isFromSocialLogin && !TextUtils.isEmpty(socialLoginEmail)){
            et_email_address.isEnabled = false
        }

        btn_create_account.setOnClickListener {

            Utils.hideSoftKeypad(activity as Context)
            if (Utils.isConnectionAvailable(activity as Context)) {
                if(registerViewModel.isValidData(activity as Context)){
                    if(registerViewModel.isValidData(activity as Context)){
                        showLoading()
                        registerViewModel.callRegisterApi()
                    }
                }
            } else {
                Utils.showNetworkErrorDialog(activity as Context)
            }
        }

        et_dob.setOnClickListener {
            showDate(Calendar.getInstance().get(Calendar.YEAR) - Constants.MINIMUM_AGE, 0, 1, R.style.DatePickerSpinner)
        }

        cb_subscribe.setOnCheckedChangeListener { buttonView, isChecked ->
            registerViewModel.isSubscribed.set(isChecked)
        }


        clickableTextView(tv_terms_and_conditions,getString(R.string.term_and_condition),getString(R.string.t_and_c))
    }


    private fun  clickableTextView(textView: TextView, termAndCodition:String,tc:String) {
        val spanText =  SpannableStringBuilder(termAndCodition)
        val url=GlobalSingelton.instance?.configuration?.terms_and_condition_url
        val startIndex: Int = termAndCodition.indexOf(tc)
        val length = tc.length
        spanText.setSpan(object :ClickableSpan() {
            override fun onClick(p0: View?) {
                openWebPage(activity as Context,url!!,getString(R.string.t_and_c))
            }

            override  fun updateDrawState(ds: TextPaint?) {
                super.updateDrawState(ds)
                ds!!.color = Color.BLACK
                ds.isUnderlineText = true
            }

        }, startIndex, startIndex + length, 0)

        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(spanText, TextView.BufferType.SPANNABLE)

    }
    private fun openWebPage(context: Context, url: String, title: String) {
        val fragment = FragmentUtils.getCurrentFragment(context as BaseActivity)
        fragment?.run {
            (fragment as BaseFragment).prepareWebPageDialog(context, url, title)
        }
    }
    private fun showDate(year: Int, monthOfYear: Int, dayOfMonth: Int, spinnerTheme: Int) {
        val dpd = SpinnerDatePickerDialogBuilder()
                .context(activity)
                .callback(this)
                .spinnerTheme(spinnerTheme)
                .year(year)
                .finalYear(Calendar.getInstance().get(Calendar.YEAR) - Constants.MINIMUM_AGE)
                .monthOfYear(monthOfYear)
                .dayOfMonth(dayOfMonth)
                .build()

        dpd.show()
        dpd.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity as Context, R.color.black))
        dpd.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity as Context, R.color.black))
    }

    override fun onDateSet(view: com.tsongkha.spinnerdatepicker.DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calender: Calendar = Calendar.getInstance()
        calender.set(year, monthOfYear, dayOfMonth)
        val dob: Date = calender.time
        registerViewModel.dob.set(dob)
        val dateFormat = SimpleDateFormat(Constants.DD_MM_YY_DATE_FORMAT)
        et_dob.setText(dateFormat.format(dob))    }

}
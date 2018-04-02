package com.ranosys.theexecutive.modules.register

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentRegisterBinding
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_register.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Mohammad Sunny on 31/1/18.
 */
class RegisterFragment: BaseFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var  registerViewModel: RegisterViewModel
    private var  isFromSocialLogin: Boolean = false
    private var  socialLoginFirstName: String = ""
    private var  socialLoginLastName: String = ""
    private var  socialLoginEmail: String = ""

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.title_register), 0, false, 0, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = arguments
        if(null != data) {
            isFromSocialLogin = (data.get(Constants.FROM_SOCIAL_LOGIN)) as Boolean
            socialLoginFirstName = data.get(Constants.FROM_SOCIAL_LOGIN_FIRST_NAME).toString()
            socialLoginLastName = data.get(Constants.FROM_SOCIAL_LOGIN_LAST_NAME).toString()
            socialLoginEmail = data.get(Constants.FROM_SOCIAL_LOGIN_EMAIL).toString()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentRegisterBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        mViewDataBinding?.registerViewModel =  registerViewModel

        registerViewModel.isSocialLogin = isFromSocialLogin
        registerViewModel.firstName.set(socialLoginFirstName)
        registerViewModel.lastName.set(socialLoginLastName)
        registerViewModel.emailAddress.set(socialLoginEmail)
        registerViewModel.callCountryApi()
        return mViewDataBinding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_create_account.setOnClickListener {

            Utils.hideSoftKeypad(activity as Context)
            if (Utils.isConnectionAvailable(activity as Context)) {
                //TODO - showLoading()
                registerViewModel.callRegisterApi()

            } else {
                Toast.makeText(activity, getString(R.string.network_error), Toast.LENGTH_LONG).show()
            }
        }

        et_dob.setOnClickListener {

            showDate(Calendar.getInstance().get(Calendar.YEAR) - Constants.MINIMUM_AGE, 0, 1, R.style.DatePickerSpinner)
        }

        cb_subscribe.text = SavedPreferences.getInstance()?.getStringValue(Constants.SUBS_MESSAGE)

        cb_subscribe.setOnCheckedChangeListener { buttonView, isChecked ->
            registerViewModel.isSubscribed.set(isChecked)
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
        dpd.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resources.getColor(android.R.color.black))
        dpd.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(resources.getColor(android.R.color.black))

    }

    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        val calender: Calendar = Calendar.getInstance()
        calender.set(year, monthOfYear, dayOfMonth)
        val dob: Date = calender.time
        registerViewModel.dob.set(dob)
        val dateFormat = SimpleDateFormat(Constants.DD_MM_YY_DATE_FORMAT)
        et_dob.setText(dateFormat.format(dob))
    }

}
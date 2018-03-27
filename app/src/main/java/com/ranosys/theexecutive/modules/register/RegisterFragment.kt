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
import java.util.*


/**
 * Created by Mohammad Sunny on 31/1/18.
 */
class RegisterFragment: BaseFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var  registerViewModel: RegisterViewModel

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.title_register), 0, false, 0, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentRegisterBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        mViewDataBinding?.registerViewModel =  registerViewModel

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
                Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            }
        }

        et_dob.setOnClickListener {

            showDate(Calendar.getInstance().get(Calendar.YEAR) - Constants.MINIMUM_AGE, 0, 1, R.style.DatePickerSpinner)
        }

        cb_subscribe.text = getString(R.string.subscribe, SavedPreferences.getInstance()?.getStringValue(Constants.VOUCHER_AMT))
        tv_terms_and_conditions.text = SavedPreferences.getInstance()?.getStringValue(Constants.SUBS_MESSAGE)
    }

   fun showDate(year: Int, monthOfYear: Int, dayOfMonth: Int, spinnerTheme: Int) {
        val dpd = SpinnerDatePickerDialogBuilder()
                .context(activity)
                .callback(this)
                .spinnerTheme(spinnerTheme)
                .year(year)
                .monthOfYear(monthOfYear)
                .dayOfMonth(dayOfMonth)
                .build()


        dpd.show()
        dpd.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resources.getColor(android.R.color.black))
        dpd.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(resources.getColor(android.R.color.black))

    }

    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val month = monthOfYear + 1
        val dob = "$dayOfMonth/$month/$year"
       et_dob.setText(dob)
    }

}
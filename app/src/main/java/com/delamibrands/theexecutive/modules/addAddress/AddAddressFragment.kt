package com.delamibrands.theexecutive.modules.addAddress

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import com.google.gson.Gson
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.base.BaseActivity
import com.delamibrands.theexecutive.base.BaseFragment
import com.delamibrands.theexecutive.databinding.FragmentAddAddressBinding
import com.delamibrands.theexecutive.modules.register.CountryAdapter
import com.delamibrands.theexecutive.modules.register.RegisterDataClass
import com.delamibrands.theexecutive.utils.DialogOkCallback
import com.delamibrands.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_add_address.*


/**
 * @Details screen to add address
 * @Author Ranosys Technologies
 * @Date 07-May-2018
 */
class AddAddressFragment: BaseFragment() {

    private lateinit var mViewModel: AddAddressViewModel
    private lateinit var mBinding: FragmentAddAddressBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_address, container, false)
        mViewModel = ViewModelProviders.of(this).get(AddAddressViewModel::class.java)
        mViewModel.prepareMaskedAddress()
        mBinding.vm = mViewModel
        callCountryApi()

        observeCountryList()
        observeEditAddressResponse()
        return mBinding.root
    }

    private fun observeEditAddressResponse() {
        mViewModel.updateAddressApiResponse.observe(this, Observer { apiResponse ->
            hideLoading()
            if(apiResponse?.error.isNullOrBlank()){
                Toast.makeText(activity,getString(R.string.add_address_success_msg), Toast.LENGTH_SHORT).show()
                activity?.onBackPressed()
            }else{
                Utils.showDialog(activity,getString(R.string.add_address_failure_msg), getString(R.string.ok), "", null)
            }
        })
    }

    private fun callCountryApi() {
        if (Utils.isConnectionAvailable(activity as Context)) {
            showLoading()
            mViewModel.callCountryApi()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.etCountryCode.setOnTouchListener { v, event ->
            mBinding.spinnerCountryCode.performClick()
        }

        //country spinner
        val gson = Gson()
        val countries = gson.fromJson((activity as BaseActivity).getCountryJson(), RegisterDataClass.CountryCodeList:: class.java)

        val country = CountryAdapter(activity!!, countries.countryList)
        mBinding.spinnerCountryCode.adapter = country
        mBinding.spinnerCountryCode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val code: String = countries.countryList[position].dial_code
                mBinding.etCountryCode.setText(code)
            }
        }

        add_address.setOnClickListener {
            Utils.hideSoftKeypad(activity as Context)
            if (Utils.isConnectionAvailable(activity as Context)) {
                if(mViewModel.isValidData(activity as Context)){
                    showLoading()
                    mViewModel.maskedAddress?.country = mBinding.spinnerCountry.selectedItem.toString()
                    mViewModel.maskedAddress?.state = mBinding.spinnerState.selectedItem.toString()
                    mViewModel.maskedAddress?.city = mBinding.spinnerCity.selectedItem.toString()
                    mViewModel.maskedAddress?.countryCode = mBinding.etCountryCode.text.toString()
                    mViewModel.addAddress()

                }

            } else {
                Utils.showNetworkErrorDialog(activity as Context)
            }
        }
    }


    private fun observeCountryList() {
        mViewModel.countryListApiResponse.observe(this, Observer { apiResponse ->
            hideLoading()
            if(apiResponse?.error.isNullOrBlank().not()){
                Utils.showDialog(activity, getString(R.string.something_went_wrong_error), getString(R.string.ok), "", object: DialogOkCallback {
                    override fun setDone(done: Boolean) {
                        activity?.onBackPressed()
                    }

                })
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.add_addresse), 0, "", R.drawable.back, true, 0 , false)
    }

}
package com.ranosys.theexecutive.modules.editAddress

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.gson.Gson
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentEditAddressBinding
import com.ranosys.theexecutive.modules.myAccount.MyAccountDataClass
import com.ranosys.theexecutive.modules.register.CountryAdapter
import com.ranosys.theexecutive.modules.register.RegisterDataClass
import com.ranosys.theexecutive.utils.DialogOkCallback
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_edit_address.*
import kotlinx.android.synthetic.main.fragment_register.*
import java.io.IOException

/**
 * @Details screen for edit address
 * @Author Ranosys Technologies
 * @Date 03-May-2018
 */

class EditAddressFragment:BaseFragment() {
    private lateinit var mViewModel: EditAddressViewModel
    private lateinit var mBinding: FragmentEditAddressBinding
    private var address: MyAccountDataClass.Address? = null
    private var liveAddress: MutableLiveData<MyAccountDataClass.Address>?  = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_address, container, false)
        mViewModel = ViewModelProviders.of(this).get(EditAddressViewModel::class.java)
        mViewModel.prepareMaskedAddress(address)
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
                //to update selected address at checkout screen when address updated
                liveAddress?.value = apiResponse?.apiResponse?.addresses?.single { it.id == liveAddress?.value?.id }
                Toast.makeText(activity,getString(R.string.address_edit_success_msg), Toast.LENGTH_SHORT).show()
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

        edit_address.setOnClickListener {

            Utils.hideSoftKeypad(activity as Context)
            if (Utils.isConnectionAvailable(activity as Context)) {
                if(mViewModel.isValidData(activity as Context)){
                    showLoading()
                    mViewModel.maskedAddress?.country = mBinding.spinnerCountry.selectedItem.toString()
                    mViewModel.maskedAddress?.state = mBinding.spinnerState.selectedItem.toString()
                    mViewModel.maskedAddress?.city = mBinding.spinnerCity.selectedItem.toString()
                    mViewModel.maskedAddress?.countryCode = mBinding.etCountryCode.text.toString()
                    mViewModel.editAddress()

                }

            } else {
                Utils.showNetworkErrorDialog(activity as Context)
            }
        }
    }


    private fun observeCountryList() {
        mViewModel.countryListApiResponse.observe(this, Observer { apiResponse ->
            hideLoading()
            if(apiResponse?.error.isNullOrBlank()){

                val maskedAdd = mViewModel.maskedAddress
                mBinding.etCountryCode.setText(maskedAdd?._countryCode)

                val temp = mViewModel.countryList.single { it.full_name_english == maskedAdd?.country }
                mBinding.spinnerCountry.setSelection(mViewModel.countryList.indexOf(temp))
                val temp2 = mViewModel.countryList.flatMap { it.available_regions }.toList().single { it.name == maskedAdd?.state }
                mBinding.spinnerState.setSelection(mViewModel.countryList.flatMap { it.available_regions }.toList().indexOf(temp2))

            }else{
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
        setToolBarParams(getString(R.string.edit_addresse), 0, "", R.drawable.back, true, 0 , false)
    }

    companion object {

        fun getInstance(address : MyAccountDataClass.Address?, liveAddress: MutableLiveData<MyAccountDataClass.Address>? = null) =
                EditAddressFragment().apply {
                    this.address = address
                    this.liveAddress = liveAddress
                }
    }

}
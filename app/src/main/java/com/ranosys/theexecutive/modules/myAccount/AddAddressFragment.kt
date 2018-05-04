package com.ranosys.theexecutive.modules.myAccount

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentAddAddressBinding
import com.ranosys.theexecutive.utils.Utils

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 03-May-2018
 */
class AddAddressFragment:BaseFragment() {
    private lateinit var mViewModel: AddAddressViewModel
    private lateinit var mBinding: FragmentAddAddressBinding
    private var address: MyAccountDataClass.Address? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_address, container, false)
        mViewModel = ViewModelProviders.of(this).get(AddAddressViewModel::class.java)

        mViewModel.prepareMaskedAddress(address)
        callCountryApi()

        observeCountryList()
        return mBinding.root
    }

    private fun callCountryApi() {
        if (Utils.isConnectionAvailable(activity as Context)) {
           showLoading()
            mViewModel.callCountryApi()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    private fun observeCountryList() {
        mViewModel.countryList.observe(this, Observer { apiResponse ->
            hideLoading()
            if(apiResponse?.error.isNullOrBlank()){

                val maskedAdd = mViewModel.maskedAddress
                mBinding.spinnerCountryCode.setSelection((mBinding.spinnerCountryCode.adapter as ArrayAdapter<String>).getPosition(maskedAdd?._countryCode))
                mViewModel.selectedCountry.set(maskedAdd?.country)
                mViewModel.selectedState.set(maskedAdd?.state)
                mViewModel.selectedCity.set(maskedAdd?.city)

                mBinding.vm = mViewModel
//                mBinding.spinnerCountry.setSelection((mBinding.spinnerCountry.adapter as ArrayAdapter<RegisterDataClass.Country>).getPosition(mViewModel.countryList.value?.apiResponse?.single { it.full_name_english == maskedAdd?.country }))
//                mBinding.spinnerState.setSelection((mBinding.spinnerState.adapter as ArrayAdapter<RegisterDataClass.State>).getPosition(mViewModel.countryList.value?.apiResponse?.flatMap { it.available_regions }?.toList()?.single { it.name == maskedAdd?.state }))

            }else{
                Utils.showDialog(activity,"Country api failed", getString(android.R.string.ok), "", null)
            }
        })
    }


    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.add_addresse), 0, "", R.drawable.back, true, 0 , false)
    }

    companion object {

        fun getInstance(address : MyAccountDataClass.Address?) =
                AddAddressFragment().apply {
                    this.address = address
                }

    }
}
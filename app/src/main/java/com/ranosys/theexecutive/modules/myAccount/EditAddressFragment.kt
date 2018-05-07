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
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentEditAddressBinding
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_edit_address.*

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 03-May-2018
 */
class EditAddressFragment:BaseFragment() {
    private lateinit var mViewModel: EditAddressViewModel
    private lateinit var mBinding: FragmentEditAddressBinding
    private var address: MyAccountDataClass.Address? = null

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
                Toast.makeText(activity,"Address Edited Successfully", Toast.LENGTH_SHORT).show()
                FragmentUtils.addFragment(activity, AddressBookFragment(), null, AddressBookFragment::class.java.name, false )

            }else{
                Utils.showDialog(activity,"Country api failed", getString(android.R.string.ok), "", null)
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
        edit_address.setOnClickListener {

            Utils.hideSoftKeypad(activity as Context)
            if (Utils.isConnectionAvailable(activity as Context)) {
                if(mViewModel.isValidData(activity as Context)){
                    showLoading()
                    mViewModel.maskedAddress?.country = mBinding.spinnerCountry.selectedItem.toString()
                    mViewModel.maskedAddress?.state = mBinding.spinnerState.selectedItem.toString()
                    mViewModel.maskedAddress?.city = mBinding.spinnerCity.selectedItem.toString()
                    mViewModel.maskedAddress?.countryCode = mBinding.spinnerCountryCode.selectedItem.toString()
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
                mBinding.spinnerCountryCode.setSelection((mBinding.spinnerCountryCode.adapter as ArrayAdapter<String>).getPosition(maskedAdd?._countryCode))

                val temp = mViewModel.countryList.single { it.full_name_english == maskedAdd?.country }
                mBinding.spinnerCountry.setSelection(mViewModel.countryList.indexOf(temp))
                val temp2 = mViewModel.countryList.flatMap { it.available_regions }.toList().single { it.name == maskedAdd?.state }
                mBinding.spinnerState.setSelection(mViewModel.countryList.flatMap { it.available_regions }.toList().indexOf(temp2))

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
                EditAddressFragment().apply {
                    this.address = address
                }
    }
}
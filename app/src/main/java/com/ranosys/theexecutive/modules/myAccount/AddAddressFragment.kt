package com.ranosys.theexecutive.modules.myAccount

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
    private  var listener: AdapterView.OnItemSelectedListener? = null
    private  var listenerClick: AdapterView.OnItemLongClickListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_address, container, false)
        mViewModel = ViewModelProviders.of(this).get(AddAddressViewModel::class.java)
        mViewModel.prepareMaskedAddress(address)
        mBinding.vm = mViewModel
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(activity," Kuch to ho ja", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Toast.makeText(activity," Kuch to ho ja", Toast.LENGTH_SHORT).show()
            }

        }

        listenerClick = object :AdapterView.OnItemLongClickListener{
            override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Toast.makeText(activity," Kuch to ho ja", Toast.LENGTH_SHORT).show()
            }
        }
//        listener = object : AdapterView.OnItemSelectedListener{
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//            }
//
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                when(view?.id){
//                    mBinding.spinnerCountry.id -> mViewModel.onCountrySelection(position, mBinding.spinnerState)
//                    mBinding.spinnerState.id -> mViewModel.onStateSelection(position)
//                    mBinding.spinnerCity.id -> mViewModel.onCitySelection()
//
//                }
//            }
//
//        }


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
                mBinding.executePendingBindings()


                val temp = mViewModel.countryList.value?.apiResponse?.single { it.full_name_english == maskedAdd?.country }
                mBinding.spinnerCountry.setSelection(mViewModel.countryList.value?.apiResponse?.indexOf(temp)?: 0)
                val temp2 = mViewModel.countryList.value?.apiResponse?.flatMap { it.available_regions }?.toList()?.single { it.name == maskedAdd?.state }
                mBinding.spinnerState.setSelection(mViewModel.countryList.value?.apiResponse?.flatMap { it.available_regions }?.toList()?.indexOf(temp2) ?: 0)

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
package com.ranosys.theexecutive.modules.myInformation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentMyInformationBinding
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_my_information.*

/**
 * @Details Fragment showing my information screen
 * @Author Ranosys Technologies
 * @Date 27-Apr-2018
 */
class MyInformationFragment: BaseFragment() {
    private lateinit var mBinding: FragmentMyInformationBinding
    private lateinit var mViewModel: MyInformationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_information, container, false)
        mViewModel = ViewModelProviders.of(this).get(MyInformationViewModel::class.java)
        mBinding.info = mViewModel.maskedUserInfo.get()
        mBinding.vm = mViewModel

        observeUserInfoResponse()
        observeUpdateUserInfoResponse()
        getUserInformation()

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        et_mobile_number.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val newMobileNo = et_mobile_number.text.toString()
                if((mViewModel.maskedUserInfo.get()._mobile == newMobileNo).not()){
                    btn_save.background = (activity as Context).getDrawable(R.drawable.black_button_bg)
                }else{
                    btn_save.background = (activity as Context).getDrawable(R.color.hint_color)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        btn_save.setOnClickListener {
            //check if info updated
            val newMobileNo = et_mobile_number.text.toString()
            if((mViewModel.maskedUserInfo.get()._mobile == newMobileNo).not()){
                mViewModel.maskedUserInfo.get()._mobile = newMobileNo
                if(mViewModel.infoUpdated.not()) mViewModel.infoUpdated = true
            }

            val countryCode = spinner_country_code.selectedItem.toString()
            if((mViewModel.maskedUserInfo.get()._countryCode == countryCode).not()){
                mViewModel.maskedUserInfo.get()._countryCode = countryCode
                if(mViewModel.infoUpdated.not()) mViewModel.infoUpdated = true
            }

            //call api
            if (Utils.isConnectionAvailable(activity as Context)) {
                if(mViewModel.infoUpdated) {
                    if (mViewModel.isValidData(activity as Context)) {
                        showLoading()
                        mViewModel.updateUserInfo()
                    }
                }
            } else {
                Utils.showNetworkErrorDialog(activity as Context)
            }
        }

        spinner_country_code.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mViewModel.maskedUserInfo.get()?.run {
                    if(mViewModel.maskedUserInfo.get()._countryCode == (parent as Spinner).selectedItem){
                        btn_save.background = (activity as Context).getDrawable(R.color.hint_color)
                    }else{

                        btn_save.background = (activity as Context).getDrawable(R.drawable.black_button_bg)
                    }
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.my_information_option), 0, "", R.drawable.back, true, 0 , false)
    }

    private fun observeUserInfoResponse() {
        mViewModel.userInfoApiResponse.observe(this, Observer { apiResponse ->
            hideLoading()
            if(apiResponse?.error.isNullOrBlank().not()){
                Utils.showDialog(activity, getString(R.string.add_address_failure_msg), getString(R.string.ok), "", null)
            }else{
                mBinding.info = mViewModel.maskedUserInfo.get()
                mBinding.spinnerCountryCode.setSelection((mBinding.spinnerCountryCode.adapter as ArrayAdapter<String>).getPosition(mViewModel.maskedUserInfo.get()._countryCode))
            }


        })
    }

    private fun observeUpdateUserInfoResponse() {
        mViewModel.updateUserInfoApiResponse.observe(this, Observer { apiResponse ->
            hideLoading()
            if(apiResponse?.error.isNullOrBlank().not()){
                Utils.showDialog(activity, getString(R.string.add_address_failure_msg), getString(R.string.ok), "", null)
            }else{
                Toast.makeText(activity, getString(R.string.udate_profile_success_msg), Toast.LENGTH_SHORT).show()
                mBinding.info = mViewModel.maskedUserInfo.get()
                mBinding.spinnerCountryCode.setSelection((mBinding.spinnerCountryCode.adapter as ArrayAdapter<String>).getPosition(mViewModel.maskedUserInfo.get()._countryCode))
            }


        })
    }


    private fun getUserInformation() {
        if (Utils.isConnectionAvailable(activity as Context)) {
            showLoading()
            mViewModel.callUserInfoApi()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }
}
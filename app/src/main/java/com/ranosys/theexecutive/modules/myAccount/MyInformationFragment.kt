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
        getUserInformation()

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                if(mViewModel.infoUpdated){
                    if(mViewModel.isValidData(activity as Context)){
                        showLoading()
                        mViewModel.updateUserInfo()
                    }
                }else{
                    Utils.showDialog(activity,"Field not updated", getString(android.R.string.ok), "", null)
                }
            } else {
                Utils.showNetworkErrorDialog(activity as Context)
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
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }else{
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
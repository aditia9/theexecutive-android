package com.ranosys.theexecutive.modules.myAccount

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentMyInformationBinding
import com.ranosys.theexecutive.utils.GlobalSingelton
import com.ranosys.theexecutive.utils.Utils

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 27-Apr-2018
 */
class MyInformationFragment: BaseFragment() {
    private lateinit var mBinding: FragmentMyInformationBinding
    private lateinit var mViewModel: MyInformationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_information, container, false)
        mViewModel = ViewModelProviders.of(this).get(MyInformationViewModel::class.java)
        mBinding.myInfoVM = mViewModel

        getUserInformation()

        observeUserInfoResponse()
        mViewModel.callUserInfoApi()
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.my_information_option), 0, "", R.drawable.back, true, 0 , false)
    }

    private fun observeUserInfoResponse() {
        mViewModel.userInfoApiResponse?.observe(this, Observer { apiResponse ->
            hideLoading()
            if(apiResponse?.error.isNullOrBlank().not()){
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })
    }

    private fun getUserInformation() {
        if (Utils.isConnectionAvailable(activity as Context)) {
            if(GlobalSingelton.instance?.userInfo == null){
                showLoading()
            }
            mViewModel.callUserInfoApi()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }
}
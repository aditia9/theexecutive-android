package com.ranosys.theexecutive.modules.changePassword

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentChangePasswordBinding
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.DialogOkCallback
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_change_password.*


class ChangePasswordFragment : BaseFragment() {

    private lateinit var changePassVM: ChangePasswordViewModel
    private lateinit var mBinding: FragmentChangePasswordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_password, container, false)
        changePassVM = ViewModelProviders.of(this).get(ChangePasswordViewModel::class.java)
        mBinding.changePasswordViewModel = changePassVM

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        observeApiSuccess()
        observeApiFailure()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_submit?.setOnClickListener({
            if (Utils.isConnectionAvailable(activity as Context)) {
                if (changePassVM.validateData(activity as Context)) {
                    showLoading()
                    changePassVM.callChangePasswordApi()
                }

            } else {
                Utils.showNetworkErrorDialog(activity as Context)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setToolBarParams(getString(R.string.change_password), 0, "", R.drawable.back, true, 0, false)
    }

    private fun observeApiFailure() {
        changePassVM.apiFailureResponse?.observe(this, Observer { msg ->
            hideLoading()
            var errorMsg = msg
            if(msg == Constants.ERROR_CODE_404.toString()){
                errorMsg = getString(R.string.error_no_user_exist)
            }
            Utils.showDialog(activity, errorMsg, getString(android.R.string.ok), "", object : DialogOkCallback {
                override fun setDone(done: Boolean) {
                }
            })
        })
    }

    private fun observeApiSuccess() {
        changePassVM.apiSuccessResponse?.observe(this, Observer<Boolean> { isLinkSent ->
            if (isLinkSent!!) {
                hideLoading()
                Utils.showDialog(activity as Context, getString(R.string.password_change_msg), context?.getString(android.R.string.ok), "", object : DialogOkCallback {
                    override fun setDone(done: Boolean) {
                        activity?.onBackPressed()
                    }

                })

            }
        })
    }

    override fun onStop() {
        super.onStop()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}
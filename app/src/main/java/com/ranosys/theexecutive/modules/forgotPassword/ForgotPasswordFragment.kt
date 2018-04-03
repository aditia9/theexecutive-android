package com.ranosys.theexecutive.modules.forgotPassword

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentForgotPasswordBinding
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_forgot_password.*

/**
 * Created by nikhil on 8/3/18.
 */
class ForgotPasswordFragment: BaseFragment() {

    private lateinit var forgotPassVM: ForgotPasswordViewModel
    private lateinit var mBinding: FragmentForgotPasswordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false)
        forgotPassVM = ViewModelProviders.of(this).get(ForgotPasswordViewModel::class.java)
        mBinding.vm = forgotPassVM

        observeApiSuccess()
        observeApiFailure()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_submit?.setOnClickListener({
            if (Utils.isConnectionAvailable(activity as Context)) {
                if(forgotPassVM.validateData(activity as Context)){
                    showLoading()
                    forgotPassVM.callForgetPasswordApi()
                }

            }else{
                Utils.showNetworkErrorDialog(activity as Context)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.forgot_password_title), R.drawable.back, true, 0, false )
    }

    private fun observeApiFailure() {
        forgotPassVM.apiFailureResponse?.observe(this, Observer { msg ->
            hideLoading()
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        })
    }

    private fun observeApiSuccess() {
        forgotPassVM.apiSuccessResponse?.observe(this, Observer { msg ->
            hideLoading()
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        })
    }
}
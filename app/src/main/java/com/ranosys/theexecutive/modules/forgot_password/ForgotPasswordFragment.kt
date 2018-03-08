package com.ranosys.theexecutive.modules.forgot_password

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
class ForgotPasswordFragment(): BaseFragment() {

    lateinit var forgotPassVM: ForgotPasswordViewModel
    lateinit var mBinding: FragmentForgotPasswordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false)
        forgotPassVM = ViewModelProviders.of(this).get(ForgotPasswordViewModel::class.java)
        mBinding.vm = forgotPassVM

        observeSubmitClicked()
        observeApiSuccess()
        observeApiFailure()
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        setTitle("Forgot Password")
    }

    private fun observeApiFailure() {
        forgotPassVM.apiFailureResponse?.observe(this, Observer { msg ->
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        })
    }

    private fun observeApiSuccess() {
        forgotPassVM.apiSuccessResponse?.observe(this, Observer { msg ->
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        })
    }

    private fun observeSubmitClicked() {
        forgotPassVM.btnClicked?.observe(this, Observer<Int> { id ->
            when(id){
                btn_submit.id ->{
                    Utils.printLog("FORGOT PASSWORD", "SUBMIT CLICKED")

                    if (Utils.isConnectionAvailable(activity)) {
                        //validation
                        if(validateData(et_email.text.toString())){
                            //showLoading()
                            forgotPassVM?.callForgetPasswordApi()
                        }else{
                            //show validation message
                            til_email.error = "Invalid Email"
                        }
                    }else{
                        Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

    }

    private fun validateData(email: String): Boolean {
        return Utils.isValidEmail(email)
    }


}
package com.ranosys.theexecutive.modules.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ranosys.theexecutive.DashBoardActivity
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentLoginBinding
import com.ranosys.theexecutive.modules.register.RegisterFragment
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.annotations.Nullable


/**
 * Created by Mohammad Sunny on 25/1/18.
 */
class LoginFragment : BaseFragment() {

    lateinit var loginViewModel: LoginViewModel
    lateinit var mBinding: FragmentLoginBinding





    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        mBinding?.loginViewModel = loginViewModel
        observeEvent()
        observeApiFailure()
        observeApiSuccess()
        return mBinding?.root
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.title_login))
    }

    private fun observeEvent() {

        loginViewModel?.clickedBtnId?.observe(this, Observer<Int> { id ->

            when (id) {
//                tv_already_have_ac.id -> {
//                    if(null == fragmentManager.findFragmentByTag(RegisterFragment::class.java.name))
//                        FragmentUtils.replaceFragment(activity, RegisterFragment.newInstance(), RegisterFragment::class.java.name)
//                    loginViewModel?.clickedBtnId?.value = null
//
//                }
//                tv_forgot_password.id -> {
//                    showLoading()
//                    sendResetPasswordMail(loginViewModel?.email?.get()!!)
//                    loginViewModel?.clickedBtnId?.value = null
//                }

                btn_login.id -> {
                    Utils.hideSoftKeypad(activity)
                    if (Utils.isConnectionAvailable(activity)) {
                        //showLoading()
                        loginViewModel.login()

                    } else {
                        Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun observeApiFailure() {
        loginViewModel?.apiFailureResponse?.observe(this, Observer { msg ->
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        })

    }

    private fun observeApiSuccess() {
        loginViewModel?.apiSuccessResponse?.observe(this, Observer { token ->
            Toast.makeText(activity, token, Toast.LENGTH_SHORT).show()
           //load home fragment
        })

    }

}
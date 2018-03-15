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
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.activities.DashBoardActivity
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
 * Created by Mohammad Sunny on 21/2/18.
 */
class LoginFragment : BaseFragment() {

    private var loginViewModel: LoginViewModel? = null
    private var savedPreferences: SavedPreferences? = null

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedPreferences = SavedPreferences.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentLoginBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java!!)
        mViewDataBinding?.loginModel = loginViewModel
        mViewDataBinding?.executePendingBindings()
        observeNewClick()
        observeLoginApiResponse()
        return mViewDataBinding?.root
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.login),0, false, 0, false )
    }

    private fun observeNewClick() {
        loginViewModel?.clickedBtnId?.observe(this, Observer<Int> { id ->

            when (id) {
                tv_already_have_ac.id -> {
                    if(null == fragmentManager.findFragmentByTag(RegisterFragment::class.java.name))
                        FragmentUtils.replaceFragment(activity, RegisterFragment.newInstance(), RegisterFragment::class.java.name)
                    loginViewModel?.clickedBtnId?.value = null

                }
                tv_forgot_password.id -> {
                    showLoading()
                    sendResetPasswordMail(loginViewModel?.email?.get()!!)
                    loginViewModel?.clickedBtnId?.value = null
                }

                btn_login.id -> {
                    Utils.hideSoftKeypad(activity)
                    if (Utils.isConnectionAvailable(activity)) {
                        showLoading()
                        loginViewModel?.login()
                        //logIn()
                    } else {
                        Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
                    }
                    loginViewModel?.clickedBtnId?.value = null
                }
            }
        })
    }

    private fun sendResetPasswordMail(mail: String) {
        Toast.makeText(activity, "Reset password functionality.", Toast.LENGTH_LONG).show()
    }



    private fun observeLoginApiResponse() {
        loginViewModel?.mutualresponse?.observe(this, object : Observer<ApiResponse<LoginDataClass.LoginResponse>> {
            override fun onChanged(@Nullable apiResponse: ApiResponse<LoginDataClass.LoginResponse>?) {
                hideLoading()
                val response = apiResponse?.apiResponse ?: apiResponse?.error
                if (response is LoginDataClass.LoginResponse) {
                    loginViewModel?.login?.set(response)
                    Log.i("logInResponse ", response.accessToken)
                    val dashboard = Intent(activity, DashBoardActivity::class.java)
                    startActivity(dashboard)
                    activity.finish()
                    Toast.makeText(activity, "Verified User Login.", Toast.LENGTH_LONG).show()

                } else {
                    Log.i("logInResponse error ", response.toString())
                    Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

}
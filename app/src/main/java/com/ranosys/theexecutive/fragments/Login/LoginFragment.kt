package com.ranosys.theexecutive.fragments.Login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.ranosys.theexecutive.BR
import com.ranosys.theexecutive.DashBoardActivity
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentLoginBinding
import com.ranosys.theexecutive.fragments.Register.RegisterFragment
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.GlobalSingelton
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.annotations.Nullable


/**
 * Created by Mohammad Sunny on 25/1/18.
 */
class LoginFragment : BaseFragment() {

    private var loginViewModel: LoginViewModel? = null
    private var mAuth: FirebaseAuth? = null
    private var savedPreferences: SavedPreferences? = null

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        savedPreferences = SavedPreferences.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentLoginBinding? = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java!!)
        mViewDataBinding?.setVariable(getBindingVariable(), loginViewModel)
        mViewDataBinding?.executePendingBindings()
        observeNewClick()
        observeLoginApiResponse()
        return mViewDataBinding?.root
    }

    override fun onResume() {
        super.onResume()
        setTitle()
    }

    private fun observeNewClick() {
        loginViewModel?.clickedBtnId?.observe(this, Observer<Int> { id ->

            when (id) {
                R.id.tv_already_have_ac -> {
                    if(null == fragmentManager.findFragmentByTag(RegisterFragment::class.java.name))
                        FragmentUtils.replaceFragment(activity, RegisterFragment.newInstance(), RegisterFragment::class.java.name)
                    loginViewModel?.clickedBtnId?.value = null

                }
                R.id.tv_forgot_password -> {
                    showLoading()
                    sendResetPasswordMail(loginViewModel?.email?.get()!!)
                    loginViewModel?.clickedBtnId?.value = null
                }

                R.id.btn_login -> {
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

    override fun getTitle(): String? {
        return getString(R.string.title_login)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_login
    }

    override fun getBindingVariable(): Int {
        return BR.loginModel
    }
}
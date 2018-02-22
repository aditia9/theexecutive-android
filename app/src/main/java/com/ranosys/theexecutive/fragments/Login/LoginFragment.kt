package com.ranosys.theexecutive.fragments.Login

import android.arch.lifecycle.Observer
import android.content.Intent
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
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    private var loginViewModel: LoginViewModel? = null
    private var mAuth: FirebaseAuth? = null
    private var globalSingelton: GlobalSingelton? = null
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
        loginViewModel = LoginViewModel(LoginDataClass.LoginRequest("", "", "", "", ""))
        //  loginViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java!!)
        observeButtonClick()
        observeNewClick()
        observeLoginApiResponse()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        setTitle()
    }

    private fun observeNewClick() {
        loginViewModel?.isCreateAcctClicked?.observe(this, Observer<Int> { id ->
            when (id) {
                R.id.tv_already_have_ac -> {
                    FragmentUtils.replaceFragment(activity, RegisterFragment.newInstance(), RegisterFragment::class.java.name)
                }
                R.id.tv_forgot_password -> {
                    if (!TextUtils.isEmpty(loginViewModel?.email?.get())) {
                        if (Utils.isValidEmail(loginViewModel?.email?.get())) {
                            showLoading()
                            sendResetPasswordMail(loginViewModel?.email?.get()!!)
                        } else {
                            loginViewModel?.emailError?.set("Invalid mail id.")
                        }
                    } else {
                        loginViewModel?.emailError?.set("Provide email id.")
                    }
                }
            }
        })
    }

    private fun sendResetPasswordMail(mail: String) {
        mAuth?.sendPasswordResetEmail(mail)?.addOnCompleteListener(object : OnCompleteListener<Void> {
            override fun onComplete(task: Task<Void>) {
                hideLoading()
                if (task.isSuccessful) {
                    Toast.makeText(activity, "An email with link to reset password has been sent to you.", Toast.LENGTH_LONG).show()
                } else {
                    loginViewModel?.emailError?.set("Invalid mail id.")
                }
            }
        })
    }

    private fun observeButtonClick() {
        loginViewModel?.isButtonClicked?.observe(this, Observer<Int> { id ->
            when (id) {
                R.id.btn_login -> {
                    Utils.hideSoftKeypad(activity)
                    if (Utils.isConnectionAvailable(activity)) {
                        showLoading()
                        loginViewModel?.login()
                        //logIn()
                    } else {
                        Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
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
        return "Login"
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_login
    }

    override fun getBindingVariable(): Int {
        return BR.loginModel
    }

    override fun getViewModel(): LoginViewModel {
        return loginViewModel as LoginViewModel
    }

    fun logIn() {
        mAuth?.signInWithEmailAndPassword(loginViewModel?.email!!.get(),
                loginViewModel?.password!!.get())!!
                .addOnCompleteListener(activity, object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        hideLoading()
                        if (task.isSuccessful) {
                            val user = mAuth?.getCurrentUser()
                            if (user?.isEmailVerified!!) {
                                savedPreferences?.storeUserEmail(user.email!!)
                                savedPreferences?.setIsLogin(true)
                                val dashboard = Intent(activity, DashBoardActivity::class.java)
                                startActivity(dashboard)
                                activity.finish()
                                Toast.makeText(activity, "Verified User Login.", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(activity, "Please verify your mail id to proceed login.", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            val errorCode = (task.exception as FirebaseAuthException).errorCode
                            when (errorCode) {
                                "ERROR_INVALID_CREDENTIAL" -> {
                                    loginViewModel?.emailError?.set("Invalid email id.")
                                    et_emailid.requestFocus()
                                }
                                "ERROR_INVALID_EMAIL" -> {
                                    loginViewModel?.emailError?.set("The email address is badly formatted.")
                                    et_emailid.requestFocus()
                                }
                                "ERROR_WRONG_PASSWORD" -> {
                                    loginViewModel?.passwordError?.set("Password is incorrect ")
                                    password_et.requestFocus()
                                    loginViewModel?.password?.set("")
                                }
                                "ERROR_USER_MISMATCH" ->
                                    Toast.makeText(activity, "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show()
                                "ERROR_USER_DISABLED" ->
                                    Toast.makeText(activity, "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show()
                                "ERROR_USER_NOT_FOUND" -> {
                                    loginViewModel?.emailError?.set("This account does not exists.")
                                    et_emailid.requestFocus()
                                }
                            }
                            Toast.makeText(activity, errorCode, Toast.LENGTH_LONG).show()
                        }

                    }

                })
    }

}
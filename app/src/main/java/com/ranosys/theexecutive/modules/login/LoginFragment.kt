package com.ranosys.theexecutive.modules.login

import AppLog
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentLoginBinding
import com.ranosys.theexecutive.modules.forgotPassword.ForgotPasswordFragment
import com.ranosys.theexecutive.modules.home.HomeFragment
import com.ranosys.theexecutive.modules.register.RegisterFragment
import com.ranosys.theexecutive.utils.*
import com.ranosys.theexecutive.utils.Utils.showNetworkErrorDialog
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by Nikhil Agarwal on 23/2/18.
 */

class LoginFragment: BaseFragment() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var mBinding: FragmentLoginBinding
    private lateinit var callBackManager: CallbackManager
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var loginRequiredPrompt: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = arguments
        data?.let {
            loginRequiredPrompt = data.get(Constants.LOGIN_REQUIRED_PROMPT) as Boolean
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        mBinding.loginVM = loginViewModel

        observeEvent()
        observeApiFailure()
        observeApiSuccess()
        observeIsEmailAvailableResponse()


        //call backs for fb login
        callBackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callBackManager, object : FacebookCallback<LoginResult>{
            override fun onError(error: FacebookException?) {
                LoginManager.getInstance().logOut()
            }

            override fun onCancel() {
                LoginManager.getInstance().logOut()
            }

            override fun onSuccess(result: LoginResult) {
                val fbLoginToken: AccessToken = result.accessToken
                getFbUserData(fbLoginToken)
            }

        })

        initialiseGmailLoginParams()

        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        et_password.transformationMethod = PasswordTransformationMethod()
    }

    private fun initialiseGmailLoginParams() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.gmail_server_client_id))
                .requestEmail()
                .requestProfile()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(activity as Activity, gso)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        callBackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GMAIL_SIGN_IN) {
            val task :Task<GoogleSignInAccount> =  GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGmailSignInResult(task)
        }
    }


    private fun observeEvent() {

        loginViewModel.clickedBtnId?.observe(this, Observer<Int> { id ->

            when (id) {
                btn_register.id -> {
                    FragmentUtils.addFragment(activity as Context, RegisterFragment(),null,  RegisterFragment::class.java.name, true)
                }

                btn_login.id -> {
                    Utils.hideSoftKeypad(activity as Context)
                    if (Utils.isConnectionAvailable(activity as Context)) {
                        if(loginViewModel.validateData(activity as Context)){
                            showLoading()
                            loginViewModel.login()
                        }

                    } else {
                        showNetworkErrorDialog(activity as Context)
                    }
                }

                btn_fb_login.id -> {
                    if (Utils.isConnectionAvailable(activity as Context)) {
                        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday", "user_photos"))

                    } else {
                        showNetworkErrorDialog(activity as Context)
                    }
                }

                btn_gmail_login.id -> {
                    if (Utils.isConnectionAvailable(activity as Context)) {
                        gmailSignIn()
                    } else {
                        showNetworkErrorDialog(activity as Context)
                    }
                }

                tv_forgot_password.id -> {
                    Utils.hideSoftKeypad(activity as Context)
                    FragmentUtils.addFragment(activity as Context, ForgotPasswordFragment(), null, ForgotPasswordFragment::class.java.name, true)

                }

            }
        })

        loginViewModel.userCartIdResponse?.observe(this, Observer {
            response ->
            val userCartId = response?.apiResponse ?: response?.error
            if(userCartId is String){
                loginViewModel.getUserCartCount()
            }
            else {
                Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
            }

        })

        loginViewModel.userCartCountResponse?.observe(this, Observer {
            response ->
            val userCount = response?.apiResponse
            if(userCount is String){
                try {
                    Utils.updateCartCount(userCount.toInt())
                }catch (e : NumberFormatException){
                    AppLog.printStackTrace(e)
                }
            }
            else {
                Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun observeApiFailure() {
        loginViewModel.apiFailureResponse?.observe(this, Observer { msg ->
            hideLoading()
            Utils.showDialog(activity, msg, getString(android.R.string.ok),"", object : DialogOkCallback{
                override fun setDone(done: Boolean) {
                }
            })
        })
    }

    private fun observeApiSuccess() {
        loginViewModel.apiSuccessResponse?.observe(this, Observer { token ->
            hideLoading()
            //api to get cart id
            loginViewModel.getCartIdForUser(token)
            SavedPreferences.getInstance()?.saveStringValue(token, Constants.USER_ACCESS_TOKEN_KEY)
            SavedPreferences.getInstance()?.saveStringValue(loginViewModel.email.get(), Constants.USER_EMAIL)
            FragmentUtils.addFragment(activity, HomeFragment(), null, HomeFragment::class.java.name, false)
        })

    }

    private fun observeIsEmailAvailableResponse() {
        loginViewModel.isEmailNotAvailable?.observe(this, Observer { data ->
            hideLoading()
            val bundle = Bundle()
            bundle.putBoolean(Constants.FROM_SOCIAL_LOGIN, true)
            bundle.putString(Constants.FROM_SOCIAL_LOGIN_FIRST_NAME, data?.firstName)
            bundle.putString(Constants.FROM_SOCIAL_LOGIN_LAST_NAME, data?.latsName)
            bundle.putString(Constants.FROM_SOCIAL_LOGIN_EMAIL, data?.email)
            FragmentUtils.addFragment(activity as Context, RegisterFragment(), bundle, RegisterFragment::class.java.name, true)
        })
    }

    private fun gmailSignIn() {
        val gmailSignInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(gmailSignInIntent, RC_GMAIL_SIGN_IN)
    }

    //method to get user data from FB
    private fun getFbUserData(fbLoginToken: AccessToken) {
        val request = GraphRequest.newMeRequest(fbLoginToken) { `object`, response ->
            val fbData = parseFbData(`object`)
            fbData.token = fbLoginToken.token
            if(!TextUtils.isEmpty(fbData.email)){
                showLoading()
                loginViewModel.isEmailAvailableApi(fbData)
            } else {
                Utils.printLog("Fb User Data", "error in fb data")
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "id, first_name, last_name, gender, email, birthday")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun parseFbData(`object`: JSONObject): LoginDataClass.SocialLoginData {
        var id = ""
        var firstName = ""
        var lastName = ""
        var email = ""
        var gender = ""
        try {
            val gson = Gson()
            val fbDataResult : FbData?
            fbDataResult = gson.fromJson(`object`.toString(), FbData::class.java)
            fbDataResult?.run {
                id = fbDataResult.id
                firstName = fbDataResult.first_name
                lastName = fbDataResult.last_name
                email = fbDataResult.email
                gender = fbDataResult.gender
            }

        } catch (e: JSONException) {
            AppLog.printStackTrace(e)
        }
        Utils.printLog("FB USER_INFO", "" + firstName + lastName + gender + email + id + "")

        return LoginDataClass.SocialLoginData(firstName, lastName, email = email, gender = gender, type = Constants.TYPE_FACEBOOK, token = "")
    }


    //method to get user data from Gmail
    private fun handleGmailSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)

            val gmailToken : String? = account.idToken
            val gmailData = getGmailData(account)
            gmailData.token = gmailToken!!

            if(!TextUtils.isEmpty(gmailData.email)){
                showLoading()
                loginViewModel.isEmailAvailableApi(gmailData)
            }else {
                Utils.printLog("Gmail User Data", "error in gmail data")
            }

        } catch (e : ApiException ) {
            AppLog.printStackTrace(e)
        }
    }

    private fun getGmailData(account: GoogleSignInAccount): LoginDataClass.SocialLoginData {

        val firstName = account.displayName
        val lastName = account.familyName
        val email = account.email

        //return all data
        return LoginDataClass.SocialLoginData(firstName!!, lastName!!, email = email!!, gender = "", type = Constants.TYPE_GMAIL, token = "")
    }


    companion object {
        const val RC_GMAIL_SIGN_IN = 200
    }

}


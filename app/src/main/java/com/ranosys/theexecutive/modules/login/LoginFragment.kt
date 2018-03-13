package com.ranosys.theexecutive.modules.login

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.TextUtils
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
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentLoginBinding
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONException
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.URL
import java.util.*


/**
 * Created by Mohammad Sunny on 25/1/18.
 */
class LoginFragment : BaseFragment() {

    lateinit var loginViewModel: LoginViewModel
    lateinit var mBinding: FragmentLoginBinding
    lateinit var callBackManager: CallbackManager
    lateinit var mGoogleSignInClient: GoogleSignInClient


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        mBinding.loginViewModel = loginViewModel

        observeEvent()
        observeApiFailure()
        observeApiSuccess()

        //call backs for fb login
        callBackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callBackManager, object : FacebookCallback<LoginResult>{
            override fun onError(error: FacebookException?) {
                Utils.printLog("FB LOGIN", "some error occurred")
                LoginManager.getInstance().logOut()
            }

            override fun onCancel() {
                Utils.printLog("FB LOGIN", "login failed")
            }

            override fun onSuccess(result: LoginResult) {
                val fbLoginToken: AccessToken = result.accessToken
                getFbUserData(fbLoginToken)
            }

        })

        //gmail login
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(activity as Activity, gso)


        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.title_login))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        callBackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GMAIL_SIGN_IN) {

            val task :Task<GoogleSignInAccount> =  GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGmailSignInResult(task);
        }
    }


    private fun observeEvent() {

        loginViewModel?.clickedBtnId?.observe(this, Observer<Int> { id ->

            when (id) {
                btn_login.id -> {
                    Utils.hideSoftKeypad(activity as Context)
                    if (Utils.isConnectionAvailable(activity as Context)) {
                        //showLoading()
                        loginViewModel.login()

                    } else {
                        Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
                    }
                }

                btn_fb_login.id -> {
                    if (Utils.isConnectionAvailable(activity as Context)) {
                        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday", "user_photos"))

                    } else {
                        Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
                    }
                }

                btn_gmail_login.id -> {
                    if (Utils.isConnectionAvailable(activity as Context)) {
                        gmailSignIn()
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

    private fun gmailSignIn() {
        val gmailSignInIntent = mGoogleSignInClient.getSignInIntent()
        startActivityForResult(gmailSignInIntent, RC_GMAIL_SIGN_IN)
    }

    //method to get user data from FB
    private fun getFbUserData(fbLoginToken: AccessToken) {
        val request = GraphRequest.newMeRequest(fbLoginToken) { `object`, response ->
            val fbData = parseFbData(`object`)
            fbData.token = fbLoginToken.token
            if(!TextUtils.isEmpty(fbData.email)) loginViewModel.isEmailAvailableApi(fbData) else Utils.printLog("Fb Uase Data", "error in fb data")
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
            id = `object`.getString("id")

            if (`object`.has("first_name")) {
                firstName = `object`.getString("first_name")
            }

            if (`object`.has("last_name")) {
                lastName = `object`.getString("last_name")
            }

            if (`object`.has("email")) {
                email = `object`.getString("email")
            }

            if (`object`.has("gender")) {
                gender = `object`.getString("gender")
            }

            try {
                val profilePicUrl = URL("https://graph.facebook.com/$id/picture?type=large")
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utils.printLog("FB USER_INFO", "" + firstName + lastName + gender + email + id + "")

        return LoginDataClass.SocialLoginData(firstName, lastName, email = email, gender = gender, type = "facebook", token = "")
    }


    //method to get user data from Gmail
    private fun handleGmailSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            var account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java);

            var gmailToken : String? = account.idToken
            val gmailData = getGmailData(account)
            gmailData?.token = gmailToken!!

            if(!TextUtils.isEmpty(gmailData.email))loginViewModel.isEmailAvailableApi(gmailData)else Utils.printLog("Gmail User Data", "error in gmail data")

        } catch (e : ApiException ) {
            // The ApiException status code indicates the detailed failure reason.
            Utils.printLog("GMAIL LOG IN", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    fun getGmailData(account: GoogleSignInAccount): LoginDataClass.SocialLoginData {

            val firstName = account.displayName
            val lastName = account.familyName
            val email = account.email

            //return all data
            return LoginDataClass.SocialLoginData(firstName!!, lastName!!, email = email!!, gender = "", type = "gmail", token = "")
    }


    companion object {
        const val RC_GMAIL_SIGN_IN = 200
    }

}


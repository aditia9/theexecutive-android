package com.ranosys.theexecutive.modules.login

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





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        mBinding?.loginViewModel = loginViewModel

        observeEvent()
        observeApiFailure()
        observeApiSuccess()

        callBackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callBackManager, object : FacebookCallback<LoginResult>{
            override fun onError(error: FacebookException?) {
                Utils.printLog("FB LOGIN", "some error eccurred")
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

        return mBinding.root
    }

    private fun getFbUserData(fbLoginToken: AccessToken) {
        //create request to get user data
        val request = GraphRequest.newMeRequest(fbLoginToken) { `object`, response ->
            //extract user data from json object
            val fbData = parseFbData(`object`)
            fbData.token = fbLoginToken.token
            if(!TextUtils.isEmpty(fbData.email)) loginViewModel.isEmailAvailableApi(fbData) else Utils.printLog("Fb Uase Data", "error in fb data")
        }

        val parameters = Bundle()
        parameters.putString("fields", "id, first_name, last_name, gender, email, birthday")
        request.parameters = parameters
        request.executeAsync()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        callBackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun parseFbData(`object`: JSONObject): LoginDataClass.SocialLoginData {
        var id = ""
        var firstName = ""
        var lastName = ""
        var email = ""
        var gender = ""
        var profilePic = ""
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
                profilePic = profilePicUrl.toString() // profilePicUrl + "";
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utils.printLog("FB USER_INFO", "" + firstName + lastName + gender + email + id + "")

        //return all data
        return LoginDataClass.SocialLoginData(firstName, lastName, email = email, gender = gender, type = "facebook", token = "")
    }

}
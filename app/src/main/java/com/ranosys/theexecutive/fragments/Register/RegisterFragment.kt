package com.ranosys.theexecutive.fragments.Register

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.ranosys.theexecutive.BR
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.databinding.FragmentRegisterBinding
import com.ranosys.theexecutive.utils.Utils


/**
 * Created by Mohammad Sunny on 31/1/18.
 */
class RegisterFragment: BaseFragment<FragmentRegisterBinding, BaseViewModel>() {
    private var registerViewModel: RegisterViewModel? = null
    private var mAuth: FirebaseAuth? = null
    var database = FirebaseDatabase.getInstance()

    companion object {
        fun newInstance(): RegisterFragment {
            return RegisterFragment()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle()

    }

    override fun onResume() {
        super.onResume()
        setTitle()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        registerViewModel = RegisterViewModel(RegisterDataClass.RegisterRequest("", "","","","",",",""))
        mAuth = FirebaseAuth.getInstance()
        observeRegisterButton()
        return super.onCreateView(inflater, container, savedInstanceState)

    }

    private fun observeRegisterButton() {
        registerViewModel?.buttonClicked?.observe(this, Observer<Int> { id ->
            Utils.hideSoftKeypad(activity)
            when (id) {
                R.id.btn_signup -> register()
            }
        })
    }

    private fun register() {
        if (Utils.isConnectionAvailable(activity)) {
            showLoading()
            // loginViewModel?.login()
            registerCall()
        } else {
            Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
        }
    }
    private fun registerCall() {
        mAuth?.createUserWithEmailAndPassword(registerViewModel?.emailId?.get().toString(), registerViewModel?.password?.get().toString())?.addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                    override fun onComplete(task: Task<AuthResult>) {
                        hideLoading()
                        if(task.isSuccessful){
                            val user = mAuth!!.currentUser
                            sendVarificationMail(user)
                            val rootReference = database.getReference()
                            val result = user?.email?.replace("[-+.^:,@#]".toRegex(),"")
                            rootReference.root.child(result).child("HOMEDATA").setValue(registerViewModel?.registerRequest)
                            Log.e("User", user?.email)
                        }else{
                            try {
                                throw task.getException()!!
                            } catch (e: FirebaseAuthWeakPasswordException) {
                                registerViewModel?.passwordError?.set("Weak password entered")
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                registerViewModel?.emailError?.set("Invalid Email id entered")
                            } catch (e: FirebaseAuthUserCollisionException) {
                                registerViewModel?.emailError?.set("User already exists")
                            } catch (e: Exception) {
                                Log.e("Register Error", e.message)
                            }
                        }
                    }

                })
    }

    private fun sendVarificationMail(user: FirebaseUser?) {
        user?.sendEmailVerification()?.addOnCompleteListener(object : OnCompleteListener<Void>{
            override fun onComplete(response: Task<Void>) {
                if(response.isSuccessful){
                    Toast.makeText(activity, "A varification mail has been sent to "+user.email+", verify to login.", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(activity, "Something Went Wrong.", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun getTitle(): String? {
        return "Register"
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_register

    }

    override fun getBindingVariable(): Int {
        return BR.registerViewModel
    }

    override fun getViewModel(): RegisterViewModel {
        return registerViewModel as RegisterViewModel
    }

}
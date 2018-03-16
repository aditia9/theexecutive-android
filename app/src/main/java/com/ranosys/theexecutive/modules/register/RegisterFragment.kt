package com.ranosys.theexecutive.modules.register

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
import com.ranosys.theexecutive.databinding.FragmentRegisterBinding
import com.ranosys.theexecutive.utils.Utils


/**
 * Created by Mohammad Sunny on 31/1/18.
 */
class RegisterFragment: BaseFragment() {
    private var registerViewModel: RegisterViewModel? = null

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
        setTitle(getString(R.string.title_register))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentRegisterBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java!!)
        mViewDataBinding?.registerViewModel =  registerViewModel
        mViewDataBinding?.executePendingBindings()
        observeRegisterButton()
        return mViewDataBinding?.root

    }

    private fun observeRegisterButton() {
        registerViewModel?.buttonClicked?.observe(this, Observer<Int> { id ->
            Utils.hideSoftKeypad(activity as Context)
            when (id) {
            //btn_signup.id -> register()
            }
        })
    }

    private fun register() {
        if (Utils.isConnectionAvailable(activity as Context)) {
            showLoading()
            //do register
        } else {
            Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
        }
    }

}
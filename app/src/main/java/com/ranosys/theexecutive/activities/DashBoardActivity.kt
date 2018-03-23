package com.ranosys.theexecutive.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.databinding.ActivityDashboardBinding
import com.ranosys.theexecutive.modules.login.LoginFragment
import com.ranosys.theexecutive.modules.register.RegisterFragment
import com.ranosys.theexecutive.utils.FragmentUtils

/**
 * Created by Mohammad Sunny on 19/2/18.
 */
class DashBoardActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbarBinding : ActivityDashboardBinding? = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        toolbarBinding?.toolbarViewModel = toolbarViewModel
        FragmentUtils.addFragment(this, RegisterFragment(), RegisterFragment::class.java.name)
    }

}

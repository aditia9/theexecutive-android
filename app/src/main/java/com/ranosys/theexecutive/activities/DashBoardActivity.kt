package com.ranosys.theexecutive

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.databinding.ActivityDashboardBinding
import com.ranosys.theexecutive.modules.home.HomeFragment
import com.ranosys.theexecutive.utils.FragmentUtils

/**
 * Created by Vikash Kumar Bijarniya on 2/2/18.
 */
class DashBoardActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbarBinding : ActivityDashboardBinding? = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        toolbarBinding?.toolbarViewModel = toolbarViewModel
        FragmentUtils.addFragment(this, HomeFragment.newInstance(), HomeFragment::class.java.name)
    }

}

package com.ranosys.theexecutive

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.ranosys.theexecutive.activities.ToolbarViewModel
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.databinding.ActivityDashboardBinding
import com.ranosys.theexecutive.databinding.FragmentHomeBinding
import com.ranosys.theexecutive.modules.home.HomeFragment
import com.ranosys.theexecutive.utils.FragmentUtils
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_user.*

/**
 * Created by Vikash Kumar Bijarniya on 2/2/18.
 */
class DashBoardActivity: BaseActivity() {
    companion object{
        var toolbarViewModel: ToolbarViewModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbarViewModel = ViewModelProviders.of(this).get(ToolbarViewModel::class.java)
        val toolbarBinding : ActivityDashboardBinding? = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbarBinding?.vm = toolbarViewModel

        toolbarViewModel!!.title.set("DashBoard")

        FragmentUtils.addFragment(this, HomeFragment.newInstance(), HomeFragment::class.java.name)
    }

}

package com.ranosys.theexecutive

import android.os.Bundle
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.fragments.Dashboard.HomeFragment
import com.ranosys.theexecutive.utils.FragmentUtils
import kotlinx.android.synthetic.main.activity_user.*

/**
 * Created by Vikash Kumar Bijarniya on 2/2/18.
 */
class DashBoardActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(toolbar)
        FragmentUtils.addFragment(this, HomeFragment.newInstance(), HomeFragment::class.java.name)
    }

}

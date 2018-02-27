package com.ranosys.theexecutive

import android.content.Intent
import android.os.Bundle
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.modules.Login.LoginFragment
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.SavedPreferences
import kotlinx.android.synthetic.main.activity_user.*

/**
 * Created by Mohammad Sunny on 25/1/18.
 */
class UserActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        setSupportActionBar(toolbar)
        if(SavedPreferences.getInstance()?.getIsLogin()!!){
            val homeIntent = Intent(this, DashBoardActivity::class.java)
            startActivity(homeIntent)
            finish()
        }else {
            if (savedInstanceState == null) {
                FragmentUtils.addFragment(this, LoginFragment.newInstance(), LoginFragment::class.java.name)
            }
        }
    }

}
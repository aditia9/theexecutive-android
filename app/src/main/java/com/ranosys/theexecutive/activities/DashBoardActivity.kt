package com.ranosys.theexecutive.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.TextUtils
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.databinding.ActivityDashboardBinding
import com.ranosys.theexecutive.modules.login.LoginFragment
import com.ranosys.theexecutive.modules.myAccount.ChangeLanguageFragment
import com.ranosys.theexecutive.modules.productListing.ProductListingFragment
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.SavedPreferences

/**
 * Created by Mohammad Sunny on 19/2/18.
 */
class DashBoardActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbarBinding : ActivityDashboardBinding? = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        toolbarBinding?.toolbarViewModel = toolbarViewModel
        if(TextUtils.isEmpty(SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY))){
            FragmentUtils.addFragment(this, ChangeLanguageFragment(), ChangeLanguageFragment::class.java.name)

        }else{
            FragmentUtils.addFragment(this, ProductListingFragment(), ProductListingFragment::class.java.name)
        }
    }

}

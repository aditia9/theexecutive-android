package com.ranosys.theexecutive.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.ActivityDashboardBinding
import com.ranosys.theexecutive.modules.home.HomeFragment
import com.ranosys.theexecutive.modules.login.LoginFragment
import com.ranosys.theexecutive.modules.myAccount.ChangeLanguageFragment
import com.ranosys.theexecutive.modules.productListing.ProductListingFragment
import com.ranosys.theexecutive.modules.register.RegisterFragment
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
//        if(TextUtils.isEmpty(SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY))){
//            FragmentUtils.addFragment(this, ChangeLanguageFragment(), null, ChangeLanguageFragment::class.java.name, false)
//
//        }else{
//            FragmentUtils.addFragment(this, HomeFragment(), null, HomeFragment::class.java.name, true)
//        }
        FragmentUtils.addFragment(this, RegisterFragment(), null, RegisterFragment::class.java.name, true)

        supportFragmentManager.addOnBackStackChangedListener(object : FragmentManager.OnBackStackChangedListener{
            override fun onBackStackChanged() {
                val backStackCount = supportFragmentManager.backStackEntryCount
                if(backStackCount > 0){
                    val fragment = FragmentUtils.getCurrentFragment(this@DashBoardActivity)
                    if(null != fragment){
                        if(fragment is HomeFragment) {
                            when(HomeFragment.fragmentPosition){
                                0 -> {
                                    (fragment as BaseFragment).setToolBarParams(getString(R.string.app_title), 0, false, R.drawable.bag, true)
                                }
                                1 -> {
                                    (fragment as BaseFragment).setToolBarParams(getString(R.string.login), 0, false, 0, false)
                                }
                                2 -> {
                                    (fragment as BaseFragment).setToolBarParams(getString(R.string.wishlist), 0, false, 0, false)
                                }
                            }
                        }
                        if(fragment is ProductListingFragment)
                            (fragment as BaseFragment).setToolBarParams(ProductListingFragment.category_name,R.drawable.back, true, R.drawable.bag, true )
                        if(fragment is LoginFragment) {
                            (fragment as BaseFragment).setToolBarParams(getString(R.string.login),0, false, 0, false)
                        }
                    }
                }
            }

        })

    }
}

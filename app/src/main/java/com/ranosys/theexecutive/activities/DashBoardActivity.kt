package com.ranosys.theexecutive.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
        val model = ViewModelProviders.of(this).get(DashBoardViewModel::class.java)
        model.manageFragments().observe(this, Observer { isCreated ->
            if(isCreated!!){
                if(TextUtils.isEmpty(SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY))){
                    FragmentUtils.addFragment(this, ChangeLanguageFragment(), null, ChangeLanguageFragment::class.java.name, false)
                }else{
                    FragmentUtils.addFragment(this, HomeFragment(), null, HomeFragment::class.java.name, true)
                }
            }

        })

        supportFragmentManager.addOnBackStackChangedListener(object : FragmentManager.OnBackStackChangedListener{
            override fun onBackStackChanged() {
                val backStackCount = supportFragmentManager.backStackEntryCount
                if(backStackCount > 0){
                    val fragment = FragmentUtils.getCurrentFragment(this@DashBoardActivity)
                    fragment?.run{
                        if(fragment is HomeFragment) {
                            when(HomeFragment.fragmentPosition){
                                0 -> {
                                    (fragment as BaseFragment).setToolBarParams("", R.drawable.logo, "", 0, false, R.drawable.bag, true, true)
                                }
                                1 -> {
                                    val isLogin = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
                                    if(TextUtils.isEmpty(isLogin)){
                                        (fragment as BaseFragment).setToolBarParams(getString(R.string.login), 0, "", R.drawable.cancel, true, 0, false, true)
                                    }else{
                                        (fragment as BaseFragment).setToolBarParams(getString(R.string.my_account_title), 0, "", 0, false, 0, false)
                                    }
                                }
                                2 -> {
                                    (fragment as BaseFragment).setToolBarParams(getString(R.string.wishlist), 0, "", 0, false, 0, false)
                                }
                            }
                        }
                        if(fragment is ProductListingFragment)
                            (fragment as BaseFragment).setToolBarParams(ProductListingFragment.categoryName, 0, "", R.drawable.back, true, R.drawable.bag, true )
                        if(fragment is LoginFragment) {
                            (fragment as BaseFragment).setToolBarParams(getString(R.string.login),0, "", 0,false, 0, false, true)
                        }
                    }
                }
            }

        })

    }
}

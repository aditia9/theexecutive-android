package com.ranosys.theexecutive.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.databinding.ActivityDashboardBinding
import com.ranosys.theexecutive.modules.home.HomeFragment
import com.ranosys.theexecutive.utils.FragmentUtils

/**
 * Created by Mohammad Sunny on 19/2/18.
 */
class DashBoardActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbarBinding : ActivityDashboardBinding? = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        toolbarBinding?.toolbarViewModel = toolbarViewModel
        FragmentUtils.addFragment(this, HomeFragment.newInstance(), HomeFragment::class.java.name)

        supportFragmentManager.addOnBackStackChangedListener(object : FragmentManager.OnBackStackChangedListener{
            override fun onBackStackChanged() {
                 var backStackCount = supportFragmentManager.getBackStackEntryCount()
                if(backStackCount > 0){
                    var fragment = FragmentUtils.getCurrentFragment(this@DashBoardActivity)
                    if(null != fragment){

                    }
                }
            }

        })

    }

   /* fun  getListener() : FragmentManager.OnBackStackChangedListener{
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                FragmentManager manager = getSupportFragmentManager();
                if (manager != null) {
                    int backStackEntryCount = manager.getBackStackEntryCount();
                    if (backStackEntryCount > 0) {
                        BaseFragment fragment = FragmentUtils.getCurrentFragment(DashboardActivity.this);
                        if (null != fragment) {
                            fragment.setTitle();
                            if(fragment instanceof MyProfileFragment){
                                showEditMenuItem();
                            } else {
                                hideEditMenuItem();
                            }
                            if(fragment instanceof MyProfileFragment || fragment instanceof EditProfileFragment
                                    || fragment instanceof PromotionListFragment || fragment instanceof PromotionDetailFragment) {
                                hideNotificationMenuItem();
                            } else {
                                showNotificationMenuItem();
                            }
                            if(fragment instanceof SearchDialog) {
                                setTransparentScreen();
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                            } else {
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                            }
                            if(fragment instanceof MyRewardsFragment) {
                                ((MyRewardsFragment)fragment).getGiftPointData();
                            }
                        }
                    }

                }
            }
        };
        return result;
    }
*/

}

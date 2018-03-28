package com.ranosys.theexecutive.modules.home

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.ranosys.theexecutive.modules.category.CategoryFragment
import com.ranosys.theexecutive.modules.login.LoginFragment
import com.ranosys.theexecutive.modules.myAccount.MyAccountFragment
import com.ranosys.theexecutive.modules.wishlist.WishlistFragment

/**
 * Created by Mohammad Sunny on 19/3/18.
 */
class HomeViewPager(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment? {
        when(position){
            0 -> {
                return CategoryFragment()
            }

            1 -> {
                return MyAccountFragment()
            }

            2 -> {
                return WishlistFragment()
            }
        }
        return null
    }

}
package com.ranosys.theexecutive.modules.home

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.text.TextUtils
import android.util.SparseArray
import android.view.ViewGroup
import com.ranosys.theexecutive.modules.category.CategoryFragment
import com.ranosys.theexecutive.modules.login.LoginFragment
import com.ranosys.theexecutive.modules.myAccount.MyAccountFragment
import com.ranosys.theexecutive.modules.wishlist.WishlistFragment
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences


/**
 * Created by Mohammad Sunny on 19/3/18.
 */
class HomeViewPager(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {

    private var fragmentManager : FragmentManager? = null
    private var sparseArray: SparseArray<Fragment>? = null

    init {
        fragmentManager = fm
        sparseArray = SparseArray()
    }


    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment? {
        when(position){
            0 -> {
                return CategoryFragment()
            }

            1 -> {
                val isLogin = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
                return if(TextUtils.isEmpty(isLogin)){
                    LoginFragment()
                }else {
                    MyAccountFragment()
                }
            }

            2 -> {
                val isLogin = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
                return if(TextUtils.isEmpty(isLogin)){
                    LoginFragment()
                }else {
                    WishlistFragment()
                }
            }
        }
        return null
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (0 <= sparseArray?.indexOfKey(position)!!) {
            sparseArray?.remove(position)
        }
        super.destroyItem(container, position, `object`)
    }

    /**
     * Get the item at the specified position in the adapter.
     *
     * @param position position of the item in the adapter
     * @return fragment instance
     */
    fun getItemAt(position: Int): Fragment {
        return sparseArray?.get(position)!!
    }


}
package com.delamibrands.theexecutive.modules.home

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.text.TextUtils
import android.util.SparseArray
import android.view.ViewGroup
import com.delamibrands.theexecutive.modules.category.CategoryFragment
import com.delamibrands.theexecutive.modules.login.LoginFragment
import com.delamibrands.theexecutive.modules.myAccount.MyAccountFragment
import com.delamibrands.theexecutive.modules.wishlist.WishlistFragment
import com.delamibrands.theexecutive.utils.Constants
import com.delamibrands.theexecutive.utils.SavedPreferences


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


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        sparseArray?.put(position, fragment)
        return fragment
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
                return WishlistFragment()
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
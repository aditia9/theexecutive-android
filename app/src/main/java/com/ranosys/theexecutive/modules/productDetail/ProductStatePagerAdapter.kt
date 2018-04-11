package com.ranosys.theexecutive.modules.productDetail

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * @Details An adapter class for all products showing in viewpager.
 * @Author Ranosys Technologies
 * @Date 11,Apr,2018
 */
class ProductStatePagerAdapter(fm : FragmentManager?, productList : List<Any>?) : FragmentStatePagerAdapter(fm){

    private var mProductList : List<Any>?

    init {
        mProductList = productList
    }

    override fun getItem(position: Int): Fragment {
           return ProductViewFragment()
    }

    override fun getCount(): Int {
        mProductList?.run {
            return size
        }
        return 0
    }
}
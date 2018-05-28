package com.ranosys.theexecutive.modules.productDetail

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass

/**
 * @Details An adapter class for all products showing in viewpager.
 * @Author Ranosys Technologies
 * @Date 11,Apr,2018
 */
class ProductStatePagerAdapter(fm : FragmentManager?, productList : List<ProductListingDataClass.Item>?, var pagerPosition : Int?) : FragmentStatePagerAdapter(fm){

    private var mProductList : List<ProductListingDataClass.Item>?

    init {
        mProductList = productList
    }

    override fun getItem(position: Int): Fragment {
        return ProductViewFragment.getInstance(mProductList?.get(position), mProductList?.get(position)?.sku, position, pagerPosition)
    }

    override fun getCount(): Int {
        mProductList?.run {
            return size
        }
        return 0
    }

    override fun destroyItem(container: ViewGroup, position: Int, o: Any) {
        Glide.get(container.context).clearMemory()

    }
}
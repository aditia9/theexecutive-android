package com.ranosys.theexecutive.modules.productDetail

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.ProductDetailViewBinding



/**
 * @Class An adapter class for all products showing in viewpager.
 * @author Ranosys Technologies
 * @Date 04-Apr-2018
 */
class ProductViewPagerAdapter(context: Context) : PagerAdapter()  {

    private var mContext : Context? = null
    private var layoutInflater : LayoutInflater? = null

    init {
        mContext = context
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as View
    }

    override fun getCount(): Int {
        return 5
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val listGroupBinding: ProductDetailViewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.product_detail_view, container, false);
        container.addView(listGroupBinding.root)
        return listGroupBinding.root
    }
}
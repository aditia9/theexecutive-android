package com.ranosys.theexecutive.modules.productDetail

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View

/**
 * Created by Mohammad Sunny on 4/4/18.
 */
class ProductViewPagerAdapter(context: Context) : PagerAdapter()  {

    private var mContext : Context? = null

    init {
        mContext = context
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return true
    }

    override fun getCount(): Int {
        return 0
    }

   /* override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val itemView = mLayoutInflater!!.inflate(R.layout.product_detail_view, container, false)
        return itemView

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun getItemPosition(`object`: Any?): Int {
        val index = data!!.indexOf(`object`)
        if (index == -1)
            return PagerAdapter.POSITION_NONE
        else
            return index
    }*/

}
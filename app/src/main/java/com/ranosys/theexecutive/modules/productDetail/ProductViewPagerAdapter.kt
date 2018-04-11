package com.ranosys.theexecutive.modules.productDetail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
class ProductViewPagerAdapter(fragment: ProductDetailFragment, productList : List<Any>) : PagerAdapter()  {

    private var mContext : ProductDetailFragment? = null
    private var mProductList : List<Any>?
    private var mLayoutInflater : LayoutInflater? = null
    private var mProductItemViewModel: ProductItemViewModel? = null

    init {
        mContext = fragment
        mProductList = productList
        mLayoutInflater = fragment.activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as View
    }

    override fun getCount(): Int {
        mProductList?.run {
            return size
        }
        return 0
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val listGroupBinding: ProductDetailViewBinding? = DataBindingUtil.inflate(mLayoutInflater, R.layout.product_detail_view, container, false);
        mProductItemViewModel = ViewModelProviders.of(mContext as ProductDetailFragment).get(ProductItemViewModel::class.java)
        //listGroupBinding?.btnAddToBag?.tag = position
        listGroupBinding?.productItemVM = mProductItemViewModel

        //observeAddToBagEvent(position)
        container.addView(listGroupBinding?.root)
        return listGroupBinding!!.root
    }

    fun observeAddToBagEvent(position : Int){
        mProductItemViewModel?.clickedAddBtnId?.observe(mContext as ProductDetailFragment, Observer<ProductItemViewModel.ViewClass> { viewClass ->
            when(viewClass){
                ProductItemViewModel.ViewClass(R.id.btn_add_to_bag, position) -> {
                    (mContext as ProductDetailFragment).openBottomSheet()
                }
            }

        })
    }
}
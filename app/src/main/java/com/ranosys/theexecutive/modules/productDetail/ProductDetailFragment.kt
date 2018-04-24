package com.ranosys.theexecutive.modules.productDetail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentProductDetailBinding
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_product_detail.*

/**
 * @Class The class shows the details of the product.
 * @author Ranosys Technologies
 * @Date 02-Apr-2018
 */
class ProductDetailFragment : BaseFragment() {

    private lateinit var productDetailViewModel : ProductDetailViewModel
    var productList : MutableList<ProductListingDataClass.Item>? = null
    var position : Int? = 0
    var productSku : String? = ""
    var pagerAdapter : ProductStatePagerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentProductDetailBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_product_detail, container, false)
        productDetailViewModel = ViewModelProviders.of(this).get(ProductDetailViewModel::class.java)
        productDetailViewModel.productList?.set(productList)
        mViewDataBinding?.productDetailVM = productDetailViewModel
        mViewDataBinding?.executePendingBindings()


        observeEvents()
        return mViewDataBinding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(null == productList){
            if (Utils.isConnectionAvailable(activity as Context)) {
                getProductDetail(productSku)
            } else {
                Utils.showNetworkErrorDialog(activity as Context)
            }

        }else{
            pagerAdapter = ProductStatePagerAdapter(childFragmentManager, productList)
            product_viewpager.adapter = pagerAdapter
            product_viewpager.offscreenPageLimit = 2
            product_viewpager.currentItem = position!!
        }

        product_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                setToolBarParams(productList?.get(position)?.name, 0,"", R.drawable.cancel, true, R.drawable.bag, true )
            }

            override fun onPageSelected(position: Int) {

            }
        })

    }

    override fun onResume() {
        super.onResume()
        productList?.let {
            setToolBarParams(productList?.get(position!!)?.name, 0,"", R.drawable.cancel, true, R.drawable.bag, true )
        }
    }


    private fun getProductDetail(productSku : String?){
        productDetailViewModel.getProductDetail(productSku)
    }

    private fun observeEvents() {
        productDetailViewModel.productDetailResponse?.observe(this, Observer<ApiResponse<ProductListingDataClass.Item>> { apiResponse ->
            val response = apiResponse?.apiResponse ?: apiResponse?.error
            if (response is ProductListingDataClass.Item) {
                productList = mutableListOf()
                productList?.add(response)
                setToolBarParams(productList?.get(position!!)?.name, 0,"", R.drawable.cancel, true, R.drawable.bag, true )
                pagerAdapter = ProductStatePagerAdapter(activity?.supportFragmentManager,productList)
                product_viewpager.adapter = pagerAdapter
                product_viewpager.offscreenPageLimit = 2
                product_viewpager.currentItem = position!!
            } else {
                Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
            }
        })
    }

    companion object {

        fun getInstance(productList : MutableList<ProductListingDataClass.Item>?, productSku : String?, position : Int?) =
                ProductDetailFragment().apply {
                    this.productList = productList
                    this.productSku = productSku
                    this.position = position
                }

    }
}
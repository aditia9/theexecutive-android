package com.ranosys.theexecutive.modules.productDetail

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentProductDetailBinding
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ProductDetailResponse
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass
import com.ranosys.theexecutive.utils.Constants
import kotlinx.android.synthetic.main.bottom_size_layout.*
import kotlinx.android.synthetic.main.fragment_product_detail.*

/**
 * @Class The class shows the details of the product.
 * @author Ranosys Technologies
 * @Date 02-Apr-2018
 */
class ProductDetailFragment : BaseFragment() {

    lateinit var productDetailViewModel : ProductDetailViewModel
    var productList : List<ProductListingDataClass.Item>? = null
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
        setToolBarParams(productList?.get(position!!)?.name, 0,"", R.drawable.cancel, true, R.drawable.bag, true )
        if(null == productList){
            productList = listOf()
            getProductDetail(productSku)
        }else{
            pagerAdapter = ProductStatePagerAdapter(childFragmentManager, productList)
            product_viewpager.adapter = pagerAdapter
            product_viewpager.offscreenPageLimit = 2
            product_viewpager.setCurrentItem(position!!)
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


    fun getProductDetail(productSku : String?){
        productDetailViewModel.getProductDetail(productSku)
    }

    fun observeEvents() {
        productDetailViewModel.productDetailResponse?.observe(this, object : Observer<ApiResponse<ProductDetailResponse>> {
            override fun onChanged(apiResponse: ApiResponse<ProductDetailResponse>?) {
                val response = apiResponse?.apiResponse ?: apiResponse?.error
                if (response is ProductDetailResponse) {
                    pagerAdapter = ProductStatePagerAdapter(activity?.supportFragmentManager,productList)
                    product_viewpager.adapter = pagerAdapter
                    product_viewpager.offscreenPageLimit = 2
                    product_viewpager.setCurrentItem(position!!)
                } else {
                    Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun openBottomSizeSheet ()
    {
        val view = layoutInflater.inflate(R.layout.bottom_size_layout, null)
        val mBottomSheetDialog = Dialog(activity, R.style.MaterialDialogSheet)
        mBottomSheetDialog.setContentView(view)
        mBottomSheetDialog.setCancelable(true)
        mBottomSheetDialog.getWindow()!!.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT /*+ rl_add_to_box.height*/)
        mBottomSheetDialog.getWindow()!!.setGravity(Gravity.BOTTOM)
        mBottomSheetDialog.show()

        mBottomSheetDialog.btn_done.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(mBottomSheetDialog.isShowing){
                    mBottomSheetDialog.dismiss()
                }
            }
        })
    }

    companion object {

        fun getInstance(productList : List<ProductListingDataClass.Item>?, productSku : String?, position : Int?) =
                ProductDetailFragment().apply {
                    this.productList = productList
                    this.productSku = productSku
                    this.position = position
                }

    }
}
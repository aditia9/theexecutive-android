package com.ranosys.theexecutive.modules.productDetail

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentProductDetailBinding
import kotlinx.android.synthetic.main.fragment_product_detail.*
import kotlinx.android.synthetic.main.product_detail_view.*

/**
 * @Class The class shows the details of the product.
 * @author Ranosys Technologies
 * @Date 02-Apr-2018
 */
class ProductDetailFragment : BaseFragment() {

    lateinit var productDetailViewModel : ProductDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentProductDetailBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_product_detail, container, false)
        productDetailViewModel = ViewModelProviders.of(this).get(ProductDetailViewModel::class.java)
        mViewDataBinding?.productDetailVM = productDetailViewModel
        mViewDataBinding?.executePendingBindings()
        return mViewDataBinding?.root

        observeAddToBagEvent()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeViewPager = ProductViewPagerAdapter(activity as Context)
        product_viewpager.adapter = homeViewPager
        //product_viewpager.offscreenPageLimit = 2
    }

    fun observeAddToBagEvent(){
        productDetailViewModel.clickedAddBtnId?.observe(this, Observer<Int> {id ->
            when(id){
                btn_add_to_bag.id -> {
                    openBottomSheet ()
                }
            }

        })
    }

    fun openBottomSheet ()
    {

        val view = layoutInflater.inflate(R.layout.bottom_size_layout, null)

        val mBottomSheetDialog = Dialog(activity, R.style.MaterialDialogSheet)
        mBottomSheetDialog.setContentView(view)
        mBottomSheetDialog.setCancelable(true)
        mBottomSheetDialog.getWindow()!!.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, 250)
        mBottomSheetDialog.getWindow()!!.setGravity(Gravity.BOTTOM)
        mBottomSheetDialog.show()


    }
}
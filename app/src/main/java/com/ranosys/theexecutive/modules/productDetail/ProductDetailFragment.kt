package com.ranosys.theexecutive.modules.productDetail

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentProductDetailBinding

/**
 * Created by Mohammad Sunny on 2/4/18.
 */
class ProductDetailFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentProductDetailBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_product_detail, container, false)
        mViewDataBinding?.executePendingBindings()
        return mViewDataBinding?.root
    }
}
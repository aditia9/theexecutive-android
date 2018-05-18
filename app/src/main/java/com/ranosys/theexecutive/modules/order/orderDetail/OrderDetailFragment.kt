package com.ranosys.theexecutive.modules.order.orderDetail

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentOrderDetailBinding


class OrderDetailFragment : BaseFragment(){

   private lateinit var orderDetailViewModel : OrderDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewBinder: FragmentOrderDetailBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_order_detail, container, false)
        orderDetailViewModel = ViewModelProviders.of(this).get(OrderDetailViewModel::class.java)
        viewBinder?.orderDetailViewModel = orderDetailViewModel

        return viewBinder?.root
    }
}
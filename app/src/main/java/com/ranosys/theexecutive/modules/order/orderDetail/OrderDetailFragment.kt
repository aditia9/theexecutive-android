package com.ranosys.theexecutive.modules.order.orderDetail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentOrderDetailBinding
import com.ranosys.theexecutive.modules.myAccount.DividerDecoration
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_order_detail.*

/**
 * @Details fragment shows Order Detail
 * @Author Ranosys Technologies
 * @Date 21, May,2018
 */

class OrderDetailFragment : BaseFragment() {

    private lateinit var orderDetailViewModel: OrderDetailViewModel
    private var orderId: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewBinder: FragmentOrderDetailBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_order_detail, container, false)
        orderDetailViewModel = ViewModelProviders.of(this).get(OrderDetailViewModel::class.java)
        viewBinder?.orderDetailViewModel = orderDetailViewModel


        val data = arguments
        data?.let {
            orderId = data.get(Constants.ORDER_ID) as String
        }

        getOrderDetail(orderId)
        showLoading()
        observeEvents()
        return viewBinder?.root
    }

    private fun observeEvents() {
        orderDetailViewModel.orderDetailResponse.observe(this, Observer<ApiResponse<OrderDetailResponse>> { apiResponse ->
            hideLoading()
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is OrderDetailResponse) {
                    orderDetailViewModel.orderDetailObservable?.set(response)
                    setOrderDetailAdapter()
                }
            } else {
                hideLoading()
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })

    }

    private fun setOrderDetailAdapter() {
        val linearLayoutManager = object : LinearLayoutManager(activity as Context, LinearLayoutManager.VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return true
            }
        }
        rv_order_detail_list.layoutManager = linearLayoutManager
        val itemDecor = DividerDecoration(resources.getDrawable(R.drawable.horizontal_divider, null))
        rv_order_detail_list.addItemDecoration(itemDecor)

        val orderDetailAdapter = OrderDetailAdapter(activity as Context, orderDetailViewModel.orderDetailObservable?.get())
        rv_order_detail_list.adapter = orderDetailAdapter

    }

    private fun getOrderDetail(orderId: String) {
        orderDetailViewModel.getOrderList(orderId = orderId)
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.order_no).toUpperCase() + " " + orderId, 0, "", R.drawable.back, true, 0, false)
    }
}
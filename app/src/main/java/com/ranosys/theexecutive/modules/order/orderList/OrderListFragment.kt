package com.ranosys.theexecutive.modules.order.orderList

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
import com.ranosys.theexecutive.databinding.FragmentOrderListBinding
import com.ranosys.theexecutive.modules.order.orderDetail.OrderDetailFragment
import com.ranosys.theexecutive.modules.order.orderReturn.OrderReturnFragment
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_order_list.*

/**
 * @Class An data class for Order List
 * @author Ranosys Technologies
 * @Date 21-May-2018
 */


class OrderListFragment : BaseFragment() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private var orderListModelView: OrderListViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val mViewDataBinding: FragmentOrderListBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_order_list, container, false)
        orderListModelView = ViewModelProviders.of(this).get(OrderListViewModel::class.java)
        mViewDataBinding?.executePendingBindings()
        observeEvents()
        callOrderListApi()
        return mViewDataBinding?.root
    }

    private fun callOrderListApi() {
        if (Utils.isConnectionAvailable(activity as Context)) {
            orderListModelView?.getOrderList()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(activity as Context)
        rv_order_list.layoutManager = linearLayoutManager

        observeEvents()
    }

    private fun observeEvents() {
        orderListModelView?.mutualOrderListResponse?.observe(this, Observer<ApiResponse<List<OrderListResponse>>> { apiResponse ->
            hideLoading()
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is List<OrderListResponse>) {
                    orderListModelView?.orderListResponse?.set(response as MutableList<OrderListResponse>?)
                    setShoppingBagAdapter()
                }
            } else {
                hideLoading()
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })


    }

    private fun setShoppingBagAdapter() {
        val linearLayoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.VERTICAL, false)
        rv_order_list.layoutManager = linearLayoutManager
        if (orderListModelView?.orderListResponse?.get()?.size!! > 0) {
            tv_no_items.visibility = View.GONE
            val orderListAdapter = OrderListAdapter(activity as Context, orderListModelView?.orderListResponse?.get(), { id: Int, orderId: String, item: OrderListResponse? ->
                when (id) {
                    0 -> {
                        val bundle = Bundle()
                        bundle.putString(Constants.ORDER_ID, orderId)
                        FragmentUtils.addFragment(context, OrderDetailFragment(), bundle, OrderDetailFragment::class.java.name, true)
                    }

                    R.id.btn_return -> {
                        val bundle = Bundle()
                        bundle.putString(Constants.ORDER_ID, orderId)
                        FragmentUtils.addFragment(context, OrderReturnFragment(), bundle, OrderReturnFragment::class.java.name, true)

                    }
                }
            })
            rv_order_list.adapter = orderListAdapter
        } else {
            tv_no_items.visibility = View.VISIBLE

        }
    }


    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.my_orders), 0, "", R.drawable.back, true, 0, false)
    }
}
package com.ranosys.theexecutive.modules.order

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
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_order_list.*

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
            val orderListAdapter = OrderListAdapter(activity as Context, orderListModelView?.orderListResponse?.get(), { id: Int, pos: Int, item: OrderListResponse? ->
                when (id) {
                    0 -> {

                    }

                }
            })
            rv_order_list.adapter = orderListAdapter
        }else{
            tv_no_items.visibility = View.VISIBLE

        }

    }
}
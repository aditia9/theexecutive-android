package com.ranosys.theexecutive.modules.order.orderReturn

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentOrderReturnBinding
import com.ranosys.theexecutive.modules.myAccount.DividerDecoration
import com.ranosys.theexecutive.modules.order.orderDetail.OrderDetailResponse
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_order_return.*


/**
 * @Class An data class for Order return Fragment
 * @author Ranosys Technologies
 * @Date 24-May-2018
 */
class OrderReturnFragment : BaseFragment() {

    private var orderId: String = ""
    private lateinit var orderReturnViewModel: OrderReturnViewModel
    private var isReturnable = false
    private var isReason = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewBinder: FragmentOrderReturnBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_order_return, container, false)
        orderReturnViewModel = ViewModelProviders.of(this).get(OrderReturnViewModel::class.java)
        viewBinder?.orderReturnVM = orderReturnViewModel

        val data = arguments
        data?.let {
            orderId = data.get(Constants.ORDER_ID) as String
        }

        getOrderDetail(orderId)
        observeEvents()
        showLoading()

        return viewBinder?.root
    }

    private fun observeEvents() {
        orderReturnViewModel.orderReturnResponse.observe(this, Observer<ApiResponse<OrderDetailResponse>> { apiResponse ->
            hideLoading()
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is OrderDetailResponse) {
                    orderReturnViewModel.orderDetailObservable?.set(response)
                    setOrderReturnAdapter()
                }
            } else {
                hideLoading()
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })

        orderReturnViewModel.orderReturnResponseStatus.observe(this, Observer<ApiResponse<String>> { apiResponse ->
            hideLoading()
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is String) {
                    Toast.makeText(activity, response, Toast.LENGTH_LONG).show()
                }
            } else {
                hideLoading()
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })


    }

    private fun setOrderReturnAdapter() {
        val linearLayoutManager = object : LinearLayoutManager(activity as Context, LinearLayoutManager.VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return true
            }
        }

        rv_order_return_list.layoutManager = linearLayoutManager
        val itemDecor = DividerDecoration(resources.getDrawable(R.drawable.horizontal_divider, null),1)
        rv_order_return_list.addItemDecoration(itemDecor)
        tv_order_id.text = getString(R.string.order_no) +" " +orderId

        val orderReturnAdapter = OrderReturnAdapter(activity as Context, orderReturnViewModel.orderDetailObservable?.get(), { id: Int ->

            when (id) {
                R.id.btn_return -> {
                    isReturnable = false
                    getOrderReturnRequest()
                    if (isReturnable && isReason){
                        showLoading()
                        orderReturnViewModel.returnProduct(getOrderReturnRequest())
                    }else if(!isReturnable){
                        Toast.makeText(activity, getString(R.string.no_item_selected), Toast.LENGTH_SHORT).show()
                    }else if(!isReason){
                        Toast.makeText(activity, getString(R.string.no_reason_selected), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
        rv_order_return_list.adapter = orderReturnAdapter

    }

    private fun getOrderReturnRequest(): OrderReturnRequest {
        val list = orderReturnViewModel.orderDetailObservable?.get()?.items

        var i = 0
        var item: Item
        val mutableItemList: MutableList<Item> = mutableListOf()
        list?.forEach {
            if (orderReturnViewModel.orderDetailObservable?.get()?.items?.get(i)?.request_return!!) {
                item = Item(orderReturnViewModel.orderDetailObservable?.get()?.items?.get(i)?.item_id!!, orderReturnViewModel.orderDetailObservable?.get()?.items?.get(i)?.request_reason!!, orderReturnViewModel.orderDetailObservable?.get()?.items?.get(i)?.request_qty!!)
                mutableItemList.add(i, item)
                isReturnable = true
            }

            if(!orderReturnViewModel.orderDetailObservable?.get()?.items?.get(i)?.request_reason!!.equals(getString(R.string.select_reason))){
                isReason = true
            }
            i++
        }

        val rmaData = RmaData(orderId, mutableItemList)
        return OrderReturnRequest(rmaData)
    }

    private fun getOrderDetail(orderId: String) {
        orderReturnViewModel.getOrderList(orderId = orderId)
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.returns), 0, "", R.drawable.back, true, 0, false)
    }

}
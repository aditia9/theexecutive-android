package com.ranosys.theexecutive.modules.checkout

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentOrderResultBinding
import com.ranosys.theexecutive.modules.order.orderDetail.OrderDetailFragment
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.DialogOkCallback
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_order_result.*

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 31-May-2018
 */
class OrderResultFragment: BaseFragment(){

    private lateinit var status: String
    private lateinit var orderId: String
    private lateinit var orderResultViewModel: OrderResultViewModel
    private lateinit var mBinding: FragmentOrderResultBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        orderId = arguments?.getString(Constants.ORDER_ID) ?: ""
        status = arguments?.getString(Constants.STATUS) ?: ""

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_result, container, false)
        orderResultViewModel = ViewModelProviders.of(this).get(OrderResultViewModel::class.java)
        mBinding.vm = orderResultViewModel
        orderResultViewModel.orderId.set(orderId)
        orderResultViewModel.status.set(status)

        observeEvents()
        callOrderStatusApi()


        //update user cart count
        Utils.updateCartCount( 0)

        return mBinding.root
    }

    private fun callOrderStatusApi() {
        if (Utils.isConnectionAvailable(activity as Context)) {
            orderResultViewModel.getOrderDetails()
        }
    }

    private fun observeEvents() {
        orderResultViewModel.apiError.observe(this, Observer { error ->
            handleError(error)
        })
    }

    private fun handleError(error: String?) {
        Utils.showDialog(activity as Context, error, (activity as Context).getString(android.R.string.ok), "", object: DialogOkCallback {
            override fun setDone(done: Boolean) {
                activity?.onBackPressed()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btn_action.setOnClickListener {
            when(status){
                Constants.SUCCESS -> {
                    popUpAllFragments()
                    val bundle = Bundle()
                    bundle.putString(Constants.ORDER_ID, orderResultViewModel.incrementalOrderId.get())
                    FragmentUtils.addFragment(activity as Context, OrderDetailFragment(), bundle, OrderDetailFragment::class.java.name, true)
                }

                Constants.FAILURE -> {
                    activity?.onBackPressed()
                }

                Constants.CANCEL -> {
                    activity?.onBackPressed()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams("", 0, "", 0, true, 0, true)
    }

    companion object {
        fun getInstance(orderId: String, status: String): OrderResultFragment{

            var bundle = Bundle()
            bundle.putString(Constants.ORDER_ID, orderId)
            bundle.putString(Constants.STATUS, status)
            val orderResultFragment = OrderResultFragment()
            orderResultFragment.arguments = bundle

            return orderResultFragment
        }
    }
}
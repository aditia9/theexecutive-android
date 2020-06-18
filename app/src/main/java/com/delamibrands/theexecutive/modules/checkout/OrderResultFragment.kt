package com.delamibrands.theexecutive.modules.checkout

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.base.BaseFragment
import com.delamibrands.theexecutive.databinding.FragmentOrderResultBinding
import com.delamibrands.theexecutive.modules.order.orderDetail.OrderDetailFragment
import com.delamibrands.theexecutive.utils.*
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
        orderResultViewModel.status = status

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

        orderResultViewModel.orderStatus.observe(this, Observer {
            processOrderStatus()
        })
    }

    private fun handleError(error: String?) {
        Utils.showDialog(activity as Context, error, (activity as Context).getString(R.string.ok), "", object: DialogOkCallback {
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
                    val parameters = Bundle()
                    parameters.putString(Constants.FB_EVENT_ORDER_ID, orderResultViewModel.incrementalOrderId.get())
                    getLogger()?.logEvent(Constants.FB_EVENT_NAME_PURCHASED, parameters)
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

            val bundle = Bundle()
            bundle.putString(Constants.ORDER_ID, orderId)
            bundle.putString(Constants.STATUS, status)
            val orderResultFragment = OrderResultFragment()
            orderResultFragment.arguments = bundle

            //clear payment initiated flag
            GlobalSingelton.instance?.paymentInitiated = false

            //clearing orderId form global singleton
            GlobalSingelton.instance?.orderId = ""

            return orderResultFragment
        }
    }


    private fun processOrderStatus() {
        var statusStr = ""
        var btnStr = ""
        var infoStr = ""

        if (orderResultViewModel.orderStatus.value?.order_state == Constants.CANCEL_STATUS) {
            status = Constants.CANCEL
        }

        when (status) {
            Constants.SUCCESS -> {
                statusStr = getString(R.string.order_success_msg)
                btnStr = getString(R.string.order_success_btn_text)
                infoStr = getString(R.string.order_success_info)
            }
            Constants.CANCEL -> {
                statusStr = getString(R.string.order_cancel_msg)
                btnStr = getString(R.string.order_cancel_btn_text)
                infoStr = getString(R.string.order_cancel_info)
            }
            Constants.FAILURE -> {
                statusStr = getString(R.string.order_failure_msg)
                btnStr = getString(R.string.order_failure_btn_text)
                infoStr = getString(R.string.order_failure_info)
            }
        }

        orderResultViewModel.statusMsg.set(statusStr)
        orderResultViewModel.infoMsg.set(infoStr)
        orderResultViewModel.btnAction.set(btnStr)
        orderResultViewModel.statusImg.set(status)
    }
}
package com.ranosys.theexecutive.modules.checkout

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentOrderResultBinding
import com.ranosys.theexecutive.modules.home.HomeFragment
import com.ranosys.theexecutive.utils.FragmentUtils
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

        orderId = arguments?.getString("order_id") ?: ""
        status = arguments?.getString("status") ?: ""

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_result, container, false)
        orderResultViewModel = ViewModelProviders.of(this).get(OrderResultViewModel::class.java)
        orderResultViewModel.orderId.set(orderId)
        orderResultViewModel.status.set(status)
        orderResultViewModel.getOrderDetails()

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btn_action.setOnClickListener {
            when(status){
                "success" -> {
                    FragmentUtils.addFragment(activity, HomeFragment(), null, HomeFragment::class.java.name, false)
                }

                "fail" -> {
                    FragmentUtils.addFragment(activity, HomeFragment(), null, HomeFragment::class.java.name, false)
                }

                "cancel" -> {
                    FragmentUtils.addFragment(activity, HomeFragment(), null, HomeFragment::class.java.name, false)
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
            bundle.putString("order_id", orderId)
            bundle.putString("status", status)
            val orderResultFragment = OrderResultFragment()
            orderResultFragment.arguments = bundle

            return orderResultFragment
        }
    }
}
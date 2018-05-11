package com.ranosys.theexecutive.modules.checkout

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentCheckoutBinding
import com.ranosys.theexecutive.utils.Utils

class CheckoutFragment : BaseFragment(){

    private lateinit var checkoutViewModel: CheckoutViewModel
    private lateinit var checkoutBinding: FragmentCheckoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        checkoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout, container, false)
        checkoutViewModel = ViewModelProviders.of(this).get(CheckoutViewModel::class.java)

        observeApiResponse()

        initiateCheckoutProcess()

        return checkoutBinding.root
    }

    private fun initiateCheckoutProcess() {
        if (Utils.isConnectionAvailable(activity as Context)) {
            showLoading()
            checkoutViewModel.getAddressApi()
            checkoutViewModel.getCartItemsApi()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    private fun observeApiResponse() {
        checkoutViewModel.selectedAddress.observe(this, Observer { address ->
            hideLoading()
            Toast.makeText(activity, "reponse aa gya", Toast.LENGTH_SHORT).show()
        })
    }
}
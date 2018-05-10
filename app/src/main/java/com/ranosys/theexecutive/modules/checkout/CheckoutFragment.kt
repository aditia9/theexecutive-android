package com.ranosys.theexecutive.modules.checkout

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentCheckoutBinding

class CheckoutFragment : BaseFragment(){

    private lateinit var checkoutViewModel: CheckoutViewModel
    private lateinit var checkoutBinding: FragmentCheckoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        checkoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout, container, false)
        checkoutViewModel = ViewModelProviders.of(this).get(CheckoutViewModel::class.java)

        checkoutViewModel.getAddressApi()
        checkoutViewModel.getCartItemsApi()
        return checkoutBinding.root
    }
}
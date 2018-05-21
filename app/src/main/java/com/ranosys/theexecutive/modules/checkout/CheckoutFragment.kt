package com.ranosys.theexecutive.modules.checkout

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
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentCheckoutBinding
import com.ranosys.theexecutive.modules.addressBook.AddressBookFragment
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_checkout.*

class CheckoutFragment : BaseFragment(){

    private lateinit var checkoutViewModel: CheckoutViewModel
    private lateinit var checkoutBinding: FragmentCheckoutBinding
    private lateinit var shoppingItemAdapter: ShoppingItemAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        checkoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout, container, false)
        checkoutViewModel = ViewModelProviders.of(this).get(CheckoutViewModel::class.java)
        shoppingItemAdapter = ShoppingItemAdapter(mutableListOf())

        observeApiResponse()

        initiateCheckoutProcess()

        return checkoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.HORIZONTAL, false)
        cart_item_list.layoutManager = linearLayoutManager
        cart_item_list.adapter = shoppingItemAdapter

        address_expand_img.setOnClickListener {
            FragmentUtils.addFragment(context, AddressBookFragment.getInstance(true, checkoutViewModel.selectedAddress),null, AddressBookFragment::class.java.name, true )
        }
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.checkout), 0, "", R.drawable.back, true, 0 , true)
        initiateCheckoutProcess()
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
            checkoutBinding.address = address

        })

        checkoutViewModel.country.observe(this, Observer { country->
            checkoutBinding.country = country
        })

        //observe shopping bag items
        checkoutViewModel.shoppingBagItems.observe(this, Observer { items ->
            Toast.makeText(activity, "LIst : " + items?.size.toString(), Toast.LENGTH_SHORT).show()
            shoppingItemAdapter.itemList = items?.toMutableList()
            shoppingItemAdapter.notifyDataSetChanged()
        })
    }
}
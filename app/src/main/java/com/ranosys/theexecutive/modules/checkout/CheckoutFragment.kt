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
    private lateinit var shippingMethodAdapter: ShippingtMethodAdapter
    private lateinit var paymentMethodAdapter: PaymentMethodAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        checkoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout, container, false)
        checkoutViewModel = ViewModelProviders.of(this).get(CheckoutViewModel::class.java)
        shoppingItemAdapter = ShoppingItemAdapter(mutableListOf())

        shippingMethodAdapter = ShippingtMethodAdapter(mutableListOf(), {
            isChecked: Boolean, shippingMethod: CheckoutDataClass.GetShippingMethodsResponse ->
            handleShippingMethodSelection(isChecked, shippingMethod)
        })

        paymentMethodAdapter = PaymentMethodAdapter(mutableListOf(), {
            isChecked: Boolean, paymentMethod: CheckoutDataClass.PaymentMethod ->
            handlePaymentMethodSelection(isChecked, paymentMethod)
        })

        observeApiResponse()

        initiateCheckoutProcess()

        return checkoutBinding.root
    }

    private fun handleShippingMethodSelection(checked: Boolean, shippingMethod: CheckoutDataClass.GetShippingMethodsResponse) {
        shippingMethodAdapter.notifyDataSetChanged()
        checkoutViewModel.getPaymentMethods(shippingMethod)
    }

    private fun handlePaymentMethodSelection(checked: Boolean, paymentMethod: CheckoutDataClass.PaymentMethod) {
        paymentMethodAdapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManagerHorizontal = LinearLayoutManager(activity as Context, LinearLayoutManager.HORIZONTAL, false)
        cart_item_list.layoutManager = linearLayoutManagerHorizontal
        cart_item_list.adapter = shoppingItemAdapter

        val linearLayoutManagerShippingMethod = LinearLayoutManager(activity)
        shipping_methods_list.layoutManager = linearLayoutManagerShippingMethod
        shipping_methods_list.adapter = shippingMethodAdapter

        val linearLayoutManagerPaymentMethod = LinearLayoutManager(activity)
        payment_methods_list.layoutManager = linearLayoutManagerPaymentMethod
        payment_methods_list.adapter = paymentMethodAdapter


        checkoutBinding.addressExpandView.setOnClickListener {
            FragmentUtils.addFragment(context, AddressBookFragment.getInstance(true, checkoutViewModel.selectedAddress),null, AddressBookFragment::class.java.name, true )
        }
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.checkout), 0, "", R.drawable.back, true, 0 , true)
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

            //call shipping method api according to updated address
            checkoutViewModel.getShippingMethodsApi(address?.id!!)

        })

        checkoutViewModel.country.observe(this, Observer { country->
            checkoutBinding.country = country
        })

        //observe shopping bag items
        checkoutViewModel.shoppingBagItems.observe(this, Observer { items ->
            shoppingItemAdapter.itemList = items?.toMutableList()
            shoppingItemAdapter.notifyDataSetChanged()
        })

        //observe shipping method list
        checkoutViewModel.shippingMethodList.observe(this, Observer { shippingMethods ->
            shippingMethodAdapter.shippingMethodList = shippingMethods?.toMutableList()
            shippingMethodAdapter.notifyDataSetChanged()
        })

        //observe payment methods
        checkoutViewModel.paymentMethodList.observe(this, Observer { paymentMethodList ->
            paymentMethodAdapter.paymentMethodList = paymentMethodList
            paymentMethodAdapter.notifyDataSetChanged()
        })
    }
}
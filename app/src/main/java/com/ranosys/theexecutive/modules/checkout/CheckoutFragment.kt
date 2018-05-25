package com.ranosys.theexecutive.modules.checkout

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.ranosys.theexecutive.BuildConfig
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentCheckoutBinding
import com.ranosys.theexecutive.modules.addressBook.AddressBookFragment
import com.ranosys.theexecutive.utils.*
import kotlinx.android.synthetic.main.fragment_checkout.*
import kotlinx.android.synthetic.main.pay_amount_detail_bottom_sheet.*
import kotlinx.android.synthetic.main.total_segment_item.view.*


class CheckoutFragment : BaseFragment() {

    private lateinit var checkoutViewModel: CheckoutViewModel
    private lateinit var checkoutBinding: FragmentCheckoutBinding
    private lateinit var shoppingItemAdapter: ShoppingItemAdapter
    private lateinit var shippingMethodAdapter: ShippingtMethodAdapter
    private lateinit var paymentMethodAdapter: PaymentMethodAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        checkoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout, container, false)
        checkoutViewModel = ViewModelProviders.of(this).get(CheckoutViewModel::class.java)
        shoppingItemAdapter = ShoppingItemAdapter(mutableListOf())

        shippingMethodAdapter = ShippingtMethodAdapter(mutableListOf(), { isChecked: Boolean, shippingMethod: CheckoutDataClass.GetShippingMethodsResponse ->
            handleShippingMethodSelection(isChecked, shippingMethod)
        })

        paymentMethodAdapter = PaymentMethodAdapter(mutableListOf(), { isChecked: Boolean, paymentMethod: CheckoutDataClass.PaymentMethod ->
            handlePaymentMethodSelection(isChecked, paymentMethod)
        })

        observeApiResponse()
        observeCommanError()

        initiateCheckoutProcess()

        return checkoutBinding.root
    }


    private fun handleError(error: String?) {

        Utils.showDialog(activity as Context, error, (activity as Context).getString(android.R.string.ok), "", object: DialogOkCallback{
            override fun setDone(done: Boolean) {
                activity?.onBackPressed()
            }
        })
    }

    private fun handleShippingMethodSelection(checked: Boolean, shippingMethod: CheckoutDataClass.GetShippingMethodsResponse) {

        if(checked){
            checkoutViewModel.selectedShippingMethod = shippingMethod
            checkoutViewModel.getPaymentMethods(shippingMethod)
        }else{
            checkoutViewModel.selectedShippingMethod = shippingMethod
            paymentMethodAdapter.paymentMethodList = emptyList()
        }
        shippingMethodAdapter.notifyDataSetChanged()

    }

    private fun handlePaymentMethodSelection(checked: Boolean, paymentMethod: CheckoutDataClass.PaymentMethod) {
        paymentMethodAdapter.notifyDataSetChanged()
        checkoutViewModel.selectedPaymentMethod = paymentMethod
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


        tv_shipping_method.setOnClickListener {
            if(cv_shipping_method.visibility == View.GONE && shippingMethodAdapter.itemCount > 0){
                cv_shipping_method.visibility = View.VISIBLE
                cv_payment_method.visibility = View.GONE
            }else if(cv_shipping_method.visibility == View.VISIBLE){
                cv_shipping_method.visibility = View.GONE
            }
        }

        tv_payment_method.setOnClickListener {
            if(cv_payment_method.visibility == View.GONE && checkoutViewModel.selectedShippingMethod != null && paymentMethodAdapter.itemCount > 0){
                cv_payment_method.visibility = View.VISIBLE
                cv_shipping_method.visibility = View.GONE
            }else if(cv_payment_method.visibility == View.VISIBLE || checkoutViewModel.selectedShippingMethod == null){
                cv_payment_method.visibility = View.GONE
            }
        }


        checkoutBinding.addressExpandView.setOnClickListener {
            FragmentUtils.addFragment(context, AddressBookFragment.getInstance(true, checkoutViewModel.selectedAddress), null, AddressBookFragment::class.java.name, true)
        }

        btn_pay.setOnClickListener {
            checkoutViewModel.placeOrderApi(checkoutViewModel.selectedPaymentMethod)
        }
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.checkout), 0, "", R.drawable.back, true, 0, true)
    }

    private fun initiateCheckoutProcess() {
        if (Utils.isConnectionAvailable(activity as Context)) {
            showLoading()
            checkoutViewModel.getCartItemsApi()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }


    private fun observeCommanError() {
        checkoutViewModel.commanError.observe(this, Observer { error ->
            handleError(error)
        })
    }

    private fun observeApiResponse() {
        checkoutViewModel.selectedAddress.observe(this, Observer { address ->
            hideLoading()
            checkoutBinding.address = address

            //call shipping method api according to updated address
            checkoutViewModel.getShippingMethodsApi(address?.id!!)

        })

        checkoutViewModel.country.observe(this, Observer { country ->
            checkoutBinding.country = country
        })

        //observe shopping bag items
        checkoutViewModel.shoppingBagItems.observe(this, Observer { items ->
            hideLoading()
            if(items != null && items.isNotEmpty()){
                shoppingItemAdapter.itemList = items.toMutableList()
                shoppingItemAdapter.notifyDataSetChanged()

                //call user info api and total api
                callAddressApi()
                checkoutViewModel.getTotalAmountsApi()
            }else{
                handleError("No bag items: " + (activity as Context).getString(R.string.something_went_wrong_error))
            }

        })

        //observe shipping method list
        checkoutViewModel.shippingMethodList.observe(this, Observer { shippingMethods ->

            if (shippingMethods?.size ?: 0 <= 0) {
                Utils.showErrorDialog(activity as Context, (activity as Context).getString(R.string.empty_shipping_method_error))
            } else {
                shippingMethodAdapter.shippingMethodList = shippingMethods?.toMutableList()
                shippingMethodAdapter.notifyDataSetChanged()
            }

        })

        //observe payment methods
        checkoutViewModel.paymentMethodList.observe(this, Observer { paymentMethodList ->
            paymentMethodAdapter.paymentMethodList = paymentMethodList
            paymentMethodAdapter.notifyDataSetChanged()
        })

        //observe total segment
        checkoutViewModel.totalAmounts.observe(this, Observer { totalAmts ->
            updateTotalSegment(totalAmts)
        })

        //observe order id
        checkoutViewModel.orderId.observe(this, Observer { orderId ->
            val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
            val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)
                    ?: Constants.DEFAULT_STORE_CODE
            createOrderUrl(orderId, storeCode, userToken)
        })
    }

    private fun callAddressApi() {
        if (Utils.isConnectionAvailable(activity as Context)) {
            showLoading()
            checkoutViewModel.getAddressApi()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    private fun updateTotalSegment(totalAmts: List<CheckoutDataClass.TotalSegment>?) {
        ll_total_segment.removeAllViews()
        totalAmts?.run {
            for (totalSegment in totalAmts) {
                if(totalSegment == totalAmts.last()){
                    addChildViewsToFooter(ll_total_segment, totalSegment, true)
                }else{
                    addChildViewsToFooter(ll_total_segment, totalSegment)
                }


            }
        }
    }


    private fun createOrderUrl(orderId: String?, storeCode: String, userToken: String?) {
        val url = "${BuildConfig.API_URL}apppayment/?___store=$storeCode&orderid=$orderId&token=$userToken"
        prepareWebPageDialog(activity as Context, url, "ORDER")
    }

    private fun addChildViewsToFooter(footerContainer: LinearLayout, totalSegment: CheckoutDataClass.TotalSegment, isLastItem: Boolean = false) {

        val convertView = LayoutInflater.from(activity).inflate(R.layout.total_segment_item, null)
        convertView.item_name.text = totalSegment.title
        convertView.item_value.text = "IDR ${totalSegment.value}"

        if(isLastItem){
            convertView.item_name.setTypeface(null, Typeface.BOLD)
            convertView.item_value.setTypeface(null, Typeface.BOLD)
        }

        footerContainer.addView(convertView)
    }

}
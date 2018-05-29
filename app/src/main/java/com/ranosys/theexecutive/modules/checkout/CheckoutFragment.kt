package com.ranosys.theexecutive.modules.checkout

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import android.widget.Toast
import com.ranosys.theexecutive.BuildConfig
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentCheckoutBinding
import com.ranosys.theexecutive.databinding.PaymentMethodItemBinding
import com.ranosys.theexecutive.databinding.ShippingMethodItemBinding
import com.ranosys.theexecutive.modules.addressBook.AddressBookFragment
import com.ranosys.theexecutive.utils.*
import kotlinx.android.synthetic.main.fragment_checkout.*
import kotlinx.android.synthetic.main.pay_amount_detail_bottom_sheet.*
import kotlinx.android.synthetic.main.total_segment_item.view.*


class CheckoutFragment : BaseFragment() {

    private lateinit var checkoutViewModel: CheckoutViewModel
    private lateinit var checkoutBinding: FragmentCheckoutBinding
    private lateinit var shoppingItemAdapter: ShoppingItemAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        checkoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout, container, false)
        checkoutViewModel = ViewModelProviders.of(this).get(CheckoutViewModel::class.java)
        shoppingItemAdapter = ShoppingItemAdapter(mutableListOf())


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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManagerHorizontal = LinearLayoutManager(activity as Context, LinearLayoutManager.HORIZONTAL, false)
        cart_item_list.layoutManager = linearLayoutManagerHorizontal
        cart_item_list.adapter = shoppingItemAdapter


        tv_shipping_method.setOnClickListener {
            checkoutViewModel.shippingMethodList.value?.run {
                if(cv_shipping_method.visibility == View.GONE && checkoutViewModel.shippingMethodList.value!!.isNotEmpty()){
                    cv_shipping_method.visibility = View.VISIBLE
                    divider_shipping_method_below.visibility = View.GONE
                    cv_payment_method.visibility = View.GONE
                    shipping_method_expand_img.setImageResource(R.drawable.dropdown)

                }else if(cv_shipping_method.visibility == View.VISIBLE){
                    cv_shipping_method.visibility = View.GONE
                    divider_shipping_method_below.visibility = View.VISIBLE
                    shipping_method_expand_img.setImageResource(R.drawable.forward)
                }
            }

        }

        tv_payment_method.setOnClickListener {

            checkoutViewModel.paymentMethodList.value?.run {
                if(cv_payment_method.visibility == View.GONE && checkoutViewModel.selectedShippingMethod != null && checkoutViewModel.paymentMethodList.value!!.isNotEmpty()){
                    cv_payment_method.visibility = View.VISIBLE
                    cv_shipping_method.visibility = View.GONE
                    divider_payment_method_below.visibility = View.GONE
                    payment_method_expand_img.setImageResource(R.drawable.dropdown)
                }else if(cv_payment_method.visibility == View.VISIBLE || checkoutViewModel.selectedShippingMethod == null){
                    cv_payment_method.visibility = View.GONE
                    divider_payment_method_below.visibility = View.VISIBLE
                    payment_method_expand_img.setImageResource(R.drawable.forward)
                }
            }

        }


        checkoutBinding.addressExpandView.setOnClickListener {
            FragmentUtils.addFragment(context, AddressBookFragment.getInstance(true, checkoutViewModel.selectedAddress), null, AddressBookFragment::class.java.name, true)
        }

        btn_pay.setOnClickListener {
            Toast.makeText(activity, "place order", Toast.LENGTH_SHORT).show()
            //checkoutViewModel.placeOrderApi(checkoutViewModel.selectedPaymentMethod)
        }

        checkoutBinding.shippingMethodRg.setOnCheckedChangeListener { buttonView, _ ->
            val position = buttonView.checkedRadioButtonId
            val shippingMethod = checkoutViewModel.shippingMethodList.value?.get(position)
            checkoutViewModel.selectedShippingMethod = shippingMethod
            checkoutViewModel.getPaymentMethods(shippingMethod!!)
        }

        checkoutBinding.paymentMethodRg.setOnCheckedChangeListener { buttonView, _ ->
            val position = buttonView.checkedRadioButtonId
            if (position != -1) {
                val paymentMethod = checkoutViewModel.paymentMethodList.value?.get(position)
                checkoutViewModel.selectedPaymentMethod = paymentMethod
            }

            if(checkoutViewModel.selectedPaymentMethod != null){
                btn_pay.background = (activity as Context).getDrawable(R.drawable.green_button_bg)
            }else{
                btn_pay.background = (activity as Context).getDrawable(R.drawable.black_button_bg)
            }

        }


        img_total_segment.setOnClickListener {
            checkoutViewModel.totalSegmentVisible = checkoutViewModel.totalSegmentVisible.not()

            if(checkoutViewModel.totalSegmentVisible){
                ll_total_segment.visibility = View.VISIBLE
                tv_total_amount.visibility = View.GONE
                img_total_segment.setImageResource(R.drawable.dropdown)
                BottomSheetBehavior.from(total_segment_bottom_sheet).state = BottomSheetBehavior.STATE_EXPANDED

            }else{
                ll_total_segment.visibility = View.GONE
                tv_total_amount.visibility = View.VISIBLE
                img_total_segment.setImageResource(R.drawable.forward)
            }

            getViewHeight(total_segment_bottom_sheet)

        }
    }

    private fun getViewHeight(view : View): Int {
        var height = 0
        view.getViewTreeObserver().addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            @SuppressLint("NewApi")
            override fun onGlobalLayout() {
                //now we can retrieve the width and height
                //val width = view.getWidth()
                height = view.getHeight()

                //your tasks
                updateOffsetViewHeight(height)

                //this is an important step not to keep receiving callbacks:
                //we should remove this listener
                //I use the function to remove it based on the api level!

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                else
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this)
            }
        })

        return height
    }

    private fun updateOffsetViewHeight(height: Int) {
        val params = checkoutBinding.offsetView.getLayoutParams()
        params.height = height + 30
        checkoutBinding.offsetView.layoutParams = params

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
                checkoutBinding.cartItemSize = items.size

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
                populateShippingMethods(shippingMethods)
                checkoutBinding.shippingMethodCount = shippingMethods?.size
            }

        })

        //observe payment methods
        checkoutViewModel.paymentMethodList.observe(this, Observer { paymentMethodList ->
            if (paymentMethodList?.size ?: 0 <= 0) {
                Utils.showErrorDialog(activity as Context, (activity as Context).getString(R.string.empty_payment_method_error))
            } else {
                checkoutBinding.paymentMethodRg.clearCheck()
                populatePaymentMethods(paymentMethodList)
                checkoutBinding.paymentMethodCount = paymentMethodList?.size
            }
        })

        //observe total segment
        checkoutViewModel.totalAmounts.observe(this, Observer { totalAmts ->
            tv_total_amount.text = "IDR ${totalAmts?.single { it.code == "grand_total" }?.value}"
            updateTotalSegment(totalAmts)
        })

        //observe order id
        checkoutViewModel.orderId.observe(this, Observer { orderId ->
            val userToken: String? = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
            val storeCode: String = SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)?: Constants.DEFAULT_STORE_CODE

            when(checkoutViewModel.selectedPaymentMethod?.code){
                "banktransfer" -> {
                    Toast.makeText(activity, "Bank transfer", Toast.LENGTH_SHORT).show()
                }

                "cashondelivery" -> {
                    Toast.makeText(activity, "Bank transfer", Toast.LENGTH_SHORT).show()
                }

                else ->{
                    Toast.makeText(activity, "Bank transfer", Toast.LENGTH_SHORT).show()
                    //createOrderUrl(orderId, storeCode, userToken)
                }
            }

        })
    }

    private fun populatePaymentMethods(paymentMethodList: List<CheckoutDataClass.PaymentMethod>?) {

        var position = 0

        checkoutBinding.paymentMethodRg.removeAllViews()
        for(method in paymentMethodList?.toMutableList()!!) {
            val rbBinding: PaymentMethodItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.payment_method_item, checkoutBinding.paymentMethodRg, false)
            rbBinding.paymentMethod = method
            rbBinding.rbPaymentMethod.id = position
            rbBinding.rbPaymentMethod.isChecked = false
            checkoutBinding.paymentMethodRg.addView(rbBinding.root)
            position++
        }

    }

    private fun populateShippingMethods(shippingMethods: List<CheckoutDataClass.GetShippingMethodsResponse>?) {
        var position = 0
        for(method in shippingMethods?.toMutableList()!!) {
            val rbBinding: ShippingMethodItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.shipping_method_item, checkoutBinding.shippingMethodRg, false)
            rbBinding.shippingMethod = method
            rbBinding.rbShippingMethod.id = position
            checkoutBinding.shippingMethodRg.addView(rbBinding.root)
            position++
        }
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
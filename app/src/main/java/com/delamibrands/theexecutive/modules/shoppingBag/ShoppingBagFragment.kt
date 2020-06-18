package com.delamibrands.theexecutive.modules.shoppingBag

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.api.ApiResponse
import com.delamibrands.theexecutive.base.BaseFragment
import com.delamibrands.theexecutive.databinding.FragmentShoppingBagBinding
import com.delamibrands.theexecutive.modules.checkout.CheckoutFragment
import com.delamibrands.theexecutive.modules.login.LoginFragment
import com.delamibrands.theexecutive.modules.myAccount.DividerDecoration
import com.delamibrands.theexecutive.modules.productDetail.ProductDetailFragment
import com.delamibrands.theexecutive.utils.*
import kotlinx.android.synthetic.main.fragment_shopping_bag.*
import kotlinx.android.synthetic.main.shopping_bag_footer.*


/**
 * @Details fragment shows Shopping bag
 * @Author Ranosys Technologies
 * @Date 15, May,2018
 */
class ShoppingBagFragment : BaseFragment() {

    private lateinit var shoppingBagViewModel: ShoppingBagViewModel
    private var userToken = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
    private var itemPosition: Int = 0
    private var cartQty = 0
    private var updateQty = 0
    private var promoCode: String = ""
    private var totalResponse : TotalResponse ?= null
    private lateinit var viewBinder: FragmentShoppingBagBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_shopping_bag, container, false)
        shoppingBagViewModel = ViewModelProviders.of(this).get(ShoppingBagViewModel::class.java)
        viewBinder?.shoppingBagViewModel = shoppingBagViewModel
        observeEvents()
        return viewBinder?.root
    }

    private fun observeEvents() {
        shoppingBagViewModel.mutualShoppingBagListResponse.observe(this, Observer<ApiResponse<List<ShoppingBagResponse>>> { apiResponse ->
            hideLoading()
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is List<ShoppingBagResponse>) {
                    if(response.isNotEmpty()){
                        getTotalForUser()
                    }else{
                        c2_main_layout.visibility = View.GONE
                        tv_no_items.visibility = View.VISIBLE
                        cartQty = 0
                        setCartTitle()
                    }
                    shoppingBagViewModel.shoppingBagListResponse?.set(response as MutableList<ShoppingBagResponse>?)
                }
            } else {
                hideLoading()
                if(apiResponse?.error.equals(Constants.CART_DE_ACTIVE)){
                    shoppingBagViewModel.getCartIdForUser()
                }else{
                    Utils.showDialog(activity, apiResponse?.error, getString(R.string.ok), "", null)
                }
            }
        })


        // for delete item
        shoppingBagViewModel.mutualDeleteItemResponse.observe(this, Observer<ApiResponse<String>> { apiResponse ->
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is String) {
                    getShoppingBag()
                }
            } else {
                hideLoading()
                Utils.showDialog(activity, apiResponse?.error, getString(R.string.ok), "", null)
            }
        })

        //for item move to wishlist
        shoppingBagViewModel.mutualMoveToWishlistResponse.observe(this, Observer<ApiResponse<String>> { apiResponse ->
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is String) {
                    Toast.makeText(activity as Context, response, Toast.LENGTH_SHORT).show()
                    hideLoading()
                    //getShoppingBag()
                }
            } else {
                hideLoading()
                Utils.showDialog(activity, apiResponse?.error, getString(R.string.ok), "", null)
            }
        })


        // getPromo code apply
        shoppingBagViewModel.mutualPromoCodeResponse.observe(this, Observer<ApiResponse<String>> { apiResponse ->
           hideLoading()
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is String) {
                    if (!TextUtils.isEmpty(response)) {
                        promoCode = response
                    }
                    setShoppingBagAdapter()
                }
            } else {
                hideLoading()
                Utils.showDialog(activity, apiResponse?.error, getString(R.string.ok), "", null)
            }
        })


        // Apply Promo code apply
        shoppingBagViewModel.mutualApplyPromoCodeResponse.observe(this, Observer<ApiResponse<String>> { apiResponse ->
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is String) {
                    if (!TextUtils.isEmpty(response)) {
                        getShoppingBag()
                    }
                }
                tv_invalid.visibility = View.GONE
            } else {
                hideLoading()
                et_promo_code.setText("")
                tv_invalid.visibility = View.VISIBLE
            }
        })


        //for get delete promo code
        shoppingBagViewModel.mutualPromoCodeDeleteResponse.observe(this, Observer<ApiResponse<String>> { apiResponse ->
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is String) {
                    promoCode = ""
                    getShoppingBag()
                }
            } else {
                Utils.showDialog(activity, apiResponse?.error, getString(R.string.ok), "", null)
                hideLoading()
            }
        })

        // for update Qty
        shoppingBagViewModel.mutualShoppingBagItemResponse.observe(this, Observer<ApiResponse<ShoppingBagQtyUpdateRequest>> { apiResponse ->
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is ShoppingBagQtyUpdateRequest) {
                    rv_shopping_bag_list.adapter.notifyDataSetChanged()
                }
                getShoppingBag()
                setCartTitle()
            } else {
                hideLoading()
                Utils.showDialog(activity, apiResponse?.error, getString(R.string.ok), "", null)
            }
        })

        // For get Total prices
        shoppingBagViewModel.mutualTotalResponse.observe(this, Observer<ApiResponse<TotalResponse>> { apiResponse ->
           hideLoading()
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is TotalResponse) {
                    totalResponse = response
                    getPromoCode()
                }
            } else {
                hideLoading()
                Utils.showDialog(activity, apiResponse?.error, getString(R.string.ok), "", null)
            }
        })


        shoppingBagViewModel.guestCartIdResponse?.observe(this, Observer {
            response ->
            if(response?.error.isNullOrEmpty()) {
                val guestCartId = response?.apiResponse
                if (guestCartId is String) {
                    getShoppingBag()
                }
            }
            else {
                hideLoading()
                Toast.makeText(activity,response?.error, Toast.LENGTH_LONG).show()
            }

        })

        shoppingBagViewModel.userCartIdResponse?.observe(this, Observer {
            response ->
            val userCartId = response?.apiResponse ?: response?.error
            if(userCartId is String){
                getShoppingBag()
            }
            else {
                Toast.makeText(activity, getString(R.string.common_error), Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun setShoppingBagAdapter() {
        val linearLayoutManager = object : LinearLayoutManager(activity as Context, LinearLayoutManager.VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return true
            }
        }
        cartQty = 0
        val itemDecor = DividerDecoration(resources.getDrawable(R.drawable.horizontal_divider, null),1)
        rv_shopping_bag_list.addItemDecoration(itemDecor)
        rv_shopping_bag_list.layoutManager = linearLayoutManager

        if (shoppingBagViewModel.shoppingBagListResponse?.get()?.size!! > 0) {

            var size = shoppingBagViewModel.shoppingBagListResponse?.get()?.size

            tv_no_items.visibility = View.GONE
            while (size!! > 0) {
                val qty = shoppingBagViewModel.shoppingBagListResponse?.get()!![size - 1].qty
                cartQty = cartQty.plus(qty)
                size--
            }
            setCartTitle()

            val shoppingBagAdapter = ShoppingBagAdapter(activity as Context, shoppingBagViewModel.shoppingBagListResponse!!.get(), promoCode, totalResponse, { id: Int, pos: Int, item: ShoppingBagResponse?, qty: Int?, promoCode: String? ->

                when (id) {
                    0 -> {
                        val fragment = ProductDetailFragment.getInstance(null, item?.extension_attributes?.configurable_sku, item?.name, 0)
                        FragmentUtils.addFragment(context!!, fragment, null, ProductDetailFragment::class.java.name, true)
                    }

                    R.id.img_wishlist -> {
                        updateQty = qty!!
                        callAddToWishListFromBag(item?.item_id)

                        val parameters = Bundle()
                        parameters.putString(Constants.FB_EVENT_PRODUCT_NAME, item?.name)
                        parameters.putString(Constants.FB_EVENT_PRODUCT_SKU, item?.sku)
                        getLogger()!!.logEvent(Constants.FB_EVENT_NAME_ADDED_TO_WISHLIST, parameters)

                        itemPosition = pos
                    }
                    R.id.img_delete -> {
                        updateQty = qty!!
                        Utils.showDialog(context, getString(R.string.remove_item_text_bag),
                                getString(R.string.yes), getString(R.string.no), object : DialogOkCallback {
                            override fun setDone(done: Boolean) {
                                callDeleteItemFromBag(item?.item_id)
                                itemPosition = pos
                            }
                        })
                    }

                    R.id.img_increment -> {
                        updateQty = qty!!
                        cartQty += 1
                        updateCartItem(ShoppingBagQtyUpdateRequest(CartItem(item_id = item?.item_id.toString(), qty = qty.toString(), quote_id = item?.quote_id)))
                    }

                    R.id.img_decrement -> {
                        updateQty = qty!!
                        cartQty -= 1
                        updateCartItem(ShoppingBagQtyUpdateRequest(CartItem(item_id = item?.item_id.toString(), qty = qty.toString(), quote_id = item?.quote_id)))
                    }


                    R.id.btn_apply -> {
                        applyCouponCode(promoCode)
                    }

                    R.id.imv_delete_promo_code -> {
                        deleteCouponCode()
                    }

                    R.id.btn_checkout -> {
                        val userToken = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
                        if (userToken.isNullOrBlank().not()) {
                            FragmentUtils.addFragment(context, CheckoutFragment(),null, CheckoutFragment::class.java.name, true )
                        } else {
                            redirectToLogin()
                        }
                    }

                }
            })
            rv_shopping_bag_list.adapter = shoppingBagAdapter
        } else {
            c2_main_layout.visibility = View.GONE
            tv_no_items.visibility = View.VISIBLE
            cartQty = 0
            setCartTitle()
        }
    }

    private fun redirectToLogin() {
        Utils.showDialog(activity, getString(R.string.login_required_for_checkout), getString(R.string.ok), getString(R.string.cancel), object : DialogOkCallback {
            override fun setDone(done: Boolean) {
                setToolBarParams(getString(R.string.login), 0, "", R.drawable.cancel, true, 0, false, true)
                val bundle = Bundle()
                bundle.putBoolean(Constants.LOGIN_REQUIRED_PROMPT, true)
                FragmentUtils.addFragment(activity as Context, LoginFragment(), bundle, LoginFragment::class.java.name, true)
            }
        })
    }

    private fun updateCartItem(shoppingBagQtyUpdateRequest: ShoppingBagQtyUpdateRequest) {

        if (userToken.isNullOrBlank().not()) {
            showLoading()
            shoppingBagViewModel.updateItemFromShoppingBagUser(shoppingBagQtyUpdateRequest)
        } else {
            val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY)
                    ?: ""
            if (guestCartId.isNotBlank()) {
                showLoading()
                shoppingBagViewModel.updateItemFromShoppingBagGuest(shoppingBagQtyUpdateRequest)
            }
        }
    }

    private fun callDeleteItemFromBag(item_id: Int?) {
        if (userToken.isNullOrBlank().not()) {
            showLoading()
            shoppingBagViewModel.deleteItemFromShoppingBagUser(item_id)
        } else {
            val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY)
                    ?: ""
            if (guestCartId.isNotBlank()) {
                showLoading()
                shoppingBagViewModel.deleteItemFromShoppingBagGuest(item_id, guestCartId)
            }
        }
    }

    private fun callAddToWishListFromBag(item_id: Int?) {
        if (userToken.isNullOrBlank().not()) {
            showLoading()
            shoppingBagViewModel.moveItemFromCart(item_id)



        } else {
            LoginFragment()
        }
    }


    private fun applyCouponCode(promoCode: String?) {
        if (userToken.isNullOrBlank().not()) {
            showLoading()
            shoppingBagViewModel.applyCouponCodeForUser(promoCode)
        } else {
            val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY)
                    ?: ""
            if (guestCartId.isNotBlank()) {
                showLoading()
                shoppingBagViewModel.applyCouponCodeForGuestUser(promoCode, guestCartId)
            }
        }
    }


    fun getShoppingBag() {
        showLoading()
        userToken =  SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        if (userToken.isNullOrBlank().not()) {
            shoppingBagViewModel.getShoppingBagForUser()
        } else {
            val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY)
                    ?: ""
            if (guestCartId.isNotBlank()) {
                shoppingBagViewModel.getShoppingBagForGuestUser(guestCartId)
            }else{
                hideLoading()
                viewBinder.c2MainLayout.visibility = View.GONE
                viewBinder.tvNoItems.visibility = View.VISIBLE
            }
        }
    }

    private fun getPromoCode() {
        if (userToken.isNullOrBlank().not()) {
            shoppingBagViewModel.getCouponCodeForUser()
        }else{
            val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY)
                    ?: ""
            if (guestCartId.isNotBlank()) {
                shoppingBagViewModel.getCouponCodeForGuestUser(guestCartId)
            }
        }
    }


    private fun deleteCouponCode() {
        showLoading()
        if (userToken.isNullOrBlank().not()) {
            shoppingBagViewModel.deleteCouponCodeForUser()

        } else {
            val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY)
                    ?: ""
            if (guestCartId.isNotBlank()) {
                shoppingBagViewModel.deleteCouponCodeForGuestUser(guestCartId)

            }
        }
    }


    private fun getTotalForUser() {
        showLoading()
        if (userToken.isNullOrBlank().not()) {
            shoppingBagViewModel.getTotalForUser()

        } else {
            val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY)
                    ?: ""
            if (guestCartId.isNotBlank()) {
                shoppingBagViewModel.getTotalForGuestUser(guestCartId)

            }
        }
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.shopping_bag), 0, "", R.drawable.cancel, true, 0, false)
    }

    private fun setCartTitle() {
        val qtyMsg = getString(R.string.items_in_your_cart, cartQty)
        tv_cart_quantity.text = qtyMsg
        Utils.updateCartCount(cartQty)
    }
}
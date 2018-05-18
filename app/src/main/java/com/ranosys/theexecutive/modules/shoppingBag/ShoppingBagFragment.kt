package com.ranosys.theexecutive.modules.shoppingBag

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
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentShoppingBagBinding
import com.ranosys.theexecutive.modules.login.LoginFragment
import com.ranosys.theexecutive.modules.myAccount.DividerDecoration
import com.ranosys.theexecutive.modules.productDetail.ProductDetailFragment
import com.ranosys.theexecutive.utils.*
import kotlinx.android.synthetic.main.fragment_shopping_bag.*
import kotlinx.android.synthetic.main.shopping_bag_footer.*


/**
 * @Details fragment shows Shopping bag
 * @Author Ranosys Technologies
 * @Date 15, May,2018
 */
class ShoppingBagFragment : BaseFragment() {

    private lateinit var shoppingBagViewModel: ShoppingBagViewModel
    private val userToken = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
    private var itemPosition: Int = 0
    private var cartQty = 0
    private var updateQty = 0
    private var promoCode: String = ""
    private var isFromPromoCode = false
    private var isFromFirstTime = false
    private var totalPrice: Int = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewBinder: FragmentShoppingBagBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_shopping_bag, container, false)
        shoppingBagViewModel = ViewModelProviders.of(this).get(ShoppingBagViewModel::class.java)
        viewBinder?.shoppingBagViewModel = shoppingBagViewModel
        isFromPromoCode = false
        isFromFirstTime = true
        getShoppingBag()
        observeEvents()
        return viewBinder?.root
    }

    private fun observeEvents() {
        shoppingBagViewModel.mutualShoppingBagListResponse.observe(this, Observer<ApiResponse<List<ShoppingBagResponse>>> { apiResponse ->
            hideLoading()
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is List<ShoppingBagResponse>) {
                    shoppingBagViewModel.shoppingBagListResponse?.set(response as MutableList<ShoppingBagResponse>?)
                    setShoppingBagAdapter()
                }
            } else {
                hideLoading()
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })


        // for delete item
        shoppingBagViewModel.mutualDeleteItemResponse.observe(this, Observer<ApiResponse<String>> { apiResponse ->
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is String) {
                    isFromPromoCode = false
                    getShoppingBag()
                    /*     shoppingBagViewModel.shoppingBagListResponse?.get()?.items?.removeAt(itemPosition)
                         rv_shopping_bag_list.adapter.notifyDataSetChanged()
                         cartQty -= updateQty
                         setCartTitle()
                         if (shoppingBagViewModel.shoppingBagListResponse?.get()?.items?.size!! == 0) {
                             c2_main_layout.visibility = View.GONE
                             tv_no_items.visibility = View.VISIBLE
                         }*/
                }
            } else {
                hideLoading()
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })


        // getPromo code apply
        shoppingBagViewModel.mutualPromoCodeResponse.observe(this, Observer<ApiResponse<String>> { apiResponse ->
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is String) {
                    if (!TextUtils.isEmpty(response)) {
                        promoCode = response
                        isFromPromoCode = true
                        if (!isFromFirstTime) {
                            getShoppingBag()
                        } else {
                            isFromFirstTime = false
                        }
                    }
                }
            } else {
                hideLoading()
                et_promo_code.setText("")
                et_promo_code.error = getString(R.string.promo_code_invalid)
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
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
            } else {
                hideLoading()
                et_promo_code.setText("")
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })


        //for get delete promo code
        shoppingBagViewModel.mutualPromoCodeDeleteResponse.observe(this, Observer<ApiResponse<String>> { apiResponse ->
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is String) {
                    promoCode = ""
                    isFromPromoCode = false
                    getShoppingBag()
                }
            } else {
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
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
                isFromPromoCode = false
                getShoppingBag()
                setCartTitle()
            } else {
                hideLoading()
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })

        // For get Total prices
        shoppingBagViewModel.mutualTotalResponse.observe(this, Observer<ApiResponse<TotalResponse>> { apiResponse ->
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is TotalResponse) {
                    totalPrice = response.subtotal
                }
            } else {
                hideLoading()
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
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

    }

    private fun setShoppingBagAdapter() {
        val linearLayoutManager = object : LinearLayoutManager(activity as Context, LinearLayoutManager.VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return true
            }
        }
        cartQty = 0
        val itemDecor = DividerDecoration(resources.getDrawable(R.drawable.horizontal_divider, null))
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

            val shoppingBagAdapter = ShoppingBagAdapter(activity as Context, shoppingBagViewModel.shoppingBagListResponse!!.get(), promoCode, totalPrice, { id: Int, pos: Int, item: ShoppingBagResponse?, qty: Int?, promoCode: String? ->

                when (id) {
                    0 -> {
                        val fragment = ProductDetailFragment.getInstance(null, item?.sku, item?.name, 0)
                        FragmentUtils.addFragment(context!!, fragment, null, ProductDetailFragment::class.java.name, true)
                    }

                    R.id.img_wishlist -> {
                        updateQty = qty!!
                        callAddToWishListFromBag(item?.item_id)
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

                }
            })
            rv_shopping_bag_list.adapter = shoppingBagAdapter
        } else {
            c2_main_layout.visibility = View.GONE
            tv_no_items.visibility = View.VISIBLE

        }
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
            FragmentUtils.addFragment(context, LoginFragment(), null, LoginFragment::class.java.name, true)
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


    private fun getShoppingBag() {
        showLoading()
        if (userToken.isNullOrBlank().not()) {
            if (!isFromPromoCode) {
                shoppingBagViewModel.getCouponCodeForUser()
            }
            getTotalForUser()
            shoppingBagViewModel.getShoppingBagForUser()
        } else {
            val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY)
                    ?: ""
            if (guestCartId.isNotBlank()) {
                if (!isFromPromoCode) {
                    shoppingBagViewModel.getCouponCodeForGuestUser(guestCartId)
                }
                getTotalForUser()
                shoppingBagViewModel.getShoppingBagForGuestUser(guestCartId)
            }else {
                shoppingBagViewModel.getCartIdForGuest()
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
        setToolBarParams(getString(R.string.shopping_bag), 0, "", R.drawable.back, true, 0, false)
    }

    private fun setCartTitle() {
        val qtyMsg = getString(R.string.items_in_your_cart, cartQty)
        tv_cart_quantity.text = qtyMsg
        Utils.updateCartCount(cartQty)
    }
}
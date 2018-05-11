package com.ranosys.theexecutive.modules.shoppingBag

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
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentShoppingBagBinding
import com.ranosys.theexecutive.modules.productDetail.ProductDetailFragment
import com.ranosys.theexecutive.utils.*
import kotlinx.android.synthetic.main.fragment_shopping_bag.*


class ShoppingBagFragment : BaseFragment() {

    private lateinit var shoppingBagViewModel: ShoppingBagViewModel
    val userToken = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
    private var itemPosition: Int = 0
    var cartQty = 0
    var updateQty = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewBinder: FragmentShoppingBagBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_shopping_bag, container, false)
        shoppingBagViewModel = ViewModelProviders.of(this).get(ShoppingBagViewModel::class.java)
        viewBinder?.shoppingBagViewModel = shoppingBagViewModel

        getShoppingBag()
        observeEvents()
        return viewBinder?.root
    }

    private fun observeEvents() {
        shoppingBagViewModel.mutualShoppingBaglistResponse.observe(this, Observer<ApiResponse<List<ShoppingBagResponse>>> { apiResponse ->
            hideLoading()

            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is List<ShoppingBagResponse>) {
                    shoppingBagViewModel.shoppingBagListResponse?.set(response as MutableList<ShoppingBagResponse>?)
                    setShoppingBagAdapter()
                }
            } else {
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })

        shoppingBagViewModel.mutualDeleteItemResponse.observe(this, Observer<ApiResponse<String>> { apiResponse ->
            hideLoading()
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is String) {
                    shoppingBagViewModel.shoppingBagListResponse?.get()?.removeAt(itemPosition)
                    rv_shopping_bag_list.adapter.notifyDataSetChanged()
                    cartQty -= updateQty
                    setCartTitle()
                }
            } else {
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })


        shoppingBagViewModel.mutualShoppingBagItemResponse.observe(this, Observer<ApiResponse<ShoppingBagResponse>> { apiResponse ->
            hideLoading()
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is ShoppingBagResponse) {
                    rv_shopping_bag_list.adapter.notifyDataSetChanged()
                }
                setCartTitle()
            } else {
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })
    }

    private fun setShoppingBagAdapter() {
        val linearLayoutManager = object : LinearLayoutManager(activity as Context, LinearLayoutManager.VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return true
            }
        }

        rv_shopping_bag_list.layoutManager = linearLayoutManager



        if (shoppingBagViewModel.shoppingBagListResponse?.get()?.size!! > 0) {


            var size = shoppingBagViewModel.shoppingBagListResponse?.get()?.size!!

            while (size > 0) {
                val qty = shoppingBagViewModel.shoppingBagListResponse?.get()!![size - 1].qty
                cartQty = cartQty.plus(qty)
                size--
            }

            setCartTitle()

            val shoppingBagAdapter = ShoppingBagAdapter(activity as Context, shoppingBagViewModel.shoppingBagListResponse!!.get(), { id: Int, pos: Int, item: ShoppingBagResponse?, qty: Int? ->

                updateQty = qty!!

                when (id) {
                    0 -> {
                        val fragment = ProductDetailFragment.getInstance(null, item?.sku, item?.name, 0)
                        FragmentUtils.addFragment(context!!, fragment, null, ProductDetailFragment::class.java.name, true)
                    }

                    R.id.img_wishlist -> {
                        callAddToWishListFromBag(item?.item_id)
                        itemPosition = pos
                    }
                    R.id.img_delete -> {
                        Utils.showDialog(context, getString(R.string.remove_item_text_bag),
                                getString(R.string.yes), getString(R.string.no), object : DialogOkCallback {
                            override fun setDone(done: Boolean) {
                                callDeleteItemFromBag(item?.item_id)
                                itemPosition = pos
                            }
                        })
                    }

                    R.id.img_increment -> {
                        cartQty += 1
                        updateCartItem(ShoppingBagQtyUpdateRequest(CartItem(item_id = item?.item_id.toString(), qty = qty.toString(), quote_id = item?.quote_id.toString())))
                    }

                    R.id.img_decrement -> {
                        cartQty -= 1
                        updateCartItem(ShoppingBagQtyUpdateRequest(CartItem(item_id = item?.item_id.toString(), qty = qty.toString(), quote_id = item?.quote_id.toString())))
                    }
                }
            })

            rv_shopping_bag_list.adapter = shoppingBagAdapter
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
        showLoading()
        shoppingBagViewModel.moveItemFromCart(item_id)
    }

    private fun getShoppingBag() {
        showLoading()
        if (userToken.isNullOrBlank().not()) {
            shoppingBagViewModel.getShoppingBagForUser()
        } else {
            val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY)
                    ?: ""
            if (guestCartId.isNotBlank()) {
                shoppingBagViewModel.getShoppingBagForGuestUser(guestCartId)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.shopping_bag), 0, "", R.drawable.back, true, 0, false)
    }

    private fun setCartTitle() {
        tv_cart_quantity.setText("Total " + cartQty + " Items in your cart")
    }
}
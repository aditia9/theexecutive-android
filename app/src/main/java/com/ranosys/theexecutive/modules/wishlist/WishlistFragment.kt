package com.ranosys.theexecutive.modules.wishlist

import android.annotation.SuppressLint
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
import com.ranosys.theexecutive.databinding.FragmentWishlistBinding
import com.ranosys.theexecutive.modules.productDetail.ProductDetailFragment
import com.ranosys.theexecutive.utils.DialogOkCallback
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_wishlist.*

/**
 * @Details Fragment for wishlist screen
 * @Author Ranosys Technologies
 * @Date 13,Mar,2018
 */
class WishlistFragment : BaseFragment() {

    private var wishlistModelView: WishlistViewModel? = null
    private var itemPosition : Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentWishlistBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_wishlist, container, false)
        wishlistModelView = ViewModelProviders.of(this).get(WishlistViewModel::class.java)
        mViewDataBinding?.executePendingBindings()
        observeEvents()
        callWishlistApi()
        return mViewDataBinding?.root
    }

    @SuppressLint("StringFormatInvalid")
    private fun observeEvents() {
        wishlistModelView?.mutualWishlistResponse?.observe(this, Observer<ApiResponse<WishlistResponse>> { apiResponse ->
            hideLoading()
            if(apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is WishlistResponse) {
                    wishlistModelView?.wishlistResponse?.set(response)
                    tv_wishlist_count.text = getString(R.string.wishlist_items_count, response.items_count)
                    setWishlistAdapter()
                }
            }else {
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })

        wishlistModelView?.mutualDeleteItemResponse?.observe(this, Observer<ApiResponse<String>> { apiResponse ->
            if(apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is String) {
                    wishlistModelView?.wishlistResponse?.get()?.items?.removeAt(itemPosition)
                    rv_wishlist.adapter.notifyDataSetChanged()
                    callWishlistApi()
                }
            }else {
                hideLoading()
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })
    }

    private fun setWishlistAdapter(){
        val linearLayoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.VERTICAL, false)
        rv_wishlist.layoutManager = linearLayoutManager
        if (wishlistModelView?.wishlistResponse?.get()?.items?.size!! > 0) {
            tv_no_items.visibility = View.GONE
            tv_wishlist_count.visibility = View.VISIBLE
            val colorViewAdapter = WishlistAdapter(activity as Context, wishlistModelView?.wishlistResponse?.get()?.items, {
                id:Int, pos: Int, item: Item? ->
                when(id){
                    0 -> {
                        val fragment = ProductDetailFragment.getInstance(null, item?.sku, item?.name, 0)
                        FragmentUtils.addFragment(context!!, fragment, null, ProductDetailFragment::class.java.name, true)
                    }
                    R.id.img_bag -> {
                        //Toast.makeText(activity, item?.sku, Toast.LENGTH_SHORT).show()
                    }
                    R.id.img_delete -> {
                        Utils.showDialog(context, getString(R.string.remove_item_text),
                                getString(R.string.yes), getString(R.string.no), object : DialogOkCallback {
                            override fun setDone(done: Boolean) {
                                itemPosition = pos
                                callDeleteItemFromWishlist(item?.id)
                            }
                        })
                    }
                }
            })
            rv_wishlist.adapter = colorViewAdapter
        }else{
            tv_no_items.visibility = View.VISIBLE
            tv_wishlist_count.visibility = View.GONE
        }
    }

    private fun callWishlistApi(){
        if (Utils.isConnectionAvailable(activity as Context)) {
            wishlistModelView?.getWishlist()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    private fun callDeleteItemFromWishlist(itemId : Int?){
        if (Utils.isConnectionAvailable(activity as Context)) {
            showLoading()
            wishlistModelView?.deleteItemFromWishlist(itemId)
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }
}
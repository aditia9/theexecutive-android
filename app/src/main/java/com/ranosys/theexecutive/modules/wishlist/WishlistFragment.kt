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
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentWishlistBinding
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_wishlist.*

/**
 * @Details Fragment for wishlist screen
 * @Author Ranosys Technologies
 * @Date 13,Mar,2018
 */
class WishlistFragment : BaseFragment() {

    var wishlistModelView: WishlistViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentWishlistBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_wishlist, container, false)
        wishlistModelView = ViewModelProviders.of(this).get(WishlistViewModel::class.java)
        mViewDataBinding?.executePendingBindings()
        observeEvents()
        callWishlistApi()
        return mViewDataBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("StringFormatInvalid")
    private fun observeEvents() {
        wishlistModelView?.mutualWishlistResponse?.observe(this, Observer<ApiResponse<WishlistResponse>> { apiResponse ->
            // hideLoading()
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
            //hideLoading()
            if(apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is String) {

                }
            }else {
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })
    }

    private fun setWishlistAdapter(){
        val linearLayoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.VERTICAL, false)
        rv_wishlist.layoutManager = linearLayoutManager
        if (wishlistModelView?.wishlistResponse?.get()?.items?.size!! > 0) {
            val colorViewAdapter = WishlistAdapter(activity as Context, wishlistModelView?.wishlistResponse?.get()?.items, {
                id:Int, pos: Int, item: Item? ->
                when(id){
                    0 -> {
                        Toast.makeText(activity, ""+id, Toast.LENGTH_SHORT).show()
                    }
                    R.id.img_bag -> {
                        Toast.makeText(activity, item?.sku, Toast.LENGTH_SHORT).show()
                    }
                    R.id.img_delete -> {
                        Toast.makeText(activity, item?.regular_price.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            })
            rv_wishlist.adapter = colorViewAdapter
        }else{

        }
    }

    private fun callWishlistApi(){
        if (Utils.isConnectionAvailable(activity as Context)) {
            //showLoading()
            wishlistModelView?.getWishlist()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    private fun callDeleteItemFromWishlist(itemId : Int){
        if (Utils.isConnectionAvailable(activity as Context)) {
            showLoading()
            wishlistModelView?.deleteItemFromWishlist(itemId)
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }
}
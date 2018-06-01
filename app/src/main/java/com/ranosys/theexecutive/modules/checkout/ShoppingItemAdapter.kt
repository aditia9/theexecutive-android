package com.ranosys.theexecutive.modules.checkout

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.CheckoutBagItemBinding
import com.ranosys.theexecutive.modules.shoppingBag.ShoppingBagResponse

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 21-May-2018
 */
class ShoppingItemAdapter(var itemList: MutableList<ShoppingBagResponse>?): RecyclerView.Adapter<ShoppingItemAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
        val binding: CheckoutBagItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.checkout_bag_item, parent,false)
        return Holder(binding)
    }

    override fun getItemCount() =  itemList?.size ?: 0

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        holder?.bind(itemList?.get(position), position)
    }


    class Holder(val itemBinding: CheckoutBagItemBinding): RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(shoppingBagResponse: ShoppingBagResponse?, position: Int) {
            itemBinding.item = shoppingBagResponse
        }

    }
}
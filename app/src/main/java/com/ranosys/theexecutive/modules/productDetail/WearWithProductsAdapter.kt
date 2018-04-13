package com.ranosys.theexecutive.modules.productDetail

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.WearWithLayoutBinding
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 13,Apr,2018
 */
class WearWithProductsAdapter(var context : Context, var list : List<ProductListingDataClass.ProductLinks>?) : RecyclerView.Adapter<WearWithProductsAdapter.Holder>() {

    var mContext : Context? = null
    var wearWithList : List<ProductListingDataClass.ProductLinks>? = null
    var clickListener: WearWithProductsAdapter.OnItemClickListener? = null

    init {
        mContext = context
        wearWithList = list
    }

    interface OnItemClickListener {
        fun onItemClick(item : ProductListingDataClass.ProductLinks?)
    }

    fun setItemClickListener(listener: OnItemClickListener){
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
        val binding: WearWithLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.wear_with_layout, parent,false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        wearWithList?.run {
            return size
        }
        return 0
    }

    override fun onBindViewHolder(holder: Holder?, position: Int) {
    }

    class Holder(itemBinding: WearWithLayoutBinding): RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(productLinks : ProductListingDataClass.ProductLinks?, listener: OnItemClickListener){

            itemView.setOnClickListener {
                listener.onItemClick(productLinks)
            }
        }
    }
}
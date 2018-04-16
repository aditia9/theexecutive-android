package com.ranosys.theexecutive.modules.productDetail

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.WearWithLayoutBinding
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass
import com.ranosys.theexecutive.utils.Utils

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 13,Apr,2018
 */
class WearWithProductsAdapter(var context : Context, var list : List<ProductListingDataClass.ProductLinks?>?) : RecyclerView.Adapter<WearWithProductsAdapter.Holder>() {

    var mContext : Context? = null
    var wearWithList : List<ProductListingDataClass.ProductLinks?>? = null
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
        val binding: WearWithLayoutBinding? = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.wear_with_layout, parent,false)
        binding?.productImage?.layoutParams?.width = Utils.getDeviceWidth(context)/2 - Utils.convertDpIntoPx(context, 25f)
        binding?.productImage?.requestLayout()
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        wearWithList?.run {
            return size
        }
        return 0
    }


    fun getItem(position: Int) : ProductListingDataClass.ProductLinks?{
        return wearWithList?.get(position)
    }

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        holder?.bind(getItem(position), clickListener)
    }

    class Holder(var itemBinding: WearWithLayoutBinding?): RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(productLinks : ProductListingDataClass.ProductLinks?, listener: OnItemClickListener?){

            val normalPrice = "IDR\u00A0" + productLinks?.extension_attributes?.linked_product_regularprice
            val spPrice = " IDR\u00A0" + productLinks?.extension_attributes?.linked_product_finalprice
            val price = "$normalPrice $spPrice"

            val ss = SpannableStringBuilder(price)
            ss.setSpan(StrikethroughSpan(), 0, normalPrice.length, 0)
            ss.setSpan(ForegroundColorSpan(Color.RED), normalPrice.length, price.length, 0)
            ss.setSpan(RelativeSizeSpan(1.1f), normalPrice.length, price.length, 0)

            itemBinding?.productLinks = productLinks
            itemBinding?.tvNormalPrice?.text = ss

            itemView.setOnClickListener {
                listener?.onItemClick(productLinks)
            }
        }
    }
}
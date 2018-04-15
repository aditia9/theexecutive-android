package com.ranosys.theexecutive.modules.productListing

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
import com.ranosys.theexecutive.databinding.ProductListItemBinding
import com.ranosys.theexecutive.utils.Utils

/**
 * @Class An adapter class for all products listing
 * @author Ranosys Technologies
 * @Date 20-Mar-2018
 */

class ProductListAdapter(var productList: ArrayList<ProductListingDataClass.ProductMaskedResponse>, var clickListener: OnItemClickListener): RecyclerView.Adapter<ProductListAdapter.Holder>() {

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        holder?.bind(productList[position], position,  listener = clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding: ProductListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.product_list_item, parent,false)
        Utils.setImageViewHeightWrtDeviceWidth(parent.context, binding.image, .65)
        return ProductListAdapter.Holder(binding)
    }

    override fun getItemCount() = productList.size


    fun addProducts(products: ArrayList<ProductListingDataClass.ProductMaskedResponse>){
        val lastPos = productList.size
        productList.addAll(products)
        notifyItemRangeInserted(lastPos, products.size)
    }

    class Holder(val itemBinding: ProductListItemBinding): RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(product: ProductListingDataClass.ProductMaskedResponse, position : Int, listener: ProductListAdapter.OnItemClickListener){


            val normalPrice = "IDR\u00A0" + product.normalPrice
            val spPrice = " IDR\u00A0" + product.specialPrice
            val price = "$normalPrice $spPrice"

            val ss = SpannableStringBuilder(price)
            ss.setSpan(StrikethroughSpan(), 0, normalPrice.length, 0)
            ss.setSpan(ForegroundColorSpan(Color.RED), normalPrice.length, price.length, 0)
            ss.setSpan(RelativeSizeSpan(1.1f), normalPrice.length, price.length, 0)

            itemBinding.productItem = product
            itemBinding.tvNormalPrice.text = ss

            itemView.setOnClickListener {
                listener.onItemClick(product, position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(selectedProduct: ProductListingDataClass.ProductMaskedResponse, position: Int)
    }
}
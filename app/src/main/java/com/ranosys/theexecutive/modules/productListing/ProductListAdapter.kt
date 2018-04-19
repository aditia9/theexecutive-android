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
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Class An adapter class for all products listing
 * @author Ranosys Technologies
 * @Date 20-Mar-2018
 */

class ProductListAdapter(var productList: MutableList<ProductListingDataClass.Item>, var clickListener: OnItemClickListener): RecyclerView.Adapter<ProductListAdapter.Holder>() {

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        holder?.bind(productList[position], position,  listener = clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding: ProductListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.product_list_item, parent,false)
        Utils.setImageViewHeightWrtDeviceWidth(parent.context, binding.image, .6)
        return ProductListAdapter.Holder(binding)
    }

    override fun getItemCount() = productList.size


    fun addProducts(products: ArrayList<ProductListingDataClass.Item>){
        val lastPos = productList.size
        productList.addAll(products)
        notifyItemRangeInserted(lastPos, products.size)
    }

    class Holder(val itemBinding: ProductListItemBinding): RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(productItem: ProductListingDataClass.Item, position : Int, listener: ProductListAdapter.OnItemClickListener){

            val product = prepareMaskedResponse(productItem)

            itemBinding.productItem = product
            val normalPrice = "IDR\u00A0" + product.normalPrice
            val spPrice = " IDR\u00A0" + product.specialPrice
            var price = ""

            if(product.normalPrice == product.specialPrice){
                price = "$normalPrice"
                itemBinding.tvNormalPrice.text = price
            }else{
                price = "$normalPrice $spPrice"
                val ss = SpannableStringBuilder(price)
                ss.setSpan(StrikethroughSpan(), 0, normalPrice.length, 0)
                ss.setSpan(ForegroundColorSpan(Color.RED), normalPrice.length, price.length, 0)
                ss.setSpan(RelativeSizeSpan(1.1f), normalPrice.length, price.length, 0)
                itemBinding.tvNormalPrice.text = ss
            }

            itemView.setOnClickListener {
                listener.onItemClick(product, position)
            }
        }

        private fun prepareMaskedResponse(product: ProductListingDataClass.Item): ProductListingDataClass.ProductMaskedResponse {
            val sku = product.sku
            val name = product.name
            val productType = product.type_id
            var price: Double
            var specialPrice = 0.0
            if(productType == Constants.FILTER_CONFIGURABLE_LABEL){
                price = product.extension_attributes.regular_price
                specialPrice = product.extension_attributes.final_price
            }else{
                price = product.price
                val attributes = product.custom_attributes.filter { it.attribute_code == Constants.FILTER_SPECIAL_PRICE_LABEL }.toList()
                if(attributes.isNotEmpty()) {
                    specialPrice = attributes[0].value.toString().toDouble()
                }
            }


            var toDate = ""
            var fromDate = ""
            var attributes = product.custom_attributes.filter { it.attribute_code == Constants.NEW_FROM_DATE_LABEL }.toList()
            if(attributes.isNotEmpty()){
                fromDate = attributes.single().value.toString()
            }

            attributes = product.custom_attributes.filter { it.attribute_code == Constants.NEW_TO_DATE_LABEL }.toList()
            if(attributes.isNotEmpty()){
                toDate = attributes.single().value.toString()
            }
            val type = if(toDate.isNotBlank() && fromDate.isNotBlank()) isNewProduct(fromDate, toDate) else ""

            val discount = (((price - specialPrice).div(price)).times(100)).toInt()
            var imgUrl = ""
            if(product.media_gallery_entries?.isNotEmpty()!!)   imgUrl = product.media_gallery_entries[0]?.file.toString()

            val product = ProductListingDataClass.ProductMaskedResponse(
                    sku = sku,
                    name = name,
                    normalPrice = price.toString(),
                    specialPrice = specialPrice.toString(),
                    type = type,
                    discountPer = discount,
                    imageUrl = imgUrl)

            return product
        }

        private fun isNewProduct(fromDate: String, toDate: String): String {

            val sdf = SimpleDateFormat(Constants.YY_MM__DD_DATE_FORMAT)
            val d = Date()
            val currentDate= sdf.format(d)
            val cDate=sdf.parse(currentDate)
            val sDtate=sdf.parse(fromDate)
            val eDate=sdf.parse(toDate)

            if(!(cDate.compareTo(sDtate) < 0 || cDate.compareTo(eDate) > 0)) {
                return Constants.NEW_TAG
            } else  return ""
        }
    }

    interface OnItemClickListener {
        fun onItemClick(selectedProduct: ProductListingDataClass.ProductMaskedResponse, position: Int)
    }
}
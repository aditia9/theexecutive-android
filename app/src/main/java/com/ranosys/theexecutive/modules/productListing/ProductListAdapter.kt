package com.ranosys.theexecutive.modules.productListing

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

        return ProductListAdapter.Holder(binding, parent.context)
    }

    override fun getItemCount() = productList.size

    class Holder(val itemBinding: ProductListItemBinding, val ctx: Context): RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(productItem: ProductListingDataClass.Item, position : Int, listener: ProductListAdapter.OnItemClickListener){

            if((position + 1) % ProductListingFragment.COLUMN_CHANGE_FACTOR == 0){
                Utils.setImageViewHeightWrtDeviceWidth(ctx, itemBinding.image, 1.4)
            }else{
                Utils.setImageViewHeightWrtDeviceWidth(ctx, itemBinding.image, .6)
            }


            val product = prepareMaskedResponse(productItem)
            itemBinding.productItem = product
            val normalPrice = "IDR\u00A0" + product.normalPrice
            val spPrice = " IDR\u00A0" + product.specialPrice

            if(product.normalPrice == product.specialPrice || product.specialPrice.isBlank()){
                product.displayPrice = normalPrice
                itemBinding.tvDisplayPrice?.text = normalPrice
            }else{
                val price = "$normalPrice $spPrice"
                val ss = SpannableStringBuilder(price)
                ss.setSpan(StrikethroughSpan(), 0, normalPrice.length, 0)
                ss.setSpan(ForegroundColorSpan(Color.RED), normalPrice.length, price.length, 0)
                ss.setSpan(RelativeSizeSpan(1.3f), normalPrice.length, price.length, 0)
                product.displayPrice = ss.toString()
                itemBinding.tvDisplayPrice?.text = ss
            }


            itemView.setOnClickListener {
                listener.onItemClick(product, position)
            }
        }

        private fun prepareMaskedResponse(product: ProductListingDataClass.Item): ProductListingDataClass.ProductMaskedResponse {
            val sku = product.sku
            val name = product.name
            val productType = product.type_id
            val tag: String? = product.extension_attributes.tag_text
            val price: String
            var specialPrice = ""
            if(productType == Constants.CONFIGURABLE){
                price = Utils.getFromattedPrice(product.extension_attributes.regular_price)
                specialPrice = Utils.getFromattedPrice(product.extension_attributes.final_price)
            }else{
                price = Utils.getFromattedPrice(product.price)
                val attributes = product.custom_attributes.filter { it.attribute_code == Constants.FILTER_SPECIAL_PRICE_LABEL }.toList()
                if(attributes.isNotEmpty()) {
                    specialPrice = Utils.getFromattedPrice(attributes[0].value.toString())
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

            var discount = 0
            if(specialPrice.isNotBlank()){
                discount = Math.round((((Utils.getDoubleFromFormattedPrice(price) - Utils.getDoubleFromFormattedPrice(specialPrice)).div(Utils.getDoubleFromFormattedPrice(price))).times(100))).toInt()
            }
            var imgUrl = ""
            if(product.media_gallery_entries?.isNotEmpty()!!)   imgUrl = product.media_gallery_entries[0].file

            val newNP = price.replace(",",".")
            val newSP = specialPrice.replace(",",".")

            return ProductListingDataClass.ProductMaskedResponse(
                    sku = sku,
                    name = name,
                    normalPrice = newNP,
                    specialPrice = newSP,
                    type = type,
                    collectionTag = tag ?:  "",
                    discountPer = discount,
                    imageUrl = imgUrl)
        }

        private fun isNewProduct(fromDate: String, toDate: String): String {

            val sdf = SimpleDateFormat(Constants.YY_MM__DD_DATE_FORMAT)
            val d = Date()
            val currentDate= sdf.format(d)
            val cDate=sdf.parse(currentDate)
            val sDtate=sdf.parse(fromDate)
            val eDate=sdf.parse(toDate)

            return if(!(cDate.compareTo(sDtate) < 0 || cDate.compareTo(eDate) > 0)) {
                Constants.NEW_TAG
            } else ""
        }
    }

    interface OnItemClickListener {
        fun onItemClick(selectedProduct: ProductListingDataClass.ProductMaskedResponse, position: Int)
    }
}
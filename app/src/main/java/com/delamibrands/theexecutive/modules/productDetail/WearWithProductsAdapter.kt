package com.delamibrands.theexecutive.modules.productDetail

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.databinding.WearWithLayoutBinding
import com.delamibrands.theexecutive.modules.productListing.ProductListingDataClass
import com.delamibrands.theexecutive.utils.Constants
import com.delamibrands.theexecutive.utils.Utils

/**
 * @Details Adapter to show wear with items
 * @Author Ranosys Technologies
 * @Date 13,Apr,2018
 */
class WearWithProductsAdapter(var context : Context, var list : List<ProductListingDataClass.ProductLinks?>?) : RecyclerView.Adapter<WearWithProductsAdapter.Holder>() {

    private var mContext : Context? = null
    private var wearWithList : List<ProductListingDataClass.ProductLinks?>? = null
    private var clickListener: WearWithProductsAdapter.OnItemClickListener? = null

    init {
        mContext = context
        wearWithList = list
    }

    interface OnItemClickListener {
        fun onItemClick(item : ProductListingDataClass.ProductLinks?, position : Int)
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


    private fun getItem(position: Int) : ProductListingDataClass.ProductLinks?{
        return wearWithList?.get(position)
    }

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        holder?.bind(getItem(position), clickListener, position, context)
    }

    class Holder(private var itemBinding: WearWithLayoutBinding?): RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(productLinks: ProductListingDataClass.ProductLinks?, listener: OnItemClickListener?, position: Int, context: Context){

            val normalPrice = " ${context.getString(R.string.currency)}\u00A0" + productLinks?.extension_attributes?.linked_product_regularprice
            val spPrice = " ${context.getString(R.string.currency)}\u00A0" + productLinks?.extension_attributes?.linked_product_finalprice

            itemBinding?.tvNormalPrice?.text = Utils.getDisplayPrice(productLinks?.extension_attributes?.linked_product_regularprice.toString(), productLinks?.extension_attributes?.linked_product_finalprice.toString(), context?.getString(R.string.currency) ?: Constants.IDR)
//            if(productLinks?.extension_attributes?.linked_product_regularprice == productLinks?.extension_attributes?.linked_product_finalprice || productLinks?.extension_attributes?.linked_product_finalprice.toString().isBlank()){
//                itemBinding?.tvNormalPrice?.text = normalPrice
//            }else{
//                val price = "$normalPrice $spPrice"
//                val ss = SpannableStringBuilder(price)
//                ss.setSpan(StrikethroughSpan(), 0, normalPrice.length, 0)
//                ss.setSpan(ForegroundColorSpan(Color.RED), normalPrice.length, price.length, 0)
//                ss.setSpan(RelativeSizeSpan(1.1f), normalPrice.length, price.length, 0)
//                itemBinding?.tvNormalPrice?.text = ss
//            }


            itemBinding?.productLinks = productLinks


            itemView.setOnClickListener {
                listener?.onItemClick(productLinks,position)
            }
        }
    }
}
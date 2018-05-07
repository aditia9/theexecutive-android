package com.ranosys.theexecutive.modules.productDetail

import AppLog
import android.annotation.SuppressLint
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.SizeViewLayoutBinding

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 16,Apr,2018
 */
class SizeRecyclerAdapter (var context: Context, var list: List<ProductViewFragment.SizeView>?, colorValue: String?, maxQuantityList: MutableList<ProductViewFragment.MaxQuantity>?) : RecyclerView.Adapter<SizeRecyclerAdapter.Holder>() {

    var mContext : Context? = null
    var sizeViewList : List<ProductViewFragment.SizeView>? = null
    var clickListener: SizeRecyclerAdapter.OnItemClickListener? = null
    var colorValue: String? = ""
    var isInStockList: MutableList<ProductViewFragment.MaxQuantity>? = null

    init {
        mContext = context
        sizeViewList = list
        this.colorValue = colorValue
        isInStockList = maxQuantityList
    }

    interface OnItemClickListener {
        fun onItemClick(item: ProductViewFragment.SizeView?, position: Int, priceList : List<ProductViewFragment.MaxQuantity>?, inStock : Boolean?)
    }

    fun setItemClickListener(listener: OnItemClickListener){
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder{
        val binding: SizeViewLayoutBinding? = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.size_view_layout, parent,false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        sizeViewList?.run {
            return size
        }
        return 0
    }

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        holder?.bind(mContext, getItem(position), clickListener, position, colorValue, isInStockList)
    }

    fun getItem(position: Int) : ProductViewFragment.SizeView?{
        return sizeViewList?.get(position)
    }

    class Holder(var itemBinding: SizeViewLayoutBinding?): RecyclerView.ViewHolder(itemBinding?.root) {

        @SuppressLint("ResourceAsColor")
        fun bind(context: Context?, sizeView: ProductViewFragment.SizeView?, listener: OnItemClickListener?, position: Int, colorValue: String?, inStockList: MutableList<ProductViewFragment.MaxQuantity>?){
            var priceList : List<ProductViewFragment.MaxQuantity>? = listOf()
            var inStock : Boolean? = true
            itemBinding?.sizeView = sizeView
            if(sizeView?.isSelected!!){
                itemBinding?.tvSize?.background = context?.resources?.getDrawable(R.drawable.size_border)
                itemBinding?.tvSize?.setTypeface(Typeface.DEFAULT_BOLD)
            }
            else{
                itemBinding?.tvSize?.background = context?.resources?.getDrawable(R.color.white)
                itemBinding?.tvSize?.setTypeface(Typeface.DEFAULT)
            }
            try {
                inStock = inStockList?.filter {
                    it.colorValue == colorValue && it.sizeValue == sizeView.value
                }?.single()?.isInStock
                priceList = inStockList?.filter {
                    it.colorValue == colorValue
                }?.toList()
                if(inStock!!){
                    itemBinding?.tvOutOfStock?.visibility = View.GONE
                }else{
                    itemBinding?.tvOutOfStock?.visibility = View.VISIBLE
                    itemBinding?.tvSize?.isEnabled = false
                    itemBinding?.tvSize?.isClickable = false
                    itemBinding?.tvSize?.setTextColor(R.color.divider_color)
                    itemBinding?.tvSize?.setTypeface(Typeface.DEFAULT)

                }
            }catch (e : Exception){
                AppLog.printStackTrace(e)
            }
            itemView.setOnClickListener {
                listener?.onItemClick(sizeView, position, priceList, inStock)
            }
        }
    }


}
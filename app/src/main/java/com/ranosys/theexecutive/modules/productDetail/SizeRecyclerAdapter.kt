package com.ranosys.theexecutive.modules.productDetail

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.SizeViewLayoutBinding

/**
 * @Details Adapter to show size options of product
 * @Author Ranosys Technologies
 * @Date 16,Apr,2018
 */
class SizeRecyclerAdapter (var context : Context, var list : List<ProductViewFragment.SizeView>?) : RecyclerView.Adapter<SizeRecyclerAdapter.Holder>() {

    private var sizeViewList : List<ProductViewFragment.SizeView>? = null
    private var clickListener: SizeRecyclerAdapter.OnItemClickListener? = null

    init {
        sizeViewList = list
    }

    interface OnItemClickListener {
        fun onItemClick(item : ProductViewFragment.SizeView?, position: Int)
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
        holder?.bind(context, getItem(position), clickListener, position)
    }

    private fun getItem(position: Int) : ProductViewFragment.SizeView?{
        return sizeViewList?.get(position)
    }

    class Holder(private var itemBinding: SizeViewLayoutBinding?): RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(context : Context?, colorView : ProductViewFragment.SizeView?, listener: OnItemClickListener?, position: Int){
            itemBinding?.sizeView = colorView
            if(colorView?.isSelected!!){
                itemBinding?.tvSize?.background = ContextCompat.getDrawable(context!!, R.drawable.size_border)
                itemBinding?.tvSize?.typeface = Typeface.DEFAULT_BOLD
            }
            else{
                itemBinding?.tvSize?.background = ContextCompat.getDrawable(context!!, R.color.white)
                itemBinding?.tvSize?.typeface = Typeface.DEFAULT
            }
            itemView.setOnClickListener {
                listener?.onItemClick(colorView, position)
            }
        }
    }


}
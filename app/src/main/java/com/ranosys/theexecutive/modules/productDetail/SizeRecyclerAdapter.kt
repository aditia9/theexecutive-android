package com.ranosys.theexecutive.modules.productDetail

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.SizeViewLayoutBinding

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 16,Apr,2018
 */
class SizeRecyclerAdapter (var context : Context, var list : List<ProductViewFragment.SizeView>?) : RecyclerView.Adapter<SizeRecyclerAdapter.Holder>() {

    var mContext : Context? = null
    var sizeViewList : List<ProductViewFragment.SizeView>? = null
    var clickListener: SizeRecyclerAdapter.OnItemClickListener? = null

    init {
        mContext = context
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
        holder?.bind(mContext, getItem(position), clickListener, position)
    }

    fun getItem(position: Int) : ProductViewFragment.SizeView?{
        return sizeViewList?.get(position)
    }

    class Holder(var itemBinding: SizeViewLayoutBinding?): RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(context : Context?, colorView : ProductViewFragment.SizeView?, listener: OnItemClickListener?, position: Int){
            itemBinding?.sizeView = colorView
            if(colorView?.isSelected!!){
                itemBinding?.tvSize?.background = context?.resources?.getDrawable(R.drawable.size_border)
                itemBinding?.tvSize?.setTypeface(Typeface.DEFAULT_BOLD)
            }
            else{
                itemBinding?.tvSize?.background = context?.resources?.getDrawable(R.color.white)
                itemBinding?.tvSize?.setTypeface(Typeface.DEFAULT)
            }
            itemView.setOnClickListener {
                listener?.onItemClick(colorView, position)
            }
        }
    }


}
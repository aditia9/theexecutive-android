package com.ranosys.theexecutive.modules.productDetail

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.ColorViewLayoutBinding

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 16,Apr,2018
 */
class ColorRecyclerAdapter(var context : Context, var list : List<ProductViewFragment.ColorsView>?) : RecyclerView.Adapter<ColorRecyclerAdapter.Holder>() {

    var mContext : Context? = null
    var colorViewList : List<ProductViewFragment.ColorsView>? = null
    var clickListener: ColorRecyclerAdapter.OnItemClickListener? = null

    init {
        mContext = context
        colorViewList = list
    }

    interface OnItemClickListener {
        fun onItemClick(item : ProductViewFragment.ColorsView?)
    }

    fun setItemClickListener(listener: OnItemClickListener){
        clickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder{
        val binding: ColorViewLayoutBinding? = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.color_view_layout, parent,false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        colorViewList?.run {
            return size
        }
        return 0
    }

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        holder?.bind(getItem(position), clickListener)
    }

    fun getItem(position: Int) : ProductViewFragment.ColorsView?{
        return colorViewList?.get(position)
    }

    class Holder(var itemBinding: ColorViewLayoutBinding?): RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(colorView : ProductViewFragment.ColorsView?, listener: OnItemClickListener?){
            itemBinding?.colorView = colorView
            itemView.setOnClickListener {
                listener?.onItemClick(colorView)
            }
        }
    }
}
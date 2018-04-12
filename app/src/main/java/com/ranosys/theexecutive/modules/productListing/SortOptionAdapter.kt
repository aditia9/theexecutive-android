package com.ranosys.theexecutive.modules.productListing

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.SortOptionItemBinding

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 11-Apr-2018
 */
class SortOptionAdapter(val mViewModel: ProductListingViewModel, var sortOptions: MutableList<ProductListingDataClass.SortOptionResponse>?) : RecyclerView.Adapter<SortOptionAdapter.ViewHolder>() {

    var clickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(item : ProductListingDataClass.SortOptionResponse)
    }

    fun setItemClickListener(listener: SortOptionAdapter.OnItemClickListener){
        clickListener = listener
    }

    override fun getItemCount() = sortOptions?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sortOptions!!.get(position), mViewModel, clickListener!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:SortOptionItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.sort_option_item, parent,false)
        return ViewHolder(binding)
    }


    class ViewHolder(val itemBinding: SortOptionItemBinding):RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(option: ProductListingDataClass.SortOptionResponse, mViewModel: ProductListingViewModel, listener: SortOptionAdapter.OnItemClickListener) {
            itemBinding.option = option

            if(mViewModel.selectedSortOption.attribute_name == option.attribute_name)
                itemBinding.isSelectedIv.visibility = View.VISIBLE
            else
                itemBinding.isSelectedIv.visibility = View.GONE

            itemView.setOnClickListener {
                listener.onItemClick(option)
            }
        }


    }
}
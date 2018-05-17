package com.ranosys.theexecutive.modules.changeLanguage

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.StoreListItemBinding
import com.ranosys.theexecutive.modules.splash.StoreResponse

/**
 * Created by nikhil on 16/3/18.
 */
class StoreListAdapter(var storeListData: List<StoreResponse>?, var selectedStoreCode: String): RecyclerView.Adapter<StoreListAdapter.Holder>() {

    var clickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(item : StoreResponse)
    }

    fun setItemClickListener(listener: OnItemClickListener){
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding: StoreListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.store_list_item, parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(storeListData!!.get(position), selectedStoreCode = selectedStoreCode, listener = clickListener!!)
    }

    override fun getItemCount(): Int = storeListData?.size?:0


    class Holder(val itemBinding: StoreListItemBinding): RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(store: StoreResponse?, selectedStoreCode: String, listener: OnItemClickListener){
            itemBinding.store = store
            if(store?.code == selectedStoreCode)
                itemBinding.isSelectedIv.visibility = View.VISIBLE
            else
                itemBinding.isSelectedIv.visibility = View.GONE


            itemView.setOnClickListener {
                listener.onItemClick(store!!) }
        }
    }
}
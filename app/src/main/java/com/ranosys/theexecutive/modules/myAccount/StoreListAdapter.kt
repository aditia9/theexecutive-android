package com.ranosys.theexecutive.modules.myAccount

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.StoreListItemBinding
import com.ranosys.theexecutive.modules.splash.StoreResponse

/**
 * Created by nikhil on 16/3/18.
 */
class StoreListAdapter(var storeListData: List<StoreResponse>?): RecyclerView.Adapter<StoreListAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var binding: StoreListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.store_list_item, parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(storeListData!!.get(position))
    }

    override fun getItemCount(): Int = storeListData?.size?:0


    class Holder(val itemBinding: StoreListItemBinding): RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(store: StoreResponse?){
            itemBinding.store = store
        }
    }
}
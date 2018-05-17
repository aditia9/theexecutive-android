package com.ranosys.theexecutive.modules.order

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.MyOrdersItemBinding

class OrderListAdapter(var context: Context, var orderList: List<OrderListResponse>?, val action: (Int, Int, OrderListResponse?) -> Unit) : RecyclerView.Adapter<OrderListAdapter.Holder>() {

    var mContext: Context? = null
    var clickListener: OrderListAdapter.OnItemClickListener? = null

    init {
        mContext = context
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): OrderListAdapter.Holder {
        val binding: MyOrdersItemBinding? = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.my_orders_item, parent, false)
        return OrderListAdapter.Holder(binding)
    }

    override fun getItemCount(): Int {
        orderList?.run {
            return size
        }
        return 0
    }

    override fun onBindViewHolder(holder: OrderListAdapter.Holder?, position: Int) {
        holder?.bind(mContext, getItem(position), position, action, clickListener)
    }

    fun getItem(position: Int): OrderListResponse? {
        return orderList?.get(position)
    }


    class Holder(var itemBinding: MyOrdersItemBinding?) : RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(context: Context?, item: OrderListResponse?, position: Int, action: (Int, Int, OrderListResponse?) -> Unit, listener: OrderListAdapter.OnItemClickListener?) {
            itemBinding?.item = item


        }
    }


}
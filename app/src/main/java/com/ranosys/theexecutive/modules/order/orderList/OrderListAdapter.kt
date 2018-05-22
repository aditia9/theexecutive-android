package com.ranosys.theexecutive.modules.order.orderList

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.MyOrdersItemBinding

class OrderListAdapter(var context: Context, var orderList: List<OrderListResponse>?, private val action: (Int, String, OrderListResponse?) -> Unit) : RecyclerView.Adapter<OrderListAdapter.Holder>() {

    var mContext: Context? = null
    var clickListener: OnItemClickListener? = null

    init {
        mContext = context
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
        val binding: MyOrdersItemBinding? = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.my_orders_item, parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        orderList?.run {
            return size
        }
        return 0
    }

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        holder?.bind(mContext, getItem(position), position, action, clickListener)
    }

    fun getItem(position: Int): OrderListResponse? {
        return orderList?.get(position)
    }


    class Holder(var itemBinding: MyOrdersItemBinding?) : RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(context: Context?, item: OrderListResponse?, position: Int, action: (Int, String, OrderListResponse?) -> Unit, listener: OnItemClickListener?) {
            itemBinding?.item = item

            itemView.setOnClickListener {
                item?.id?.let { action(0, it, item) }
            }
        }
    }
}
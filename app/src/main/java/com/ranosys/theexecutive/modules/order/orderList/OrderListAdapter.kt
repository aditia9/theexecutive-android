package com.ranosys.theexecutive.modules.order.orderList

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.MyOrdersItemBinding
import com.ranosys.theexecutive.utils.Utils

/**
 * @Class An data class for Order List
 * @author Ranosys Technologies
 * @Date 15-May-2018
 */


class OrderListAdapter(var context: Context, private var orderList: List<OrderListResponse>?, private val action: (Int, String, OrderListResponse?) -> Unit) : RecyclerView.Adapter<OrderListAdapter.Holder>() {

    var clickListener: OnItemClickListener? = null


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
        holder?.bind(getItem(position), action)
    }

    fun getItem(position: Int): OrderListResponse? {
        return orderList?.get(position)
    }


    class Holder(var itemBinding: MyOrdersItemBinding?) : RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(item: OrderListResponse?, action: (Int, String, OrderListResponse?) -> Unit) {
            itemBinding?.item = item


            if(itemBinding?.item?.date != null){
                var date = itemBinding?.item?.date
                itemBinding?.tvDate?.text = Utils.getDateFormat(date!!)
            }

            itemView.setOnClickListener {
                item?.id?.let { action(0, it, item) }
            }
            if (null != item){
                itemBinding?.tvPrice?.text = Utils.getDisplayPrice(item.amount, item.amount).toString()
                if(item.is_refundable){
                    itemBinding?.btnReturn?.visibility = View.GONE
                }else{
                    itemBinding?.btnReturn?.visibility = View.VISIBLE
                }
            }


            itemBinding?.btnReturn?.setOnClickListener { view ->
                item?.id?.let { action(view.id, it, item) }
            }
        }
    }
}
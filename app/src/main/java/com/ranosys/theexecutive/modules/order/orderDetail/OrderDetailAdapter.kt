package com.ranosys.theexecutive.modules.order.orderDetail

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.OrderDetailAddressBinding
import com.ranosys.theexecutive.databinding.OrderDetailItemListBinding
import com.ranosys.theexecutive.databinding.OrderDetailPriceBinding


const val ORDER_PRICE = 2
const val ORDER_ITEM = 1
const val ORDER_ADDRESS = 0

class OrderDetailAdapter(var context: Context, private var OrderDetail: OrderDetailResponse?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mContext: Context? = null

    private var clickListener: OnItemClickListener? = null

    private var listSize : Int = 0

    init {
        mContext = context
        listSize = OrderDetail?.items?.size!!
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ORDER_ADDRESS
            listSize + 1 -> ORDER_PRICE
            else -> ORDER_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            ORDER_ADDRESS -> {
                val binding: OrderDetailAddressBinding? = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.order_detail_address, parent, false)
                OrderDetailAddressHolder(itemBinding = binding)
            }
            ORDER_PRICE -> {
                val binding: OrderDetailPriceBinding? = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.order_detail_price, parent, false)
                OrderDetailPriceHolder(itemBinding = binding)
            }
            else -> {
                val binding: OrderDetailItemListBinding? = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.order_detail_item_list, parent, false)
                OrderDetailItemHolder(itemBinding = binding)
            }
        }
    }

    override fun getItemCount(): Int {
        OrderDetail?.items?.run {
            return (size + 2)
        }
        return 0
    }


    private fun getItem(position: Int): Item? {
        return OrderDetail?.items?.get(position - 1)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is OrderDetailItemHolder -> holder.bind(mContext, getItem(position), position)
            is OrderDetailAddressHolder -> holder.bind(mContext, OrderDetail, position)
            is OrderDetailPriceHolder -> holder.bind(mContext, OrderDetail, position)
        }
    }

    class OrderDetailItemHolder(var itemBinding: OrderDetailItemListBinding?) : RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(context: Context?, item: Item?, position: Int) {
            itemBinding?.item = item

        }
    }

    class OrderDetailPriceHolder(var itemBinding: OrderDetailPriceBinding?) : RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(context: Context?, item: OrderDetailResponse?, position: Int) {
            itemBinding?.item = item

            itemBinding?.tvProductPrice?.text = item?.subtotal_incl_tax.toString()
            itemBinding?.tvShippingPrice?.text = item?.shipping_incl_tax.toString()
            itemBinding?.tvTotalPrice?.text = item?.grand_total.toString()
            itemBinding?.tvProductQty?.text = (item?.items?.size.toString() +" "+ context?.getString(R.string.products))
        }
    }

    class OrderDetailAddressHolder(var itemBinding: OrderDetailAddressBinding?) : RecyclerView.ViewHolder(itemBinding?.root) {

        @SuppressLint("SetTextI18n")
        fun bind(context: Context?, item: OrderDetailResponse?, position: Int) {
            itemBinding?.item = item

            itemBinding?.tvUserName?.text = item?.billing_address?.firstname + " "+ item?.billing_address?.lastname
            itemBinding?.tvUserEmailId?.text = item?.billing_address?.email
            itemBinding?.tvOrderDate?.text = item?.items!![0].created_at
           var size =  item.billing_address.street.size
           var street = ""

            if(size == 1){
                street = item.billing_address.street[0]
            }else if (size > 1){
                street = item.billing_address.street[0] + item.billing_address.street[1]
            }
            var address = street+ " " + item.billing_address.postcode +", " + item.billing_address.country_id
            itemBinding?.tvUserAddress?.text = address
        }
    }
}
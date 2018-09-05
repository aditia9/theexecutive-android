package com.ranosys.theexecutive.modules.order.orderDetail

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.OrderDetailAddressBinding
import com.ranosys.theexecutive.databinding.OrderDetailItemListBinding
import com.ranosys.theexecutive.databinding.OrderDetailPriceBinding
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.Utils

/**
 * @Class An data class for Order detail adapter
 * @author Ranosys Technologies
 * @Date 21-May-2018
 */

const val ORDER_PRICE = 2
const val ORDER_ITEM = 1
const val ORDER_ADDRESS = 0
private var totalProductQty = 0

class OrderDetailAdapter(var context: Context, private var OrderDetail: OrderDetailResponse?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var clickListener: OnItemClickListener? = null

    private var listSize: Int = 0

    init {
        listSize = OrderDetail?.items?.size!!
        totalProductQty = 0
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
            is OrderDetailItemHolder -> holder.bind(context, getItem(position), OrderDetail, position)
            is OrderDetailAddressHolder -> holder.bind(context, OrderDetail, position)
            is OrderDetailPriceHolder -> holder.bind(context, OrderDetail, position)
        }
    }

    class OrderDetailItemHolder(var itemBinding: OrderDetailItemListBinding?) : RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(context: Context?, orderItem: Item?, item: OrderDetailResponse?, position: Int) {
            itemBinding?.item = orderItem

            if(item?.items?.get(position -1)?.extension_attributes != null && item.items.get(position -1).extension_attributes?.options != null){
                item.items[position - 1].extension_attributes?.options?.forEach {
                    when (it.label) {
                        Constants.COLOR_ -> {
                            if(!TextUtils.isEmpty(it.label)){
                                itemBinding?.tvProductColor?.text = it.value
                            }else{
                                itemBinding?.tvProductColor?.visibility = View.GONE
                                itemBinding?.viewVertical?.visibility = View.GONE
                            }
                        }
                        Constants.SIZE_ -> {
                            if(!TextUtils.isEmpty(it.label)){
                                itemBinding?.tvProductSize?.text = it.value
                            }else{
                                itemBinding?.tvProductSize?.visibility = View.GONE
                            }

                        }
                    }
                }
                itemBinding?.tvProductQty?.text = item?.items?.get(position -1).qty_ordered.toString() +" "+itemBinding?.tvProductQty?.context?.getString(R.string.item)

                totalProductQty = totalProductQty + item?.items?.get(position - 1).qty_ordered
            }else{
                itemBinding?.layoutColorSize?.visibility = View.GONE
            }

            if(item?.items!![position - 1].original_price != 0){
                itemBinding?.tvRegularPrice?.text = Utils.getDisplayPrice(item.items[position - 1].price.toString(), item.items[position - 1].price.toString(), context?.getString(R.string.currency) ?: Constants.IDR)
            }
        }
    }

    class OrderDetailPriceHolder(var itemBinding: OrderDetailPriceBinding?) : RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(context: Context?, item: OrderDetailResponse?, position: Int) {
            itemBinding?.item = item

            itemBinding?.tvProductPrice?.text = Utils.getDisplayPrice(item?.subtotal_incl_tax.toString(), item?.subtotal_incl_tax.toString(), context?.getString(R.string.currency) ?: Constants.IDR)
            itemBinding?.tvShippingPrice?.text = Utils.getDisplayPrice(item?.shipping_incl_tax.toString(), item?.shipping_incl_tax.toString(), context?.getString(R.string.currency) ?: Constants.IDR)
            itemBinding?.tvTotalPrice?.text = Utils.getDisplayPrice(item?.grand_total.toString(), item?.grand_total.toString(), context?.getString(R.string.currency) ?: Constants.IDR)

            itemBinding?.tvProductQty?.text = (totalProductQty.toString() + " " + context?.getString(R.string.item))

            if(!TextUtils.isEmpty(item?.extension_attributes?.virtual_account_number)){
                itemBinding?.tvPaymentId?.visibility = View.VISIBLE
                itemBinding?.tvPayId?.visibility = View.VISIBLE
                itemBinding?.tvPaymentId?.text = item?.extension_attributes?.virtual_account_number
            }

            if(!TextUtils.isEmpty(item?.extension_attributes?.payment_method)){
                itemBinding?.tvPaymentMethod?.visibility = View.VISIBLE
                itemBinding?.tvPayMethod?.visibility = View.VISIBLE
                itemBinding?.tvPaymentMethod?.text = item?.extension_attributes?.payment_method
            }
        }
    }

    class OrderDetailAddressHolder(var itemBinding: OrderDetailAddressBinding?) : RecyclerView.ViewHolder(itemBinding?.root) {


        @SuppressLint("SetTextI18n")
        fun bind(context: Context?, item: OrderDetailResponse?, position: Int) {
            itemBinding?.item = item

            itemBinding?.tvUserName?.text = item?.billing_address?.firstname + " " + item?.billing_address?.lastname
            itemBinding?.tvUserEmailId?.text = item?.billing_address?.email

            if(item?.items!![0].created_at.isNotBlank()){
                itemBinding?.tvOrderDate?.text = Utils.getDateFormat(item.items[0].created_at)
            }


            val size = item.billing_address.street.size
            var street = ""

            if (size == 1) {
                street = item.billing_address.street[0]
            } else if (size > 1) {
                street = item.billing_address.street[0] + item.billing_address.street[1]
            }
            val address = street + ", " + item.billing_address.city +", "+item.billing_address.postcode + ", " + item.extension_attributes.formatted_shipping_address.extension_attributes.country_name
            itemBinding?.tvUserAddress?.text = address
        }
    }
}
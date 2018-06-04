package com.ranosys.theexecutive.modules.order.orderReturn

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.OrderReturnAddressBinding
import com.ranosys.theexecutive.databinding.ReturnItemBinding
import com.ranosys.theexecutive.modules.order.orderDetail.Item
import com.ranosys.theexecutive.modules.order.orderDetail.OrderDetailResponse
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.Utils


const val ORDER_ITEM = 0
const val ORDER_ADDRESS = 1

/**
 * @Class An data class for Order return adapter
 * @author Ranosys Technologies
 * @Date 24-May-2018
 */
class OrderReturnAdapter(var context: Context, private var OrderDetail: OrderDetailResponse?, private val action: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var clickListener: OnItemClickListener? = null

    private var listSize: Int = 0

    init {
        listSize = OrderDetail?.items?.size!!
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == OrderDetail?.items?.size) ORDER_ADDRESS else ORDER_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            ORDER_ADDRESS -> {
                val binding: OrderReturnAddressBinding? = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.order_return_address, parent, false)
                OrderDetailAddressHolder(itemBinding = binding)
            }
            else -> {
                val binding: ReturnItemBinding? = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.return_item, parent, false)
                OrderDetailItemHolder(itemBinding = binding)
            }
        }
    }

    override fun getItemCount(): Int {
        OrderDetail?.items?.run {
            return (size + 1)
        }
        return 0
    }


    private fun getItem(position: Int): Item? {
        return OrderDetail?.items?.get(position)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is OrderDetailItemHolder -> holder.bind(context, getItem(position), OrderDetail, position)
            is OrderDetailAddressHolder -> holder.bind(OrderDetail, action)
        }
    }

    class OrderDetailItemHolder(var itemBinding: ReturnItemBinding?) : RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(context: Context?, orderItem: Item?, item: OrderDetailResponse?, position: Int) {
            itemBinding?.item = orderItem
            orderItem?.request_reason = context?.resources?.getStringArray(R.array.reason_array)?.get(0).toString()
            orderItem?.request_qty = 1

            if (item?.items?.get(position)?.extension_attributes != null && item.items[position].extension_attributes?.options != null) {
                item.items[position].extension_attributes?.options?.forEach {
                    when (it.label) {
                        Constants.COLOR_ -> {
                            if (!TextUtils.isEmpty(it.label)) {
                                itemBinding?.tvProductColor?.text = it.value
                            } else {
                                itemBinding?.tvProductColor?.visibility = View.GONE
                                itemBinding?.viewVertical?.visibility = View.GONE
                            }
                        }
                        Constants.SIZE_ -> {
                            if (!TextUtils.isEmpty(it.label)) {
                                itemBinding?.tvProductSize?.text = it.value
                            } else {
                                itemBinding?.tvProductSize?.visibility = View.GONE
                            }

                        }
                    }
                }
            } else {
                itemBinding?.layoutColorSize?.visibility = View.GONE
            }

            itemBinding?.imgIncrement?.setOnClickListener {
                val qty = (itemBinding!!.txtQuantity.text.toString()).toInt()
                if (qty < item?.items!![position].qty_ordered) {
                    itemBinding!!.txtQuantity.text = (qty.plus(1)).toString()
                    orderItem?.request_qty = (qty.plus(1))
                }
            }


            if(item?.items?.get(position)?.original_price != 0){
                itemBinding?.tvPrice?.text = Utils.getDisplayPrice(item?.items?.get(position)?.original_price.toString(), item?.items?.get(position)?.original_price.toString())
            }

            itemBinding?.spReason?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    Log.d(context?.getString(R.string.app_name), "" + (view as AppCompatTextView).text)
                    orderItem?.request_reason = view.text.toString()
                }
            }


            itemBinding?.imgDecrement?.setOnClickListener {

                val qty = (itemBinding!!.txtQuantity.text.toString()).toInt()
                if (qty > 1) {
                    itemBinding!!.txtQuantity.text = (qty.minus(1)).toString()
                    orderItem?.request_qty = (qty.minus(1))
                }
            }

            itemBinding?.chkSelect?.setOnCheckedChangeListener { compoundButton, isReturn ->
                orderItem?.request_return = isReturn
            }
        }
    }

    class OrderDetailAddressHolder(var itemBinding: OrderReturnAddressBinding?) : RecyclerView.ViewHolder(itemBinding?.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: OrderDetailResponse?, action: (Int) -> Unit) {
            itemBinding?.item = item

            itemBinding?.tvUserName?.text = item?.billing_address?.firstname + " " + item?.billing_address?.lastname
            itemBinding?.tvUserEmailId?.text = item?.billing_address?.email
            val size = item?.billing_address?.street?.size
            var street = ""

            if (size != null) {
                if (size == 1) {
                    street = item.billing_address.street[0]
                } else if (size > 1) {
                    street = item.billing_address.street[0] + ", "+ item.billing_address.street[1]
                }
                val address = street + ", " + item.billing_address.city + ", " + item.billing_address.postcode + ", " + item.billing_address.country_id
                itemBinding?.tvUserAddress?.text = address


                if(!TextUtils.isEmpty(item.extension_attributes.returnto_address.returnto_name)){
                    itemBinding?.tvOfficeName?.text = item.extension_attributes.returnto_address.returnto_name
                }

                if(!TextUtils.isEmpty(item.extension_attributes.returnto_address.returnto_address)){
                    itemBinding?.tvOfficeAddress?.text = item.extension_attributes.returnto_address.returnto_address + ", " + item.extension_attributes.returnto_address.returnto_contact
                }

                itemBinding?.btnReturn?.setOnClickListener { view ->
                    action(view.id)
                }
            }
        }
    }
}
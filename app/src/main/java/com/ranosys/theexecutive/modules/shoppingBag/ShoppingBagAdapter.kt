package com.ranosys.theexecutive.modules.shoppingBag

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.ShoppingBagFooterBinding
import com.ranosys.theexecutive.databinding.ShoppingBagItemBinding


class ShoppingBagAdapter(var context: Context, var shoppingBagList: List<ShoppingBagResponse>?, val action: (Int, Int, ShoppingBagResponse?, Int?) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var mContext: Context? = null
    private val TYPE_FOOTER = 0
    private val TYPE_ITEM = 1
    var clickListener: ShoppingBagAdapter.OnItemClickListener? = null

    init {
        mContext = context
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == shoppingBagList?.size) TYPE_FOOTER else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == TYPE_FOOTER) {
            val binding: ShoppingBagFooterBinding? = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.shopping_bag_footer, parent, false)
            return ShoppingBagFooterHolder(itemBinding = binding)

        } else {
            val binding: ShoppingBagItemBinding? = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.shopping_bag_item, parent, false)
            return Holder(itemBinding = binding)
        }
    }

    override fun getItemCount(): Int {
        shoppingBagList?.run {
            return size + 1
        }
        return 0
    }


    fun getItem(position: Int): ShoppingBagResponse? {
        return shoppingBagList?.get(position)
    }


    class Holder(var itemBinding: ShoppingBagItemBinding?) : RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(context: Context?, item: ShoppingBagResponse?, position: Int, action: (Int, Int, ShoppingBagResponse?, Int?) -> Unit, listener: ShoppingBagAdapter.OnItemClickListener?) {
            itemBinding?.item = item
            var updateQty = item?.qty

            itemBinding?.imgProduct?.setOnClickListener { view ->
                action(0, position, item, updateQty)
            }

            itemBinding?.imgDecrement?.setOnClickListener { view ->
                if (item?.qty!! > 1) {
                    updateQty = --item.qty
                    itemBinding?.tvQuantity?.text = updateQty.toString()
                    action(view.id, position, item, updateQty)
                }
            }

            itemBinding?.imgIncrement?.setOnClickListener { view ->

                if (item?.qty!! >= 1) {
                    updateQty = ++item.qty
                    itemBinding?.tvQuantity?.text = updateQty.toString()
                    action(view.id, position, item, updateQty)
                }
            }

            itemBinding?.imgWishlist?.setOnClickListener { view ->
                action(view.id, position, item, updateQty)
            }

            itemBinding?.imgDelete?.setOnClickListener { view ->
                action(view.id, position, item, updateQty)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is Holder) {
            holder.bind(mContext, getItem(position), position, action, clickListener)
        } else if (holder is ShoppingBagFooterHolder) {
            holder.bind(mContext, null, position, action, clickListener)
        }
    }

    class ShoppingBagFooterHolder(var itemBinding: ShoppingBagFooterBinding?) : RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(context: Context?, item: ShoppingBagResponse?, position: Int, action: (Int, Int, ShoppingBagResponse?, Int?) -> Unit, listener: ShoppingBagAdapter.OnItemClickListener?) {

            itemBinding?.tvTotal?.setText("57486787 ")
            itemBinding?.etPromoCode?.setText("21341dkfhgjkdf")

            itemBinding?.btnApply?.setOnClickListener { view ->
                Toast.makeText(context, "Promo code apply", Toast.LENGTH_SHORT).show()
            }


            itemBinding?.btnCheckout?.setOnClickListener { view ->
                Toast.makeText(context, "Checkout", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
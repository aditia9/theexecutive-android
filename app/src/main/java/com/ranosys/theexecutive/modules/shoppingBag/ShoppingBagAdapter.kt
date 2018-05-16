package com.ranosys.theexecutive.modules.shoppingBag

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.ShoppingBagFooterBinding
import com.ranosys.theexecutive.databinding.ShoppingBagItemBinding
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.Utils

/**
 * @Class An data class for Shopping bag adapter
 * @author Ranosys Technologies
 * @Date 15-May-2018
 */

const val TYPE_FOOTER = 0
const val TYPE_ITEM = 1

class ShoppingBagAdapter(var context: Context, private var  shoppingBagList: List<ShoppingBagResponse>?, promoCode: String, grandTotal: Int, private val action: (Int, Int, ShoppingBagResponse?, Int?, String?) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mContext: Context? = null
    private var mPromoCode: String
    private var mGrandTotal: Int = 0

    private var clickListener: ShoppingBagAdapter.OnItemClickListener? = null

    init {
        mContext = context
        mPromoCode = promoCode
        mGrandTotal = grandTotal
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

        return if (viewType == TYPE_FOOTER) {
            val binding: ShoppingBagFooterBinding? = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.shopping_bag_footer, parent, false)
            ShoppingBagFooterHolder(itemBinding = binding)

        } else {
            val binding: ShoppingBagItemBinding? = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.shopping_bag_item, parent, false)
            Holder(itemBinding = binding)
        }
    }

    override fun getItemCount(): Int {
        shoppingBagList?.run {
            return size + 1
        }
        return 0
    }


    private fun getItem(position: Int): ShoppingBagResponse? {
        return shoppingBagList?.get(position)
    }


    class Holder(var itemBinding: ShoppingBagItemBinding?) : RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(context: Context?, item: ShoppingBagResponse?, position: Int, action: (Int, Int, ShoppingBagResponse?, Int?, String?) -> Unit) {
            itemBinding?.item = item
            var updateQty = item?.qty

            itemBinding?.imgProduct?.setOnClickListener {
                action(0, position, item, updateQty, null)
            }

            item?.product_option?.extension_attributes?.configurable_item_options.run {

                if (item?.product_option?.extension_attributes?.configurable_item_options != null && item.product_option.extension_attributes.configurable_item_options.isNotEmpty()) {
                    item.product_option.extension_attributes.configurable_item_options.forEach {
                        when (it.extension_attributes.attribute_label) {
                            Constants.COLOR_ -> {
                                if(!TextUtils.isEmpty(it.extension_attributes.option_label)){
                                    itemBinding?.tvProductColor?.text = it.extension_attributes.option_label
                                }else{
                                    itemBinding?.tvProductColor?.visibility = View.GONE
                                    itemBinding?.viewVertical?.visibility = View.GONE
                                }
                            }
                            Constants.SIZE_ -> {
                                if(!TextUtils.isEmpty(it.extension_attributes.option_label)){
                                    itemBinding?.tvProductSize?.text = it.extension_attributes.option_label
                                }else{
                                    itemBinding?.tvProductSize?.visibility = View.GONE
                                }

                            }
                        }
                    }
                } else {
                    itemBinding?.layoutColorSize?.visibility = View.INVISIBLE
                }


                item?.extension_attributes?.stock_item?.run {
                    if (is_in_stock) {
                        itemBinding?.tvOutOfStock?.visibility = View.GONE
                    } else {
                        itemBinding?.tvOutOfStock?.visibility = View.VISIBLE
                    }
                }

                itemBinding?.imgDecrement?.setOnClickListener { view ->

                    if (item?.extension_attributes?.stock_item?.is_in_stock!!) {
                        if (item.qty > 1) {
                            updateQty = item.qty
                            updateQty = (updateQty!! - 1)
                           // itemBinding?.tvQuantity?.text = updateQty.toString()
                            action(view.id, position, item, updateQty, null)
                        }
                    }
                }

                itemBinding?.imgIncrement?.setOnClickListener { view ->

                    if (item?.extension_attributes?.stock_item?.is_in_stock!!) {
                        var localItemCount = item.qty
                        if (item.qty >= 1 && item.extension_attributes.stock_item.qty >= ++localItemCount) {
                            updateQty = localItemCount
                            action(view.id, position, item, updateQty, null)
                        } else {
                            Toast.makeText(context, context?.getString(R.string.no_more_products), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                itemBinding?.imgWishlist?.setOnClickListener { view ->
                    action(view.id, position, item, updateQty, null)
                }

                itemBinding?.imgDelete?.setOnClickListener { view ->
                    action(view.id, position, item, updateQty, null)
                }


                if(item?.qty!! > 0){
                    itemBinding?.tvRegularPrice?.text = Utils.getDisplayPrice((item.price * item.qty).toString(), (item.extension_attributes.regular_price *  item.qty).toString())
                }

                if(item.extension_attributes.stock_item.is_in_stock){
                    if(item.qty > item.extension_attributes.stock_item.qty){
                        val fullMsg = context?.getString(R.string.only) + " "+ item.extension_attributes.stock_item.qty + " " + context?.getString(R.string.product_available)
                        itemBinding?.tvQtyMsg?.text = fullMsg
                        itemBinding?.tvQtyMsg?.visibility = View.VISIBLE
                    }else{
                        itemBinding?.tvQtyMsg?.visibility = View.GONE
                    }
                }

            }

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is Holder) {
            holder.bind(mContext, getItem(position), position, action)
        } else if (holder is ShoppingBagFooterHolder) {
            holder.bind(mContext, null, position, action, mPromoCode, mGrandTotal)
        }
    }

    class ShoppingBagFooterHolder(var itemBinding: ShoppingBagFooterBinding?) : RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(context: Context?, item: ShoppingBagResponse?, position: Int, action: (Int, Int, ShoppingBagResponse?, Int?, String?) -> Unit, mPromoCode: String, mGrandTotal: Int) {

            itemBinding?.btnApply?.setOnClickListener { view ->
                if (!TextUtils.isEmpty(itemBinding!!.etPromoCode.text.toString())) {
                    action(view.id, position, item, null, itemBinding!!.etPromoCode.text.toString())
                }
            }

            itemBinding?.btnCheckout?.setOnClickListener {
            }

            if (mGrandTotal != 0) {
                itemBinding?.tvTotal?.text = Utils.getFromattedPrice(mGrandTotal.toString())
            }

            if (!TextUtils.isEmpty(mPromoCode)) {
                itemBinding?.tvAppliedPromoCode?.setText(mPromoCode)
                itemBinding?.imvDeletePromoCode?.visibility = View.VISIBLE
                itemBinding?.etPromoCode?.visibility = View.GONE
                itemBinding?.btnApply?.visibility = View.GONE
            } else {
                itemBinding?.imvDeletePromoCode?.visibility = View.GONE
                itemBinding?.tvAppliedPromoCode?.visibility = View.GONE
                itemBinding?.btnApply?.visibility = View.VISIBLE
            }

            itemBinding?.imvDeletePromoCode?.setOnClickListener { view ->
                action(view.id, position, item, null, itemBinding!!.etPromoCode.text.toString())
            }
        }
    }
}
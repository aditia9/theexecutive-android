package com.delamibrands.theexecutive.modules.shoppingBag

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.databinding.ShoppingBagFooterBinding
import com.delamibrands.theexecutive.databinding.ShoppingBagItemBinding
import com.delamibrands.theexecutive.utils.Constants
import com.delamibrands.theexecutive.utils.Utils

/**
 * @Class An data class for Shopping bag adapter
 * @author Ranosys Technologies
 * @Date 15-May-2018
 */

const val TYPE_FOOTER = 0
const val TYPE_ITEM = 1
var isOutOfProductInCart : Boolean =  false

class ShoppingBagAdapter(var context: Context, private var shoppingBagList: List<ShoppingBagResponse>?, promoCode: String, totalResponse: TotalResponse?, private val action: (Int, Int, ShoppingBagResponse?, Int?, String?) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mContext: Context? = null
    private var mPromoCode: String
    private var mTotalResponse : TotalResponse ?= null


    private var clickListener: OnItemClickListener? = null

    init {
        mContext = context
        mPromoCode = promoCode
        mTotalResponse = totalResponse
        isOutOfProductInCart = false
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

            itemBinding?.tvProductName?.setOnClickListener{
                action(0, position, item, updateQty, null)
            }

            item?.product_option?.extension_attributes?.configurable_item_options.run {

                if (item?.product_option?.extension_attributes?.configurable_item_options != null && item.product_option.extension_attributes.configurable_item_options.isNotEmpty()) {
                    item.product_option.extension_attributes.configurable_item_options.forEach {
                        when (it.extension_attributes.attribute_label) {
                            Constants.COLOR_, Constants.IN_COLOR -> {
                                if(!TextUtils.isEmpty(it.extension_attributes.option_label)){
                                    itemBinding?.tvProductColor?.text = it.extension_attributes.option_label
                                }else{
                                    itemBinding?.tvProductColor?.visibility = View.GONE
                                    itemBinding?.viewVertical?.visibility = View.GONE
                                }
                            }
                            Constants.SIZE_, Constants.IN_SIZE -> {
                                if(!TextUtils.isEmpty(it.extension_attributes.option_label)){
                                    itemBinding?.tvProductSize?.text = it.extension_attributes.option_label
                                }else{
                                    itemBinding?.tvProductSize?.visibility = View.GONE
                                }

                            }
                        }
                    }
                } else {
                    itemBinding?.layoutColorSize?.visibility = View.GONE
                }


                item?.extension_attributes?.stock_item?.run {
                    if (is_in_stock) {
                        itemBinding?.tvOutOfStock?.visibility = View.GONE
                        itemBinding?.imgProductBlur?.visibility = View.GONE
                    } else {
                        itemBinding?.tvOutOfStock?.visibility = View.VISIBLE
                        itemBinding?.imgProductBlur?.visibility = View.VISIBLE
                        isOutOfProductInCart = true
                    }
                }

                itemBinding?.imgDecrement?.setOnClickListener { view ->

                    if (item?.extension_attributes?.stock_item?.is_in_stock!!) {
                        if (item.qty > 1) {
                            updateQty = (updateQty!! - 1)
                            if(item.qty > item.extension_attributes.stock_item.qty && updateQty!! > item.extension_attributes.stock_item.qty){
                                val fullMsg = context?.getString(R.string.only) + " "+ item.extension_attributes.stock_item.qty + " " + context?.getString(R.string.product_available)
                                itemBinding?.tvQtyMsg?.text = fullMsg
                                itemBinding?.tvQtyMsg?.visibility = View.VISIBLE
                                itemBinding?.tvQuantity?.text = updateQty.toString()
                            }else{
                                itemBinding?.tvQtyMsg?.visibility = View.GONE
                                action(view.id, position, item, updateQty, null)
                            }
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
                    itemBinding?.tvRegularPrice?.text = Utils.getDisplayPrice((item.extension_attributes.regular_price  * item.qty).toString(), (item.price*  item.qty).toString(), context?.getString(R.string.currency) ?: Constants.IDR)
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
            holder.bind(mContext, getItem(position), position,  action)
        } else if (holder is ShoppingBagFooterHolder) {
            holder.bind(mContext, null, position, action, mPromoCode, mTotalResponse!!)
        }
    }

    class ShoppingBagFooterHolder(var itemBinding: ShoppingBagFooterBinding?) : RecyclerView.ViewHolder(itemBinding?.root) {

        @SuppressLint("SetTextI18n")
        fun bind(context: Context?, item: ShoppingBagResponse?, position: Int, action: (Int, Int, ShoppingBagResponse?, Int?, String?) -> Unit, mPromoCode: String, mTotalResponse: TotalResponse) {

            itemBinding?.btnApply?.setOnClickListener { view ->
                if (!TextUtils.isEmpty(itemBinding!!.etPromoCode.text.toString())) {
                    action(view.id, position, item, null, itemBinding!!.etPromoCode.text.toString())
                }
            }

            itemBinding?.btnCheckout?.setOnClickListener { view->
                if(isOutOfProductInCart){
                    Toast.makeText(context, context?.getText(R.string.cart_out_of_stock), Toast.LENGTH_SHORT).show()
                }else{
                    action(view.id, position, item, null, itemBinding!!.etPromoCode.text.toString())
                }
            }

            if (mTotalResponse.subtotal != 0) {
                itemBinding?.tvTotal?.text = context?.getString(R.string.currency) +" "+ Utils.getFromattedPrice(mTotalResponse.subtotal.toString())
            }

            if(!TextUtils.isEmpty(mPromoCode)){
                itemBinding?.labelDiscount?.visibility = View.VISIBLE
                itemBinding?.tvDiscount?.visibility = View.VISIBLE
                itemBinding?.labelGrandTotal?.visibility = View.VISIBLE
                itemBinding?.tvGrandTotal?.visibility = View.VISIBLE

                itemBinding?.tvDiscount?.text = context?.getString(R.string.currency) +" "+ Utils.getFromattedPrice(mTotalResponse.discount_amount.toString())
                itemBinding?.tvGrandTotal?.text = context?.getString(R.string.currency) +" "+ Utils.getFromattedPrice(mTotalResponse.subtotal_with_discount.toString())
            }

            if (!TextUtils.isEmpty(mPromoCode)) {
                itemBinding?.tvAppliedPromoCode?.text = mPromoCode
                itemBinding?.imvDeletePromoCode?.visibility = View.VISIBLE
                itemBinding?.tvAppliedPromoCode?.visibility = View.VISIBLE
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
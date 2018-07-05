package com.ranosys.theexecutive.modules.wishlist

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.WishlistItemBinding
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.Utils

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 02,May,2018
 */
class WishlistAdapter (var context: Context, var wishlist: List<Item>?, val action: (Int, Int, Item?) -> Unit) : RecyclerView.Adapter<WishlistAdapter.Holder>() {

    private var mContext : Context? = null
    var clickListener: WishlistAdapter.OnItemClickListener? = null

    init {
        mContext = context
    }

    interface OnItemClickListener {
        fun onItemClick( position: Int)
    }

    fun setItemClickListener(listener: OnItemClickListener){
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder{
        val binding: WishlistItemBinding? = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.wishlist_item, parent,false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        wishlist?.run {
            return size
        }
        return 0
    }

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        holder?.bind(mContext, getItem(position),  position, action, clickListener)
    }

    fun getItem(position: Int) : Item?{
        return wishlist?.get(position)
    }

    class Holder(var itemBinding: WishlistItemBinding?): RecyclerView.ViewHolder(itemBinding?.root) {

        fun bind(context: Context?, item : Item?, position : Int, action: (Int, Int, Item?) -> Unit, listener: OnItemClickListener?){
            itemBinding?.item = item

            itemBinding?.tvRegularPrice?.text = Utils.getDisplayPrice(item?.regular_price.toString(), item?.final_price.toString())
            if(item?.type_id == Constants.CONFIGURABLE){
                itemBinding?.imgBag?.setImageResource(R.drawable.eye)
                itemBinding?.imgProductOverlay?.visibility = View.GONE
                item.stock_item?.run {
                    if(is_in_stock){
                        itemBinding?.tvOutOfStock?.visibility = View.GONE

                    }else{
                        itemBinding?.tvOutOfStock?.visibility = View.VISIBLE
                    }
                }
            }else{

                item?.stock_item?.run {
                    if(is_in_stock){
                        itemBinding?.imgBag?.setImageResource(R.drawable.bag)
                        itemBinding?.tvOutOfStock?.visibility = View.GONE
                        itemBinding?.imgProductOverlay?.visibility = View.GONE

                    }else{
                        itemBinding?.imgBag?.setImageResource(R.drawable.bag_disable)
                        itemBinding?.tvOutOfStock?.visibility = View.VISIBLE
                        itemBinding?.imgProductOverlay?.visibility = View.VISIBLE
                    }
                }
            }


            itemView.setOnClickListener {
                view -> action(0, position, item)
            }

            itemBinding?.imgBag?.setOnClickListener {
                view -> action(view.id, position, item)
            }

            itemBinding?.imgDelete?.setOnClickListener{
                view -> action(view.id, position, item)
            }
            
        }
    }
}
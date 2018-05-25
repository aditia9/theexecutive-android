package com.ranosys.theexecutive.modules.checkout

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.ShippingMethodItemBinding

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 22-May-2018
 */
class ShippingtMethodAdapter(var shippingMethodList: List<CheckoutDataClass.GetShippingMethodsResponse>?,
                             val action:(isChecked: Boolean, selecetdMethod: CheckoutDataClass.GetShippingMethodsResponse) -> Unit): RecyclerView.Adapter<ShippingtMethodAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val binding: ShippingMethodItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.shipping_method_item, parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = shippingMethodList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(shippingMethodList?.get(position), position, action, shippingMethodList)
    }


    class ViewHolder(val itemBinding: ShippingMethodItemBinding): RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(shippingMethod: CheckoutDataClass.GetShippingMethodsResponse?, position: Int, action: (isChecked: Boolean, selecetdMethod: CheckoutDataClass.GetShippingMethodsResponse) -> Unit, shippingMethodList: List<CheckoutDataClass.GetShippingMethodsResponse>?) {
            itemBinding.shippingMethod = shippingMethod
            itemBinding.cbShippingMethod.isChecked = shippingMethod?.isSelected!!

            itemBinding.cbShippingMethod.setOnCheckedChangeListener { buttonView, isChecked ->
                action(isChecked, shippingMethodList?.get(position)!!)
                if(isChecked){
                    resetCheckbox(shippingMethodList)
                    shippingMethod.isSelected = true

                }else{
                    shippingMethod.isSelected = false
                }

            }
        }

        private fun resetCheckbox(shippingMethodList: List<CheckoutDataClass.GetShippingMethodsResponse>?) {
            if(null != shippingMethodList){
                for(item in shippingMethodList){
                    item.isSelected = false
                }
            }
        }
    }
}
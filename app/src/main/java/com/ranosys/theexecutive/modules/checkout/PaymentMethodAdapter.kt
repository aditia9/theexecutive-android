package com.ranosys.theexecutive.modules.checkout

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.PaymentMethodItemBinding

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 23-May-2018
 */
class PaymentMethodAdapter(var paymentMethodList: List<CheckoutDataClass.PaymentMethod>?,
                           val action:(isChecked: Boolean, selectedMethod: CheckoutDataClass.PaymentMethod) -> Unit): RecyclerView.Adapter<PaymentMethodAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val binding: PaymentMethodItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.payment_method_item, parent,false)
        return PaymentMethodAdapter.ViewHolder(binding)
    }

    override fun getItemCount() = paymentMethodList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(paymentMethodList?.get(position), position, action, paymentMethodList)
    }

    class ViewHolder(val itemBinding: PaymentMethodItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(paymentMethod: CheckoutDataClass.PaymentMethod?, position: Int, action: (isChecked: Boolean, selectedMethod: CheckoutDataClass.PaymentMethod) -> Unit, paymentMethodList: List<CheckoutDataClass.PaymentMethod>?) {
            itemBinding.paymentMethod = paymentMethod
            itemBinding.cbPaymentMethod.isChecked = paymentMethod?.isSelected!!

            itemBinding.cbPaymentMethod.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    resetCheckbox(paymentMethodList)
                    paymentMethod.isSelected = true
                    action(isChecked, paymentMethodList?.get(position)!!)

                }else{
                    paymentMethod.isSelected = false
                }
            }
        }

        private fun resetCheckbox(shippingMethodList: List<CheckoutDataClass.PaymentMethod>?) {
            if(null != shippingMethodList){
                for(item in shippingMethodList){
                    item.isSelected = false
                }
            }
        }

    }
}
package com.delamibrands.theexecutive.modules.addressBook

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.databinding.AddressListItemBinding
import com.delamibrands.theexecutive.modules.myAccount.MyAccountDataClass
import com.delamibrands.theexecutive.utils.Utils

/**
 * @Details Adapter for address list
 * @Author Ranosys Technologies
 * @Date 01-May-2018
 */
class AddressBookAdapter(var addressList: MutableList<MyAccountDataClass.Address>?,
                         val action:(id:Int, itemPos: Int) -> Unit) : RecyclerView.Adapter<AddressBookAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding: AddressListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.address_list_item, parent,false)
        return Holder(binding)
    }

    override fun getItemCount(): Int = addressList?.size?:0

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(addressList?.get(position), position, action)
    }


    class Holder(private val itemBinding: AddressListItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(address: MyAccountDataClass.Address?, position: Int, action:(id: Int, itemPos: Int) -> Unit) {
            address.let {


                itemBinding.country = Utils.getCountryName(it?.country_id!!)
                itemBinding.address = it
                itemBinding.isDefault = it == Utils.getDefaultAddress()



                itemView.setOnClickListener {view ->
                    action(0, position)
                }

                itemBinding.tvRemoveAddress.setOnClickListener { view ->
                    action(view.id, position)
                }

                itemBinding.tvEditAddress.setOnClickListener {view ->
                    action(view.id, position)
                }

                itemBinding.chkDefault.setOnClickListener {view ->
                    action(view.id, position)

                }

            }
        }

    }
}
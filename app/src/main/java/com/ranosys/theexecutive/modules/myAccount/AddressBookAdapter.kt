package com.ranosys.theexecutive.modules.myAccount

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.ranosys.theexecutive.databinding.AddressListItemBinding
import com.ranosys.theexecutive.utils.Utils

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 01-May-2018
 */
class AddressBookAdapter(var addressList: MutableList<MyAccountDataClass.Address>?, val addressItemBinding: AddressListItemBinding) : RecyclerView.Adapter<AddressBookAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return AddressBookAdapter.Holder(addressItemBinding)
    }

    override fun getItemCount(): Int = addressList?.size?:0

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(addressList?.get(position))
    }


    class Holder(val itemBinding: AddressListItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(address: MyAccountDataClass.Address?) {
            address.let {
                itemBinding.country = Utils.getCountryName(it?.country_id!!)
                itemBinding.address = it
            }
        }

    }
}
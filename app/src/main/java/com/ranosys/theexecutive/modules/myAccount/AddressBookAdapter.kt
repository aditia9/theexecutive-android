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
class AddressBookAdapter(var addressList: MutableList<MyAccountDataClass.Address>?, private val addressItemBinding: AddressListItemBinding,
                        val action:(id:Int, itemPos: Int) -> Unit) : RecyclerView.Adapter<AddressBookAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return AddressBookAdapter.Holder(addressItemBinding)
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



                itemBinding.tvRemoveAddress.setOnClickListener { view ->
                    action(view.id, position)
                }

                itemBinding.tvEditAddress.setOnClickListener {view ->
                    action(view.id, position)
                }

            }
        }

    }
}
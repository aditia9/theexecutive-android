package com.ranosys.theexecutive.modules.myAccount

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.AddressListItemBinding
import com.ranosys.theexecutive.utils.DialogOkCallback
import com.ranosys.theexecutive.utils.GlobalSingelton
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_address_book.*

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 01-May-2018
 */
class AddressBookFragment: BaseFragment() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var mViewModel: AddressBookViewModel
    private var addressList: MutableList<MyAccountDataClass.Address>? = null
    private lateinit var addressBookAdapter: AddressBookAdapter
    private lateinit var addressItemBinding: AddressListItemBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_address_book, container, false)
        addressItemBinding = DataBindingUtil.inflate(inflater, R.layout.address_list_item, container,false)

        mViewModel = ViewModelProviders.of(this).get(AddressBookViewModel::class.java)
        addressList = GlobalSingelton.instance?.userInfo?.addresses?.toMutableList()

        //api for getting address
        mViewModel.getAddressList()
        observeAddressList()
        return view
    }

    private fun observeAddressList() {
        mViewModel.addressList.observe(this, Observer { apiResponse ->

            if(apiResponse?.error.isNullOrEmpty()){
                //update list in adapter
                addressBookAdapter.let {
                    addressList = apiResponse?.apiResponse
                    addressBookAdapter.addressList = addressList
                    addressBookAdapter.notifyDataSetChanged()
                }

            }else{
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(activity as Context)
        address_list.layoutManager = linearLayoutManager

        //get stored
        addressBookAdapter = AddressBookAdapter(addressList, addressItemBinding, action = {id: Int, pos: Int ->
            handleAddressEvents(id, pos)
        })
        address_list.adapter = addressBookAdapter



//        addressItemBinding.tvEditAddress.setOnClickListener {
//            Toast.makeText(activity as Context, "edit address", Toast.LENGTH_SHORT).show()
//        }
//
//        addressItemBinding.tvRemoveAddress.setOnClickListener {
//            //Toast.makeText(activity as Context, "remove address", Toast.LENGTH_SHORT).show()
//
//        }
//
//        addressItemBinding.chkDefault.setOnCheckedChangeListener { buttonView, isChecked ->
//            if(isChecked){
//                Toast.makeText(activity as Context, "save default address", Toast.LENGTH_SHORT).show()
//            }
//        }


    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.address_book), 0, "", R.drawable.back, true, R.drawable.back , true)
    }

    private fun handleAddressEvents(id: Int, addressPostion: Int): Unit{
        when(id){
            R.id.tv_remove_address -> {
                removeAddress(addressPostion)
            }

            R.id.tv_edit_address -> {
                editAddress(addressPostion)
            }

            else -> Toast.makeText(activity as Context, "Some other action", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editAddress(addressPostion: Int) {
        Toast.makeText(activity as Context, "edit item: $addressPostion", Toast.LENGTH_SHORT).show()
    }

    private fun removeAddress(addressPostion: Int) {
        Toast.makeText(activity as Context, "Remove item: $addressPostion", Toast.LENGTH_SHORT).show()
        Utils.showDialog(activity, "You want to remove this address", getString(android.R.string.ok), "", object: DialogOkCallback{
            override fun setDone(done: Boolean) {
                mViewModel.removeAddress(addressList?.get(addressPostion))
            }

        })

    }

}
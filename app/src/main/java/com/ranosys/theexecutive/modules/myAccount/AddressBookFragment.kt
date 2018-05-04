package com.ranosys.theexecutive.modules.myAccount

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.utils.DialogOkCallback
import com.ranosys.theexecutive.utils.FragmentUtils
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_address_book, container, false)

        mViewModel = ViewModelProviders.of(this).get(AddressBookViewModel::class.java)
        addressList = GlobalSingelton.instance?.userInfo?.addresses?.toMutableList()

        //api for getting address
        mViewModel.getAddressList()
        observeAddressList()
        observeRemoveAddressApiResponse()
        return view
    }

    private fun observeRemoveAddressApiResponse() {
        mViewModel.removeAddressApiResponse.observe(this, Observer { apiResponse ->
            hideLoading()
            if(apiResponse?.error.isNullOrBlank()){

                Toast.makeText(activity as Context, "Address removed successfully", Toast.LENGTH_SHORT).show()
                addressList = apiResponse?.apiResponse?.addresses
                addressBookAdapter.addressList = addressList
                addressBookAdapter.notifyDataSetChanged()

            }else{
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })
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
        addressBookAdapter = AddressBookAdapter(addressList, action = {id: Int, pos: Int ->
            handleAddressEvents(id, pos)
        })
        address_list.adapter = addressBookAdapter

    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.address_book), 0, "", R.drawable.back, true, R.drawable.add , true)
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

    private fun editAddress(addressPosition: Int) {
        //todo - move to add address screen with address
        val addAddressFragment = AddAddressFragment.getInstance(addressList?.get(addressPosition))
        FragmentUtils.addFragment(context, addAddressFragment,null, AddAddressFragment::class.java.name, true )
    }

    private fun removeAddress(addressPosition: Int) {
        if(addressList?.get(addressPosition) == Utils.getDefaultAddress()){
            Utils.showDialog(activity, "can't delete default address", getString(android.R.string.ok), "", null)
        }else{
            Utils.showDialog(activity, "You want to remove this address", getString(android.R.string.ok), getString(android.R.string.cancel), object: DialogOkCallback{
                override fun setDone(done: Boolean) {
                    callRemoveAddressApi(addressPosition)
                }

            })
        }
    }

    private fun callRemoveAddressApi(addressPosition: Int) {
        if (Utils.isConnectionAvailable(activity as Context)) {
            showLoading()
            mViewModel.removeAddress(addressList?.get(addressPosition))
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

}
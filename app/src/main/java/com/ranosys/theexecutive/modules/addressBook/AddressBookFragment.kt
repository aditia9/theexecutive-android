package com.ranosys.theexecutive.modules.addressBook

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
import com.ranosys.theexecutive.activities.DashBoardActivity
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.modules.addAddress.AddAddressFragment
import com.ranosys.theexecutive.modules.editAddress.EditAddressFragment
import com.ranosys.theexecutive.modules.myAccount.MyAccountDataClass
import com.ranosys.theexecutive.utils.DialogOkCallback
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.GlobalSingelton
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_address_book.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*

/**
 * @Details screen showing address list
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


        observeAddressList()
        observeRemoveAddressApiResponse()
        observeSetDefaultAddressApiResponse()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(activity as Context)
        address_list.layoutManager = linearLayoutManager

        //get stored
        addressBookAdapter = AddressBookAdapter(addressList, action = { id: Int, pos: Int ->
            handleAddressEvents(id, pos)
        })
        address_list.adapter = addressBookAdapter

        (activity as DashBoardActivity).toolbarBinding.root.toolbar_right_icon_image.setOnClickListener {
            addAddress()
        }

    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.address_book), 0, "", R.drawable.back, true, R.drawable.add , true)
        addressBookAdapter.addressList = GlobalSingelton.instance?.userInfo?.addresses
        getAddressList()
    }

    private fun getAddressList(){
        if (Utils.isConnectionAvailable(activity as Context)) {
            mViewModel.getAddressList()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    private fun observeRemoveAddressApiResponse() {
        mViewModel.removeAddressApiResponse.observe(this, Observer { apiResponse ->
            hideLoading()
            if(apiResponse?.error.isNullOrBlank()){

                Toast.makeText(activity as Context, getString(R.string.address_remove_success_msg), Toast.LENGTH_SHORT).show()
                addressList = apiResponse?.apiResponse?.addresses
                addressBookAdapter.addressList = addressList
                addressBookAdapter.notifyDataSetChanged()

            }else{
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })
    }

    private fun observeSetDefaultAddressApiResponse() {
        mViewModel.setDefaultAddressApiResponse.observe(this, Observer { apiResponse ->
            hideLoading()
            if(apiResponse?.error.isNullOrBlank()){
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


    private fun handleAddressEvents(id: Int, addressPosition: Int){
        when(id){
            R.id.tv_remove_address -> {
                removeAddress(addressPosition)
            }

            R.id.tv_edit_address -> {
                editAddress(addressPosition)
            }

            R.id.chk_default -> {
                changeDefaultAddress(addressPosition)
            }

        }
    }

    private fun changeDefaultAddress(addressPosition: Int) {

        if (Utils.isConnectionAvailable(activity as Context)) {
            showLoading()
            mViewModel.setDefaultAddress(addressList?.get(addressPosition))
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }

    }

    private fun editAddress(addressPosition: Int) {
        val editAddressFragment = EditAddressFragment.getInstance(addressList?.get(addressPosition))
        FragmentUtils.addFragment(context, editAddressFragment,null, EditAddressFragment::class.java.name, true )
    }

    private fun addAddress() {
        FragmentUtils.addFragment(context, AddAddressFragment(),null, AddAddressFragment::class.java.name, true )
    }

    private fun removeAddress(addressPosition: Int) {
        if(addressList?.get(addressPosition) == Utils.getDefaultAddress()){
            Utils.showDialog(activity, getString(R.string.dafault_address_delete_warning), getString(android.R.string.ok), "", null)
        }else{
            Utils.showDialog(activity, getString(R.string.delete_address_confirmation), getString(android.R.string.ok), getString(android.R.string.cancel), object: DialogOkCallback{
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
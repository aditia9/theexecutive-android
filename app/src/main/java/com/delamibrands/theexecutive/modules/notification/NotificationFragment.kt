package com.delamibrands.theexecutive.modules.notification

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.api.ApiResponse
import com.delamibrands.theexecutive.base.BaseFragment
import com.delamibrands.theexecutive.modules.notification.dataclasses.NotificationChangeStatusRequest
import com.delamibrands.theexecutive.modules.notification.dataclasses.NotificationListResponse
import com.delamibrands.theexecutive.modules.order.orderDetail.OrderDetailFragment
import com.delamibrands.theexecutive.modules.productDetail.ProductDetailFragment
import com.delamibrands.theexecutive.modules.productListing.ProductListingFragment
import com.delamibrands.theexecutive.modules.shoppingBag.ShoppingBagFragment
import com.delamibrands.theexecutive.utils.Constants
import com.delamibrands.theexecutive.utils.FragmentUtils
import com.delamibrands.theexecutive.utils.SavedPreferences
import com.delamibrands.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_notification_list.*

/**
 * @Details Class showing notification listing
 * @Author Ranosys Technologies
 * @Date 02,May,2018
 */
class NotificationFragment : BaseFragment() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private var mNotificationViewModel : NotificationViewModel? = null
    private var notificationList: List<NotificationListResponse>? = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mNotificationViewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_notification_list, container, false)
        observeNotificationListResponse()
        return view
    }

    private fun observeNotificationListResponse() {
        mNotificationViewModel?.notificationListResponse?.observe(this,  Observer<ApiResponse<List<NotificationListResponse>>>{ apiResponse ->
            hideLoading()
            val response = apiResponse?.apiResponse ?: apiResponse?.error
            if (response is List<*>) {
                if(response.size > 0){
                    notificationList = response as List<NotificationListResponse>
                    setNotificationAdapter()
                }else{
                    tv_no_item_in_notification.visibility = View.VISIBLE
                    notification_list.visibility = View.GONE
                }
            } else {
                Toast.makeText(activity, apiResponse?.error, Toast.LENGTH_LONG).show()
            }
        } )

        mNotificationViewModel?.changeNotificationStatusResponse?.observe(this,  Observer<ApiResponse<Boolean>>{ apiResponse ->
            val response = apiResponse?.apiResponse ?: apiResponse?.error
            if (response is Boolean) {
            } else {
                Toast.makeText(activity, apiResponse?.error, Toast.LENGTH_LONG).show()
            }
        } )
    }

    private fun setNotificationAdapter(){
        linearLayoutManager = LinearLayoutManager(activity as Context)
        notification_list.layoutManager = linearLayoutManager
        val notificationAdapter = NotificationAdapter(notificationList)
        notification_list.adapter = notificationAdapter
        notificationAdapter.setItemClickListener(object : NotificationAdapter.OnItemClickListener {
            override fun onItemClick(item: NotificationListResponse?) {
                if(item?.is_read?.not()!!) {
                    val request = NotificationChangeStatusRequest(item.id.toString(),
                            SavedPreferences.getInstance()?.getStringValue(Constants.ANDROID_DEVICE_ID_KEY),
                            SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY))
                    changeNotificationStatus(request)
                }
                redirectToFragment(item)
            }
        })

    }

    private fun redirectToFragment(item: NotificationListResponse?){
        when(item?.type){
            Constants.TYPE_CATEGORY -> {
                val bundle = Bundle()
                bundle.putInt(Constants.CATEGORY_ID, item.type_id.toInt())
                bundle.putString(Constants.CATEGORY_NAME, item.title)
                FragmentUtils.addFragment(activity as Context, ProductListingFragment(), bundle, ProductListingFragment::class.java.name, true)
            }

            Constants.TYPE_PRODUCT -> {
                val fragment = ProductDetailFragment.getInstance(null, item.type_id, item.title, 0)
                FragmentUtils.addFragment(context!!, fragment, null, ProductDetailFragment::class.java.name, true)
            }

            Constants.TYPE_ORDER -> {
                val bundle = Bundle()
                bundle.putString(Constants.ORDER_ID, item.type_id)
                FragmentUtils.addFragment(context, OrderDetailFragment(), bundle, OrderDetailFragment::class.java.name, true)
            }

            Constants.NOTIFICATION_TYPE_ABANDONED -> {
                val isLogin = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
                if(!TextUtils.isEmpty(isLogin)){
                    FragmentUtils.addFragment(context, ShoppingBagFragment(), null, ShoppingBagFragment::class.java.name, true)
                }
            }

        }
    }

    fun getNotification() {
        if (Utils.isConnectionAvailable(activity as Context)) {
            showLoading()
            mNotificationViewModel?.getNotificationList()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    private fun changeNotificationStatus(request: NotificationChangeStatusRequest) {
        if (Utils.isConnectionAvailable(activity as Context)) {
            mNotificationViewModel?.changeNotificationStatus(request)
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

}
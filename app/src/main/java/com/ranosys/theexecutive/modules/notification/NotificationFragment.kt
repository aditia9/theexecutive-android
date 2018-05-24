package com.ranosys.theexecutive.modules.notification

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
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.modules.myAccount.DividerDecoration
import com.ranosys.theexecutive.modules.notification.dataclasses.NotificationChangeStatusRequest
import com.ranosys.theexecutive.modules.notification.dataclasses.NotificationListResponse
import com.ranosys.theexecutive.modules.productDetail.ProductDetailFragment
import com.ranosys.theexecutive.modules.productListing.ProductListingFragment
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
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
                notificationList = response as List<NotificationListResponse>
                setNotificationAdapter()
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
        val itemDecor = DividerDecoration(resources.getDrawable(R.drawable.horizontal_divider, null))
        notification_list.addItemDecoration(itemDecor)
        val notificationAdapter = NotificationAdapter(notificationList)
        notification_list.adapter = notificationAdapter
        notificationAdapter.setItemClickListener(object : NotificationAdapter.OnItemClickListener {
            override fun onItemClick(item: NotificationListResponse?) {
                Toast.makeText(activity, item?.description, Toast.LENGTH_SHORT).show()
                if(item?.isRead?.not()!!) {
                    val request = NotificationChangeStatusRequest(item.notification_id.toString(),
                            SavedPreferences.getInstance()?.getFcmID(Constants.USER_FCM_ID),
                            SavedPreferences.getInstance()?.getFcmID(Constants.ANDROID_DEVICE_ID_KEY))
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
                //ToDo Redirect to order screen
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
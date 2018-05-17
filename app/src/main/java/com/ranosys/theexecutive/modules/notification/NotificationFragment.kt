package com.ranosys.theexecutive.modules.notification

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
import com.ranosys.theexecutive.modules.myAccount.DividerDecoration
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_notification_list.*

class NotificationFragment : BaseFragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var mViewModel : NotificationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_notification_list, container, false)
        observeNotificationListResponse()
        getNotification()
        return view
    }

    private fun observeNotificationListResponse() {
        //observe notification response
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val notificationList: MutableList<NotificationListResponse>? = mutableListOf()
        val notification = NotificationListResponse(123, "Android Developer", "https://www.artifex.com/wp-content/uploads/2017/10/Android-Authority-Logo.png", "I am android developer, I love it", true)

        notificationList?.add(notification)

        linearLayoutManager = LinearLayoutManager(activity as Context)
        notification_list.layoutManager = linearLayoutManager

        val itemDecor = DividerDecoration(resources.getDrawable(R.drawable.horizontal_divider, null))
        notification_list.addItemDecoration(itemDecor)
        val notificationAdapter = NotificationAdapter(notificationList)
        notification_list.adapter = notificationAdapter

        notificationAdapter.setItemClickListener(object : NotificationAdapter.OnItemClickListener {
            override fun onItemClick(item: NotificationListResponse) {
                Toast.makeText(activity, item.description, Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun getNotification() {
        Utils.hideSoftKeypad(activity as Context)
        if (Utils.isConnectionAvailable(activity as Context)) {
            showLoading()
            mViewModel.getNotification()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }

    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.notifications), 0, "", R.drawable.back, true, 0, false)
    }

}
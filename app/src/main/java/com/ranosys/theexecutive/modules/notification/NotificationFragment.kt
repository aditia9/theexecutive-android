package com.ranosys.theexecutive.modules.notification

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentNotificationListBinding
import com.ranosys.theexecutive.modules.myAccount.NewsLetterViewModel

class NotificationFragment : BaseFragment() {
    private lateinit var mBinding: FragmentNotificationListBinding
    private lateinit var mViewModel: NewsLetterViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification_list, container, false)
        mViewModel = ViewModelProviders.of(this).get(NewsLetterViewModel::class.java)
        mBinding.newsLetterVM = mViewModel


        getNotification()
        return mBinding.root
    }

    private fun getNotification() {

        var notificationAdapter = NotificationAdapter()

        notificationAdapter.clickListener
    }

}
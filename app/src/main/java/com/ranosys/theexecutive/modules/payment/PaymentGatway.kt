package com.ranosys.theexecutive.modules.payment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.utils.FragmentUtils


class PaymentGatway : BaseFragment() {


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_payment_gatway, container, false)


        openWebPage( activity as Context, "", "fkdj ")
        return view
    }


    private fun openWebPage(context: Context, url: String, title: String) {
        val fragment = FragmentUtils.getCurrentFragment(context as BaseActivity)
        fragment?.run {
            (fragment as BaseFragment).prepareWebPageDialog(context, "http://magento.theexecutive.co.id/" , title)
        }

    }
}



package com.ranosys.theexecutive.base

import android.app.Dialog
import android.arch.lifecycle.LifecycleFragment
import android.content.Context
import android.support.annotation.LayoutRes
import com.ranosys.rtp.IsPermissionGrantedInterface
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.utils.Utils


/**
 * Created by Mohammad Sunny on 24/1/18.
 */
abstract class BaseFragment : LifecycleFragment() {

    private var mContext : Context? = null
    private var mProgressDialog: Dialog? = null

    fun showLoading() {
        mProgressDialog = Utils.showDialog(mContext)
    }

    fun hideLoading() {
        if (mProgressDialog?.isShowing()!!) {
            mProgressDialog?.cancel()
        }
    }


    fun setTitle(title: String = getString(R.string.app_name)){
        val titleId = title
        (activity as BaseActivity).setScreenTitle(titleId)
    }

    fun getPermission(permissionList: List<String>, isPermissionGrantedInterface: IsPermissionGrantedInterface) {
        (activity as BaseActivity).getPermission(permissionList, isPermissionGrantedInterface)
    }

    protected fun onBackPressed() {
        activity.onBackPressed()

    }

    protected fun setBackButton() {
      /*  if (activity is DashboardActivity) {
            (activity as DashboardActivity).setBackButton()
        }*/
    }

}
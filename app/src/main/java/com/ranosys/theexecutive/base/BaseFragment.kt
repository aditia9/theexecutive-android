package com.ranosys.theexecutive.base

import android.app.Dialog
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import com.ranosys.rtp.IsPermissionGrantedInterface
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.activities.ToolbarViewModel
import com.ranosys.theexecutive.utils.Utils


/**
 * Created by Mohammad Sunny on 22/2/18.
 */
abstract class BaseFragment : LifecycleFragment() {

    private var mContext : Context? = null
    private var mProgressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeLeftIconClick()
    }


    fun showLoading() {
        mProgressDialog = Utils.showDialog(mContext)
    }

    fun hideLoading() {
        if (mProgressDialog?.isShowing!!) {
            mProgressDialog?.cancel()
        }
    }

    fun setToolBarParams(title: String, leftIcon : Int, leftIconVisibility : Boolean,
                         rightIcon : Int, rightIconVisibility : Boolean){
        setTitle(title)
        setLeftIcon(leftIcon)
        setLeftIconVisibilty(leftIconVisibility)
        setRightIcon(rightIcon)
        setRightIconVisibilty(rightIconVisibility)

    }

    protected fun getToolBarViewModel() : ToolbarViewModel?{
        return  (activity as BaseActivity).toolbarViewModel
    }

    fun setTitle(title: String = getString(R.string.app_name)){
        (activity as BaseActivity).setScreenTitle(title)
    }

    fun setLeftIcon(icon: Int = R.drawable.ic_action_backward){
        if(icon == 0)
            (activity as BaseActivity).setLeftIcon(android.R.color.transparent)
        else
            (activity as BaseActivity).setLeftIcon(icon)
    }

    fun setLeftIconVisibilty(isVisible: Boolean = true){
        (activity as BaseActivity).setLeftIconVisibility(isVisible)
    }

    fun setRightIcon(icon: Int = R.drawable.ic_action_backward){
        if(icon == 0)
            (activity as BaseActivity).setRightIcon(android.R.color.transparent)
        else
            (activity as BaseActivity).setRightIcon(icon)
    }

    fun setRightIconVisibilty(isVisible: Boolean = true){
        (activity as BaseActivity).setRightIconVisibility(isVisible)
    }

    fun getPermission(permissionList: List<String>, isPermissionGrantedInterface: IsPermissionGrantedInterface) {
        (activity as BaseActivity).getPermission(permissionList, isPermissionGrantedInterface)
    }

    private fun observeLeftIconClick() {
        getToolBarViewModel()?.leftIconClicked?.observe(this, Observer<Int> {  id ->
            activity?.onBackPressed()
        })
    }

}
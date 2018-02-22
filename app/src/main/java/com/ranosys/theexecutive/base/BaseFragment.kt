package com.ranosys.theexecutive.base

import android.app.Dialog
import android.arch.lifecycle.LifecycleFragment
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.rtp.IsPermissionGrantedInterface
import com.ranosys.theexecutive.utils.Utils


/**
 * Created by Mohammad Sunny on 24/1/18.
 */
abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel> : LifecycleFragment() {

    private var mContext : Context? = null
    private var mViewDataBinding: T? = null
    private var mViewModel: V? = null
    private var mProgressDialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContext = getActivity()
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        mViewModel = mViewModel ?: getViewModel()
        mViewDataBinding?.setVariable(getBindingVariable(), mViewModel)
        mViewDataBinding?.executePendingBindings()
        return mViewDataBinding?.getRoot()
    }

    fun showLoading() {
        mProgressDialog = Utils.showDialog(mContext)
    }

    fun hideLoading() {
        if (mProgressDialog?.isShowing()!!) {
            mProgressDialog?.cancel()
        }
    }


    fun setTitle(){
        val titleId = getTitle() ?: "Sample MVVM"
        (activity as BaseActivity).setScreenTitle(titleId)
    }

    abstract fun getTitle() : String?

    /**
     * @return layout resource id
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract fun getBindingVariable(): Int


    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract fun getViewModel(): V

    fun getViewDataBinding() : T?{
        return mViewDataBinding
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
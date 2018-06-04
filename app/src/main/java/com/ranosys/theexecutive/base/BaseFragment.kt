package com.ranosys.theexecutive.base

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.webkit.*
import android.widget.RelativeLayout
import android.widget.Toast
import com.ranosys.rtp.IsPermissionGrantedInterface
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.activities.ToolbarViewModel
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.web_pages_layout.*


/**
 * @Details Base class for all fragments
 * @Author Ranosys Technologies
 * @Date 22,Feb,2018
 */
abstract class BaseFragment : LifecycleFragment() {

    private var mContext : Context? = null
    private var mProgressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity
        observeLeftIconClick()
    }

    fun showLoading() {
        if(null == mProgressDialog || mProgressDialog?.isShowing?.not()!!){
            mProgressDialog = Utils.showProgressDialog(mContext)
        }

    }

    fun hideLoading() {
        mProgressDialog?.run {
            if(isShowing){
                cancel()
            }
        }
    }

    fun setToolBarParams(title: String?, titleBackground : Int?, subTitle: String?, leftIcon : Int?, leftIconVisibility : Boolean,
                         rightIcon : Int?, rightIconVisibility : Boolean, showLogo: Boolean = false){
        setTitle(title)
        setTitleBackground(titleBackground)
        setSubTitle(subTitle)
        setLeftIcon(leftIcon)
        setLeftIconVisibilty(leftIconVisibility)
        setRightIcon(rightIcon)
        setRightIconVisibilty(rightIconVisibility)
        setShowLogo(showLogo)

    }

    private fun setShowLogo(showLogo: Boolean) {
        (activity as BaseActivity).setShowLogo(showLogo)
    }

    protected fun getToolBarViewModel() : ToolbarViewModel?{
        return  (activity as BaseActivity).toolbarViewModel
    }

    fun setTitle(title: String? = getString(R.string.app_name)){
        (activity as BaseActivity).setScreenTitle(title)
    }

    fun setTitleBackground(background: Int? = 0){
        if(background == 0)
            (activity as BaseActivity).setTitleBackground(android.R.color.transparent)
        else
            (activity as BaseActivity).setTitleBackground(background)
    }

    fun setSubTitle(subTitle: String?){
        (activity as BaseActivity).setSubTitle(subTitle)
    }

    fun setLeftIcon(icon: Int? = R.drawable.ic_action_backward){
        if(icon == 0)
            (activity as BaseActivity).setLeftIcon(android.R.color.transparent)
        else
            (activity as BaseActivity).setLeftIcon(icon)
    }

    fun setLeftIconVisibilty(isVisible: Boolean = true){
        (activity as BaseActivity).setLeftIconVisibility(isVisible)
    }

    fun setRightIcon(icon: Int? = R.drawable.ic_action_backward){
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
            when(id) {
                R.id.toolbar_left_icon -> {
                    getToolBarViewModel()?.leftIconClicked?.value = null
                    (activity as BaseActivity).onBackPressed()
                }

                R.id.toolbar_right_icon -> {
                    getToolBarViewModel()?.rightIconClicked?.value = null
                    (activity as BaseActivity).onBackPressed()
                }
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun prepareWebPageDialog(context : Context?, url : String?, title : String?) {
        val webPagesDialog = Dialog(context, R.style.Animation_Design_BottomSheetDialog)
        webPagesDialog.setContentView(R.layout.web_pages_layout)
        webPagesDialog.setCancelable(true)
        webPagesDialog.window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT /*+ rl_add_to_box.height*/)
        webPagesDialog.window.setGravity(Gravity.BOTTOM)
        webPagesDialog.tv_web_title.text = title
        webPagesDialog.webview.settings.javaScriptEnabled = true // enable javascript
        webPagesDialog.webview.settings.defaultZoom = WebSettings.ZoomDensity.FAR
        webPagesDialog.webview.settings.builtInZoomControls = true
        webPagesDialog.webview.settings.displayZoomControls = false
        webPagesDialog.webview.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show()
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            override fun onReceivedError(view: WebView, req: WebResourceRequest, rerr: WebResourceError) {
                onReceivedError(view, rerr.errorCode, rerr.description.toString(), req.url.toString())
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                showLoading()
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                hideLoading()
                super.onPageFinished(view, url)
            }
        }

        webPagesDialog.webview.loadUrl(url)
        webPagesDialog.img_back.setOnClickListener { webPagesDialog.dismiss() }
        webPagesDialog.show()
    }


}
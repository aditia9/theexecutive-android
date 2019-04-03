package com.delamibrands.theexecutive.activities

import AppLog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import com.delamibrands.theexecutive.R
import com.ranosys.dochelper.MediaHelperActivity

import com.delamibrands.theexecutive.api.AppRepository
import com.delamibrands.theexecutive.api.interfaces.ApiCallback
import com.delamibrands.theexecutive.base.BaseActivity
import com.delamibrands.theexecutive.base.BaseFragment
import com.delamibrands.theexecutive.databinding.ActivityDashboardBinding
import com.delamibrands.theexecutive.modules.addressBook.AddressBookFragment
import com.delamibrands.theexecutive.modules.bankTransfer.BankTransferFragment
import com.delamibrands.theexecutive.modules.changeLanguage.ChangeLanguageFragment
import com.delamibrands.theexecutive.modules.checkout.CheckoutFragment
import com.delamibrands.theexecutive.modules.checkout.OrderResultFragment
import com.delamibrands.theexecutive.modules.home.HomeFragment
import com.delamibrands.theexecutive.modules.home.HomeViewPager
import com.delamibrands.theexecutive.modules.login.LoginFragment
import com.delamibrands.theexecutive.modules.myAccount.MyAccountFragment
import com.delamibrands.theexecutive.modules.notification.NotificationFragment
import com.delamibrands.theexecutive.modules.notification.dataclasses.NotificationChangeStatusRequest
import com.delamibrands.theexecutive.modules.order.orderDetail.OrderDetailFragment
import com.delamibrands.theexecutive.modules.order.orderList.OrderListFragment
import com.delamibrands.theexecutive.modules.productDetail.ProductDetailFragment
import com.delamibrands.theexecutive.modules.productListing.ProductListingFragment
import com.delamibrands.theexecutive.modules.settings.SettingsFragment
import com.delamibrands.theexecutive.modules.shoppingBag.ShoppingBagFragment
import com.delamibrands.theexecutive.utils.Constants
import com.delamibrands.theexecutive.utils.FragmentUtils
import com.delamibrands.theexecutive.utils.SavedPreferences
import com.delamibrands.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * @Details Dashboard screen for an app
 * @Author Ranosys Technologies
 * @Date 19,Mar,2018
 */
class DashBoardActivity : BaseActivity() {

    lateinit var toolbarBinding: ActivityDashboardBinding
    private var mediaPicker: MediaHelperActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        toolbarBinding.toolbarViewModel = toolbarViewModel

        val intent = intent
        val bundle = intent.extras

        val model = ViewModelProviders.of(this).get(DashBoardViewModel::class.java)
        model.manageFragments().observe(this, Observer { isCreated ->
            if (isCreated!!) {
                if (TextUtils.isEmpty(SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY))) {
                    FragmentUtils.addFragment(this, ChangeLanguageFragment(), null, ChangeLanguageFragment::class.java.name, false)
                    if(null != bundle && bundle.getString(Constants.KEY_REDIRECTION_TYPE).isNullOrEmpty().not()){
                        dataFromPreviousPage()
                    }
                } else {
                    FragmentUtils.addFragment(this, HomeFragment(), null, HomeFragment::class.java.name, true)
                    if(null != bundle && bundle.getString(Constants.KEY_REDIRECTION_TYPE).isNullOrEmpty().not()){
                        dataFromPreviousPage()
                    }
                }
            }

        })

        supportFragmentManager.addOnBackStackChangedListener(object : FragmentManager.OnBackStackChangedListener {
            override fun onBackStackChanged() {
                val backStackCount = supportFragmentManager.backStackEntryCount
                if (backStackCount > 0) {
                    val fragment = FragmentUtils.getCurrentFragment(this@DashBoardActivity)
                    fragment?.run {
                        if (fragment is HomeFragment) {
                            when (HomeFragment.fragmentPosition) {
                                0 -> {
                                    (fragment as BaseFragment).setToolBarParams("", R.drawable.logo, "", 0, false, R.drawable.bag, true, true)
                                }
                                1 -> {
                                    val isLogin = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
                                    if(TextUtils.isEmpty(isLogin)){
                                        (fragment as BaseFragment).setToolBarParams(getString(R.string.login), 0, "", R.drawable.cancel, true, 0, false, true)
                                    }else{
                                        val email = SavedPreferences.getInstance()?.getStringValue(Constants.USER_EMAIL)
                                        (fragment as BaseFragment).setToolBarParams(getString(R.string.my_account_title), 0, email, 0, false, 0, false)

                                        val adapter = (fragment.viewpager.adapter as HomeViewPager)
                                        if(adapter != null){
                                            val frag = adapter.getItemAt(1)
                                            if(frag is MyAccountFragment){
                                                frag.notifyNotificationCount()
                                            }
                                        }
                                    }
                                }
                                2 -> {
                                    (fragment as BaseFragment).setToolBarParams(getString(R.string.wishlist), 0, "", R.drawable.back, true, 0, false)
                                }
                            }
                        }
                        if (fragment is ProductListingFragment)
                            (fragment as BaseFragment).setToolBarParams(ProductListingFragment.categoryName, 0, "", R.drawable.back, true, R.drawable.bag, true)
                        if (fragment is LoginFragment) {
                            (fragment as BaseFragment).setToolBarParams(getString(R.string.login), 0, "",  R.drawable.cancel, true, 0, false, true) }
                        if (fragment is NotificationFragment) {
                            (fragment as BaseFragment).setToolBarParams(getString(R.string.notifications), 0, "", R.drawable.back, true, 0, false)
                            fragment.getNotification()
                        }
                        (fragment as? ProductDetailFragment)?.onResume()
                        (fragment as? AddressBookFragment)?.setToolbarAndCallAddressApi()
                        (fragment as? ShoppingBagFragment)?.onResume()
                        (fragment as? CheckoutFragment)?.onResume()
                        (fragment as? OrderListFragment)?.onResume()
                        (fragment as? OrderResultFragment)?.onResume()
                        (fragment as? OrderDetailFragment)?.onResume()
                        (fragment as? LoginFragment)?.onResume()
                        (fragment as? SettingsFragment)?.onResume()

                        //refresh/load shopping bag list
                        if(fragment is ShoppingBagFragment){
                            fragment.getShoppingBag()
                        }
                    }
                }
            }
        })

    }


    private fun dataFromPreviousPage() {
        val extras = intent.extras

        val redirectType = extras.getString(Constants.KEY_REDIRECTION_TYPE)
        val redirectValue = extras.getString(Constants.KEY_REDIRECTION_VALUE)
        val redirectTitle = extras.getString(Constants.KEY_REDIRECTION_TITLE)
        val notificationId = extras.getString(Constants.KEY_NOTIFICATION_ID)
        //TODO("FOR RESOLVE NOTIFICATION ISSUE")
        val fromNotification = extras.getString(Constants.FROM_NOTIFICATION)

        if (!TextUtils.isEmpty(redirectType)) {
            val request = NotificationChangeStatusRequest(notificationId,
                    SavedPreferences.getInstance()?.getStringValue(Constants.ANDROID_DEVICE_ID_KEY), SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY))
            changeNotificationStatus(request)

            when (redirectType) {
                Constants.NOTIFICATION_TYPE_PRODUCT_DETAIL -> {
                    val fragment = ProductDetailFragment.getInstance(null, redirectValue, redirectTitle, 0)
                    FragmentUtils.addFragment(this, fragment, null, ProductDetailFragment::class.java.name, true)
                }

                Constants.NOTIFICATION_TYPE_CATALOG -> {
                    val bundle = Bundle()
                    bundle.putInt(Constants.CATEGORY_ID, redirectValue.toInt())
                    bundle.putString(Constants.CATEGORY_NAME, redirectTitle)
                    FragmentUtils.addFragment(this, ProductListingFragment(), bundle, ProductListingFragment::class.java.name, true)
                }

                Constants.NOTIFICATION_TYPE_ORDER_LIST -> {
                    val bundle = Bundle()
                    bundle.putString(Constants.ORDER_ID, redirectValue)
                    FragmentUtils.addFragment(this, OrderDetailFragment(), bundle, OrderDetailFragment::class.java.name, true)
                }

                Constants.NOTIFICATION_TYPE_ABANDONED -> {
                    val isLogin = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
                    if(!TextUtils.isEmpty(isLogin)){
                        FragmentUtils.addFragment(this, ShoppingBagFragment(), null, ShoppingBagFragment::class.java.name, true)
                    }
                }

                else -> {
                    FragmentUtils.addFragment(this, OrderDetailFragment(), null, NotificationFragment::class.java.name, true)
                }
            }
        }
    }


    fun initMediaPicker() : MediaHelperActivity{
        mediaPicker = MediaHelperActivity(this)
        return mediaPicker as MediaHelperActivity

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val fragment = FragmentUtils.getCurrentFragment(this@DashBoardActivity)

        if(fragment is BankTransferFragment){
            initMediaPicker().onCallbackResult(requestCode,resultCode, data)
            fragment.onActivityResult(requestCode,resultCode, data)
        }else if(fragment is HomeFragment){
            if(HomeFragment.fragmentPosition == 1) {
                val isLogin = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
                if(TextUtils.isEmpty(isLogin)){
                    for (i in fragment.childFragmentManager.fragments.indices) {
                       if(fragment.childFragmentManager.fragments[i] is LoginFragment){
                           (fragment.childFragmentManager.fragments[i] as LoginFragment).onActivityResult(requestCode, resultCode, data!!)
                       }
                    }
                }
            }
        }else if(fragment is LoginFragment){
            fragment.onActivityResult(requestCode, resultCode, data!!)
        }
    }


    private fun changeNotificationStatus(request: NotificationChangeStatusRequest) {
        if (Utils.isConnectionAvailable(this)) {
            AppRepository.changeNotificationStatus(request, object : ApiCallback<Boolean> {
                override fun onException(error: Throwable) {
                    AppLog.d(error.message!!)
                }

                override fun onError(errorMsg: String) {
                    AppLog.d(errorMsg)
                }

                override fun onSuccess(t: Boolean?) {
                  AppLog.d(""+t)
                }
            })
        } else {
            Utils.showNetworkErrorDialog(this)
        }
    }
}

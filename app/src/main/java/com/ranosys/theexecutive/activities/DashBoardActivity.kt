package com.ranosys.theexecutive.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.ActivityDashboardBinding
import com.ranosys.theexecutive.modules.addressBook.AddressBookFragment
import com.ranosys.theexecutive.modules.changeLanguage.ChangeLanguageFragment
import com.ranosys.theexecutive.modules.checkout.CheckoutFragment
import com.ranosys.theexecutive.modules.home.HomeFragment
import com.ranosys.theexecutive.modules.login.LoginFragment
import com.ranosys.theexecutive.modules.productDetail.ProductDetailFragment
import com.ranosys.theexecutive.modules.productListing.ProductListingFragment
import com.ranosys.theexecutive.modules.shoppingBag.ShoppingBagFragment
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.SavedPreferences

/**
 * @Details Dashboard screen for an app
 * @Author Ranosys Technologies
 * @Date 19,Mar,2018
 */
class DashBoardActivity : BaseActivity() {

    lateinit var toolbarBinding: ActivityDashboardBinding

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
                    if(bundle.getString(Constants.KEY_REDIRECTION_TYPE).isNullOrEmpty().not()){
                        dataFromPreviousPage()
                    }
                } else {
                    FragmentUtils.addFragment(this, HomeFragment(), null, HomeFragment::class.java.name, true)
                    if(bundle.getString(Constants.KEY_REDIRECTION_TYPE).isNullOrEmpty().not()){
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
                                    }
                                }
                                2 -> {
                                    (fragment as BaseFragment).setToolBarParams(getString(R.string.wishlist), 0, "", R.drawable.back, true, 0, false)
                                }
                            }
                        }

                        if(fragment is ProductListingFragment){
                            (fragment as BaseFragment).setToolBarParams(ProductListingFragment.categoryName, 0, "", R.drawable.back, true, R.drawable.bag, true ) }
                        if(fragment is LoginFragment) {
                            (fragment as BaseFragment).setToolBarParams(getString(R.string.login),0, "", 0,false, 0, false, true) }
                        (fragment as? ProductDetailFragment)?.onResume()
                        (fragment as? AddressBookFragment)?.onResume()
                        (fragment as? ShoppingBagFragment)?.onResume()
                        (fragment as? CheckoutFragment)?.onResume()

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

        if (!TextUtils.isEmpty(redirectType)) {
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
                    //ToDo Redirect to order list
                }

                else -> {
                    //ToDo Redirect to notification list
                }
            }
        }
    }

}

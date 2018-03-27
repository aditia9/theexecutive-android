package com.ranosys.theexecutive.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.TextUtils
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.databinding.ActivityDashboardBinding
import com.ranosys.theexecutive.modules.myAccount.ChangeLanguageFragment
import com.ranosys.theexecutive.modules.productListing.ProductListingFragment
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.SavedPreferences

/**
 * Created by Mohammad Sunny on 19/2/18.
 */
class DashBoardActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbarBinding : ActivityDashboardBinding? = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        toolbarBinding?.toolbarViewModel = toolbarViewModel
        if(TextUtils.isEmpty(SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY))){
            //redirect user to Home fragment with selected store enm is store api fails
//            if(null == GlobalSingelton.instance?.storeList || GlobalSingelton.instance?.storeList!!.size == 0){
//                SavedPreferences.getInstance()?.saveStringValue(Constants.DEFAULT_STORE_CODE, Constants.SELECTED_STORE_CODE_KEY)
//                FragmentUtils.addFragment(this, ProductListingFragment(), ProductListingFragment::class.java.name)
//            }
            FragmentUtils.addFragment(this, ChangeLanguageFragment(), ChangeLanguageFragment::class.java.name)

        }else{
            FragmentUtils.addFragment(this, ProductListingFragment(), ProductListingFragment::class.java.name)
        }
    }

}

package com.ranosys.theexecutive.modules.home
import AppLog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentHomeBinding
import com.ranosys.theexecutive.modules.notification.dataclasses.DeviceRegisterRequest
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * @Details A fragment for home
 * @Author Ranosys Technologies
 * @Date 19,Mar,2018
 */
class HomeFragment : BaseFragment() {

    lateinit var br: BroadcastReceiver

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentHomeBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        mViewDataBinding?.executePendingBindings()

        //recieve local broadcast receiver
        br = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                try{
                    setPagerAdapter()
                }catch (exception : Exception){

                }
            }

        }

        LocalBroadcastManager.getInstance(activity as Context).registerReceiver(br, IntentFilter("LOGIN"))
        return mViewDataBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBarParams("", R.drawable.logo, "", 0,false, R.drawable.bag, true, true )
        try{
            setPagerAdapter()
        }catch (exception : Exception){

        }

        //initialize Zendesk chat setup
        Utils.setUpZendeskChat()

        registerDeviceOnServer()

    }

    private fun setPagerAdapter(){
        val homeViewPager = HomeViewPager(childFragmentManager)
        viewpager.setPagingEnabled(false)
        viewpager.adapter = homeViewPager
        viewpager.offscreenPageLimit = 0
        tabLayout.setupWithViewPager(viewpager)
        createTabIcons()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> {
                        elv_parent_category.smoothScrollToPosition(0)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.run {
                    val view = tab?.customView as TextView
                    view.setTextColor(ContextCompat.getColor(activity as Context,R.color.theme_accent_color))
                    view.setTypeface(null, Typeface.NORMAL)
                    when(tab.position){
                        0 ->{
                            view.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.home, 0, 0)
                        }
                        1 ->{
                            view.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.my_account, 0, 0)
                        }
                        2 ->{
                            view.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.wishlist, 0, 0)
                        }
                    }
                }

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.customView?.run {
                    val view = tab?.customView as TextView
                    view.setTextColor(ContextCompat.getColor(activity as Context,R.color.theme_black_color))
                    view.setTypeface(null, Typeface.BOLD)
                    when(tab.position) {
                        0 -> {
                            view.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.home_dark, 0, 0)
                        }
                        1 -> {
                            view.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.my_account_dark, 0, 0)
                        }
                        2 -> {
                            view.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.wishlist_dark, 0, 0)
                        }
                    }
                }

            }
        })

        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> {
                        Utils.hideSoftKeypad(activity as Context)
                        fragmentPosition = 0
                        tabLayout.visibility = View.VISIBLE
                        setToolBarParams("", R.drawable.logo, "", 0, false, R.drawable.bag, true, true)
                    }
                    1 -> {
                        Utils.hideSoftKeypad(activity as Context)
                        elv_parent_category.smoothScrollToPosition(0)
                        fragmentPosition = 1
                        val isLogin = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
                        if(TextUtils.isEmpty(isLogin)){
                            tabLayout.visibility = View.GONE
                            setToolBarParams(getString(R.string.login), 0, "", R.drawable.cancel, true, 0, false, true)
                        }
                        else{
                            tabLayout.visibility = View.VISIBLE
                            val email = SavedPreferences.getInstance()?.getStringValue(Constants.USER_EMAIL)
                            setToolBarParams(getString(R.string.my_account_title), 0, email, 0, false, 0, false)
                        }
                    }
                    2 -> {
                        Utils.hideSoftKeypad(activity as Context)
                        elv_parent_category.smoothScrollToPosition(0)
                        fragmentPosition = 2
                        tabLayout.visibility = View.VISIBLE
                        setToolBarParams(getString(R.string.wishlist), 0, "", R.drawable.back, true, 0, false)
                    }
                }
            }
        })
    }

    private fun createTabIcons() {
        val tabOne = LayoutInflater.from(activity).inflate(R.layout.custom_tab, null) as TextView
        tabOne.text = getString(R.string.home)
        fragmentPosition = 0
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.home_dark, 0, 0)
        tabOne.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        tabLayout.getTabAt(0)?.customView = tabOne
        val tabTwo = LayoutInflater.from(activity).inflate(R.layout.custom_tab, null) as TextView
        tabTwo.text = getString(R.string.my_account)
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.my_account, 0, 0)
        tabLayout.getTabAt(1)?.customView = tabTwo
        val tabThree = LayoutInflater.from(activity).inflate(R.layout.custom_tab, null) as TextView
        tabThree.text = getString(R.string.wishlist)
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.wishlist, 0, 0)
        tabLayout.getTabAt(2)?.customView = tabThree
    }

    companion object {
        var fragmentPosition : Int? = null
    }


    private fun registerDeviceOnServer() {
        val request = DeviceRegisterRequest(Constants.OS_TYPE,
                SavedPreferences.getInstance()?.getStringValue(Constants.USER_FCM_ID),
                SavedPreferences.getInstance()?.getStringValue(Constants.ANDROID_DEVICE_ID_KEY))

        AppRepository.registerDevice(request, object : ApiCallback<Boolean> {
            override fun onSuccess(t: Boolean?) {
                AppLog.d(t.toString())
            }

            override fun onException(error: Throwable) {
                AppLog.d(error.message!!)
            }

            override fun onError(errorMsg: String) {
                AppLog.d(errorMsg)
            }
        })
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(activity as Context).registerReceiver(br, IntentFilter("LOGIN"))
        super.onDestroy()
    }
}
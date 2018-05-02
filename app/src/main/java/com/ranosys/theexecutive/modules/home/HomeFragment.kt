package com.ranosys.theexecutive.modules.home
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.FacebookSdk.getApplicationContext
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentHomeBinding
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import com.zopim.android.sdk.prechat.ZopimChatActivity
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * Created by Mohammad Sunny on 19/3/18.
 */
class HomeFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentHomeBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        mViewDataBinding?.executePendingBindings()
        return mViewDataBinding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPagerAdapter()

        tv_chat.setOnClickListener {
            startActivity(Intent(getApplicationContext(), ZopimChatActivity::class.java))
        }
    }
    private fun setPagerAdapter(){
        val homeViewPager = HomeViewPager(childFragmentManager)
        viewpager.setPagingEnabled(false)
        viewpager.adapter = homeViewPager
        viewpager.offscreenPageLimit = 2
        tabLayout.setupWithViewPager(viewpager)
        createTabIcons()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
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
            override fun onTabSelected(tab: TabLayout.Tab?) {
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
        })
        viewpager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> {
                        fragmentPosition = 0
                        tabLayout.visibility = View.VISIBLE
                        tv_chat.visibility = View.VISIBLE
                        setToolBarParams("", R.drawable.logo, "", 0, false, R.drawable.bag, true, true)
                    }
                    1 -> {
                        fragmentPosition = 1
                        tv_chat.visibility = View.GONE
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
                        fragmentPosition = 2
                        tabLayout.visibility = View.VISIBLE
                        tv_chat.visibility = View.GONE
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
}
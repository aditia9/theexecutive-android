package com.ranosys.theexecutive.modules.home

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentHomeBinding
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
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val view = tab?.customView as TextView
                view.setTextColor(resources.getColor(R.color.theme_black_color))
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
                view.setTextColor(resources.getColor(R.color.white))
                   when(tab.position) {
                      0 -> {
                          view.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.home_white, 0, 0)
                      }
                      1 -> {
                          view.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.my_account_white, 0, 0)
                      }
                      2 -> {
                          view.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.wishlist_white, 0, 0)
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
                        setToolBarParams(getString(R.string.app_title),0, false, R.drawable.bag, true )
                    }
                    1 -> {
                        setToolBarParams(getString(R.string.login), R.drawable.back, true, 0, false )
                    }
                    2 -> {
                        setToolBarParams(getString(R.string.my_wishlist),0, false, 0, false )
                    }
                }
            }


        })
    }

    private fun createTabIcons() {

        val tabOne = LayoutInflater.from(activity).inflate(R.layout.custom_tab, null) as TextView
        tabOne.text = getString(R.string.home)
        tabOne.setTextColor(resources.getColor(R.color.white))
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.home_white, 0, 0)
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
}
package com.ranosys.theexecutive.modules.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.design.widget.BottomNavigationView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentHomeBinding
import com.ranosys.theexecutive.modules.login.LoginFragment
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * Created by Mohammad Sunny on 21/2/18.
 */
class HomeFragment : BaseFragment() {

    private var homeModelView: HomeModelView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentHomeBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        homeModelView = ViewModelProviders.of(this).get(HomeModelView::class.java)
        mViewDataBinding?.homeViewModel = homeModelView
        mViewDataBinding?.executePendingBindings()
        return mViewDataBinding?.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        elv_parent_category.setOnGroupExpandListener(object : ExpandableListView.OnGroupExpandListener{
            var previousGroup = -1
            override fun onGroupExpand(p0: Int) {
                if(p0 != previousGroup){
                    elv_parent_category.collapseGroup(previousGroup)
                }
                previousGroup = p0

            }

        })

        bottom_navigation.setOnNavigationItemSelectedListener(
                object : BottomNavigationView.OnNavigationItemSelectedListener {
                    override
                    fun onNavigationItemSelected(item: MenuItem): Boolean {
                        when (item.itemId) {
                            R.id.action_home -> {

                            }
                            R.id.action_my_account -> {
                                FragmentUtils.replaceFragment(activity, LoginFragment.newInstance(), LoginFragment::class.java.name)
                            }
                            R.id.action_wishlist -> {

                            }
                        }
                        return true
                    }
                });

        observeLoginApiResponse()
        getCategories()
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.app_title),0, false, 0, false )
    }

    private fun getCategories() {
        homeModelView?.getCategories()
    }

    private fun observeLoginApiResponse() {
        homeModelView?.mutualHomeResponse?.observe(this, object : Observer<ApiResponse<HomeResponseDataClass>> {
            override fun onChanged(@Nullable apiResponse: ApiResponse<HomeResponseDataClass>?) {
                // hideLoading()
                val response = apiResponse?.apiResponse ?: apiResponse?.error
                if (response is HomeResponseDataClass) {
                    homeModelView?.homeResponse?.set(response)
                } else {
                    Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

}

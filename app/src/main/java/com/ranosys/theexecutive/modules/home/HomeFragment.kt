package com.ranosys.theexecutive.modules.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentHomeBinding

/**
 * Created by Mohammad Sunny on 2/2/18.
 */
class HomeFragment : BaseFragment() {

    var homeModelView: HomeModelView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentHomeBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        homeModelView = ViewModelProviders.of(this).get(HomeModelView::class.java)
        mViewDataBinding?.homeViewModel = homeModelView
        mViewDataBinding?.executePendingBindings()
        return mViewDataBinding?.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLoginApiResponse()
        getCategories()
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.title_home), R.drawable.ic_action_backward, true, 0, false )
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
                    response.children_data
                    homeModelView?.categoryList?.set(0, response.children_data as ChildrenData?)
                    Toast.makeText(activity, "Got categories", Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
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

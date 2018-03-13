package com.ranosys.theexecutive.modules.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ExpandableListView
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*


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
        elv_parent_category.setOnGroupExpandListener(object : ExpandableListView.OnGroupExpandListener{
            var previousGroup = -1
            override fun onGroupExpand(p0: Int) {
                if(p0 != previousGroup){
                    elv_parent_category.collapseGroup(previousGroup)
                }
                previousGroup = p0

            }

        })
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
                    homeModelView?.homeResponse?.set(response)
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

    fun expand(v: View) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.layoutParams.height = if (interpolatedTime == 1f)
                    ViewGroup.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }
}

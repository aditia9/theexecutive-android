package com.ranosys.theexecutive.modules.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentHomeBinding

/**
 * Created by Mohammad Sunny on 21/2/18.
 */
class HomeFragment : BaseFragment() {

    private var homeModelView: HomeModelView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentHomeBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        homeModelView = ViewModelProviders.of(this).get(HomeModelView::class.java)
        mViewDataBinding?.mainfragmentviewmodel = homeModelView
        mViewDataBinding?.executePendingBindings()
        observeButtonClicks()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.title_home), R.drawable.ic_action_backward, true, 0, false )
    }

    private fun observeButtonClicks() {
        homeModelView?.buttonClicked?.observe(this, Observer<HomeDataClass.HomeUserData>{ userData ->
        })
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}

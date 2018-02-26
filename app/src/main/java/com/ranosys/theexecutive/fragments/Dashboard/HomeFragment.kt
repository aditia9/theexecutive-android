package com.ranosys.theexecutive.fragments.Dashboard

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ranosys.theexecutive.BR
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.UserActivity
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentHomeBinding
import com.ranosys.theexecutive.utils.SavedPreferences
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * Created by Mohammad Sunny on 2/2/18.
 */
class HomeFragment : BaseFragment() {

    var homeModelView: HomeModelView? = null

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentHomeBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        homeModelView = ViewModelProviders.of(this).get(HomeModelView::class.java!!)
        mViewDataBinding?.mainfragmentviewmodel = homeModelView
        mViewDataBinding?.executePendingBindings()
        observeButtonClicks()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.title_home))
    }

    private fun observeButtonClicks() {
        homeModelView?.buttonClicked?.observe(this, Observer<HomeDataClass.HomeUserData>{ userData ->
        })
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_items.layoutManager = LinearLayoutManager(activity)
        recycler_items.adapter = RecyclerAdapter(object : RecyclerAdapter.OnItemClickListener {
            override fun onItemClick(item: HomeDataClass.HomeUserData, position: Int) {
                when(position){
                    0->{
                        Toast.makeText(activity, item.title + " " + position, Toast.LENGTH_SHORT).show()
                    }
                    1-> {
                        Toast.makeText(activity, item.title + " " + position, Toast.LENGTH_SHORT).show()

                    }
                    2->{
                        Toast.makeText(activity, item.title + " " + position, Toast.LENGTH_SHORT).show()

                    }
                    4->{
                        val prefInstance = SavedPreferences.getInstance()
                        prefInstance?.setIsLogin(false)
                        prefInstance?.storeUserEmail("")
                        var signoutIntent = Intent(activity, UserActivity::class.java)
                        startActivity(signoutIntent)
                        activity.finish()

                    }
                }
            }
        })
    }

}
package com.ranosys.theexecutive.modules.myAccount

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentNewsLetterBinding
import kotlinx.android.synthetic.main.fragment_news_letter.*

/**
 * Created by nikhil on 23/3/18.
 */
class NewsLetterFragment: BaseFragment() {
    private lateinit var mBinding: FragmentNewsLetterBinding
    private lateinit var mViewModel: NewsLetterViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_news_letter, container, false)
        mViewModel = ViewModelProviders.of(this).get(NewsLetterViewModel::class.java)
        mBinding.newsLetterVM = mViewModel

        observeApiSuccess()
        observeApiFailure()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_subscribe.setOnClickListener({
            mViewModel.callNewsLetterSubscribeApi()
        })
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.new_letter_title), 0, "", 0, false, 0 , false)
    }

    private fun observeApiFailure() {
        mViewModel.apiFailureResponse?.observe(this, Observer { msg ->
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        })
    }

    private fun observeApiSuccess() {
        mViewModel.apiSuccessResponse?.observe(this, Observer { msg ->
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        })
    }
}
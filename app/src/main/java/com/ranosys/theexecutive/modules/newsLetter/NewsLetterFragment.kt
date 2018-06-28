package com.ranosys.theexecutive.modules.newsLetter

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentNewsLetterBinding
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
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

        val isLogin = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        if(!TextUtils.isEmpty(isLogin)) {
            mViewModel.email.set(SavedPreferences.getInstance()?.getStringValue(Constants.USER_EMAIL))
        }

        mBinding.newsLetterVM = mViewModel

        observeApiSuccess()
        observeApiFailure()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_subscribe.setOnClickListener({
            Utils.hideSoftKeypad(activity as Context)
            if (Utils.isConnectionAvailable(activity as Context)) {
                if(mViewModel.validateData(activity as Context)){
                    showLoading()
                    mViewModel.callNewsLetterSubscribeApi()
                }

            } else {
                Utils.showNetworkErrorDialog(activity as Context)
            }

        })
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.new_letter_title), 0, "", R.drawable.back, true, 0 , false)
    }

    private fun observeApiFailure() {
        mViewModel.apiFailureResponse?.observe(this, Observer { msg ->
            hideLoading()
            var errorMsg = msg
            if(msg == Constants.ERROR_CODE_404.toString()){
                errorMsg = getString(R.string.error_no_user_exist)
            }
            Utils.showDialog(activity, errorMsg, getString(R.string.ok),"", null)
        })
    }

    private fun observeApiSuccess() {
        mViewModel.apiSuccessResponse?.observe(this, Observer { msg ->
            hideLoading()
            Utils.showDialog(activity, msg, getString(R.string.ok),"", null)
        })
    }
}
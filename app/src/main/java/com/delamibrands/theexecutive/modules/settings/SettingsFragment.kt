package com.delamibrands.theexecutive.modules.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_settings.view.*
import com.delamibrands.theexecutive.utils.Constants
import com.delamibrands.theexecutive.utils.SavedPreferences


class SettingsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        if(SavedPreferences.getInstance()?.getBooleanValue(Constants.IS_NOTIFICATION_SHOW)!!){
            view.sw_notification.isChecked = SavedPreferences.getInstance()?.getBooleanValue(Constants.IS_NOTIFICATION_SHOW)!!
        }

        view.sw_notification.setOnCheckedChangeListener({ buttonView, isChecked ->
            SavedPreferences.getInstance()?.setBooleanValue(Constants.IS_NOTIFICATION_SHOW, isChecked)
        })
        return view
    }


    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.settings), 0, "", R.drawable.back, true, 0, false)
    }
}
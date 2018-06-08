package com.ranosys.theexecutive.modules.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_settings.view.*
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.SavedPreferences


class SettingsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
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
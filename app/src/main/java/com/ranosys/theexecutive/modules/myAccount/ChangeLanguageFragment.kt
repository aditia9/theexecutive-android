package com.ranosys.theexecutive.modules.myAccount

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.utils.GlobalSingelton
import kotlinx.android.synthetic.main.change_language_fragment.*

/**
 * Created by nikhil on 16/3/18.
 */
class ChangeLanguageFragment: BaseFragment() {

    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.change_language_fragment, null, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(activity)
        language_list.layoutManager = linearLayoutManager
        val storeListAdapter: StoreListAdapter = StoreListAdapter(GlobalSingelton.instance?.storeList)
        language_list.adapter = storeListAdapter

    }
}
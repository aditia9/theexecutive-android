package com.ranosys.theexecutive.modules.myAccount

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.modules.splash.StoreResponse
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.GlobalSingelton
import com.ranosys.theexecutive.utils.SavedPreferences
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

        val itemDecor = DividerDecoration(resources.getDrawable(R.drawable.horozontal_divider, null))
        language_list.addItemDecoration(itemDecor)

        val selectedStoreCode: String = if(TextUtils.isEmpty(SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY))){
            Constants.DEFAULT_STORE_CODE
        }else{
            SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)!!
        }

        val storeListAdapter: StoreListAdapter = StoreListAdapter(GlobalSingelton.instance?.storeList, selectedStoreCode)
        storeListAdapter.setClickListsner(object: StoreListAdapter.OnItemClickListener {
            override fun onItemClick(item: StoreResponse) {
                storeListAdapter.selectedStore = item.code
                storeListAdapter.notifyDataSetChanged()
            }

        })
        language_list.adapter = storeListAdapter

        btn_continue.setOnClickListener {
            Toast.makeText(activity, "Lang selected", Toast.LENGTH_SHORT).show()

            //store selected language code
            SavedPreferences.getInstance()?.saveStringValue(storeListAdapter.selectedStore, Constants.SELECTED_STORE_CODE_KEY)

            //TODO - redirecrtion
        }
    }

    override fun onResume() {
        super.onResume()
        if (TextUtils.isEmpty(SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY))) {

            setToolBarParams(getString(R.string.select_lang_title), 0, false, 0, false)
        } else {
            setToolBarParams(getString(R.string.select_lang_title), R.drawable.back, true, 0, false)
        }
    }
}
package com.ranosys.theexecutive.modules.changeLanguage

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.modules.home.HomeFragment
import com.ranosys.theexecutive.modules.myAccount.DividerDecoration
import com.ranosys.theexecutive.modules.splash.StoreResponse
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.GlobalSingelton
import com.ranosys.theexecutive.utils.SavedPreferences
import kotlinx.android.synthetic.main.change_language_fragment.*

/**
 * @Details Fragment to show language selection
 * @Author Ranosys Technologies
 * @Date 16,Mar,2018
 */

class ChangeLanguageFragment: BaseFragment() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var selectedStore: StoreResponse

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.change_language_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (TextUtils.isEmpty(SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY))) {
            setToolBarParams(getString(R.string.select_lang_title), 0, "", 0, false, 0, false)
        } else {
            setToolBarParams(getString(R.string.select_lang_title), 0, "",  R.drawable.back, true, 0, false)
        }

        linearLayoutManager = LinearLayoutManager(activity as Context)
        language_list.layoutManager = linearLayoutManager

        val itemDecor = DividerDecoration(resources.getDrawable(R.drawable.horizontal_divider, null))
        language_list.addItemDecoration(itemDecor)

        val selectedStoreCode: String = if(TextUtils.isEmpty(SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY))){
            Constants.DEFAULT_STORE_CODE
        }else{
            SavedPreferences.getInstance()?.getStringValue(Constants.SELECTED_STORE_CODE_KEY)!!
        }

        selectedStore = GlobalSingelton.instance?.storeList?.first() ?: getDefaultStore()

        val storeListAdapter = StoreListAdapter(GlobalSingelton.instance?.storeList, selectedStoreCode)
        storeListAdapter.setItemClickListener(object: StoreListAdapter.OnItemClickListener {
            override fun onItemClick(item: StoreResponse) {
                storeListAdapter.selectedStoreCode = item.code
                selectedStore = item
                storeListAdapter.notifyDataSetChanged()
            }

        })
        language_list.adapter = storeListAdapter

        btn_continue.setOnClickListener {
            //store selected language code
            SavedPreferences.getInstance()?.saveStringValue(storeListAdapter.selectedStoreCode, Constants.SELECTED_STORE_CODE_KEY)
            SavedPreferences.getInstance()?.saveIntValue(selectedStore.website_id, Constants.SELECTED_WEBSITE_ID_KEY)
            SavedPreferences.getInstance()?.saveIntValue(selectedStore.id, Constants.SELECTED_STORE_ID_KEY)
            FragmentUtils.addFragment(activity as Context, HomeFragment(), null, HomeFragment::class.java.name, true)

        }
    }

    private fun getDefaultStore(): StoreResponse {
        return  StoreResponse(id = 1,
                name = Constants.DEFAULT_STORE_LANGUAGE,
                code = Constants.DEFAULT_STORE_CODE,
                store_group_id = 1,
                website_id = 1)
    }

}
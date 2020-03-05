package com.delamibrands.theexecutive.modules.category

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.activities.DashBoardActivity
import com.delamibrands.theexecutive.base.BaseFragment
import com.delamibrands.theexecutive.utils.Constants
import com.delamibrands.theexecutive.utils.FragmentUtils
import com.delamibrands.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_search.*


/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 27-Jul-2018
 */
class SearchFragment: BaseFragment(){
    lateinit var rootlayout: View
    private var keyboardVisibility = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        searchQuery = arguments?.getString(Constants.SEARCH_QUERY) ?: ""

        // Inflate the layout for this fragment
        rootlayout = inflater.inflate(R.layout.fragment_search, container, false)

        return rootlayout

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //request focus
        et_search_home.requestFocus()

        //set search query
        et_search_home.setText(searchQuery)

        //set cursor at end of text edit text
        et_search_home.setSelection(searchQuery.length)

        //open keyboard
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(et_search_home,
                InputMethodManager.SHOW_IMPLICIT)

        Handler().postDelayed({
            setTreeObserver()
        }, 500)

        if(searchQuery.isNullOrBlank() && searchQuery.isNullOrEmpty()){
            et_search_home.setText(searchQuery)
        }

        translucent_view.setOnClickListener {
            searchQueryCallback(searchQuery)
            removeFragment(searchQuery)
        }

        btn_search_action.setOnClickListener {
            searchQueryCallback(searchQuery)
            removeFragment(searchQuery)
        }

        et_search_home.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(!s.isNullOrBlank()){
                    if(keyboardVisibility.not()){
                        keyboardVisibility = true
                    }
                    et_search_home.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancel_search, 0)
                    searchQuery = s.toString()
                }else{
                    et_search_home.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

                }
            }

        })

        //listener for action on right drawable
        et_search_home.setOnTouchListener(View.OnTouchListener { _, event ->
            val drawableRight = 2
            if(event.action == MotionEvent.ACTION_UP && et_search_home.text.isNotEmpty() && et_search_home.text.isNullOrBlank().not()) {

                et_search_home.compoundDrawables[drawableRight]?.run {

                    if(event.rawX >= et_search_home.right - et_search_home.compoundDrawables[drawableRight].bounds.width()) {
                        if(et_search_home.text.isNotBlank()){
                            searchQuery = ""
                            et_search_home.setText(searchQuery)
                        }

                    }
                }
            }

            false
        })

        //listener for search on keyboard
        et_search_home.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if(v.text.toString().isEmpty().not()){
                    Utils.hideSoftKeypad(activity as Context)
                    searchQuery = v.text.toString()
                    showSearchResult(searchQuery)
                }
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun setTreeObserver() {
        rootlayout.viewTreeObserver.addOnGlobalLayoutListener {
            if (isAdded) {
                val r = Rect()
                rootlayout.getWindowVisibleDisplayFrame(r)

                var heightDiff = rootlayout.rootView.height - (r.bottom - r.top)
                if (heightDiff < 350 && keyboardVisibility) {
                    keyboardVisibility = false
                    removeFragment()
                } else {
                    keyboardVisibility = true

                }
            }
        }
    }

    private fun showSearchResult(searchQuery: String) {
        removeFragment()
        action(searchQuery)
    }

    private fun removeFragment(searchQuery: String = "") {
        activity?.run {
            Utils.hideSoftKeypad(activity as Context)
            FragmentUtils.popFragment(activity as DashBoardActivity)
        }

    }

    companion object {

        var searchQuery = ""
        var action: (String) -> Unit = {}
        var searchQueryCallback: (String) -> Unit = {}

        fun getInstance(searchQuery: String = "", searchAction: (String) -> Unit= {}, getSearchQuery: (String) -> Unit = {}): SearchFragment{
            var args: Bundle? = Bundle()

            val INSTANCE = SearchFragment()
            args?.putString(Constants.SEARCH_QUERY, searchQuery)
            INSTANCE.arguments = args

            action = searchAction
            searchQueryCallback = getSearchQuery

            return INSTANCE
        }

    }
}
package com.ranosys.theexecutive.modules.category

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentCategoryBinding
import com.ranosys.theexecutive.utils.Constants
import kotlinx.android.synthetic.main.fragment_category.*


/**
 * Created by Mohammad Sunny on 21/2/18.
 */
class CategoryFragment : BaseFragment() {

    private var categoryModelView: CategoryModelView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentCategoryBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        categoryModelView = ViewModelProviders.of(this).get(CategoryModelView::class.java)
        mViewDataBinding?.categoryViewModel = categoryModelView
        mViewDataBinding?.executePendingBindings()
        return mViewDataBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBarParams(getString(R.string.app_title),0, false, R.drawable.bag, true )
        elv_parent_category.setOnGroupExpandListener(object : ExpandableListView.OnGroupExpandListener{
            var previousGroup = -1
            override fun onGroupExpand(p0: Int) {
                if(p0 != previousGroup){
                    elv_parent_category.collapseGroup(previousGroup)
                }
                previousGroup = p0

            }

        })

        observeCategoryApiResponse()
        observeAllCategoryDataApiResponse()
        getCategories()
    }

    private fun getCategories() {
        categoryModelView?.getCategories()
    }

    private fun getAllCategoriesData(queryMap : HashMap<String,String>?) {
        categoryModelView?.getAllCategoriesData(queryMap)
    }

    private fun observeCategoryApiResponse() {
        categoryModelView?.mutualHomeResponse?.observe(this, object : Observer<ApiResponse<CategoryResponseDataClass>> {
            override fun onChanged(@Nullable apiResponse: ApiResponse<CategoryResponseDataClass>?) {
                // hideLoading()
                val response = apiResponse?.apiResponse ?: apiResponse?.error
                if (response is CategoryResponseDataClass) {
                    categoryModelView?.categoryResponse?.set(response)
                   // getAllCategoriesData(getQueryMap(response.children_data))
                } else {
                    Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun observeAllCategoryDataApiResponse() {
        categoryModelView?.allCategoryDataResponse?.observe(this, object : Observer<ApiResponse<AllCategoryDataResponse>> {
            override fun onChanged(@Nullable apiResponse: ApiResponse<AllCategoryDataResponse>?) {
                // hideLoading()
                val response = apiResponse?.apiResponse ?: apiResponse?.error
                if (response is AllCategoryDataResponse) {

                } else {
                    Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun getQueryMap(childrenDataList: ArrayList<ChildrenData>?): HashMap<String, String> {

        val queryMap = HashMap<String, String>()

        queryMap.put("searchCriteria[filterGroups][0][filters][0][field]", "entity_id")
        queryMap.put("searchCriteria[filterGroups][0][filters][0][[conditionType]", "in")

        if (childrenDataList!!.size > 0) {

            val childrenDataListSize = childrenDataList.size
            val categoryArray = StringBuilder()

            for (k in 0 until childrenDataListSize) {

                if(childrenDataList.get(k).is_active!!){
                    categoryArray.append(childrenDataList.get(k).id)
                }
            }
            queryMap.put("searchCriteria[filterGroups][0][filters][0][[conditionType]", categoryArray.toString())

        }

        return queryMap
    }

}

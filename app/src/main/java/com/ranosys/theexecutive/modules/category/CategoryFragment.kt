package com.ranosys.theexecutive.modules.category

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.Nullable
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ExpandableListView
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentCategoryBinding
import com.ranosys.theexecutive.databinding.HomeViewPagerBinding
import com.ranosys.theexecutive.modules.productListing.ProductListingFragment
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.SavedPreferences
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.home_view_pager.view.*


/**
 * Created by Mohammad Sunny on 21/2/18.
 */
class CategoryFragment : BaseFragment() {

    var categoryModelView: CategoryModelView? = null
    var handler = Handler(Looper.getMainLooper())
    lateinit var viewPager : ViewPager


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentCategoryBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        categoryModelView = ViewModelProviders.of(this).get(CategoryModelView::class.java)
        mViewDataBinding?.categoryViewModel = categoryModelView
        mViewDataBinding?.executePendingBindings()
        return mViewDataBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBarParams("", R.drawable.logo, "", 0,false, R.drawable.bag, true )

        val inflater = LayoutInflater.from(context)
        val promotionBinding : HomeViewPagerBinding? = DataBindingUtil.inflate(inflater, R.layout.home_view_pager, null, false)
        promotionBinding?.categoryModel = categoryModelView
        promotionBinding?.root?.tv_subscriptin_text?.text = SavedPreferences.getInstance()?.getStringValue(Constants.SUBS_MESSAGE)
        viewPager = promotionBinding?.root?.viewpager!!
        elv_parent_category.addHeaderView(promotionBinding.root)

        elv_parent_category.setOnGroupExpandListener(object : ExpandableListView.OnGroupExpandListener{
            var previousGroup = -1
            override fun onGroupExpand(groupPosition: Int) {
                if(groupPosition != previousGroup){
                    elv_parent_category.collapseGroup(previousGroup)
                }
                previousGroup = groupPosition
            }

        })


        elv_parent_category.setOnGroupClickListener(object : ExpandableListView.OnGroupClickListener{
            override fun onGroupClick(p0: ExpandableListView?, p1: View?, p2: Int, p3: Long): Boolean {
                if(categoryModelView?.categoryResponse?.get()?.children_data?.get(p2)?.children_data?.size!! == 0){
                    val bundle = Bundle()
                    bundle.putInt(Constants.CATEGORY_ID, categoryModelView?.categoryResponse?.get()?.children_data?.get(p2)?.id!!)
                    bundle.putString(Constants.CATEGORY_NAME, categoryModelView?.categoryResponse?.get()?.children_data?.get(p2)?.name!!)
                    FragmentUtils.addFragment(context!!, ProductListingFragment(), bundle, ProductListingFragment::class.java.name, true)
                }
                return false
            }
        })

        elv_parent_category.setOnScrollListener(object: AbsListView.OnScrollListener{
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
            }

            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                if(scrollState != 0){
                    slideDown(view.rootView.findViewById(R.id.tabLayout))
                }else{
                    slideUp(view.rootView.findViewById(R.id.tabLayout))
                }
            }

        })

        observePromotionsApiResponse()
        observeCategoryApiResponse()
        getPromotions()
        getCategories()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    private fun getPromotions() {
        categoryModelView?.getPromotions()
    }

    private fun getCategories() {
        showLoading()
        categoryModelView?.getCategories()
    }


    private fun observePromotionsApiResponse() {
        categoryModelView?.mutualPromotionResponse?.observe(this, object : Observer<ApiResponse<List<PromotionsResponseDataClass>>> {
            override fun onChanged(@Nullable apiResponse: ApiResponse<List<PromotionsResponseDataClass>>?) {
                // hideLoading()
                val response = apiResponse?.apiResponse ?: apiResponse?.error
                if (response is List<*>) {
                    categoryModelView?.promotionResponse?.set(response as List<PromotionsResponseDataClass>?)
                    startScrollViewPager(viewPager, response.size)
                } else {
                    Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun observeCategoryApiResponse() {
        categoryModelView?.mutualHomeResponse?.observe(this, object : Observer<ApiResponse<CategoryResponseDataClass>> {
            override fun onChanged(@Nullable apiResponse: ApiResponse<CategoryResponseDataClass>?) {
                hideLoading()
                val response = apiResponse?.apiResponse ?: apiResponse?.error
                if (response is CategoryResponseDataClass) {
                    categoryModelView?.categoryResponse?.set(response)
                } else {
                    Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun startScrollViewPager(viewPager : ViewPager, count : Int){
        var currentPage = 0
        val runnable = object : Runnable {
            override fun run() {
                if (currentPage == count) {
                    currentPage = 0
                }
                viewPager.setCurrentItem(currentPage++, true)
                handler.postDelayed(this, 3000)
            }
        }
        handler.postDelayed(runnable, 3000)
    }

    // It will use in future
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

    private fun slideUp(child: TabLayout) {
        child.clearAnimation()
        child.animate().translationY(0f).duration = Constants.AIMATION_DURATION
    }

    private fun slideDown(child: TabLayout) {
        child.clearAnimation()
        child.animate().translationY(child.height.toFloat()).duration = Constants.AIMATION_DURATION
    }

}

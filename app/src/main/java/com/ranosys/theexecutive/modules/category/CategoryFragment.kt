package com.ranosys.theexecutive.modules.category

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.Nullable
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.ExpandableListView
import android.widget.TextView
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentCategoryBinding
import com.ranosys.theexecutive.databinding.HomeViewPagerBinding
import com.ranosys.theexecutive.modules.category.adapters.CustomViewPageAdapter
import com.ranosys.theexecutive.modules.productDetail.ProductDetailFragment
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass
import com.ranosys.theexecutive.modules.productListing.ProductListingFragment
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.home_view_pager.view.*


/**
 * Created by Mohammad Sunny on 21/2/18.
 */
class CategoryFragment : BaseFragment() {

    var categoryModelView: CategoryModelView? = null
    var handler = Handler(Looper.getMainLooper())
    lateinit var viewPager : ViewPager
    lateinit var pagerAdapter:CustomViewPageAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding : FragmentCategoryBinding? = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        categoryModelView = ViewModelProviders.of(this).get(CategoryModelView::class.java)
        mViewDataBinding?.categoryViewModel = categoryModelView
        mViewDataBinding?.executePendingBindings()
        return mViewDataBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBarParams("", R.drawable.logo, "", 0,false, R.drawable.bag, true, true )

        val inflater = LayoutInflater.from(context)
        val promotionBinding : HomeViewPagerBinding? = DataBindingUtil.inflate(inflater, R.layout.home_view_pager, null, false)
        promotionBinding?.categoryModel = categoryModelView
        promotionBinding?.root?.tv_promotion_text?.text = SavedPreferences.getInstance()?.getStringValue(Constants.PROMOTION_MESSAGE)
        Utils.setViewHeightWrtDeviceWidth(activity as Context, promotionBinding?.viewpager!!, 1.5)
        viewPager = promotionBinding?.root?.viewpager!!

        pagerAdapter = CustomViewPageAdapter(view.context, categoryModelView?.promotionResponse?.get())
        promotionBinding.viewpager.adapter = pagerAdapter
        pagerAdapter.setItemClickListener(listener = object: CustomViewPageAdapter.OnItemClickListener{
            override fun onItemClick(item: PromotionsResponseDataClass?) {
                when(item?.type){
                    Constants.PROMOTION_TYPE_CATEGORY -> {
                        val bundle = Bundle()
                        bundle.putInt(Constants.CATEGORY_ID, item.value.toInt())
                        bundle.putString(Constants.CATEGORY_NAME, item.title)
                        FragmentUtils.addFragment(activity as Context, ProductListingFragment(), bundle, ProductListingFragment::class.java.name, true)
                    }

                    Constants.PROMOTION_TYPE_PRODUCT -> {
                        val fragment = ProductDetailFragment.getInstance(null, item.value, 0)
                        FragmentUtils.addFragment(context!!, fragment, null, ProductDetailFragment::class.java.name, true)
                    }

                    Constants.PROMOTION_TYPE_CMS_PAGE -> {
                        Utils.openCmsPage(activity as Context, item.value)

                    }
                }
            }
        })
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

        et_search_home.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(v.text.toString().isEmpty().not()){
                        Utils.hideSoftKeypad(activity as Context)
                        //TODO -  redirect to product listing fragment
                        val bundle = Bundle()
                        bundle.putString(Constants.SEARCH_FROM_HOME_QUERY, v.text.toString())
                        FragmentUtils.addFragment(activity as Context, ProductListingFragment(), bundle, ProductListingFragment::class.java.name, true)

                    }else{
                        Toast.makeText(activity as Context, getString(R.string.enter_search_error), Toast.LENGTH_SHORT).show()
                    }
                    return true
                }
                return false
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
                    pagerAdapter.promotionList = categoryModelView?.promotionResponse?.get()
                    pagerAdapter.notifyDataSetChanged()
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

                    response?.children_data = response?.children_data?.filter { it.is_active == true } as ArrayList<ChildrenData>

                    //add view all category and remove inactive sub categories
                    for(cat in response.children_data){
                        when(cat.name){
                            "MEN" -> {
                                val viewAll = ChildrenData(id = cat.id, name = "View All", is_active = true)
                                cat.children_data?.add(0, viewAll)
                                cat.children_data = cat.children_data?.filter{ it.is_active == true} as ArrayList<ChildrenData>
                            }

                            "WOMEN" -> {
                                val viewAll = ChildrenData(id = cat.id, name = "View All", is_active = true)
                                cat.children_data?.add(0, viewAll)
                                cat.children_data = cat.children_data?.filter{ it.is_active == true} as ArrayList<ChildrenData>
                            }

                            else -> cat.children_data = cat.children_data?.filter{ it.is_active == true} as ArrayList<ChildrenData>
                        }
                    }

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

    private fun prepareProductList(item: PromotionsResponseDataClass): List<ProductListingDataClass.Item>? {
        val media = ProductListingDataClass.MediaGalleryEntry(id = 0,
                disabled = false,
                file = item.image!!,
                label = "",
                media_type = "",
                position = 0,
                types = mutableListOf())

        val mediaEntries = mutableListOf<ProductListingDataClass.MediaGalleryEntry>()
        mediaEntries.add(media)

        val ext_attr = ProductListingDataClass.ExtensionAttributes( website_ids = mutableListOf(),
                category_links = mutableListOf(),
                configurable_product_links = mutableListOf(),
                final_price = 0.0,
                regular_price = 0.0,
                configurable_product_options = mutableListOf(),
                stock_item = null)
        val product = ProductListingDataClass.Item(id = 0,
                name = item.title ?: "",
                sku = item.value,
                attribute_set_id = 0,
                created_at = item.created_at!!,
                price = 0.0,
                visibility = 0,
                updated_at = item.created_at,
                weight = 0.0,
                type_id = item.type!! ,
                status = 0,
                options = mutableListOf(),
                custom_attributes = mutableListOf(),
                tier_prices = mutableListOf(),
                product_links = mutableListOf(),
                extension_attributes = ext_attr,
                media_gallery_entries = mediaEntries
        )

        val productList = mutableListOf<ProductListingDataClass.Item>()
        productList.add(product)

        return productList
    }

}

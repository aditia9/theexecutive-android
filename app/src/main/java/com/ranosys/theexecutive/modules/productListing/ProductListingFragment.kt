package com.ranosys.theexecutive.modules.productListing

import AppLog
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ExpandableListView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiClient
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.DialogFilterOptionBinding
import com.ranosys.theexecutive.databinding.DialogSortOptionBinding
import com.ranosys.theexecutive.databinding.FragmentProductListingBinding
import com.ranosys.theexecutive.modules.myAccount.DividerDecoration
import com.ranosys.theexecutive.modules.productDetail.ProductDetailFragment
import com.ranosys.theexecutive.rangeBar.RangeSeekBar
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.dialog_filter_option.*
import kotlinx.android.synthetic.main.fragment_product_listing.*
import java.util.*

/**
 * @Details fragment shows product listing
 * @Author Ranosys Technologies
 * @Date 16,Apr,2018
 */
class ProductListingFragment: BaseFragment() {

    private lateinit var mBinding: FragmentProductListingBinding
    private lateinit var filterOptionBinding: DialogFilterOptionBinding
    private lateinit var sortOptionBinding: DialogSortOptionBinding
    private lateinit var mViewModel: ProductListingViewModel
    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var filterOptionAdapter: FilterOptionAdapter
    private lateinit var sortOptionAdapter: SortOptionAdapter
    private lateinit var filterOptionDialog: Dialog
    private lateinit var sortOptionDialog: Dialog
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var mLastClickTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = arguments
        data?.let {
            homeSearchQuery = (data.get(Constants.SEARCH_FROM_HOME_QUERY) ?: "").toString()
            categoryId = data.get(Constants.CATEGORY_ID) as Int? ?: 0
            categoryName = data.get(Constants.CATEGORY_NAME) as String? ?: ""
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_listing, container,  false)
        mViewModel = ViewModelProviders.of(this).get(ProductListingViewModel::class.java)

        //sort screen binding
        sortOptionBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_sort_option, container,  false)
        sortOptionDialog = Dialog(activity as Context, R.style.MaterialDialogSheet)
        sortOptionDialog.setContentView(sortOptionBinding.root)
        sortOptionDialog.setCancelable(true)
        sortOptionDialog.window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        sortOptionDialog.window.setGravity(Gravity.BOTTOM)
        sortOptionAdapter = SortOptionAdapter(mViewModel, mViewModel.sortOptionList?.value)
        sortOptionBinding.sortOptionList.adapter = sortOptionAdapter
        prepareSortOptionDialog()

        //filter screen binding
        filterOptionBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_filter_option, container,  false)
        filterOptionDialog = Dialog(activity as Context, R.style.MaterialDialogSheet)
        filterOptionDialog.setContentView(filterOptionBinding.root)
        filterOptionDialog.setCancelable(true)
        filterOptionDialog.window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        filterOptionDialog.window.setGravity(Gravity.BOTTOM)
        filterOptionBinding.filterList.setOnGroupExpandListener(object : ExpandableListView.OnGroupExpandListener{
            var previousGroup = -1
            override fun onGroupExpand(groupPosition: Int) {
                if(groupPosition != previousGroup){
                    filterOptionBinding.filterList.collapseGroup(previousGroup)
                }
                previousGroup = groupPosition
            }

        })
        filterOptionAdapter = FilterOptionAdapter(mViewModel, mViewModel.filterOptionList?.value)
        filterOptionBinding.filterList.setAdapter(filterOptionAdapter)
        prepareFilterDialog()

        observeProductList()
        observeApiErrors()
        observeNoProductAvailable()
        observeFilterOptions()
        observePriceFilter()
        observeSortOptions()

        callInitialApis()

        return mBinding.root
    }

    private fun observeNoProductAvailable() {
        mViewModel.noProductAvailable.observe(this, Observer { count ->
            count?.let {
                if(count <= 0){
                    mBinding.productList.visibility = View.GONE
                    mBinding.tvNoProductAvailable.visibility = View.VISIBLE
                    mBinding.tvNoProductAvailable.text = getString(R.string.no_product_available_error)
                }else{
                    mBinding.productList.visibility = View.VISIBLE
                    mBinding.tvNoProductAvailable.visibility = View.GONE
                    mBinding.tvNoProductAvailable.text = ""
                }
                mViewModel.noProductAvailable.value = null
            }
            hideLoading()
        })
    }

    private fun prepareSortOptionDialog() {
        sortOptionBinding.sortOptionList

        linearLayoutManager = LinearLayoutManager(activity as Context)
        sortOptionBinding.sortOptionList.layoutManager = linearLayoutManager

        val itemDecor = DividerDecoration(resources.getDrawable(R.drawable.horizontal_divider, null))
        sortOptionBinding.sortOptionList.addItemDecoration(itemDecor)

        sortOptionAdapter.setItemClickListener(object: SortOptionAdapter.OnItemClickListener {
            override fun onItemClick(item: ProductListingDataClass.SortOptionResponse) {
                mViewModel.selectedSortOption = item
                sortOptionAdapter.notifyDataSetChanged()
            }

        })

        //method holding all UI interaction of filter dialog
        sortOptionBinding.let {

            sortOptionBinding.cancelIv.setOnClickListener({
                sortOptionDialog.let {
                    sortOptionDialog.dismiss()
                }
            })

            sortOptionBinding.tvClear.setOnClickListener({
                sortOptionDialog.let {
                    clearSelectedSortOption()

                }
            })

            sortOptionBinding.btnApply.setOnClickListener({

                if(mViewModel.isSorted.not() && mViewModel.selectedSortOption.attribute_code.isEmpty()){
                    Toast.makeText(activity as Context, getString(R.string.empty_sort_option_selection_error), Toast.LENGTH_SHORT).show()
                }else{
                    showLoading()
                    sortOptionDialog.dismiss()
                    mViewModel.clearExistingList()
                    if(mViewModel.lastSearchQuery.isNotBlank()){
                        callProductListingApi(query = mViewModel.lastSearchQuery, fromSearch = true)
                    }else{
                        callProductListingApi(categoryId)
                    }
                    mViewModel.isSorted = !(mViewModel.isSorted && mViewModel.selectedSortOption.attribute_code.isEmpty())
                }
            })
        }
    }

    private fun clearSelectedSortOption() {
        mViewModel.selectedSortOption = ProductListingDataClass.SortOptionResponse("","")
        sortOptionAdapter.notifyDataSetChanged()
    }

    private fun observeSortOptions() {
        mViewModel.sortOptionList?.observe(this, Observer { sortOptionList ->

            if(sortOptionList?.isNotEmpty()!!){
                mBinding.tvSortOption.setTextColor(ContextCompat.getColor(activity as Context, R.color.theme_black_color))
                mBinding.tvSortOption.isEnabled = true
                sortOptionAdapter.sortOptions = sortOptionList
                sortOptionAdapter.notifyDataSetChanged()

            }else{
                mBinding.tvSortOption.setTextColor(ContextCompat.getColor(activity as Context, R.color.hint_color))
                mBinding.tvSortOption.isEnabled = false

            }
        })
    }

    private fun callInitialApis() {
        if (Utils.isConnectionAvailable(activity as Context)) {

            if(homeSearchQuery.isNotBlank()){
                mBinding.etSearch.setText(homeSearchQuery)
                handleSearchAction(homeSearchQuery)
            }else{
                mViewModel.getFilterOptions(categoryId)
                mViewModel.getSortOptions(Constants.SORT_TYPE_CATALOG)
                callProductListingApi(categoryId)
            }
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    private fun observePriceFilter() {
        mViewModel.priceFilter.observe(this, Observer { priceFilter ->

            filterOptionBinding.priceRangeBar.setCurrency(priceFilter?.options?.get(0)?.label)
            val range = priceFilter?.options?.get(0)?.value
            val min = range?.split("-")?.get(0)?.toLong()
            val max = range?.split("-")?.get(1)?.toLong()
            if(max == min)
                filterOptionDialog.price_range_bar.isEnabled = false

            filterOptionDialog.price_range_bar.visibility = View.VISIBLE
            filterOptionBinding.priceRangeBar.setRangeValues(min, max)
            filterOptionBinding.priceRangeBar.selectedMinValue = min
            filterOptionBinding.priceRangeBar.selectedMaxValue = max
            filterOptionBinding.etMinPrice.setText(Utils.getFromattedPrice(min.toString()))
            filterOptionBinding.etMaxPrice.setText(Utils.getFromattedPrice(max.toString()))
        })
    }

    private fun observeFilterOptions() {
        mViewModel.filterOptionList?.observe(this, Observer { filterList ->

            if(filterList?.isNotEmpty()!!){
                mBinding.tvFilterOption.setTextColor(ContextCompat.getColor(activity as Context, R.color.theme_black_color))
                mBinding.tvFilterOption.isEnabled = true
                filterOptionAdapter.optionsList = filterList.filterNot { it.name == Constants.FILTER_PRICE_LABEL }
                filterOptionAdapter.notifyDataSetChanged()

                //filterOptionBinding.filterList.expandGroup(0, true)
            }else{
                mBinding.tvFilterOption.setTextColor(ContextCompat.getColor(activity as Context, R.color.hint_color))
                mBinding.tvFilterOption.isEnabled = false

            }

        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val gridLayoutManager = GridLayoutManager(view.context, COLUMN_TWO)
        gridLayoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return if((position + 1) % COLUMN_CHANGE_FACTOR == 0) COLUMN_TWO else COLUMN_ONE
            }
        }

        val threshold = 5
        product_list.layoutManager = gridLayoutManager

        product_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //if down scroll
                if(dy > 0){

                    val visibleItemCount = gridLayoutManager.childCount
                    val totalItemCount = gridLayoutManager.itemCount
                    val firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition()

                    val allProductLoaded = productListAdapter.itemCount >= mViewModel.productListResponse?.total_count?:0
                    val shouldPaging = (visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - threshold)

                    if(mViewModel.isLoading.not() && allProductLoaded.not() && shouldPaging){
                        callProductListingApi(categoryId, mViewModel.lastSearchQuery, mViewModel.lastSearchQuery.isEmpty().not(), true)
                        Toast.makeText(activity as Context, getString(R.string.loading_data), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })


        val emptyList = ArrayList<ProductListingDataClass.Item>()
        productListAdapter = ProductListAdapter(emptyList, object: ProductListAdapter.OnItemClickListener{
            override fun onItemClick(selectedProduct: ProductListingDataClass.ProductMaskedResponse, position: Int) {
                Utils.hideSoftKeypad(activity as Context)
                if (SystemClock.elapsedRealtime() - mLastClickTime < Constants.CLICK_TIMEOUT){
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                ApiClient.client?.dispatcher()?.cancelAll()
                mViewModel.isLoading = false
                val fragment = ProductDetailFragment.getInstance(mViewModel.productListResponse?.items!!, selectedProduct.sku, selectedProduct.name, position)
                FragmentUtils.addFragment(context!!, fragment, null, ProductDetailFragment::class.java.name, true)
            }

        })

        product_list.adapter = productListAdapter

        tv_filter_option.setOnClickListener {
            filterOptionDialog.show()
        }


        tv_sort_option.setOnClickListener{
            sortOptionDialog.show()
        }


        et_search.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.isNullOrBlank().not()){
                    mBinding.etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search, 0)
                }
            }

        })

        et_search.setOnTouchListener(View.OnTouchListener { _, event ->
            val drawableRight = 2
            if(event.action == MotionEvent.ACTION_UP) {
                if(event.rawX >= et_search.right - et_search.compoundDrawables[drawableRight].bounds.width()) {
                    if(Utils.compareDrawable(activity as Context, et_search.compoundDrawables[drawableRight], (activity as Context).getDrawable(R.drawable.cancel))){
                        et_search.setText("")
                        return@OnTouchListener true
                    }else if(Utils.compareDrawable(activity as Context, et_search.compoundDrawables[drawableRight], (activity as Context).getDrawable(R.drawable.search))){
                        if(et_search.text.isNotBlank()){
                            handleSearchAction(et_search.text.toString())
                            mBinding.etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancel, 0)
                        }else{
                            Toast.makeText(activity as Context, getString(R.string.enter_search_error), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            false
        })


        et_search.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                handleSearchAction(v.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun handleSearchAction(searchQuery: String) {
        if(searchQuery.isEmpty().not()){
            clearSelectedSortOption()
            resetFilters()
            mViewModel.getSearchFilterOptions(searchQuery)
            mViewModel.getSortOptions(Constants.SORT_TYPE_SEARCH)
            performSearch(searchQuery)
            Utils.hideSoftKeypad(activity as Context)
            mBinding.etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancel, 0)
        }else{
            Toast.makeText(activity as Context, getString(R.string.enter_search_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun performSearch(searchQuery: String) {
        mViewModel.lastSearchQuery = searchQuery
        callProductListingApi(categoryId, searchQuery, true)
    }


    private fun prepareFilterDialog() {
        //method holding all UI interaction of filter dialog
        filterOptionBinding.let {

            filterOptionBinding.cancelIv.setOnClickListener({
                filterOptionDialog.let {
                    filterOptionDialog.dismiss()
                }
            })

            filterOptionBinding.tvClear.setOnClickListener({
                filterOptionDialog.let {
                    resetFilters()
                }
            })

            filterOptionBinding.btnApply.setOnClickListener({

                val isFilterChanged = (isPriceRangeAltered() || isFilterSelected())
                if (isFilterChanged || mViewModel.isFiltered){
                    filterOptionDialog.dismiss()
                    mViewModel.clearExistingList()
                    if(isFilterChanged){
                        mViewModel.isFiltered = true
                        mViewModel.selectedPriceRange.min = Utils.getStringFromFormattedPrice(filterOptionBinding.etMinPrice.text.toString())
                        mViewModel.selectedPriceRange.max = Utils.getStringFromFormattedPrice(filterOptionBinding.etMaxPrice.text.toString())
                    }else{
                        mViewModel.isFiltered = false
                    }

                    if(mViewModel.lastSearchQuery.isNotBlank()){
                        callProductListingApi(query = mViewModel.lastSearchQuery, fromSearch = true)
                    }else{
                        callProductListingApi(categoryId)
                    }
                }else{
                    Toast.makeText(activity as Context, getString(R.string.empty_filter_option_selection_error), Toast.LENGTH_SHORT).show()
                }
            })

            //price range bar listeners
            filterOptionBinding.priceRangeBar.setOnRangeSeekBarChangeListener(object : RangeSeekBar.OnRangeSeekBarChangeListener<Long>{
                override fun onRangeSeekBarValuesChanged(bar: RangeSeekBar<Long>?, minValue: Long, maxValue: Long) {
                    if(minValue <= maxValue ){
                        filterOptionBinding.etMinPrice.setText(Utils.getFromattedPrice(minValue.toString()))
                        filterOptionBinding.etMaxPrice.setText(Utils.getFromattedPrice(maxValue.toString()))
                    }
                }

            })

            filterOptionBinding.etMinPrice.addTextChangedListener(object: TextWatcher{
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(s?.isNotBlank() == true){
                        try {
                            val input = Utils.getStringFromFormattedPrice(s.toString()).toLong()
                            filterOptionBinding.priceRangeBar.selectedMinValue = input
                        }
                        catch (e : NumberFormatException){
                            AppLog.printStackTrace(e)
                        }

                    }
                }
            })

            filterOptionBinding.etMaxPrice.addTextChangedListener(object: TextWatcher{
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(s?.isNotBlank() == true && s.contains(".").not()){
                        val input = Utils.getStringFromFormattedPrice(s.toString()).toLong()
                        filterOptionBinding.priceRangeBar.selectedMaxValue = input
                    }
                }
            })
        }
    }

    private fun isFilterSelected(): Boolean {

        return mViewModel.selectedFilterMap.values.count { it.isNotBlank() } > 0
    }

    private fun isPriceRangeAltered(): Boolean {
        return (filterOptionBinding.priceRangeBar.absoluteMaxValue != filterOptionBinding.priceRangeBar.selectedMaxValue
                || filterOptionBinding.priceRangeBar.absoluteMinValue != filterOptionBinding.priceRangeBar.selectedMinValue)

    }

    private fun resetFilters() {

        //reset price filter
        mViewModel.selectedPriceRange.min = ""
        mViewModel.selectedPriceRange.max = ""
        filterOptionBinding.priceRangeBar.resetSelectedValues()
        filterOptionDialog.et_max_price.setText(Utils.getFromattedPrice(filterOptionBinding.priceRangeBar.selectedMaxValue.toString()))
        filterOptionDialog.et_min_price.setText(Utils.getFromattedPrice(filterOptionBinding.priceRangeBar.selectedMinValue.toString()))

        //reset other filters
        val keys = mViewModel.selectedFilterMap.keys
        for (key in keys){
            mViewModel.selectedFilterMap[key] = ""
        }

        filterOptionAdapter.resetFilter()
    }

    private fun observeProductList() {
        mViewModel.productList.observe(this, Observer<MutableList<ProductListingDataClass.Item>> { partialProductList ->
            partialProductList?.run {
                productListAdapter.productList = partialProductList
                productListAdapter.notifyDataSetChanged()
                hideLoading()
            }
        })
    }

    private fun observeApiErrors() {
        mViewModel.apiFailureResponse?.observe(this, Observer<String> { error ->
            hideLoading()
            Utils.showErrorDialog(activity as Context, getString(R.string.something_went_wrong_error))
        })
    }


    fun callProductListingApi(catId: Int? = 0, query: String = "", fromSearch:Boolean  = false, fromPagination: Boolean = false){
        if (Utils.isConnectionAvailable(activity as Context)) {

            if(fromPagination.not()){
                showLoading()
            }
            mViewModel.getProductListing(catId, query, fromSearch, fromPagination)


        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(categoryName, 0, "", R.drawable.back, true, R.drawable.bag, true)
    }

    companion object {
        const val COLUMN_TWO = 2
        const val COLUMN_ONE = 1
        const val COLUMN_CHANGE_FACTOR = 5
        var categoryName: String? = null
        var categoryId: Int? = null
        var homeSearchQuery: String = ""
    }
}

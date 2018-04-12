package com.ranosys.theexecutive.modules.productListing

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.DialogFilterOptionBinding
import com.ranosys.theexecutive.databinding.FragmentProductListingBinding
import com.ranosys.theexecutive.rangeBar.RangeSeekBar
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.dialog_filter_option.*
import kotlinx.android.synthetic.main.fragment_product_listing.*
import java.util.*

/**
 * Created by nikhil on 20/3/18.
 */
class ProductListingFragment: BaseFragment() {

    private lateinit var mBinding: FragmentProductListingBinding
    private lateinit var filterOptionBinding: DialogFilterOptionBinding
    private lateinit var mViewModel: ProductListingViewModel
    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var filterOptionAdapter: FilterOptionAdapter
    private lateinit var filterOptionDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = arguments
        categoryId = data?.get(Constants.CATEGORY_ID) as Int?
        categoryName = data?.get(Constants.CATEGORY_NAME) as String?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {



        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_listing, container,  false)
        mViewModel = ViewModelProviders.of(this).get(ProductListingViewModel::class.java)

        callInitialApis()

        //filter screen binding
        filterOptionBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_filter_option, container,  false)
        filterOptionDialog = Dialog(activity as Context, R.style.MaterialDialogSheet)
        filterOptionDialog.setContentView(filterOptionBinding.root)
        filterOptionDialog.setCancelable(true)
        filterOptionDialog.window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        filterOptionDialog.window.setGravity(Gravity.BOTTOM)

        filterOptionAdapter = FilterOptionAdapter(mViewModel, mViewModel.filterOptionList?.value)
        filterOptionBinding.filterList.setAdapter(filterOptionAdapter)

        observeProductList()
        observeFilterOptions()
        observePriceFilter()

        return mBinding.root
    }

    private fun callInitialApis() {
        if (Utils.isConnectionAvailable(activity as Context)) {
            showLoading()
            mViewModel.getProductListing(categoryId.toString())
            mViewModel.getSortOptions()
            mViewModel.getFilterOptions(categoryId)

        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    private fun observePriceFilter() {
        mViewModel.priceFilter.observe(this, Observer { priceFilter ->
            hideLoading()
            filterOptionBinding.priceRangeBar.setCurrency(priceFilter?.options?.get(0)?.label)
            val range = priceFilter?.options?.get(0)?.value
            val min = range?.split("-")?.get(0)?.toFloat()
            val max = range?.split("-")?.get(1)?.toFloat()
            if(max == min){
                filterOptionDialog.price_range_bar.visibility = View.GONE
            }else{
                filterOptionDialog.price_range_bar.visibility = View.VISIBLE
                filterOptionBinding.priceRangeBar.setRangeValues(min, max)
                filterOptionBinding.priceRangeBar.selectedMinValue = min
                filterOptionBinding.priceRangeBar.selectedMaxValue =  max
                filterOptionBinding.etMinPrice.setText(min.toString())
                filterOptionBinding.etMaxPrice.setText(max.toString())
            }
        })
    }

    private fun observeFilterOptions() {
        mViewModel.filterOptionList?.observe(this, Observer { filterList ->
            hideLoading()
            if(filterList?.isNotEmpty()!!){
                mBinding.tvFilterOption.isClickable = true
                filterOptionAdapter.optionsList = filterList.filterNot { it.name == Constants.FILTER_PRICE_LABEL }
            }else{
                mBinding.tvFilterOption.isClickable = false
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

        val threshold = 2
        product_list.layoutManager = gridLayoutManager

        product_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //if down scroll
                if(dy > 0){

                    val visibleItemCount = gridLayoutManager.childCount
                    val totalItemCount = gridLayoutManager.itemCount
                    val firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition()

                    val allProductLoaded = productListAdapter.itemCount >= mViewModel.totalProductCount
                    val shouldPaging = (visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - threshold)

                    if(mViewModel.isLoading.not() && allProductLoaded.not() && shouldPaging){
                        Toast.makeText(recyclerView?.context, "load data", Toast.LENGTH_SHORT).show()
                        mViewModel.getProductListing(categoryId.toString())
                    }
                }
            }
        })


        val emptyList = ArrayList<ProductListingDataClass.ProductMaskedResponse>()
        productListAdapter = ProductListAdapter(emptyList, object: ProductListAdapter.OnItemClickListener{
            override fun onItemClick(selectedProduct: ProductListingDataClass.ProductMaskedResponse, position: Int) {
                Toast.makeText(activity, selectedProduct.name + " product selected", Toast.LENGTH_SHORT).show()
                //FragmentUtils.addFragment(context!!, ProductDetailFragment.newInstance(mViewModel.productListResponse), null, ProductDetailFragment::class.java.name, true)
            }

        })

        product_list.adapter = productListAdapter

        tv_filter_option.setOnClickListener {
            prepareFilterDialog()
            filterOptionDialog.show()
        }
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
                showLoading()
                mViewModel.selectedPriceRange.min = filterOptionBinding.priceRangeBar.selectedMinValue.toString()
                mViewModel.selectedPriceRange.max = filterOptionBinding.priceRangeBar.selectedMaxValue.toString()
                filterOptionDialog.dismiss()
                mViewModel.getProductListing(categoryId.toString())
            })

            //price range bar listeners
            filterOptionBinding.priceRangeBar.setOnRangeSeekBarChangeListener(object : RangeSeekBar.OnRangeSeekBarChangeListener<Float>{
                override fun onRangeSeekBarValuesChanged(bar: RangeSeekBar<Float>?, minValue: Float, maxValue: Float) {
                    if(minValue <= maxValue ){
                        filterOptionBinding.etMinPrice.setText(minValue.toString())
                        filterOptionBinding.etMaxPrice.setText(maxValue.toString())
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
                        val input = s.toString().toFloat()
                        filterOptionBinding.priceRangeBar.selectedMinValue = input

                    }
                }
            })

            filterOptionBinding.etMaxPrice.addTextChangedListener(object: TextWatcher{
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(s?.isNotBlank() == true){
                        val input = s.toString().toFloat()
                        filterOptionBinding.priceRangeBar.selectedMaxValue = input
                    }
                }
            })
        }
    }

    private fun resetFilters() {

        //reset price filter
        mViewModel.selectedPriceRange.min = ""
        mViewModel.selectedPriceRange.max = ""
        filterOptionBinding.priceRangeBar.resetSelectedValues()
        filterOptionDialog.et_max_price.setText(filterOptionBinding.priceRangeBar.selectedMaxValue.toString())
        filterOptionDialog.et_min_price.setText(filterOptionBinding.priceRangeBar.selectedMinValue.toString())

        //reset other filters
        val keys = mViewModel.selectedFilterMap.keys
        for (key in keys){
            mViewModel.selectedFilterMap.put(key, "")
        }

    }

    private fun observeProductList() {
        mViewModel.maskedProductList.observe(this, Observer<ArrayList<ProductListingDataClass.ProductMaskedResponse>> { partialProductList ->
            partialProductList?.run {
                hideLoading()
                productListAdapter.addProducts(partialProductList)
            }
        })
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
    }
}

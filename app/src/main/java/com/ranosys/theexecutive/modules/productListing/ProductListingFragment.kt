package com.ranosys.theexecutive.modules.productListing

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
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
import com.ranosys.theexecutive.utils.Constants
import kotlinx.android.synthetic.main.fragment_product_listing.*

/**
 * Created by nikhil on 20/3/18.
 */
class ProductListingFragment: BaseFragment() {

    var category_id : Int? = null


    private lateinit var mBinding: FragmentProductListingBinding
    private lateinit var filterOptionBinding: DialogFilterOptionBinding
    private lateinit var mViewModel: ProductListingViewModel
    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var filterOptionAdapter: FilterOptionAdapter

    private lateinit var filterOptionDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = arguments
        category_id = data?.get(Constants.CATEGORY_ID) as Int?
        category_name = data?.get(Constants.CATEGORY_NAME) as String?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {



        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_listing, container,  false)
        mViewModel = ViewModelProviders.of(this).get(ProductListingViewModel::class.java)

        mViewModel.getProductListing(category_id.toString())
        mViewModel.getSortOptions()
        mViewModel.getFilterOptions()

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

        return mBinding.root
    }

    private fun observeFilterOptions() {
        mViewModel.filterOptionList?.observe(this, Observer { filterList ->
            if(filterList?.isNotEmpty()!!){
                mBinding.tvFilterOption.isClickable = true
                filterOptionAdapter.optionsList = filterList
                filterOptionAdapter.notifyDataSetChanged()
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
                        //TODO - call product listing api
                    }
                }
            }
        })


        val emptyList = ArrayList<ProductListingDataClass.DummyResponse>()
        productListAdapter = ProductListAdapter(emptyList, object: ProductListAdapter.OnItemClickListener{
            override fun onItemClick(selectedProduct: ProductListingDataClass.DummyResponse) {
                Toast.makeText(activity, selectedProduct.name + " product selected", Toast.LENGTH_SHORT).show()
                //TODO - geather necessary info and move to product details
            }

        })

        product_list.adapter = productListAdapter

        tv_filter_option.setOnClickListener {
            filterOptionDialog.show()
        }
    }

    private fun observeProductList() {
        mViewModel.partialProductList.observe(this, Observer<ArrayList<ProductListingDataClass.DummyResponse>> { partialProductList ->
            partialProductList?.run {
                productListAdapter.addProducts(partialProductList)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(category_name, 0, "", R.drawable.back, true, R.drawable.bag, true)
    }

    companion object {
        const val COLUMN_TWO = 2
        const val COLUMN_ONE = 1
        const val COLUMN_CHANGE_FACTOR = 5
        var category_name : String? = null
    }
}

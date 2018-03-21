package com.ranosys.theexecutive.modules.productListing

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentProductListingBinding
import kotlinx.android.synthetic.main.fragment_product_listing.*

/**
 * Created by nikhil on 20/3/18.
 */
class ProductListingFragment: BaseFragment() {
    private lateinit var mBinding: FragmentProductListingBinding
    private lateinit var mViewModel: ProductListingViewModel
    private lateinit var productListAdapter: ProductListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_listing, container,  false)
        mViewModel = ViewModelProviders.of(this).get(ProductListingViewModel::class.java)

        //TODO - call sort option api
        mViewModel.getSortOptions()
        //TODO - call filter option api
        mViewModel.getFilterOptions()
        //TODO - call product listing api
        val sku: String = "1" //need to be replace with selected category sku
        mViewModel.getProductListing(sku)

        observeProductList()

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridLayoutManager = GridLayoutManager(view.context, 2)
        gridLayoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return if((position + 1) % 5 == 0) 2 else 1
            }
        }

        product_list.layoutManager = gridLayoutManager

        val emptyList = ArrayList<ProductListingDataClass.DummyResponse>()
        productListAdapter = ProductListAdapter(emptyList, object: ProductListAdapter.OnItemClickListener{
            override fun onItemClick(selectedProduct: ProductListingDataClass.DummyResponse) {
                Toast.makeText(activity, "Producted selected", Toast.LENGTH_SHORT).show()
                //TODO - geather necessary info and move to product details
            }

        })
        product_list.adapter = productListAdapter

    }

    private fun observeProductList() {
        mViewModel.productList.observe(this, Observer<ArrayList<ProductListingDataClass.DummyResponse>> { productList ->
            if (productList != null) {
                productListAdapter.addProducts(productList)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val categoryName = "Men" //replace with selected category
        setToolBarParams(categoryName, R.drawable.back, true, R.drawable.bag, true)
    }
}
package com.ranosys.theexecutive.modules.productDetail

import AppLog
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.ProductDetailViewBinding
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ChildProductsResponse
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ProductOptionsResponse
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass
import com.ranosys.theexecutive.utils.Constants
import kotlinx.android.synthetic.main.bottom_size_layout.*
import kotlinx.android.synthetic.main.product_detail_view.*

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 11,Apr,2018
 */
class ProductViewFragment : BaseFragment() {

    lateinit var productItemViewModel : ProductItemViewModel
    var productItem : ProductListingDataClass.Item? = null
    var position : Int? = 0
    var productSku : String? = ""
    var attribute : String? = ""
    var colorList = mutableListOf<String>()
    var sizeList = mutableListOf<String>()
    val colorMap: HashMap<Int, String>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val listGroupBinding: ProductDetailViewBinding? = DataBindingUtil.inflate(inflater, R.layout.product_detail_view, container, false);
        productItemViewModel = ViewModelProviders.of(this).get(ProductItemViewModel::class.java)
        productItemViewModel.productItem = productItem
        listGroupBinding?.productItemVM = productItemViewModel

        observeEvents()
        getStaticPagesUrl()

        if(productItem?.type_id.equals("configurable")){
            setData()
            getProductChildren(productItem?.sku)
        }

        return listGroupBinding!!.root
    }

    fun setData(){

        val linearLayoutManager = LinearLayoutManager(activity as Context)
        list_wear_with_products.layoutManager = linearLayoutManager

        val wearWithAdapter = WearWithProductsAdapter(activity as Context, productItem?.product_links)
        list_wear_with_products.adapter = wearWithAdapter
        wearWithAdapter.setItemClickListener(object  : WearWithProductsAdapter.OnItemClickListener{
            override fun onItemClick(item: ProductListingDataClass.ProductLinks?) {

            }
        })

        val length = productItem?.extension_attributes?.configurable_product_options?.size!!
        for(i in 0..length-1){
            val option = productItem?.extension_attributes?.configurable_product_options?.get(i)
            when(option?.label){
                "Color" -> {
                    option.values.forEach { value -> colorList.add(value.value_index.toString()) }
                    //option.values.forEach { value -> colorMap?.put() }
                    AppLog.e("ColorList : " +colorList.toString())
                    getProductOptions(productItem?.extension_attributes?.configurable_product_options?.get(i)?.attribute_id, "color")
                }
                "Size" -> {
                    option.values.forEach { value -> sizeList.add(value.value_index.toString()) }
                    AppLog.e("Sizelist : " + sizeList.toString())
                    getProductOptions(productItem?.extension_attributes?.configurable_product_options?.get(i)?.attribute_id, "size")
                }
            }
        }
    }

    fun getProductChildren(productSku : String?){
        productItemViewModel.getProductChildren(productSku)
    }

    fun getProductOptions(attributeId : String?, label : String?){
        productItemViewModel.getProductOptions(attributeId, label)
    }

    fun getStaticPagesUrl(){
        productItemViewModel.getStaticPagesUrl()
    }

    fun observeEvents(){
        productItemViewModel.clickedAddBtnId?.observe(this, Observer<Int> { id ->
            when (id){
                R.id.btn_add_to_bag -> {
                    openBottomSizeSheet ()
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_composition_and_care -> {
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_size_guideline -> {
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_shipping -> {
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_return -> {
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_share -> {
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_buying_guidelinie -> {
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_chat -> {
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_wishlist -> {
                    productItemViewModel.clickedAddBtnId?.value = null
                }
            }

        })

        productItemViewModel.productChildrenResponse?.observe(this, object : Observer<ApiResponse<ChildProductsResponse>> {
            override fun onChanged(apiResponse: ApiResponse<ChildProductsResponse>?) {
                val response = apiResponse?.apiResponse ?: apiResponse?.error
                if (response is ChildProductsResponse) {

                } else {
                    Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
                }
            }
        })

        productItemViewModel.productOptionResponse?.observe(this, object : Observer<ApiResponse<List<ProductOptionsResponse>>> {
            override fun onChanged(apiResponse: ApiResponse<List<ProductOptionsResponse>>?) {
                val response = apiResponse?.apiResponse ?: apiResponse?.error
                if (response is List<*>) {
                   val list = response as List<ProductOptionsResponse>
                    list.get(0).label
                    when(list.get(0).label){
                        "color" -> AppLog.e("color index : " + (response.get(1) as ProductOptionsResponse).label!!)
                        "size" -> AppLog.e("size index : " + (response.get(1) as ProductOptionsResponse).label!!)
                    }

                } else {
                    Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun openBottomSizeSheet()
    {
        val view = layoutInflater.inflate(R.layout.bottom_size_layout, null)
        val mBottomSheetDialog = Dialog(activity, R.style.MaterialDialogSheet)
        mBottomSheetDialog.setContentView(view)
        mBottomSheetDialog.setCancelable(true)
        mBottomSheetDialog.getWindow()!!.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT /*+ rl_add_to_box.height*/)
        mBottomSheetDialog.getWindow()!!.setGravity(Gravity.BOTTOM)
        mBottomSheetDialog.show()

        mBottomSheetDialog.btn_done.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(mBottomSheetDialog.isShowing){
                    mBottomSheetDialog.dismiss()
                }
            }
        })


    }

    companion object {

        fun getInstance(productItem : ProductListingDataClass.Item?, productSku : String?, position : Int?) =
                ProductViewFragment().apply {
                    this.productItem = productItem
                    this.productSku = productSku
                    this.position = position
                }

    }



}
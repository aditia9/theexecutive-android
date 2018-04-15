package com.ranosys.theexecutive.modules.productDetail

import AppLog
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
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
import com.ranosys.theexecutive.databinding.ProductImagesLayoutBinding
import com.ranosys.theexecutive.modules.login.LoginFragment
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ChildProductsResponse
import com.ranosys.theexecutive.modules.productDetail.dataClassess.ProductOptionsResponse
import com.ranosys.theexecutive.modules.productDetail.dataClassess.StaticPagesUrlResponse
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.bottom_size_layout.*
import kotlinx.android.synthetic.main.bottom_size_layout.view.*
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

        return listGroupBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(productItem?.type_id.equals("configurable")){
            setData()
            getProductChildren(productItem?.sku)
        }

    }

    fun setData(){

        setDescription()
        setProductImages()
        setWearWithProductsData()
    }

    fun setDescription(){
        try {
            val productDescription = productItem?.custom_attributes?.filter { s ->
                s.attribute_code == "short_description"
            }?.single()
            tv_description.setText(Html.fromHtml(productDescription?.value.toString()))
        }catch (e : NoSuchElementException){
            AppLog.printStackTrace(e)
        }

    }

    fun setWearWithProductsData(){
        val linearLayoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.HORIZONTAL, false)
        list_wear_with_products.layoutManager = linearLayoutManager

        if(productItem?.product_links?.size!! > 0) {
            val wearWithAdapter = WearWithProductsAdapter(activity as Context, productItem?.product_links)
            list_wear_with_products.adapter = wearWithAdapter
            wearWithAdapter.setItemClickListener(object : WearWithProductsAdapter.OnItemClickListener {
                override fun onItemClick(item: ProductListingDataClass.ProductLinks?) {

                }
            })
        }else {
            rl_wear_with_layout.visibility = View.GONE
        }

    }

    fun setProductImages(){
        Utils.setImageViewHeight(activity as Context, img_one, 27)
        Utils.setImageViewHeight(activity as Context, img_two, 27)
        val listSize = productItem?.media_gallery_entries?.size
        for(i in 2..listSize!!.minus(1)){
            val productImagesBinding : ProductImagesLayoutBinding? = DataBindingUtil.inflate(activity?.layoutInflater, R.layout.product_images_layout, null, false)
            productImagesBinding?.mediaGalleryEntry = productItem?.media_gallery_entries?.get(i)
            Utils.setImageViewHeight(activity as Context, productImagesBinding?.imgProductImage, 27)
            ll_color_choice.addView(productImagesBinding?.root)
        }
    }

    fun setColorImagesList(){
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
                    Utils.openPages(activity as Context, productItemViewModel.staticPages?.composition_and_care)
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_size_guideline -> {
                    Utils.openPages(activity as Context, productItemViewModel.staticPages?.size_guideline)
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_shipping -> {
                    Utils.openPages(activity as Context, productItemViewModel.staticPages?.shipping)
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_return -> {
                    Utils.openPages(activity as Context, productItemViewModel.staticPages?.returns)
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_share -> {
                    val url = ""
                    Utils.shareUrl(activity as Context, url)
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_buying_guidelinie -> {
                    Utils.openPages(activity as Context, productItemViewModel.staticPages?.buying_guideline)
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_chat -> {
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_wishlist -> {

                    if (Utils.isConnectionAvailable(activity as Context)) {
                        //check for logged in user
                        if((SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY) ?: "").isBlank()){
                            //show toast to user to login
                            Toast.makeText(activity as Context, getString(R.string.login_required_error), Toast.LENGTH_SHORT).show()
                            setToolBarParams(getString(R.string.login), 0, "", R.drawable.cancel, true, 0, false, true)
                            val bundle = Bundle()
                            bundle.putBoolean(Constants.LOGIN_REQUIRED_PROMPT, true)
                            FragmentUtils.addFragment(activity as Context, LoginFragment(), bundle, LoginFragment::class.java.name, true)
                        }else{
                            showLoading()
                            productItemViewModel.callAddToWishListApi()
                        }
                    } else {
                        Utils.showNetworkErrorDialog(activity as Context)
                    }
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

        productItemViewModel.staticPagesUrlResponse?.observe( this, object : Observer<ApiResponse<StaticPagesUrlResponse>> {
            override fun onChanged(apiResponse: ApiResponse<StaticPagesUrlResponse>?) {
                val response = apiResponse?.apiResponse ?: apiResponse?.error
                if(response is StaticPagesUrlResponse){
                    productItemViewModel.staticPages = response
                }
                else {
                    Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
                }
            }
        })

        productItemViewModel.addToWIshListResponse?.observe(this, Observer { apiResponse ->
            hideLoading()
            val response = apiResponse?.apiResponse ?: apiResponse?.error
            Toast.makeText(activity as Context, response, Toast.LENGTH_SHORT).show()
        })
    }

    fun openBottomSizeSheet()
    {
        val view = layoutInflater.inflate(R.layout.bottom_size_layout, null)
        val mBottomSheetDialog = Dialog(activity, R.style.MaterialDialogSheet)
        mBottomSheetDialog.setContentView(view)
        mBottomSheetDialog.setCancelable(true)
        mBottomSheetDialog.window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT /*+ rl_add_to_box.height*/)
        mBottomSheetDialog.window.setGravity(Gravity.BOTTOM)
        view.tv_price.setText(Constants.IDR + productItem?.price.toString())
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
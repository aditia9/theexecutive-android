package com.ranosys.theexecutive.modules.productDetail

import AppLog
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.ranosys.theexecutive.BuildConfig
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.BottomSizeLayoutBinding
import com.ranosys.theexecutive.databinding.ProductDetailViewBinding
import com.ranosys.theexecutive.databinding.ProductImagesLayoutBinding
import com.ranosys.theexecutive.modules.login.LoginFragment
import com.ranosys.theexecutive.modules.productDetail.dataClassess.*
import com.ranosys.theexecutive.modules.productListing.ProductListingDataClass
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.SavedPreferences
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.bottom_size_layout.*
import kotlinx.android.synthetic.main.dialog_product_image.view.*
import kotlinx.android.synthetic.main.product_detail_view.*
import kotlinx.android.synthetic.main.product_images_layout.view.*

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
    var colorAttrId : String? = ""
    var sizeAttrId : String? = ""
    var productColorValue : String? = ""
    var colorMap = HashMap<String, String>()
    var sizeMap = HashMap<String, String>()
    var childProductsMap = HashMap<String, MutableList<ProductListingDataClass.MediaGalleryEntry>?>()
    var colorOptionList : List<ProductOptionsResponse>? = null
    var sizeOptionList : List<ProductOptionsResponse>? = null
    private lateinit var sizeDilaogBinding: BottomSizeLayoutBinding
    private lateinit var sizeDilaog: Dialog
    var colorsViewList : MutableList<ColorsView>? = mutableListOf()
    var sizeViewList : MutableList<SizeView>? = mutableListOf()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val listGroupBinding: ProductDetailViewBinding? = DataBindingUtil.inflate(inflater, R.layout.product_detail_view, container, false);
        productItemViewModel = ViewModelProviders.of(this).get(ProductItemViewModel::class.java)
        productItemViewModel.productItem = productItem
        listGroupBinding?.productItemVM = productItemViewModel

        sizeDilaogBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_size_layout, container,  false)
        prepareSizeDialog()


        observeEvents()
        getStaticPagesUrl()

        return listGroupBinding!!.root
    }

    private fun prepareSizeDialog() {
        sizeDilaog = Dialog(activity, R.style.MaterialDialogSheet)
        sizeDilaog.setContentView(sizeDilaogBinding.root)
        sizeDilaog.setCancelable(true)
        sizeDilaog.window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT /*+ rl_add_to_box.height*/)
        sizeDilaog.window.setGravity(Gravity.BOTTOM)

        sizeDilaog.tv_price_dialog.setText(Constants.IDR + productItem?.price.toString())

        sizeDilaog.btn_done.setOnClickListener(View.OnClickListener {
            if(sizeDilaog.isShowing){
                sizeDilaog.dismiss()
            }


            val item = CartItem(sku = "5-BLWBBX417L014",
                    qty = 2,
                    quote_id = "48",
                    extension_attributes = null,
                    product_option = null
            )
            val dummy = AddTOCartRequest(item)

            val userToken = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)

            if(userToken.isNullOrBlank().not()){

                var cartId = SavedPreferences.getInstance()?.getStringValue(Constants.USER_CART_ID_KEY)

                if(cartId.isNullOrBlank()){
                    (activity as BaseActivity).baseViewModel.let {
                        it.getCartIdForUser(userToken)
                    }
                }
                productItemViewModel.addToUserCart(dummy)

            }else{
                val cartId = (activity as BaseActivity).baseViewModel.let {
                    it.getCartIdForGuest()
                }

                productItemViewModel.addToGuestCart(cartId, dummy)
            }
        })

        val itemQty = productItem?.extension_attributes?.stock_item?.qty ?: 0
        val selectedQty = 0

        sizeDilaog.tv_quantity.text = selectedQty.toString()
        sizeDilaog.img_forward.setOnClickListener {
            if(selectedQty < itemQty){
                selectedQty.plus(1)
                sizeDilaog.tv_quantity.text = selectedQty.toString()
            }else{
                Toast.makeText(activity as Context, "No more product available", Toast.LENGTH_SHORT).show()
            }
        }

        sizeDilaog.img_back.setOnClickListener {
            if(selectedQty > 0){
                selectedQty.minus(1)
                sizeDilaog.tv_quantity.text = selectedQty.toString()
            }
        }

        val linearLayoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.HORIZONTAL, false)
        sizeDilaog.rv_size_view.layoutManager = linearLayoutManager
        if(sizeViewList?.size!! > 0) {
            val sizeViewAdapter = SizeRecyclerAdapter(activity as Context, sizeViewList)
            sizeDilaog.rv_size_view.adapter = sizeViewAdapter
            sizeViewAdapter.setItemClickListener(object : SizeRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(item: ProductViewFragment.SizeView?) {

                }
            })
        }

        sizeDilaog.tv_size_guide.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                Utils.openPages(activity as Context, productItemViewModel.staticPages?.size_guideline)
            }
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData()

        if(productItem?.type_id.equals("configurable")){
            getProductChildren(productItem?.sku)
        }

        img_one.setOnClickListener{
            val drawable=img_one.drawable as BitmapDrawable
            val bitmap=drawable.bitmap
            openProdcutImage(bitmap)
        }

        img_two.setOnClickListener {
            val drawable=img_two.drawable as BitmapDrawable
            val bitmap=drawable.bitmap
            openProdcutImage(bitmap)
        }


    }

    fun setData(){

        setDescription()
        setProductImages(productItem?.media_gallery_entries)
        setColorImagesList()
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

    fun setProductImages(mediaGalleryList : List<ProductListingDataClass.MediaGalleryEntry?>?){
        Utils.setImageViewHeight(activity as Context, img_one, 27)
        Utils.setImageViewHeight(activity as Context, img_two, 27)
        val listSize = mediaGalleryList?.size
        for(i in 2..listSize!!.minus(1)){
            val productImagesBinding : ProductImagesLayoutBinding? = DataBindingUtil.inflate(activity?.layoutInflater, R.layout.product_images_layout, null, false)
            productImagesBinding?.mediaGalleryEntry = productItem?.media_gallery_entries?.get(i)
            Utils.setImageViewHeight(activity as Context, productImagesBinding?.imgProductImage, 27)
            val view = productImagesBinding!!.root.img_product_image
            view.setOnClickListener {
                val drawable=view.drawable as BitmapDrawable
                val bitmap=drawable.bitmap
                openProdcutImage(bitmap)
            }
            ll_color_choice.addView(productImagesBinding?.root)
        }
    }

    fun setColorImagesList(){
        productItem?.extension_attributes?.configurable_product_options?.run{
            val length = productItem?.extension_attributes?.configurable_product_options?.size!!
            for(i in 0..length-1) {
                val option = productItem?.extension_attributes?.configurable_product_options?.get(i)
                when (option?.label) {
                    "Color" -> {
                        option.values.forEachIndexed { index, value ->
                            if(index == 0) { productColorValue = value.value_index.toString() }
                            colorMap.put(index.toString(), value = value.value_index.toString())
                        }
                        AppLog.e("ColorList : " + colorMap.toString())
                        colorAttrId = productItem?.extension_attributes?.configurable_product_options?.get(i)?.attribute_id
                        getProductOptions(colorAttrId, "color")
                    }
                    "Size" -> {
                        option.values.forEachIndexed { index, value ->
                            sizeMap.put(index.toString(), value = value.value_index.toString())
                        }
                        AppLog.e("Sizelist : " + sizeMap.toString())
                        sizeAttrId = productItem?.extension_attributes?.configurable_product_options?.get(i)?.attribute_id
                        getProductOptions(sizeAttrId, "size")
                    }
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
                    openBottomSizeSheet()
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
                    shareProductUrl()
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

        productItemViewModel.productChildrenResponse?.observe(this, object : Observer<ApiResponse<List<ChildProductsResponse>>> {
            override fun onChanged(apiResponse: ApiResponse<List<ChildProductsResponse>>?) {
                val response = apiResponse?.apiResponse ?: apiResponse?.error
                if (response is List<*>) {
                    val list = response as List<ChildProductsResponse>

                    list.forEach {
                        val value =it.custom_attributes.filter { s ->
                            s.attribute_code == "color"
                        }.single().value.toString()
                        if(!childProductsMap.containsKey(value))
                            if(value.equals(productColorValue)){
                                childProductsMap.put(value, productItem?.media_gallery_entries)
                            }else {
                                childProductsMap.put(value, it.media_gallery_entries)
                            }
                    }

                    setColorViewList()
                    setSizeViewList()

                    AppLog.e("ChildProductsMap : " + childProductsMap.toString())


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
                        "color" -> {
                            AppLog.e("color index : " + (response.get(0) as ProductOptionsResponse).label!!)
                            colorOptionList = list.filter {
                                it.value in colorMap.values
                            }
                            AppLog.e("New color list : " + colorOptionList.toString())
                        }
                        "size" -> {
                            AppLog.e("size index : " + (response.get(0) as ProductOptionsResponse).label!!)
                            sizeOptionList = list.filter {
                                it.value in sizeMap.values
                            }
                            AppLog.e("New size list : " + sizeOptionList.toString())
                        }
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

        productItemViewModel.addToCartSuccess?.observe(this, Observer {
            val userToken = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
            var cartCount = "0"

            if(userToken.isNullOrBlank().not()){

                cartCount = (activity as BaseActivity).baseViewModel.getUserCartCount()

            }else{
                val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY)
                if(guestCartId.isNullOrBlank().not()){
                    cartCount = (activity as BaseActivity).baseViewModel.getGuestCartCount(guestCartId ?: "")
                }
            }
            Utils.updateCartCount(cartCount.toInt())
            Toast.makeText(activity as Context, getString(R.string.add_to_cart_success_msg),Toast.LENGTH_SHORT).show()
        })

        productItemViewModel.addToCartFailure?.observe(this, Observer {
            Toast.makeText(activity as Context, "Please try again",Toast.LENGTH_SHORT).show()
        })
    }

    private fun shareProductUrl() {
        val baseUrl = BuildConfig.API_URL
        val url = productItem?.custom_attributes?.find { it.attribute_code == Constants.URL_KEY }.let { it?.value }.toString()
        val urlSuffix = Constants.URL_SUFFIX
        if(url.isNotBlank()){
            Utils.shareUrl(activity as Context, "$baseUrl$url$urlSuffix")
        }

    }

    fun setColorViewList(){
        colorOptionList?.forEach {
            colorsViewList?.add(ColorsView(it.label, colorAttrId, it.value, childProductsMap.get(it.value)))
        }
        AppLog.e("colorsViewList : " + colorsViewList.toString())

        val linearLayoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.HORIZONTAL, false)
        rv_color_view.layoutManager = linearLayoutManager
        if(colorsViewList?.size!! > 0) {
            val colorViewAdapter = ColorRecyclerAdapter(activity as Context, colorsViewList)
            rv_color_view.adapter = colorViewAdapter
            colorViewAdapter.setItemClickListener(object : ColorRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(item: ProductViewFragment.ColorsView?) {

                }
            })
        }

    }

    fun setSizeViewList(){
        sizeOptionList?.forEach {
            sizeViewList?.add(SizeView(it.label, colorAttrId, it.value))
        }
        AppLog.e("sizeViewList : " + sizeViewList.toString())
    }

    fun openBottomSizeSheet()
    {
        sizeDilaog.show()
    }

    fun openProdcutImage(bitmap: Bitmap)
    {
        val view = layoutInflater.inflate(R.layout.dialog_product_image, null)
        val mImageDialog = Dialog(activity, R.style.MaterialDialogSheet)
        mImageDialog.setContentView(view)
        mImageDialog.setCancelable(true)
        mImageDialog.window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        mImageDialog.window.setGravity(Gravity.BOTTOM)
        val imageView=view.rootView.product_imageview
        imageView.setImageBitmap(bitmap)
        val backImageView=view.rootView.cancel_img
        backImageView.setOnClickListener {

            mImageDialog.dismiss()
        }
        mImageDialog.show()
    }

    data class ColorsView(var label: String?, var attr_id:String?, var value : String?,
                          var list : List<ProductListingDataClass.MediaGalleryEntry?>?)

    data class SizeView(var label: String?, var attr_id:String?, var value : String?)

    companion object {

        fun getInstance(productItem : ProductListingDataClass.Item?, productSku : String?, position : Int?) =
                ProductViewFragment().apply {
                    this.productItem = productItem
                    this.productSku = productSku
                    this.position = position
                }

    }

}
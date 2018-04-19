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
    var colorValue : String? = ""
    var sizeAttrId : String? = ""
    var sizeValue : String? = ""
    var itemQty : Int? = 1
    var productColorValue : String? = ""
    var productSizeValue : String? = ""
    var selectedQty : Int = 1
    var price : Double? = 0.0
    var specialPrice : Double? = 0.0
    var colorMap = HashMap<String, String>()
    var sizeMap = HashMap<String, String>()
    var childProductsMap = HashMap<String, MutableList<ProductListingDataClass.MediaGalleryEntry>?>()
    var colorOptionList : List<ProductOptionsResponse>? = null
    var sizeOptionList : List<ProductOptionsResponse>? = null
    private lateinit var sizeDilaogBinding: BottomSizeLayoutBinding
    private lateinit var sizeDilaog: Dialog
    var colorsViewList : MutableList<ColorsView>? = mutableListOf()
    var sizeViewList : MutableList<SizeView>? = mutableListOf()
    var maxQuantityList : MutableList<MaxQuantity>? = mutableListOf()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val listGroupBinding: ProductDetailViewBinding? = DataBindingUtil.inflate(inflater, R.layout.product_detail_view, container, false);
        productItemViewModel = ViewModelProviders.of(this).get(ProductItemViewModel::class.java)
        productItemViewModel.productItem = productItem
        listGroupBinding?.productItemVM = productItemViewModel

        sizeDilaogBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_size_layout, container,  false)
        prepareSizeDialog()

        observeEvents()
        if (Utils.isConnectionAvailable(activity as Context)) {
            getStaticPagesUrl()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }

        return listGroupBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData()

        if(productItem?.type_id.equals("configurable")){
            rl_color_view.visibility = View.VISIBLE
            getProductChildren(productItem?.sku)
        }
        else{
            rl_color_view.visibility = View.GONE
        }

        img_one.setOnClickListener{
            img_one.drawable?.run {
                val drawable = img_one.drawable as BitmapDrawable
                val bitmap = drawable.bitmap
                openProdcutImage(bitmap)
            }
        }

        img_two.setOnClickListener {
            img_two.drawable?.run {
                val drawable = img_two.drawable as BitmapDrawable
                val bitmap = drawable.bitmap
                openProdcutImage(bitmap)
            }
        }


    }

    fun setData(){

        setDescription()
        setPrice()
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

    fun setPrice(){
        if(productItem?.type_id.equals(Constants.FILTER_CONFIGURABLE_LABEL)){
            price = productItem?.extension_attributes?.regular_price
            specialPrice = productItem?.extension_attributes?.final_price
        }else{
            price = productItem?.price
            val attributes = productItem?.custom_attributes?.filter { it.attribute_code == Constants.FILTER_SPECIAL_PRICE_LABEL }?.toList()
            if(attributes?.isNotEmpty()!!) {
                specialPrice = attributes[0].value.toString().toDouble()
            }
        }
        if(price == specialPrice){
            tv_price.setText(Constants.IDR + price)
        }else {
            tv_price.setText(Constants.IDR + specialPrice)
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
                    val fragment = ProductDetailFragment.getInstance(null, item?.linked_product_sku , item?.extension_attributes?.linked_product_name, 0)
                    FragmentUtils.addFragment(context!!, fragment, null, ProductDetailFragment::class.java.name, true)
                }
            })
        }else {
            rl_wear_with_layout.visibility = View.GONE
        }

    }

    fun setProductImages(mediaGalleryList : List<ProductListingDataClass.MediaGalleryEntry>?){
        Utils.setImageViewHeight(activity as Context, img_one, 27)
        Utils.setImageViewHeight(activity as Context, img_two, 27)
        if(mediaGalleryList?.size!! > 0)
            productItemViewModel.url_one.set(mediaGalleryList.get(0).file)
        if(mediaGalleryList.size > 1)
            productItemViewModel.url_two.set(mediaGalleryList.get(1).file)

        val listSize = mediaGalleryList.size
        for(i in 2..listSize.minus(1)){
            val productImagesBinding : ProductImagesLayoutBinding? = DataBindingUtil.inflate(activity?.layoutInflater, R.layout.product_images_layout, null, false)
            productImagesBinding?.mediaGalleryEntry = mediaGalleryList.get(i)
            Utils.setImageViewHeight(activity as Context, productImagesBinding?.imgProductImage, 27)
            val view = productImagesBinding!!.root.img_product_image
            view.setOnClickListener {
                val drawable=view.drawable as BitmapDrawable
                val bitmap=drawable.bitmap
                openProdcutImage(bitmap)
            }
            ll_color_choice.addView(productImagesBinding.root)
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
                            if(index == 0) {
                                productColorValue = value.value_index.toString()
                                colorValue = productColorValue
                            }
                            colorMap.put(index.toString(), value = value.value_index.toString())
                        }
                        AppLog.e("ColorList : " + colorMap.toString())
                        colorAttrId = productItem?.extension_attributes?.configurable_product_options?.get(i)?.attribute_id
                        getProductOptions(colorAttrId, "color")
                    }
                    "Size" -> {
                        option.values.forEachIndexed { index, value ->
                            if(index == 0) {
                                productSizeValue = value.value_index.toString()
                                sizeValue = productSizeValue
                            }
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
        if (Utils.isConnectionAvailable(activity as Context)) {
            productItemViewModel.getProductChildren(productSku)
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    fun getProductOptions(attributeId : String?, label : String?){
        if (Utils.isConnectionAvailable(activity as Context)) {
            productItemViewModel.getProductOptions(attributeId, label)
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    fun getStaticPagesUrl(){
        if (Utils.isConnectionAvailable(activity as Context)) {
            productItemViewModel.getStaticPagesUrl()
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
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
                            productItemViewModel.callAddToWishListApi(colorAttrId, colorValue, sizeAttrId, sizeValue)
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
                        val colorValue = it.custom_attributes.filter { s ->
                            s.attribute_code == "color"
                        }.single().value.toString()
                        if (!childProductsMap.containsKey(colorValue)) {
                            if (colorValue.equals(productColorValue)) {
                                childProductsMap.put(colorValue, productItem?.media_gallery_entries)
                            } else {
                                childProductsMap.put(colorValue, it.media_gallery_entries)
                            }
                        }
                        val sizeValue = it.custom_attributes.filter { s ->
                            s.attribute_code == "size"
                        }.single().value.toString()
                        maxQuantityList?.add(MaxQuantity(colorValue, sizeValue, it.extension_attributes.stock_item.qty))
                    }

                    AppLog.e("maxQuantityList : " + maxQuantityList.toString())

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
            if(response is String){
                Toast.makeText(activity as Context, getString(R.string.wishlist_success_msg), Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(activity as Context, Constants.ERROR, Toast.LENGTH_SHORT).show()
            }
        })

        productItemViewModel.addToCartResponse?.observe (this, Observer<ApiResponse<AddToCartResponse>> { apiResponse ->

            val response = apiResponse?.apiResponse ?: apiResponse?.error
            if(response is AddToCartResponse){
                val userToken = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)

                if(userToken.isNullOrBlank().not()){
                    productItemViewModel.getUserCartCount()

                }else{
                    val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY)
                    if(guestCartId.isNullOrBlank().not()){
                        productItemViewModel.getGuestCartCount(guestCartId ?: "")
                    }
                }

                Toast.makeText(activity as Context, getString(R.string.add_to_cart_success_msg),Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
            }
        })

        productItemViewModel.userCartIdResponse?.observe(this, Observer {
            response ->
            val userCartId = response?.apiResponse ?: response?.error
            if(userCartId is String){
                productItemViewModel.addToUserCart(prepareAddToCartRequest(userCartId))
            }
            else {
                Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
            }

        })

        productItemViewModel.userCartCountResponse?.observe(this, Observer {
            response ->
            val userCount = response?.apiResponse ?: response?.error
            if(userCount is String){
                Utils.updateCartCount(userCount.toInt())
            }
            else {
                Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
            }

        })

        productItemViewModel.guestCartIdResponse?.observe(this, Observer {
            response ->
            val guestCartId = response?.apiResponse ?: response?.error
            if(guestCartId is String){
                productItemViewModel.addToGuestCart(prepareAddToCartRequest(guestCartId))
            }
            else {
                Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
            }

        })

        productItemViewModel.guestCartCountResponse?.observe(this, Observer {
            response ->
            val guestCount = response?.apiResponse ?: response?.error
            if(guestCount is String){
                Utils.updateCartCount(guestCount.toInt())
            }
            else {
                Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
            }

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
        colorOptionList?.forEachIndexed { index, it ->
            if(index == 0)
                colorsViewList?.add(ColorsView(it.label, colorAttrId, it.value, childProductsMap.get(it.value), true))
            else
                colorsViewList?.add(ColorsView(it.label, colorAttrId, it.value, childProductsMap.get(it.value), false))

        }
        AppLog.e("colorsViewList : " + colorsViewList.toString())

        val linearLayoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.HORIZONTAL, false)
        rv_color_view.layoutManager = linearLayoutManager
        if (colorsViewList?.size!! > 0) {
            val colorViewAdapter = ColorRecyclerAdapter(activity as Context, colorsViewList)
            rv_color_view.adapter = colorViewAdapter
            colorViewAdapter.setItemClickListener(object : ColorRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(colorView: ProductViewFragment.ColorsView?, position: Int) {
                    colorsViewList?.forEachIndexed { index,it ->
                        if(index == position){
                            colorsViewList?.get(index)?.isSelected = true
                        }else{
                            colorsViewList?.get(index)?.isSelected = false
                        }
                    }
                    colorValue = colorView?.value
                    colorViewAdapter.notifyDataSetChanged()

                    colorView?.list?.let {
                        ll_color_choice.removeAllViews()
                        setProductImages(it)
                    }
                }
            })
        }

    }

    fun setSizeViewList(){
        sizeOptionList?.forEachIndexed { index, it ->
            if(index == 0)
                sizeViewList?.add(SizeView(it.label, colorAttrId, it.value,true))
            else
                sizeViewList?.add(SizeView(it.label, colorAttrId, it.value,false))
        }
        AppLog.e("sizeViewList : " + sizeViewList.toString())
    }

    private fun prepareSizeDialog() {
        sizeDilaog = Dialog(activity, R.style.MaterialDialogSheet)
        sizeDilaog.setContentView(sizeDilaogBinding.root)
        sizeDilaog.setCancelable(true)
        sizeDilaog.window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT /*+ rl_add_to_box.height*/)
        sizeDilaog.window.setGravity(Gravity.BOTTOM)

        sizeDilaog.btn_done.setOnClickListener(View.OnClickListener {
            if(sizeDilaog.isShowing){
                sizeDilaog.dismiss()
            }

            val userToken = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)

            if(userToken.isNullOrBlank().not()){
                productItemViewModel.getCartIdForUser(userToken)
            }else{
                productItemViewModel.getCartIdForGuest()
            }
        })

        sizeDilaog.img_forward.setOnClickListener {
            if(selectedQty <= itemQty!!){
                selectedQty++
                sizeDilaog.tv_quantity.text = selectedQty.toString()
            }else{
                Toast.makeText(activity as Context, "No more product available", Toast.LENGTH_SHORT).show()
            }
        }

        sizeDilaog.img_back.setOnClickListener {
            if(selectedQty > 1){
                selectedQty--
                sizeDilaog.tv_quantity.text =  selectedQty.toString()
            }
        }

        sizeDilaog.tv_size_guide.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                Utils.openPages(activity as Context, productItemViewModel.staticPages?.size_guideline)
            }
        })

    }

    fun prepareAddToCartRequest(quoteId :  String?) : AddToCartRequest{
        var productOption : ProductOption? = null
        if(productItem?.type_id.equals("configurable")){
            val colorOption = ConfigurableItemOption(colorAttrId, colorValue)
            val sizeOption = ConfigurableItemOption(sizeAttrId, sizeValue)
            val optionList : MutableList<ConfigurableItemOption> = mutableListOf()
            optionList.add(colorOption)
            optionList.add(sizeOption)
            val cart_ext_attrs = CartExtensionAttributes( optionList)
            productOption = ProductOption(cart_ext_attrs)
        }

        val cartItem = CartItem(sku = productSku,
                qty = selectedQty,
                quote_id = quoteId,
                product_option = productOption,
                extension_attributes = null
        )

        val request = AddToCartRequest(cartItem)

        return request
    }

    fun openBottomSizeSheet()
    {
        if(productItem?.type_id.equals("simple")){
            sizeDilaog.rv_size_view.visibility = View.GONE
        }
        else{
            sizeDilaog.rv_size_view.visibility = View.VISIBLE
        }

        if(price == specialPrice){
            sizeDilaog.tv_product_price.setText(Constants.IDR + price)
        }else {
            sizeDilaog.tv_product_price.setText(Constants.IDR + specialPrice)
        }
        sizeDilaog.tv_quantity.text = selectedQty.toString()

        val linearLayoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.HORIZONTAL, false)
        sizeDilaog.rv_size_view.layoutManager = linearLayoutManager
        if(sizeViewList?.size!! > 0) {
            val sizeViewAdapter = SizeRecyclerAdapter(activity as Context, sizeViewList)
            sizeDilaog.rv_size_view.adapter = sizeViewAdapter
            sizeViewAdapter.setItemClickListener(object : SizeRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(sizeView: ProductViewFragment.SizeView?, position: Int) {
                    selectedQty = 1
                    sizeDilaog.tv_quantity.text =  selectedQty.toString()
                    sizeViewList?.forEachIndexed { index,it ->
                        if(index == position){
                            sizeViewList?.get(index)?.isSelected = true
                        }else{
                            sizeViewList?.get(index)?.isSelected = false
                        }
                    }
                    sizeValue = sizeView?.value
                    if(productItem?.type_id.equals("simple")) {
                        itemQty = productItem?.extension_attributes?.stock_item?.qty ?: 0
                    }
                    else{
                        try {
                            if(maxQuantityList?.size!! > 0) {
                                itemQty = maxQuantityList?.filter { s ->
                                    s.colorValue == colorValue && s.sizeValue == sizeValue
                                }?.single()?.maxQuantity
                            }
                        }
                        catch (e : NoSuchElementException){
                            AppLog.printStackTrace(e)
                        }
                    }
                    sizeViewAdapter.notifyDataSetChanged()
                }
            })
        }
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
                          var list : List<ProductListingDataClass.MediaGalleryEntry>?, var isSelected : Boolean?)

    data class SizeView(var label: String?, var attr_id:String?, var value : String?, var isSelected : Boolean?)

    data class MaxQuantity(var colorValue : String?, var sizeValue : String?, var maxQuantity : Int?)

    companion object {

        fun getInstance(productItem : ProductListingDataClass.Item?, productSku : String?, position : Int?) =
                ProductViewFragment().apply {
                    this.productItem = productItem
                    this.productSku = productSku
                    this.position = position
                }

    }

}
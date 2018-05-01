package com.ranosys.theexecutive.modules.productDetail

import AppLog
import android.annotation.SuppressLint
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.facebook.FacebookSdk
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
import com.ranosys.theexecutive.utils.*
import com.zopim.android.sdk.prechat.ZopimChatActivity
import kotlinx.android.synthetic.main.bottom_size_layout.*
import kotlinx.android.synthetic.main.dialog_product_image.view.*
import kotlinx.android.synthetic.main.product_detail_view.*
import kotlinx.android.synthetic.main.product_images_layout.view.*

/**
 * @Details Fragment showing the detail of products
 * @Author Ranosys Technologies
 * @Date 11,Apr,2018
 */
class ProductViewFragment : BaseFragment() {

    private lateinit var productItemViewModel : ProductItemViewModel
    private var productItem : ProductListingDataClass.Item? = null
    private var position : Int? = 0
    private var pagerPosition : Int? = 0
    private var productSku : String? = ""
    private var colorAttrId : String? = ""
    private var colorValue : String? = ""
    private var sizeAttrId : String? = ""
    private var sizeValue : String? = ""
    private var itemQty : Int? = 1
    private var selectedQty : Int = 1
    private var relatedSku : String = ""
    private var relatedName : String = ""
    private var relatedPosition : Int = 0
    private var isFromWishList : Boolean = false
    private var price : SpannableStringBuilder? = SpannableStringBuilder(Constants.ZERO)
    private var specialPrice : String? = Constants.ZERO
    private var colorMap = HashMap<String, String>()
    private var sizeMap = HashMap<String, String>()
    private var childProductsMap = HashMap<String, ImagesWithPrice?>()
    private var colorOptionList : List<ProductOptionsResponse>? = null
    private var sizeOptionList : List<ProductOptionsResponse>? = null
    private lateinit var sizeDilaogBinding: BottomSizeLayoutBinding
    private lateinit var sizeDilaog: Dialog
    private var colorsViewList : MutableList<ColorsView>? = mutableListOf()
    private var sizeViewList : MutableList<SizeView>? = mutableListOf()
    private var maxQuantityList : MutableList<MaxQuantity>? = mutableListOf()
    private var relatedProductList : MutableList<ProductListingDataClass.Item>? = mutableListOf()
    private var productLinksList : List<ProductListingDataClass.ProductLinks?>? = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val listGroupBinding: ProductDetailViewBinding? = DataBindingUtil.inflate(inflater, R.layout.product_detail_view, container, false)
        productItemViewModel = ViewModelProviders.of(this).get(ProductItemViewModel::class.java)
        productItemViewModel.productItem = productItem
        listGroupBinding?.productItemVM = productItemViewModel

        sizeDilaogBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_size_layout, container,  false)
        prepareSizeDialog()

        observeEvents()

        return listGroupBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productItemViewModel.productItem?.run {
            setData()
        }

        if (productItemViewModel.productItem?.type_id.equals(Constants.CONFIGURABLE)) {
            rl_color_view.visibility = View.VISIBLE
            getProductChildren(productItemViewModel.productItem?.sku)
        } else {
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

    private fun setData(){

        setDescription()
        if(productItemViewModel.productItem?.type_id.equals(Constants.SIMPLE)) {
            setPrice()
            setProductImages(productItemViewModel.productItem?.media_gallery_entries)
        }
        setWearWithProductsData()
        if(productItemViewModel.productItem?.type_id.equals(Constants.CONFIGURABLE)) {
            if(position == pagerPosition) {
                showLoading()
            }
            setColorImagesList()
        }
    }

    @Suppress("DEPRECATION")
    private fun setDescription(){
        try {
            val productDescription = productItemViewModel.productItem?.custom_attributes?.single { s ->
                s.attribute_code == Constants.SHORT_DESCRIPTION
            }
            tv_description.text = Html.fromHtml(productDescription?.value.toString())
        }catch (e : NoSuchElementException){
            AppLog.printStackTrace(e)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setPrice(){
        if(productItemViewModel.productItem?.type_id.equals(Constants.SIMPLE)){
            price = SpannableStringBuilder("IDR\u00A0 ${Utils.getFromattedPrice(productItemViewModel.productItem?.price!!)}")
            val attributes = productItemViewModel.productItem?.custom_attributes?.filter { it.attribute_code == Constants.FILTER_SPECIAL_PRICE_LABEL }?.toList()
            if (attributes?.isNotEmpty()!!) {
                specialPrice = attributes[0].value.toString()
            }
            val ss = getDisplayPrice(productItemViewModel.productItem?.price!!, specialPrice.toString())
            tv_price.text = ss
        }

    }

    private fun setWearWithProductsData(){
        val linearLayoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.HORIZONTAL, false)
        list_wear_with_products.layoutManager = linearLayoutManager
        productLinksList = productItemViewModel.productItem?.product_links?.filter {
            it -> it?.link_type == Constants.RELATED
        }

        if(productLinksList?.size!! > 0) {
            rl_wear_with_layout.visibility = View.VISIBLE
            val wearWithAdapter = WearWithProductsAdapter(activity as Context, productLinksList)
            list_wear_with_products.adapter = wearWithAdapter
            wearWithAdapter.setItemClickListener(object : WearWithProductsAdapter.OnItemClickListener {
                override fun onItemClick(item: ProductListingDataClass.ProductLinks?, position: Int) {
                    relatedProductList?.clear()
                    productLinksList?.forEach {
                        showLoading()
                        relatedSku = item?.linked_product_sku!!
                        relatedName = item.extension_attributes.linked_product_name
                        relatedPosition = position
                        getProductDetail(it?.linked_product_sku)
                    }
                }
            })
        }else {
            rl_wear_with_layout.visibility = View.GONE
        }

    }

    private fun getProductDetail(productSku : String?){
        if (Utils.isConnectionAvailable(activity as Context)) {
            productItemViewModel.getProductDetail(productSku)
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }

    }

    private fun setProductImages(mediaGalleryList : List<ProductListingDataClass.MediaGalleryEntry>?){
        Utils.setImageViewHeight(activity as Context, img_one, 27)
        Utils.setImageViewHeight(activity as Context, img_two, 27)
        if(mediaGalleryList?.size!! > 0)
            productItemViewModel.url_one.set(mediaGalleryList[0].file)
        if(mediaGalleryList.size > 1) {
            img_two.visibility = View.VISIBLE
            productItemViewModel.url_two.set(mediaGalleryList[1].file)
        }else{
            img_two.visibility = View.GONE
        }

        val listSize = mediaGalleryList.size
        for(i in 2..listSize.minus(1)){
            val productImagesBinding : ProductImagesLayoutBinding? = DataBindingUtil.inflate(activity?.layoutInflater, R.layout.product_images_layout, null, false)
            productImagesBinding?.mediaGalleryEntry = mediaGalleryList[i]
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

    private fun setColorImagesList(){
        productItemViewModel.productItem?.extension_attributes?.configurable_product_options?.run{
            val length = productItemViewModel.productItem?.extension_attributes?.configurable_product_options?.size!!
            for(i in 0 .. length-1) {
                val option = productItemViewModel.productItem?.extension_attributes?.configurable_product_options?.get(i)
                when (option?.label) {
                    Constants.COLOR_ -> {
                        option.values.forEachIndexed { index, value ->
                            if(index == 0) {
                                colorValue = value.value_index.toString()
                            }
                            colorMap[index.toString()] = value.value_index.toString()
                        }
                        AppLog.e("ColorList : $colorMap")
                        colorAttrId = productItemViewModel.productItem?.extension_attributes?.configurable_product_options?.get(i)?.attribute_id
                        if(null == GlobalSingelton.instance?.colorList) {
                            getProductOptions(colorAttrId, Constants.COLOR)
                        }else{
                            colorOptionList = GlobalSingelton.instance?.colorList!!.filter {
                                it.value in colorMap.values
                            }
                            AppLog.e("New color list : $colorOptionList")
                        }
                    }
                    Constants.SIZE_ -> {
                        option.values.forEachIndexed({ index, value ->
                            sizeMap[index.toString()] = value.value_index.toString()
                        })
                        AppLog.e("Sizelist :  $sizeMap")
                        sizeAttrId = productItemViewModel.productItem?.extension_attributes?.configurable_product_options?.get(i)?.attribute_id
                        if(null == GlobalSingelton.instance?.sizeList) {
                            getProductOptions(sizeAttrId, Constants.SIZE)
                        }else{
                            sizeOptionList = GlobalSingelton.instance?.sizeList!!.filter {
                                it.value in sizeMap.values
                            }
                            AppLog.e("New size list : $sizeOptionList")
                        }
                    }
                }
            }
        }
    }

    private fun getProductChildren(productSku : String?){
        if (Utils.isConnectionAvailable(activity as Context)) {
            productItemViewModel.getProductChildren(productSku)
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    private fun getProductOptions(attributeId : String?, label : String?){
        if (Utils.isConnectionAvailable(activity as Context)) {
            productItemViewModel.getProductOptions(attributeId, label)
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    private fun observeEvents(){
        productItemViewModel.clickedAddBtnId?.observe(this, Observer<Int> { id ->
            when (id){
                R.id.btn_add_to_bag -> {
                    openBottomSizeSheet(false)
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_composition_and_care -> {
                    prepareWebPageDialog(activity as Context, GlobalSingelton.instance?.staticPagesResponse?.composition_and_care ,getString(R.string.composition))
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_size_guideline -> {
                    prepareWebPageDialog(activity as Context, GlobalSingelton.instance?.staticPagesResponse?.size_guideline ,getString(R.string.size_guideline))
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_shipping -> {
                    prepareWebPageDialog(activity as Context, GlobalSingelton.instance?.staticPagesResponse?.shipping ,getString(R.string.shipping))
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_return -> {
                    prepareWebPageDialog(activity as Context, GlobalSingelton.instance?.staticPagesResponse?.returns ,getString(R.string.returns))
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_share -> {
                    shareProductUrl()
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_buying_guidelinie -> {
                    prepareWebPageDialog(activity as Context, GlobalSingelton.instance?.staticPagesResponse?.buying_guideline ,getString(R.string.buying_guideline))
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_chat -> {
                    startActivity(Intent(FacebookSdk.getApplicationContext(), ZopimChatActivity::class.java))
                    productItemViewModel.clickedAddBtnId?.value = null
                }
                R.id.tv_wishlist -> {
                    manageWishListPopUp()
                    productItemViewModel.clickedAddBtnId?.value = null
                }
            }

        })

        productItemViewModel.productChildrenResponse?.observe(this, Observer<ApiResponse<List<ChildProductsResponse>>> { apiResponse ->
            val response = apiResponse?.apiResponse ?: apiResponse?.error
            if (response is List<*>) {
                val list = response as List<ChildProductsResponse>

                list.forEach { it ->
                    try {
                        val colorValue = it.custom_attributes.single { s ->
                            s.attribute_code == Constants.COLOR
                        }.value.toString()
                        if (!childProductsMap.containsKey(colorValue)) {
                            val configurePrice = it.price
                            var configureSpecialPrice = Constants.ZERO
                            val attributes = it.custom_attributes.filter {
                                it.attribute_code == Constants.FILTER_SPECIAL_PRICE_LABEL
                            }.toList()
                            if (attributes.isNotEmpty()) {
                                configureSpecialPrice = attributes[0].value.toString()
                            }

                            val ss = getDisplayPrice(configurePrice, configureSpecialPrice)
                            childProductsMap[colorValue] = ImagesWithPrice(ss, it.media_gallery_entries)

                        }

                        val sizeValue = it.custom_attributes.single { s ->
                            s.attribute_code == Constants.SIZE
                        }.value.toString()
                        val configSimplePrice = it.price
                        var configSpecialPrice = Constants.ZERO
                        val sp = it.custom_attributes.filter { s ->
                            s.attribute_code == Constants.FILTER_SPECIAL_PRICE_LABEL
                        }.toList()
                        if(sp.isNotEmpty()){
                            configSpecialPrice = sp[0].value.toString()
                        }
                        val ss = getDisplayPrice(configSimplePrice, configSpecialPrice)
                        maxQuantityList?.add(MaxQuantity(colorValue, sizeValue, it.extension_attributes.stock_item.qty,
                                it.extension_attributes.stock_item.is_in_stock, ss))
                    }catch (e : Exception){
                        AppLog.printStackTrace(e)
                    }
                }

                AppLog.e("maxQuantityList : " + maxQuantityList.toString())

                setColorViewList()
                setSizeViewList()

                AppLog.e("ChildProductsMap : " + childProductsMap.toString())
                hideLoading()

            } else {
                Toast.makeText(activity, apiResponse?.error, Toast.LENGTH_LONG).show()
            }
        })

        productItemViewModel.productOptionResponse?.observe(this, Observer<ApiResponse<List<ProductOptionsResponse>>> { apiResponse ->
            val response = apiResponse?.apiResponse
            if (response is List<*>) {
                when(response[0].label){
                    Constants.COLOR -> {
                        colorOptionList = response.filter {
                            it.value in colorMap.values
                        }
                        AppLog.e("New color list : $colorOptionList")
                    }
                    Constants.SIZE -> {
                        sizeOptionList = response.filter {
                            it.value in sizeMap.values
                        }
                        AppLog.e("New size list : $sizeOptionList")
                    }
                }

            } else {
                Toast.makeText(activity, apiResponse?.error, Toast.LENGTH_LONG).show()
            }
        })

        productItemViewModel.addToWIshListResponse?.observe(this, Observer { apiResponse ->
            hideLoading()
            if(apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is String) {
                    Toast.makeText(activity as Context, getString(R.string.wishlist_success_msg), Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(activity as Context, apiResponse?.error, Toast.LENGTH_SHORT).show()
            }
        })

        productItemViewModel.addToCartResponse?.observe (this, Observer<ApiResponse<AddToCartResponse>> { apiResponse ->

            if(apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is AddToCartResponse) {
                    val userToken = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
                    if (userToken.isNullOrBlank().not()) {
                        productItemViewModel.getUserCartCount()

                    } else {
                        val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY)
                        if (guestCartId.isNullOrBlank().not()) {
                            productItemViewModel.getGuestCartCount(guestCartId ?: "")
                        }
                    }
                    Toast.makeText(activity as Context, getString(R.string.add_to_cart_success_msg), Toast.LENGTH_SHORT).show()
                }
            }else {
                hideLoading()
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })

        productItemViewModel.userCartIdResponse?.observe(this, Observer {
            response ->
            if(response?.error.isNullOrEmpty()){
                val userCartId = response?.apiResponse
                if(userCartId is String){
                    productItemViewModel.addToUserCart(prepareAddToCartRequest(userCartId))
                }
            }else {
                hideLoading()
                Toast.makeText(activity, response?.error, Toast.LENGTH_LONG).show()
            }

        })

        productItemViewModel.userCartCountResponse?.observe(this, Observer {
            response ->
            hideLoading()
            if(response?.error.isNullOrEmpty()) {
                val userCount = response?.apiResponse
                if (userCount is String) {
                    try {
                        Utils.updateCartCount(userCount.toInt())
                    } catch (e: NumberFormatException) {
                        AppLog.printStackTrace(e)
                    }
                }
            }
            else {
                Toast.makeText(activity, response?.error, Toast.LENGTH_LONG).show()
            }

        })

        productItemViewModel.guestCartIdResponse?.observe(this, Observer {
            response ->
            if(response?.error.isNullOrEmpty()) {
                val guestCartId = response?.apiResponse
                if (guestCartId is String) {
                    productItemViewModel.addToGuestCart(prepareAddToCartRequest(guestCartId))
                }
            }
            else {
                hideLoading()
                Toast.makeText(activity,response?.error, Toast.LENGTH_LONG).show()
            }

        })

        productItemViewModel.guestCartCountResponse?.observe(this, Observer {
            response ->
            hideLoading()
            if(response?.error.isNullOrEmpty()) {
                val guestCount = response?.apiResponse
                if (guestCount is String) {
                    try {
                        Utils.updateCartCount(guestCount.toInt())
                    } catch (e: NumberFormatException) {
                        AppLog.printStackTrace(e)
                    }
                }
            }
            else {
                Toast.makeText(activity, response?.error, Toast.LENGTH_LONG).show()
            }

        })

        productItemViewModel.productDetailResponse?.observe(this, Observer<ApiResponse<ProductListingDataClass.Item>> { apiResponse ->
            val response = apiResponse?.apiResponse ?: apiResponse?.error
            if (response is ProductListingDataClass.Item) {
                relatedProductList?.add(response)
                if(productLinksList?.size == relatedProductList?.size){
                    hideLoading()
                    val fragment = ProductDetailFragment.getInstance(relatedProductList, relatedSku , relatedName, relatedPosition)
                    FragmentUtils.addFragment(context!!, fragment, null, ProductDetailFragment::class.java.name, true)
                }
            } else {
                hideLoading()
                Toast.makeText(activity, Constants.ERROR, Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun manageWishListPopUp(){
        //check for logged in user
        if((SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY) ?: "").isBlank()){
            //show toast to user to login
            Toast.makeText(activity as Context, getString(R.string.login_required_error), Toast.LENGTH_SHORT).show()
            setToolBarParams(getString(R.string.login), 0, "", R.drawable.cancel, true, 0, false, true)
            val bundle = Bundle()
            bundle.putBoolean(Constants.LOGIN_REQUIRED_PROMPT, true)
            FragmentUtils.addFragment(activity as Context, LoginFragment(), bundle, LoginFragment::class.java.name, true)
        }else{
            if(productItemViewModel.productItem?.type_id.equals(Constants.CONFIGURABLE)){
                openBottomSizeSheet(true)
            }
            else{
                callWishListApi()
            }
        }

    }

    private fun callWishListApi(){
        if (Utils.isConnectionAvailable(activity as Context)) {
            showLoading()
            productItemViewModel.callAddToWishListApi(colorAttrId, colorValue, sizeAttrId, sizeValue)
        } else {
            Utils.showNetworkErrorDialog(activity as Context)
        }
    }

    private fun getDisplayPrice(configurePrice: String, configureSpecialPrice: String): SpannableStringBuilder {
        return if(configurePrice.toDouble() > configureSpecialPrice.toDouble() && !configureSpecialPrice.equals(Constants.ZERO)){
            val normalP = "IDR\u00A0" + Utils.getFromattedPrice(configurePrice)
            val specialP = "IDR\u00A0" + Utils.getFromattedPrice(configureSpecialPrice)
            val displayPrice = "$normalP $specialP"
            SpannableStringBuilder(displayPrice).apply {
                setSpan(StrikethroughSpan(), 0, normalP.length, 0)
                setSpan(ForegroundColorSpan(Color.RED), normalP.length, displayPrice.length, 0)
                setSpan(RelativeSizeSpan(1.1f), normalP.length, displayPrice.length, 0)
            }
        }else{
            val normalP = "IDR\u00A0" + Utils.getFromattedPrice(configurePrice)
            SpannableStringBuilder(normalP)
        }
    }

    private fun shareProductUrl() {
        val baseUrl = BuildConfig.API_URL
        val url = productItemViewModel.productItem?.custom_attributes?.find { it.attribute_code == Constants.URL_KEY }.let { it?.value }.toString()
        val urlSuffix = Constants.URL_SUFFIX
        if(url.isNotBlank()){
            Utils.shareUrl(activity as Context, "$baseUrl$url$urlSuffix")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setColorViewList(){
        colorOptionList?.run {
            if (colorOptionList?.size!! > 0) {
                colorOptionList?.forEachIndexed { index, it ->
                    if (index == 0) {
                        colorsViewList?.add(ColorsView(it.label, colorAttrId, it.value, childProductsMap[it.value]?.list, childProductsMap[it.value]?.price, true))
                        setProductImages(childProductsMap[it.value]?.list)
                        price = childProductsMap[it.value]?.price
                        tv_price.text = childProductsMap[it.value]?.price
                    } else {
                        colorsViewList?.add(ColorsView(it.label, colorAttrId, it.value, childProductsMap[it.value]?.list, childProductsMap[it.value]?.price, false))
                    }

                }
            } else {
                childProductsMap[colorValue]?.run {
                    price = childProductsMap[colorValue]?.price
                    tv_price.text = childProductsMap[colorValue]?.price
                }
            }

            AppLog.e("colorsViewList : " + productItemViewModel.productItem?.sku + " " + colorsViewList.toString())

            val linearLayoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.HORIZONTAL, false)
            rv_color_view.layoutManager = linearLayoutManager
            if (colorsViewList?.size!! > 0) {
                val colorViewAdapter = ColorRecyclerAdapter(activity as Context, colorsViewList)
                rv_color_view.adapter = colorViewAdapter
                colorViewAdapter.setItemClickListener(object : ColorRecyclerAdapter.OnItemClickListener {
                    override fun onItemClick(item: ProductViewFragment.ColorsView?, position: Int) {
                        colorsViewList?.forEachIndexed { index, _ ->
                            colorsViewList?.get(index)?.isSelected = index == position
                        }
                        colorValue = item?.value
                        price = item?.price
                        tv_price.text = item?.price
                        colorViewAdapter.notifyDataSetChanged()
                        item?.list?.let {
                            ll_color_choice.removeAllViews()
                            setProductImages(it)
                        }
                    }
                })
            }
        }

    }

    private fun setSizeViewList(){
        sizeOptionList?.forEachIndexed { index, it ->
            if(index == 0)
                sizeViewList?.add(SizeView(it.label, sizeAttrId, it.value,false))
            else
                sizeViewList?.add(SizeView(it.label, sizeAttrId, it.value,false))
        }
        AppLog.e("sizeViewList : " + sizeViewList.toString())
    }

    private fun prepareSizeDialog() {
        sizeDilaog = Dialog(activity, R.style.MaterialDialogSheet)
        sizeDilaog.setContentView(sizeDilaogBinding.root)
        sizeDilaog.setCancelable(true)
        sizeDilaog.window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT /*+ rl_add_to_box.height*/)
        sizeDilaog.window.setGravity(Gravity.BOTTOM)

        sizeDilaog.btn_done.setOnClickListener({
            if (productItemViewModel.productItem?.type_id.equals(Constants.CONFIGURABLE)) {
                if (!sizeValue.isNullOrEmpty()) {
                    if (sizeDilaog.isShowing) {
                        sizeDilaog.dismiss()
                    }
                    if(isFromWishList) {
                        callWishListApi()
                    }else{
                        addToCartCall()
                    }
                } else {
                    Toast.makeText(activity, getString(R.string.select_size_err), Toast.LENGTH_SHORT).show()
                }
            } else {
                if (sizeDilaog.isShowing) {
                    sizeDilaog.dismiss()
                }
                addToCartCall()
            }
        })

        sizeDilaog.img_forward.setOnClickListener {
            if(productItemViewModel.productItem?.type_id.equals(Constants.CONFIGURABLE)) {
                if (!sizeValue.isNullOrEmpty()) {
                    if (selectedQty < itemQty!!) {
                        selectedQty++
                        sizeDilaog.tv_quantity.text = selectedQty.toString()
                    } else {
                        Toast.makeText(activity as Context, getString(R.string.no_more_products), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(activity, getString(R.string.select_size_err), Toast.LENGTH_SHORT).show()
                }
            }else{
                if (selectedQty < itemQty!!) {
                    selectedQty++
                    sizeDilaog.tv_quantity.text = selectedQty.toString()
                } else {
                    Toast.makeText(activity as Context, getString(R.string.no_more_products), Toast.LENGTH_SHORT).show()
                }
            }
        }

        sizeDilaog.img_back.setOnClickListener {
            if(selectedQty > 1){
                selectedQty--
                sizeDilaog.tv_quantity.text =  selectedQty.toString()
            }
        }

        sizeDilaog.tv_size_guide.setOnClickListener { prepareWebPageDialog(activity as Context, GlobalSingelton.instance?.staticPagesResponse?.size_guideline ,getString(R.string.size_guideline)) }

    }

    private fun addToCartCall(){
        showLoading()
        val userToken = SavedPreferences.getInstance()?.getStringValue(Constants.USER_ACCESS_TOKEN_KEY)
        if (userToken.isNullOrBlank().not()) {
            productItemViewModel.getCartIdForUser(userToken)
        } else {
            val guestCartId = SavedPreferences.getInstance()?.getStringValue(Constants.GUEST_CART_ID_KEY) ?: ""
            if (guestCartId.isNotBlank()) {
                productItemViewModel.addToGuestCart(prepareAddToCartRequest(guestCartId))
            } else {
                productItemViewModel.getCartIdForGuest()
            }
        }
    }

    private fun prepareAddToCartRequest(quoteId :  String?) : AddToCartRequest{
        var productOption : ProductOption? = null
        if(productItemViewModel.productItem?.type_id.equals(Constants.CONFIGURABLE)){
            val colorOption = ConfigurableItemOption(colorAttrId, colorValue)
            val sizeOption = ConfigurableItemOption(sizeAttrId, sizeValue)
            val optionList : MutableList<ConfigurableItemOption> = mutableListOf()
            optionList.add(colorOption)
            optionList.add(sizeOption)
            val cartExtAttrs = CartExtensionAttributes( optionList)
            productOption = ProductOption(cartExtAttrs)
        }

        val cartItem = CartItem(sku = productSku,
                qty = selectedQty,
                quote_id = quoteId,
                product_option = productOption,
                extension_attributes = null
        )

        AppLog.e("AddToCartRequest : " + colorValue + sizeValue + selectedQty)

        return AddToCartRequest(cartItem)
    }

    @SuppressLint("SetTextI18n")
    private fun openBottomSizeSheet(fromWishList : Boolean) {
        isFromWishList = fromWishList

        if(isFromWishList){
            sizeDilaog.btn_done.text = getString(R.string.add_to_wishlist)
            sizeDilaog.tv_select_quantity.visibility = View.GONE
            sizeDilaog.ll_layout.visibility = View.GONE
        }else{
            sizeDilaog.btn_done.text = getString(R.string.add_to_bag)
            sizeDilaog.tv_select_quantity.visibility = View.VISIBLE
            sizeDilaog.ll_layout.visibility = View.VISIBLE
        }

        if(productItemViewModel.productItem?.type_id.equals(Constants.SIMPLE)){
            sizeDilaog.rv_size_view.visibility = View.GONE
            sizeDilaog.tv_select_size.visibility = View.GONE
        }
        else{
            sizeDilaog.rv_size_view.visibility = View.VISIBLE
            sizeDilaog.tv_select_size.visibility = View.VISIBLE
        }

        sizeDilaog.tv_product_price.text = price
        selectedQty = 1
        itemQty = 1
        sizeDilaog.tv_quantity.text = selectedQty.toString()

        val linearLayoutManager = LinearLayoutManager(activity as Context, LinearLayoutManager.HORIZONTAL, false)
        sizeDilaog.rv_size_view.layoutManager = linearLayoutManager
        if(sizeViewList?.size!! > 0) {
            sizeViewList?.forEach {
                s-> s.isSelected = false
            }

            sizeValue = ""
            val sizeViewAdapter = SizeRecyclerAdapter(activity as Context, sizeViewList, colorValue, maxQuantityList)
            sizeDilaog.rv_size_view.adapter = sizeViewAdapter
            sizeViewAdapter.setItemClickListener(object : SizeRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(item: SizeView?, position: Int, priceList: List<ProductViewFragment.MaxQuantity>?, inStock : Boolean?) {

                    if (inStock!!) {
                        selectedQty = 1
                        sizeDilaog.tv_quantity.text = selectedQty.toString()
                        sizeViewList?.forEachIndexed { index, _ ->
                            sizeViewList?.get(index)?.isSelected = index == position
                        }
                        sizeValue = item?.value

                        val selectedSizePrice = priceList?.single {
                            it.colorValue == colorValue && it.sizeValue == sizeValue
                        }?.price
                        sizeDilaog.tv_product_price.text = selectedSizePrice

                        if (productItemViewModel.productItem?.type_id.equals(Constants.SIMPLE)) {
                            itemQty = productItemViewModel.productItem?.extension_attributes?.stock_item?.qty ?: 0
                        } else {
                            try {
                                if (maxQuantityList?.size!! > 0) {
                                    itemQty = maxQuantityList?.single { s ->
                                        s.colorValue == colorValue && s.sizeValue == sizeValue
                                    }?.maxQuantity
                                }
                            } catch (e: NoSuchElementException) {
                                AppLog.printStackTrace(e)
                            }
                        }
                    }
                    sizeViewAdapter.notifyDataSetChanged()
                }
            })
        }
        else{
            if(productItemViewModel.productItem?.type_id.equals(Constants.SIMPLE)) {
                itemQty = productItemViewModel.productItem?.extension_attributes?.stock_item?.qty ?: 0
            }
        }
        sizeDilaog.show()
    }

    @SuppressLint("InflateParams")
    private fun openProdcutImage(bitmap: Bitmap) {
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
                          var list : List<ProductListingDataClass.MediaGalleryEntry>?, var price: SpannableStringBuilder?, var isSelected : Boolean?)

    data class SizeView(var label: String?, var attr_id:String?, var value : String?, var isSelected : Boolean?)

    data class ImagesWithPrice(var price: SpannableStringBuilder?, var list : List<ProductListingDataClass.MediaGalleryEntry>?)

    data class MaxQuantity(var colorValue: String?, var sizeValue: String?, var maxQuantity: Int?, var isInStock: Boolean = true, var price: SpannableStringBuilder?)

    companion object {
        fun getInstance(productItem : ProductListingDataClass.Item?, productSku : String?, position : Int?, pagerPosition : Int?) =
                ProductViewFragment().apply {
                    this.productItem = productItem
                    this.productSku = productSku
                    this.position = position
                    this.pagerPosition = pagerPosition
                }
    }

}
package com.delamibrands.theexecutive.modules.category.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.delamibrands.theexecutive.R
import com.delamibrands.theexecutive.databinding.PromotionViewBinding
import com.delamibrands.theexecutive.modules.category.PromotionsResponseDataClass
import com.delamibrands.theexecutive.utils.Constants
import com.delamibrands.theexecutive.utils.Utils

/**
 * @Details adapter for promotion banners
 * @Author Ranosys Technologies
 * @Date 21,Feb,2018
 */

class CustomViewPageAdapter(context : Context, list : List<PromotionsResponseDataClass>?) : PagerAdapter() {

    private var orientationOrder = 0

    interface OnItemClickListener {
        fun onItemClick(item : PromotionsResponseDataClass?)
    }

    private var clickListener: OnItemClickListener? = null
    var context : Context? = null
    private var layoutInflater : LayoutInflater? = null
    var promotionList : List<PromotionsResponseDataClass>? = null

    fun setItemClickListener(listener: OnItemClickListener){
        clickListener = listener
    }

    init {
        this.context = context
        promotionList = list
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        //Utils.setImageViewHeightWrtDeviceWidth((view as RelativeLayout).getChildAt(0).context, (view as RelativeLayout).getChildAt(0) as ImageView, Constants.IMAGE_RATIO)
        if(orientationOrder == 0){
            ((view as RelativeLayout).getChildAt(0) as ImageView).scaleType = ImageView.ScaleType.CENTER_CROP

        }else{
            ((view as RelativeLayout).getChildAt(0) as ImageView).scaleType = ImageView.ScaleType.FIT_XY
        }

        return view == `object` as RelativeLayout
    }

    override fun getCount(): Int {
        promotionList?.run {
            return size
        }
        return 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val listGroupBinding: PromotionViewBinding? = DataBindingUtil.inflate(layoutInflater, R.layout.promotion_view, container, false)
        listGroupBinding?.promotionResponse = promotionList?.get(position)

        listGroupBinding?.imgPromotion?.setOnClickListener {
            clickListener?.onItemClick(promotionList?.get(position))

        }
        container.addView(listGroupBinding?.root)
        return listGroupBinding?.root!!
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    fun refresh(orientation:Int){
        orientationOrder = orientation
        notifyDataSetChanged()
    }
}
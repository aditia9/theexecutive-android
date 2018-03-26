package com.ranosys.theexecutive.base

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import android.databinding.ObservableField
import android.support.design.widget.TextInputLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.AppCompatSpinner
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ExpandableListView
import android.widget.ImageView
import com.ranosys.theexecutive.modules.category.CategoryResponseDataClass
import com.ranosys.theexecutive.modules.category.PromotionsResponseDataClass
import com.ranosys.theexecutive.modules.category.adapters.CategoryThreeLevelAdapter
import com.ranosys.theexecutive.modules.category.adapters.CustomViewPageAdapter


/**
 * Created by Mohammad Sunny on 22/2/18.
 */
class BindingAdapters {

    companion object {
        @JvmStatic
        @BindingAdapter("app:errorText")
        fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
            view.error = errorMessage
            if (TextUtils.isEmpty(errorMessage)) {
                view.isErrorEnabled = false
            } else {
                view.isErrorEnabled = true
            }
        }

        @JvmStatic
        @BindingAdapter(value = *arrayOf("selectedValue", "selectedValueAttrChanged"), requireAll = false)
        fun bindSpinnerData(pAppCompatSpinner: AppCompatSpinner, newSelectedValue: String?, newTextAttrChanged: InverseBindingListener) {
            pAppCompatSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    newTextAttrChanged.onChange()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            })
            if (newSelectedValue != null) {
                val pos = (pAppCompatSpinner.getAdapter() as ArrayAdapter<String>).getPosition(newSelectedValue)
                pAppCompatSpinner.setSelection(pos, true)
            }
        }

        @InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
        fun captureSelectedValue(pAppCompatSpinner: AppCompatSpinner): String {
            return pAppCompatSpinner.selectedItem.toString()

        }

        @JvmStatic
        @BindingAdapter("android:src")
        fun setImageResoruce(imageView: ImageView, resource: Int) {
            imageView.setImageResource(resource)
        }

        @JvmStatic
        @BindingAdapter("categoryItems")
        fun bindList(view: ExpandableListView, response: ObservableField<CategoryResponseDataClass>?) {
            val adapter = CategoryThreeLevelAdapter(view.context, response?.get()?.children_data)
            view.setAdapter(adapter)
        }

        @JvmStatic
        @BindingAdapter("promotionData")
        fun bindViewPager(view: ViewPager, response: ObservableField<List<PromotionsResponseDataClass>>?) {
            val customViewPagerAdapter = CustomViewPageAdapter(view.context, response?.get())
            view.adapter = customViewPagerAdapter
        }
    }
}
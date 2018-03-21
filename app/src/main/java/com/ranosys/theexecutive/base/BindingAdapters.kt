package com.ranosys.theexecutive.base

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.AppCompatSpinner
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions


/**
 * Created by Mohammad Sunny on 31/1/18.
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
        @BindingAdapter("bind:imageUrl")
        fun loadImage(imageView: ImageView, imageUrl: String) {

        }
    }
}
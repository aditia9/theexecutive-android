package com.ranosys.theexecutive.base

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import android.support.v7.widget.AppCompatSpinner
import android.view.View
import android.widget.*
import com.ranosys.theexecutive.modules.home.ChildrenData
import com.ranosys.theexecutive.modules.home.HomeTwoLevelAdapter


/**
 * Created by Mohammad Sunny on 31/1/18.
 */
class BindingAdapters {

    companion object {
        @JvmStatic
        @BindingAdapter("app:errorText")
        fun setErrorMessage(view: EditText, errorMessage: String?) {
            view.setError(errorMessage)
//        if (TextUtils.isEmpty(errorMessage)) {
//            view.setErrorEnabled(false)
//        } else {
//            view.setErrorEnabled(true)
//        }
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
            return pAppCompatSpinner.getSelectedItem().toString()

        }

        @JvmStatic
        @BindingAdapter("android:src")
        fun setImageResoruce(imageView: ImageView, resource: Int) {
            imageView.setImageResource(resource)
        }

        @JvmStatic
        @BindingAdapter("categoryItems")
        fun bindList(view: ExpandableListView, list: ArrayList<ChildrenData>) {
            val adapter = HomeTwoLevelAdapter(list)
            view.setAdapter(adapter)
        }
    }
}
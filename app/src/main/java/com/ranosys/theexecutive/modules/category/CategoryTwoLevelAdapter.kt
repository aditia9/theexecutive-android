package com.ranosys.theexecutive.modules.category

import android.content.Context
import android.database.DataSetObserver
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.RowSecondBinding
import com.ranosys.theexecutive.databinding.RowThirdBinding
import kotlinx.android.synthetic.main.row_second.view.*

/**
 * Created by Mohammad Sunny on 12/3/18.
 */
class CategoryTwoLevelAdapter(context: Context?, list :ArrayList<ChildrenData>?) : ExpandableListAdapter {

    var categoryList: ArrayList<ChildrenData>?

    init {
        categoryList = list
    }

    override fun getGroupCount(): Int {
        if(null != categoryList && categoryList!!.size > 0)
            return categoryList?.size!!
        else
            return 0
    }

    override fun getGroupView(p0: Int, isExpanded: Boolean, p2: View?, p3: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(p3?.context)
        val listGroupBinding: RowSecondBinding = DataBindingUtil.inflate(layoutInflater, R.layout.row_second, p3, false);
        listGroupBinding.childData = getGroup(p0)
        if(isExpanded){
            listGroupBinding.root.img_expand_collapse.setImageResource(R.drawable.dropdown)
        }
        else{
            listGroupBinding.root.img_expand_collapse.setImageResource(R.drawable.forward)
        }
        return listGroupBinding.root
    }

    override fun getGroup(p0: Int): ChildrenData? {
        if(null != categoryList && categoryList!!.size > 0)
            return categoryList?.get(p0)
        else
            return null
    }

    override fun getChildrenCount(p0: Int): Int {
        if(null != categoryList && categoryList!!.size > 0)
            return categoryList?.get(p0)?.children_data?.size!!
        else
            return 0
    }

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(p4?.context)
        val listChildBinding : RowThirdBinding = DataBindingUtil.inflate(layoutInflater, R.layout.row_third, p4, false);
        listChildBinding.childData = getChild(p0, p1)
        return listChildBinding.root
    }

    override fun getChild(p0: Int, p1: Int): ChildrenData? {
        return categoryList?.get(p0)?.children_data?.get(p1)
    }

    override fun onGroupCollapsed(p0: Int) {
    }

    override fun isEmpty(): Boolean {
        return false
    }

    override fun registerDataSetObserver(p0: DataSetObserver?) {
    }

    override fun onGroupExpanded(p0: Int) {
    }

    override fun getCombinedChildId(p0: Long, p1: Long): Long {
        return p1
    }

    override fun getGroupId(p0: Int): Long {
        return p0.toLong()
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun areAllItemsEnabled(): Boolean {
        return true
    }

    override fun getChildId(p0: Int, p1: Int): Long {
        return p1.toLong()
    }

    override fun getCombinedGroupId(p0: Long): Long {
        return p0
    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {
    }

}
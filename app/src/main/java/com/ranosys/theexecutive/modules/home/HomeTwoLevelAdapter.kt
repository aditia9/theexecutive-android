package com.ranosys.theexecutive.modules.home

import android.database.DataSetObserver
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.RowFirstBinding
import com.ranosys.theexecutive.databinding.RowSecondBinding

/**
 * Created by Mohammad Sunny on 9/3/18.
 */
class HomeTwoLevelAdapter(list : ArrayList<ChildrenData>?) : ExpandableListAdapter{

    var categoryList: ArrayList<ChildrenData>?

    init {
        categoryList = list
    }

    override fun getChildrenCount(p0: Int): Int {
        if(null != categoryList && categoryList!!.size > 0)
            return categoryList?.get(0)?.children_data?.get(p0)?.children_data?.size!!
        else
            return 0
    }

    override fun getGroup(p0: Int): ChildrenData? {
        return categoryList?.get(p0)?.children_data?.get(p0)
    }

    override fun onGroupCollapsed(p0: Int) {
    }

    override fun isEmpty(): Boolean {
        return false
    }

    override fun registerDataSetObserver(p0: DataSetObserver?) {
    }

    override fun getChild(p0: Int, p1: Int): ChildrenData? {
        return categoryList?.get(p0)?.children_data?.get(p0)?.children_data?.get(p1)
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

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(p4?.context)
        val listChildBinding : RowSecondBinding = DataBindingUtil.inflate(layoutInflater, R.layout.row_second, p4, false);
        listChildBinding.childData = getChild(p0, p1)
        return listChildBinding.root
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

    override fun getGroupView(p0: Int, p1: Boolean, p2: View?, p3: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(p3?.context)
        val listGroupBinding: RowFirstBinding = DataBindingUtil.inflate(layoutInflater, R.layout.row_first, p3, false);
        listGroupBinding.childData = getGroup(p0)
        return listGroupBinding.root
    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {
    }

    override fun getGroupCount(): Int {
        if(null != categoryList && categoryList!!.size > 0)
            return categoryList?.size!!
        else
            return 0
    }
}
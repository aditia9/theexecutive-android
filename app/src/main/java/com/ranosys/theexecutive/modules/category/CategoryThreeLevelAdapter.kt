package com.ranosys.theexecutive.modules.category

import android.content.Context
import android.database.DataSetObserver
import android.databinding.DataBindingUtil
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.RowFirstBinding

/**
 * Created by Mohammad Sunny on 9/3/18.
 */
class CategoryThreeLevelAdapter(context: Context?, list : ArrayList<ChildrenData>?) : ExpandableListAdapter{

    var context: Context? = null
    var categoryList: ArrayList<ChildrenData>?

    init {
        this.context = context
        categoryList = list
    }


    override fun getGroupView(p0: Int, p1: Boolean, p2: View?, p3: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(p3?.context)
        val listGroupBinding: RowFirstBinding = DataBindingUtil.inflate(layoutInflater, R.layout.row_first, p3, false);
        listGroupBinding.childData = getGroup(p0)
        return listGroupBinding.root
    }

    override fun getGroup(p0: Int): ChildrenData? {
        if(null != categoryList && categoryList!!.size > 0)
            return categoryList?.get(p0)
        else
            return null
    }

    override fun getGroupCount(): Int {
        if(null != categoryList && categoryList!!.size > 0)
            return categoryList?.size!!
        else
            return 0
    }

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        val expandableListView = SecondLevelExpandableListView(context)
        expandableListView.setAdapter(CategoryTwoLevelAdapter(context, categoryList?.get(p0)?.children_data))
        expandableListView.setGroupIndicator(null)
        expandableListView.setChildIndicator(null)
        expandableListView.setDivider(context?.getResources()?.getDrawable(android.R.color.transparent))
        expandableListView.setChildDivider(context?.getResources()?.getDrawable(android.R.color.transparent))
        expandableListView.setOnGroupExpandListener(object : ExpandableListView.OnGroupExpandListener{
            var previousGroup = -1
            override fun onGroupExpand(p0: Int) {
                if(p0 != previousGroup){
                    expandableListView.collapseGroup(previousGroup)
                }
                previousGroup = p0
            }

        })
        expandableListView.setOnGroupClickListener(object : ExpandableListView.OnGroupClickListener{
            override fun onGroupClick(expandableListView: ExpandableListView?, p1: View?, p2: Int, p3: Long): Boolean {
                Log.i("group Size",""+categoryList?.get(p2)?.children_data?.size)
                if(categoryList?.get(p2)?.children_data?.size!! == 0){

                }
                return false
            }
        })

        expandableListView.setOnChildClickListener(object : ExpandableListView.OnChildClickListener{
            override fun onChildClick(p0: ExpandableListView?, p1: View?, p2: Int, p3: Int, p4: Long): Boolean {
                Log.i("child List Size",""+categoryList?.get(p2)?.children_data?.get(p3)?.children_data?.size)
                return false
            }
        })

        return expandableListView

    }

    override fun getChildrenCount(p0: Int): Int {
        return 1
    }

    override fun getChild(p0: Int, p1: Int): ChildrenData? {
        return categoryList?.get(p0)?.children_data?.get(p1)
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

    override fun onGroupCollapsed(p0: Int) {
    }

    override fun isEmpty(): Boolean {
        return false
    }

    override fun registerDataSetObserver(p0: DataSetObserver?) {
    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {
    }
}

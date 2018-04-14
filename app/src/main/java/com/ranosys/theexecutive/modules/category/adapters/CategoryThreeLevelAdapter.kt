package com.ranosys.theexecutive.modules.category.adapters

import android.content.Context
import android.database.DataSetObserver
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.RowFirstBinding
import com.ranosys.theexecutive.modules.category.ChildrenData
import com.ranosys.theexecutive.modules.category.SecondLevelExpandableListView
import com.ranosys.theexecutive.modules.productListing.ProductListingFragment
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.FragmentUtils

/**
 * Created by Mohammad Sunny on 9/3/18.
 */
class CategoryThreeLevelAdapter(context: Context?, list : MutableList<ChildrenData>?) : ExpandableListAdapter{

    var context: Context? = null
    var categoryList: MutableList<ChildrenData>?

    init {
        this.context = context
        categoryList = list
    }

    override fun getGroupView(p0: Int, p1: Boolean, p2: View?, p3: ViewGroup?): View? {
        val layoutInflater = LayoutInflater.from(p3?.context)
        val listGroupBinding: RowFirstBinding = DataBindingUtil.inflate(layoutInflater, R.layout.row_first, p3, false);
        listGroupBinding.childData = getGroup(p0)
        return listGroupBinding.root

    }

    override fun getGroup(p0: Int): ChildrenData? {
        categoryList?.run {
            return get(p0)
        }
        return null
    }

    override fun getGroupCount(): Int {
        categoryList?.run {
            return size
        }
        return 0
    }

    override fun getChildView(group: Int, child: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        val expandableListView = SecondLevelExpandableListView(context)
        expandableListView.setAdapter(CategoryTwoLevelAdapter(context, categoryList?.get(group)?.children_data))
        expandableListView.setGroupIndicator(null)
        expandableListView.setChildIndicator(null)
        expandableListView.setDivider(context?.getResources()?.getDrawable(android.R.color.transparent))
        expandableListView.setChildDivider(context?.getResources()?.getDrawable(android.R.color.transparent))

        if(categoryList?.get(group)?.children_data?.size!! != 0)
        {
            expandableListView.expandGroup(child,true)
        }

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
                if(null != categoryList?.get(group)?.children_data?.get(p2)?.children_data){
                    if(categoryList?.get(group)?.children_data?.get(p2)?.children_data?.size!! == 0){
                        val bundle = Bundle()
                        bundle.putInt(Constants.CATEGORY_ID, categoryList?.get(group)?.children_data?.get(p2)?.id!!)
                        bundle.putString(Constants.CATEGORY_NAME, categoryList?.get(group)?.children_data?.get(p2)?.name!!)
                        FragmentUtils.addFragment(context!!, ProductListingFragment(), bundle, ProductListingFragment::class.java.name, true)
                    }
                }else if((categoryList?.get(group)?.children_data?.get(p2) as ChildrenData).name == "View All"){
                    val bundle = Bundle()
                    bundle.putInt(Constants.CATEGORY_ID, categoryList?.get(group)?.children_data?.get(p2)?.id!!)
                    bundle.putString(Constants.CATEGORY_NAME, categoryList?.get(group)?.children_data?.get(p2)?.name!!)
                    FragmentUtils.addFragment(context!!, ProductListingFragment(), bundle, ProductListingFragment::class.java.name, true)
                }


                return false
            }
        })

        expandableListView.setOnChildClickListener(object : ExpandableListView.OnChildClickListener{
            override fun onChildClick(p0: ExpandableListView?, p1: View?, p2: Int, p3: Int, p4: Long): Boolean {
                val bundle = Bundle()
                bundle.putInt(Constants.CATEGORY_ID, categoryList?.get(group)?.children_data?.get(p2)?.children_data?.get(p3)?.id!!)
                bundle.putString(Constants.CATEGORY_NAME, categoryList?.get(group)?.children_data?.get(p2)?.children_data?.get(p3)?.name!!)
                FragmentUtils.addFragment(context!!, ProductListingFragment(), bundle, ProductListingFragment::class.java.name, true)
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

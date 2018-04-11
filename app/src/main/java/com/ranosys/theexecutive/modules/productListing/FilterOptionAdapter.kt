package com.ranosys.theexecutive.modules.productListing

import android.database.DataSetObserver
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.FilterOptionItemBinding
import com.ranosys.theexecutive.databinding.FilterOptionTitleBinding

/**
 * Created by nikhil on 5/4/18.
 */
class FilterOptionAdapter(val productListVM: ProductListingViewModel, var optionsList : List<ProductListingDataClass.Filter>?): BaseExpandableListAdapter() {

    override fun getGroupCount() = optionsList?.size ?: 0

    override fun getGroup(groupPosition: Int): ProductListingDataClass.Filter? {
        optionsList?.run {
            return get(groupPosition)
        }
        return null
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(parent.context)
        val titleBinding: FilterOptionTitleBinding = DataBindingUtil.inflate(layoutInflater, R.layout.filter_option_title, parent, false)
        titleBinding.filter = getGroup(groupPosition)


        val filterList = parent as ExpandableListView

        filterList.setOnGroupExpandListener(object : ExpandableListView.OnGroupExpandListener{
            var previousGroup = -1
            override fun onGroupExpand(groupPosition: Int) {
                if(groupPosition != previousGroup){
                    filterList.collapseGroup(previousGroup)
                }
                previousGroup = groupPosition
            }
        })

        return titleBinding.root
    }

    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()

    override fun isEmpty() = if(groupCount == 0)  true else false

    override fun getChildId(groupPosition: Int, childPosition: Int) = childPosition.toLong()

    override fun getChildrenCount(groupPosition: Int) = optionsList?.get(groupPosition)?.options?.size ?: 0

    override fun getChild(groupPosition: Int, childPosition: Int) = optionsList?.get(groupPosition)?.options?.get(childPosition)

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {

        val layoutInflater = LayoutInflater.from(parent.context)
        val optionBinding: FilterOptionItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.filter_option_item, parent, false)
        optionBinding.option = getChild(groupPosition, childPosition)

        val filterList = parent as ExpandableListView

        filterList.setOnChildClickListener(object : ExpandableListView.OnChildClickListener{
            override fun onChildClick(list: ExpandableListView?, parent: View?, groupPos: Int, childPos: Int, id: Long): Boolean {
                //store selected filter
                val filter = productListVM.filterOptionList?.value?.get(groupPos)
                productListVM.selectedFilterMap.put(filter!!.name, filter.options.get(childPos).value)

                getChild(groupPosition, childPosition)?.isSelected?.set(true)
                return true
            }
        })
        return optionBinding.root
    }


    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true
    override fun getCombinedChildId(groupId: Long, childId: Long) = childId
    override fun areAllItemsEnabled() = true
    override fun getCombinedGroupId(groupId: Long) = groupId
    override fun hasStableIds() = true
    override fun onGroupExpanded(groupPosition: Int) {}
    override fun onGroupCollapsed(groupPosition: Int) {}
    override fun unregisterDataSetObserver(observer: DataSetObserver?) {}
    override fun registerDataSetObserver(observer: DataSetObserver?) {}

}

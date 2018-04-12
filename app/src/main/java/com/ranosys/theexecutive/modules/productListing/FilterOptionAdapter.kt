package com.ranosys.theexecutive.modules.productListing

import android.database.DataSetObserver
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.FilterOptionItemBinding
import com.ranosys.theexecutive.databinding.FilterOptionTitleBinding

/**
 * @Class An adapter class for filter option.
 * @author Ranosys Technologies
 * @Date 05-Apr-2018
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

    override fun isEmpty() = groupCount == 0

    override fun getChildId(groupPosition: Int, childPosition: Int) = childPosition.toLong()

    override fun getChildrenCount(groupPosition: Int) = optionsList?.get(groupPosition)?.options?.size ?: 0

    override fun getChild(groupPosition: Int, childPosition: Int) = optionsList?.get(groupPosition)?.options?.get(childPosition)

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {

        val layoutInflater = LayoutInflater.from(parent.context)
        val optionBinding: FilterOptionItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.filter_option_item, parent, false)
        val option = getChild(groupPosition, childPosition)
        if(option?._isSelected == null){
            option?._isSelected = ObservableField(false)
        }
        optionBinding.option = getChild(groupPosition, childPosition)

        val filterList = parent as ExpandableListView

        filterList.setOnChildClickListener(object : ExpandableListView.OnChildClickListener{
            override fun onChildClick(list: ExpandableListView?, parent: View?, groupPos: Int, childPos: Int, id: Long): Boolean {
                //store selected filter
                val filter = getGroup(groupPosition)
                productListVM.selectedFilterMap.put(filter!!.name, filter.options.get(childPos).value)

                for (item in getGroup(groupPos)?.options!!){
                    item._isSelected.set(false)
                }

                getChild(groupPos, childPos)?._isSelected?.set(true)
                notifyDataSetChanged()
                return false
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

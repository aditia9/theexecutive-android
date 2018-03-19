package com.ranosys.theexecutive.modules.category

import android.content.Context
import android.view.View
import android.widget.ExpandableListView

/**
 * Created by Mohammad Sunny on 12/3/18.
 */
class SecondLevelExpandableListView(context: Context?) : ExpandableListView(context) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        //999999 is a size in pixels. ExpandableListView requires a maximum height in order to do measurement calculations.
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(999999, View.MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}
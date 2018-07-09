package com.ranosys.theexecutive.modules.order.orderReturn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ranosys.theexecutive.R

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 09-Jul-2018
 */

class ReturnReasonAdapter(var context: Context, var reasonList: Array<String>?) : BaseAdapter() {

    private var mLayoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = mLayoutInflater.inflate(R.layout.reason_spinner_item, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        holder.textViewCountryName.text = reasonList?.get(position)
        return view
    }

    override fun getItem(position: Int): Any {
        return reasonList!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return reasonList!!.size
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.reason_spinner_dropdown_view, parent, false)
        }

        val item = convertView?.findViewById<TextView>(R.id.TextView01)
        item!!.text = reasonList?.get(position)
        item.post { item.setSingleLine(false) }
        return item
    }

    class ViewHolder(view: View) {
        var textViewCountryName: TextView = view.findViewById(R.id.selected_reason_tv)
    }
}
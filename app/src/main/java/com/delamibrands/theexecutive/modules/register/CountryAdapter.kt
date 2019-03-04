package com.delamibrands.theexecutive.modules.register

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.delamibrands.theexecutive.R

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 05-Sep-2018
 */
class CountryAdapter(var context: Context, var countries: List<RegisterDataClass.CountryCode>) : BaseAdapter() {

    private var mLayoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = mLayoutInflater.inflate(R.layout.item_country_list, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        holder.textViewCountryCode.text = "(${countries[position].dial_code})"
        holder.textViewCountryName.text = "${countries[position].name}"
        return view
    }

    override fun getItem(position: Int): Any {
        return countries[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return countries.size
    }

    class ViewHolder(view: View) {
        var textViewCountryCode: TextView = view.findViewById(R.id.textViewCountryCode)
        var textViewCountryName: TextView = view.findViewById(R.id.textViewCountryName)
    }
}
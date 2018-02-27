package com.ranosys.theexecutive.modules.home

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.BR
import com.ranosys.theexecutive.R
import java.util.*


/**
 * Created by Mohammad Sunny on 5/2/18.
 */
class RecyclerAdapter(listener : OnItemClickListener): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    var data: List<HomeDataClass.HomeUserData>? = null
    var listener : OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(item : HomeDataClass.HomeUserData, position: Int);
    }

    init {
        data = Arrays.asList(
                HomeDataClass.HomeUserData("View Requests", R.drawable.ic_launcher_background),
                HomeDataClass.HomeUserData("View My Requests", R.drawable.placeholder),
                HomeDataClass.HomeUserData("Generate New Request", R.drawable.ic_launcher_background),
                HomeDataClass.HomeUserData("My Profile", R.drawable.placeholder),
                HomeDataClass.HomeUserData("Sign Out", R.drawable.placeholder))

        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var layoutInflater = LayoutInflater.from(parent?.context)
        var binding: ViewDataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.list_items, parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        var itemdata = data?.get(position)
        holder?.bind(itemdata!!, listener!!)
    }

    class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(obj: HomeDataClass.HomeUserData, listener : OnItemClickListener) {
            binding.setVariable(BR.data, obj)
            binding.executePendingBindings()
            binding.root.setOnClickListener(object  : View.OnClickListener{
                override fun onClick(p0: View?) {
                    listener.onItemClick(obj, position)
                }

            })
        }
    }
}
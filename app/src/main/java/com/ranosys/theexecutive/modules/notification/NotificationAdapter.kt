package com.ranosys.theexecutive.modules.notification

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.NotificationItemBinding

class NotificationAdapter(var notificationListData: List<NotificationListResponse>?) : RecyclerView.Adapter<NotificationAdapter.Holder>() {

    var clickListener: NotificationAdapter.OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(item: NotificationListResponse)
    }

    fun setItemClickListener(listener: NotificationAdapter.OnItemClickListener) {
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.Holder {
        val binding: NotificationItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.notification_item, parent, false)
        return NotificationAdapter.Holder(binding)
    }

    override fun onBindViewHolder(holder: NotificationAdapter.Holder, position: Int) {
        holder.bind(notificationListData!!.get(position), listener = clickListener!!)
    }

    override fun getItemCount(): Int = notificationListData?.size ?: 0

    class Holder(private val itemBinding: NotificationItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(notificationResponse: NotificationListResponse?, listener: NotificationAdapter.OnItemClickListener) {

            if (notificationResponse?.isRead!!) {
                itemBinding.imgBullet
            }

            if (TextUtils.isEmpty(notificationResponse.title)) {
                itemBinding.tvTitle.text = notificationResponse.title
            }

            if (TextUtils.isEmpty(notificationResponse.description)) {

            }

            itemView.setOnClickListener {
                listener.onItemClick(notificationResponse)
            }
        }
    }
}

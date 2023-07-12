package com.eziot.demo.group.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eziot.demo.base.BaseResDataManager
import com.eziot.family.model.group.EZIoTRoomInfo
import com.eziot.iotsdkdemo.R


class EZIoTGroupSelectAdapter(private val roomList: List<EZIoTRoomInfo>) :
    RecyclerView.Adapter<EZIoTGroupSelectAdapter.EZIoTFamilySelectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EZIoTFamilySelectViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.eziot_group_select_adapter, parent, false)
        return EZIoTFamilySelectViewHolder(view)
    }

    override fun onBindViewHolder(holder: EZIoTFamilySelectViewHolder, position: Int) {
        val ezIotGroupInfo = roomList[position]
        holder.groupName.text = ezIotGroupInfo.name
        holder.groupSelectCb.isChecked = BaseResDataManager.roomInfo != null && BaseResDataManager.roomInfo!!.id == ezIotGroupInfo.id
        holder.groupSelectLayout.setOnClickListener {
            BaseResDataManager.roomInfo = ezIotGroupInfo
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

    class EZIoTFamilySelectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupName: TextView = view.findViewById(R.id.groupNameTv)
        val groupSelectCb : CheckBox = view.findViewById(R.id.selectGroupCb)
        val groupSelectLayout : ViewGroup = view.findViewById(R.id.groupSelectLayout)
    }

}
package com.eziot.demo.group.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eziot.demo.base.IntentContent
import com.eziot.demo.group.EZIoTGroupInfoActivity
import com.eziot.family.model.family.EZIoTFamilyInfo
import com.eziot.family.model.group.EZIoTGroupInfo
import com.eziot.iotsdkdemo.R

class EZIoTGroupListAdapter(private val groupList: List<EZIoTGroupInfo>,private val context: Activity) :
    RecyclerView.Adapter<EZIoTGroupListAdapter.EZIoTFamilyListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EZIoTFamilyListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.eziot_group_list_adapter, parent, false)
        return EZIoTFamilyListViewHolder(view)
    }

    override fun onBindViewHolder(holder: EZIoTFamilyListViewHolder, position: Int) {
        val ezIotGroupInfo = groupList[position]
        holder.groupNameTv.text = ezIotGroupInfo.name
        holder.groupItemLayout.setOnClickListener {
            val intent = Intent(context,EZIoTGroupInfoActivity::class.java)
            intent.putExtra(IntentContent.GROUP_ID,ezIotGroupInfo.id)
            context.startActivityForResult(intent,IntentContent.REFRESH_CODE)
        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    class EZIoTFamilyListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupNameTv: TextView = view.findViewById(R.id.groupNameTv)
        val groupItemLayout: ViewGroup = view.findViewById(R.id.groupItemLayout)
    }


}
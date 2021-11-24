package com.eziot.demo.family.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eziot.demo.base.IntentContent
import com.eziot.demo.family.EZIoTFamilyInfoActivity
import com.eziot.family.model.family.EZIoTFamilyInfo
import com.eziot.iotsdkdemo.R

class EZIoTFamilyListAdapter(private val familyList: List<EZIoTFamilyInfo>,private val activity : Activity) :
    RecyclerView.Adapter<EZIoTFamilyListAdapter.EZIoTFamilyListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EZIoTFamilyListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.eziot_family_list_adapter, parent, false)
        return EZIoTFamilyListViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EZIoTFamilyListViewHolder, position: Int) {
        val ezIoTFamilyInfo = familyList[position]
        holder.familyInfoTv.text = ezIoTFamilyInfo.deviceNum.toString() + "个设备，" + ezIoTFamilyInfo.roomNum + "个房间"
        holder.familyName.text = ezIoTFamilyInfo.familyName
        holder.familyLayout.setOnClickListener {
            val intent = Intent(activity,EZIoTFamilyInfoActivity::class.java)
            intent.putExtra(IntentContent.FAMILY_ID,ezIoTFamilyInfo.id)
            activity.startActivityForResult(intent,2)
        }
    }

    override fun getItemCount(): Int {
        return familyList.size
    }

    class EZIoTFamilyListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val familyName: TextView = view.findViewById(R.id.familyNameTv)
        val familyLayout : ViewGroup = view.findViewById(R.id.familyItemLayout)
        val familyInfoTv : TextView = view.findViewById(R.id.familyInfoTv)
    }


}
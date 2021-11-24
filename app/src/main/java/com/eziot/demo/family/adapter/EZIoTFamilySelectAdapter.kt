package com.eziot.demo.family.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eziot.demo.base.BaseResDataManager
import com.eziot.family.model.family.EZIoTFamilyInfo
import com.eziot.iotsdkdemo.R


class EZIoTFamilySelectAdapter(private val familyList: List<EZIoTFamilyInfo>) :
    RecyclerView.Adapter<EZIoTFamilySelectAdapter.EZIoTFamilySelectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EZIoTFamilySelectViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.eziot_family_select_adapter, parent, false)
        return EZIoTFamilySelectViewHolder(view)
    }

    override fun onBindViewHolder(holder: EZIoTFamilySelectViewHolder, position: Int) {
        val ezIoTFamilyInfo = familyList[position]
        holder.familyName.text = ezIoTFamilyInfo.familyName
        holder.familySelectCb.isChecked = BaseResDataManager.familyInfo != null && BaseResDataManager.familyInfo!!.id == ezIoTFamilyInfo.id
        holder.familySelectLayout.setOnClickListener {
            BaseResDataManager.familyInfo = ezIoTFamilyInfo
            BaseResDataManager.groupInfo = null
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return familyList.size
    }

    class EZIoTFamilySelectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val familyName: TextView = view.findViewById(R.id.familyNameTv)
        val familySelectCb : CheckBox = view.findViewById(R.id.selectFamilyCb)
        val familySelectLayout : ViewGroup = view.findViewById(R.id.familySelectLayout)
    }

}
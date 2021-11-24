package com.eziot.demo.group.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eziot.demo.base.IntentContent
import com.eziot.demo.group.EZIoTGroupInfoActivity
import com.eziot.device.model.DeviceInfo
import com.eziot.family.model.family.EZIoTFamilyInfo
import com.eziot.family.model.group.EZIoTGroupInfo
import com.eziot.iotsdkdemo.R

class EZIoTGroupAddDeviceListDialogAdapter(private val deviceList: List<DeviceInfo>, private val context: Context, private val ezIoTGroupDeviceListener: EZIoTGroupDeviceListener) :
    RecyclerView.Adapter<EZIoTGroupAddDeviceListDialogAdapter.EZIoTGroupDeviceListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EZIoTGroupDeviceListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.eziot_group_device_list_dialog_adapter, parent, false)
        return EZIoTGroupDeviceListViewHolder(view)
    }

    override fun onBindViewHolder(holder: EZIoTGroupDeviceListViewHolder, position: Int) {
        val deviceInfo = deviceList[position]
        holder.deviceNameTv.text = deviceInfo.name
        holder.groupDeviceItemLayout.setOnClickListener {
            ezIoTGroupDeviceListener.ezIotGroupDeviceClick(deviceInfo)
        }
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    class EZIoTGroupDeviceListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deviceNameTv: TextView = view.findViewById(R.id.deviceNameTv)
        val groupDeviceItemLayout: ViewGroup = view.findViewById(R.id.groupDeviceItemLayout)
    }

    interface EZIoTGroupDeviceListener {

        fun ezIotGroupDeviceClick(deviceInfo: DeviceInfo)

    }

}
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

class EZIoTGroupDeviceListAdapter(private val deviceList: List<DeviceInfo>, private val context: Context , private val ezIoTGroupDeviceListener: EZIoTGroupDeviceListener, val isHostMember: Boolean) :
    RecyclerView.Adapter<EZIoTGroupDeviceListAdapter.EZIoTFamilyListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EZIoTFamilyListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.eziot_group_device_list_adapter, parent, false)
        return EZIoTFamilyListViewHolder(view)
    }

    override fun onBindViewHolder(holder: EZIoTFamilyListViewHolder, position: Int) {
        val deviceInfo = deviceList[position]
        holder.deviceNameTv.text = deviceInfo.name
        if(!isHostMember){
            holder.removeDeviceRoomBtn.visibility = View.GONE
        } else {
            holder.removeDeviceRoomBtn.visibility = View.VISIBLE
        }
        holder.removeDeviceRoomBtn.setOnClickListener {
            ezIoTGroupDeviceListener.ezIotGroupRemoveDeviceClick(deviceInfo)
        }
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    class EZIoTFamilyListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deviceNameTv: TextView = view.findViewById(R.id.deviceNameTv)
        val removeDeviceRoomBtn: Button = view.findViewById(R.id.removeDeviceRoomBtn)
    }

    interface EZIoTGroupDeviceListener {

        fun ezIotGroupRemoveDeviceClick(deviceInfo: DeviceInfo)

    }

}
package com.eziot.demo.device.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.eziot.demo.base.IntentContent
import com.eziot.demo.device.EZIoTDeviceDetailActivity
import com.eziot.device.model.DeviceInfo
import com.eziot.iotsdkdemo.R

class EZIoTDeviceListAdapter(private val deviceList: MutableList<DeviceInfo>,private val activity : Activity) : RecyclerView.Adapter<EZIoTDeviceListAdapter.EZIoTFamilyListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EZIoTFamilyListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.eziot_device_list_adapter, parent, false)
        return EZIoTFamilyListViewHolder(view)
    }

    override fun onBindViewHolder(holder: EZIoTFamilyListViewHolder, position: Int) {
        val ezIotDeviceInfo = deviceList[position]
        holder.deviceName.text = ezIotDeviceInfo.name
        if(!ezIotDeviceInfo.isOnline){
            holder.deviceStateTv.text = activity.getText(R.string.deviceOffline)
            holder.deviceStateTv.visibility = View.VISIBLE
        } else {
            holder.deviceStateTv.visibility = View.GONE
        }
        holder.deviceItemLayout.setOnClickListener {
            val  intent = Intent(activity,EZIoTDeviceDetailActivity::class.java)
            intent.putExtra(IntentContent.DEVICE_ID,ezIotDeviceInfo.deviceSerial)
            activity.startActivity(intent)
        }
    }

    public fun setData(deviceList: List<DeviceInfo>){
        this.deviceList.clear()
        this.deviceList.addAll(deviceList)
        notifyDataSetChanged()
    }

    public fun addData(deviceList: List<DeviceInfo>){
        this.deviceList.addAll(deviceList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    class EZIoTFamilyListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deviceName: TextView = view.findViewById(R.id.deviceNameTv)
        val deviceItemLayout: ViewGroup = view.findViewById(R.id.deviceItemLayout)
        val deviceStateTv: TextView = view.findViewById(R.id.deviceStateTv)
    }


}
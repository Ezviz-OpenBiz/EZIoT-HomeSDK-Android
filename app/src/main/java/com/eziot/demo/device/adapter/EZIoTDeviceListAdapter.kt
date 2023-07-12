package com.eziot.demo.device.adapter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.base.IntentContent
import com.eziot.demo.device.EZIoTDeviceDetailActivity
import com.eziot.demo.utils.Utils
import com.eziot.device.EZIoTDeviceManager
import com.eziot.device.model.EZIoTDeviceInfo
import com.eziot.iotsdkdemo.R
//import com.eziot.rnmodule.EZIoTRnManager
import org.json.JSONObject
import java.lang.Exception

class EZIoTDeviceListAdapter(private val deviceList: MutableList<EZIoTDeviceInfo>, private val activity : Activity) : RecyclerView.Adapter<EZIoTDeviceListAdapter.EZIoTFamilyListViewHolder>() {

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
            val rnPackage = ezIotDeviceInfo.rnPackage
            val familyId = BaseResDataManager.familyInfo!!.id
            val bundle = Bundle()
            bundle.putString("biz","ModuleTest")
            bundle.putString("entry","FeatureTest")
            bundle.putString("deviceSerial",ezIotDeviceInfo.deviceSerial)
            bundle.putString("localIndex",ezIotDeviceInfo.resourceInfos[0].localIndex)
            bundle.putString("resourceIdentifier",ezIotDeviceInfo.resourceInfos[0].resourceIdentifier)
//            EZIoTRnManager.startReactActivity(bundle)
//            return@setOnClickListener
            if(TextUtils.isEmpty(rnPackage)){
                val intent = Intent(activity, EZIoTDeviceDetailActivity::class.java)
                intent.putExtra(IntentContent.DEVICE_ID, ezIotDeviceInfo.deviceSerial)
                activity.startActivity(intent)
            } else {
                val rnPackageJson = JSONObject(rnPackage)
                val rnName = rnPackageJson.optString("rn_name")
                if(!TextUtils.isEmpty(rnName) && rnName.equals("nativePanel")){
                    try {
                        val config = JSONObject(rnPackageJson.optString("config")).optString("payload")
                        val appName = config.split("://")[0]
                        val pageName = config.split("://")[1].split("/")[1]
                        val intent = Intent()
                        intent.setClassName("com.example.iotsdkdemo",pageName)
                        if(intent.resolveActivity(activity.packageManager) == null){
                            Utils.showToast(activity,"没有这个页面")
                        } else {
                        }
                    } catch (e : Exception){
                        Utils.showToast(activity,"格式错误")
                        val intent = Intent(activity, EZIoTDeviceDetailActivity::class.java)
                        intent.putExtra(IntentContent.DEVICE_ID, ezIotDeviceInfo.deviceSerial)
                        activity.startActivity(intent)
                    }
                } else {
                    val intent = Intent(activity, EZIoTDeviceDetailActivity::class.java)
                    intent.putExtra(IntentContent.DEVICE_ID, ezIotDeviceInfo.deviceSerial)
                    activity.startActivity(intent)
                }
            }
        }
    }

    public fun setData(deviceList: List<EZIoTDeviceInfo>){
        this.deviceList.clear()
        this.deviceList.addAll(deviceList)
        notifyDataSetChanged()
    }

    public fun addData(deviceList: List<EZIoTDeviceInfo>){
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
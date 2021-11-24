package com.eziot.demo.wificonfig.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eziot.demo.wificonfig.EZIoTAPConfigSuccessActivity
import com.eziot.demo.wificonfig.EZIoTDevWifiListActivity
import com.eziot.iotsdkdemo.R
import com.eziot.wificonfig.model.fastap.EZIoTWiFiItemInfo

class DeviceWifiListAdapter(private var context: Context?, private var wiFiItemInfos: List<EZIoTWiFiItemInfo>):
    RecyclerView.Adapter<DeviceWifiListAdapter.WifiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiViewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.eziot_wifi_device_list_adapter, null)
        return WifiViewHolder(view)
    }

    override fun getItemCount(): Int {
        return wiFiItemInfos?.size
    }

    override fun onBindViewHolder(holder: WifiViewHolder, position: Int) {
        holder.wifiName.text = wiFiItemInfos[position].ssid
        holder.wifiNameLayout.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                showDialogForInputWifiPassword(holder)
            }
        })
    }

    fun showDialogForInputWifiPassword(holder: WifiViewHolder) {
        val layoutView: View = LayoutInflater.from(context).inflate(R.layout.layout_input_dialog_edittext, null)
        val inputSecurityAreaName =
            layoutView.findViewById<View>(R.id.dialog_edit_text) as EditText
        var build: AlertDialog.Builder = AlertDialog.Builder(context).setView(layoutView)
            .setTitle("输入wifi密码").setNegativeButton("取消", null)

        build.setPositiveButton("确认", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {

                var intent = Intent(context, EZIoTAPConfigSuccessActivity::class.java)
                intent.putExtra("ssid", holder.wifiName.text.toString())
//                val password = inputSecurityAreaName.text.toString()
                intent.putExtra("password", inputSecurityAreaName.text.toString())
                context?.startActivity(intent)
            }
        }).create().show()

    }

    class WifiViewHolder(view: View): RecyclerView.ViewHolder(view){
        var wifiName: TextView = view.findViewById(R.id.wifi_item_name)
        var wifiNameLayout: View = view.findViewById(R.id.wifi_item)
    }
}


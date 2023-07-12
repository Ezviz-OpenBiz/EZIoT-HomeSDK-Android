package com.eziot.demo.ble.adapter

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eziot.ble.EZIoTBleManager
import com.eziot.ble.callback.EZIoTBleConnectCallback
import com.eziot.ble.callback.EZIoTBleInitCallback
import com.eziot.ble.model.EZIoTPeripheral
import com.eziot.ble.operate.bleinit.BleInitType
import com.eziot.ble.utils.BleUtils
import com.eziot.ble.utils.LogUtil
import com.eziot.demo.base.IntentContent
import com.eziot.demo.ble.EZIoTBleMainActivity
import com.eziot.demo.group.EZIoTGroupInfoActivity
import com.eziot.demo.utils.Utils
import com.eziot.family.model.group.EZIoTRoomInfo
import com.eziot.iotsdkdemo.R

class EZIoTBleListAdapter(private val ezIoTPeripherals: List<EZIoTPeripheral>, private val context: Activity) :
    RecyclerView.Adapter<EZIoTBleListAdapter.EZIoTBleListViewHolder>() {

    var successTimes = 0

    var failTimes = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EZIoTBleListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.eziot_ble_list_adapter, parent, false)
        return EZIoTBleListViewHolder(view)
    }

    override fun onBindViewHolder(holder: EZIoTBleListViewHolder, position: Int) {
        val ezIoTPeripheral = ezIoTPeripherals[position]
        holder.nameTv.text = ezIoTPeripheral.name
        holder.macNameTv.text = ezIoTPeripheral.address
        if(!TextUtils.isEmpty(ezIoTPeripheral.displayName)){
            holder.deviceNameTv.text = ezIoTPeripheral.displayName
        } else {
            holder.deviceNameTv.text = ezIoTPeripheral.name
        }
//        holder.encryptTypeTv.text = ezIoTPeripheral.encryptType.toString()
        holder.connectBtn.setOnClickListener {
            Utils.showWaitDialog(context)
            val convert16Binarys = BleUtils.convert16Binarys(ezIoTPeripheral.rawData)
            LogUtil.i("EZIoT_BLE_CLICK",convert16Binarys)
            val bleDevice = EZIoTBleManager.getBleDevice(context, ezIoTPeripheral.deviceSerial!!)
            bleDevice!!.connect(object : EZIoTBleConnectCallback{
                override fun connectSuccess() {
                    Handler(Looper.getMainLooper()).post {
                        Utils.dismissWaitDialog()
                        Utils.showToast(context, "连接成功")
                        val intent = Intent(context, EZIoTBleMainActivity::class.java)
                        intent.putExtra(IntentContent.DEVICE_ID, ezIoTPeripheral.deviceSerial)
                        context.startActivity(intent)
                    }
                }

                override fun connectFailed(errorCode: Int, errMsg: String?) {
                    Handler(Looper.getMainLooper()).post {
                        Utils.dismissWaitDialog()
                        Utils.showToast(context, "连接失败，错误码：$errorCode")
                    }
                }

                override fun onConnectionStateChange(status: Int) {

                }

            })
//            bleDevice!!.startBleConnectInit("0",object : EZIoTBleInitCallback{
//                override fun onComplete() {
//
//                }
//
//                override fun onError(bleInitType: BleInitType, errorCode: Int, errorDes: String?) {
//
//                }
//
//                override fun process(bleInitType: BleInitType) {
//
//                }
//
//                override fun controlInitComplete() {
//
//                }
//
//                override fun controlInitFail(bleInitType: BleInitType, errorCode: Int, errorDes: String?) {
//
//                }
//
//                override fun onConnectionStateChange(status: Int) {
//
//                }
//
//            })
        }
    }

    fun autoConnect(deviceSerial : String){
        LogUtil.i("AUTO_BLE", "autoConnect 成功次数 ： $successTimes 失败次数 ： $failTimes")
        val intent = Intent(context, EZIoTBleMainActivity::class.java)
        intent.putExtra(IntentContent.DEVICE_ID, deviceSerial)
        context.startActivity(intent)
        val bleDevice = EZIoTBleManager.getBleDevice(context, deviceSerial)
        bleDevice!!.connect(object : EZIoTBleConnectCallback{
            override fun connectSuccess() {
                successTimes++
                LogUtil.i("AUTO_BLE","连接成功")
            }

            override fun connectFailed(errorCode: Int, errMsg: String?) {
                failTimes++
                LogUtil.i("AUTO_BLE", "连接失败 错误码  ： $errorCode")
            }

            override fun onConnectionStateChange(status: Int) {

            }

        })
    }

    override fun getItemCount(): Int {
        return ezIoTPeripherals.size
    }

    class EZIoTBleListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deviceNameTv: TextView = view.findViewById(R.id.deviceNameTv)
        val macNameTv: TextView = view.findViewById(R.id.macNameTv)
        val connectBtn: Button = view.findViewById(R.id.connectBtn)
        val nameTv: TextView = view.findViewById(R.id.nameTv)
        val encryptTypeTv: TextView = view.findViewById(R.id.encryptType)
    }


}
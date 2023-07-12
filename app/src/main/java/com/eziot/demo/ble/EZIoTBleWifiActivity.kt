package com.eziot.demo.ble

import android.content.*
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.eziot.ble.EZIoTBleManager
//import com.eziot.ble.callback.EZIoTBleConnectionInfoChangeListener
import com.eziot.ble.device.IEZIoTBleDevice
import com.eziot.ble.http.core.callback.IEZIoTResultCallback
import com.eziot.ble.http.core.callback.IResultCallback
import com.eziot.ble.model.EZIoTBleWifiInfo
//import com.eziot.ble.model.EZIoTConnectInfo
//import com.eziot.ble.model.EZIoTRegisterInfo
import com.eziot.ble.model.EZIoTWifiEncryptType
import com.eziot.ble.utils.JsonUtils
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.IntentContent
import com.eziot.demo.utils.Utils
import com.eziot.iotsdkdemo.R
import kotlinx.android.synthetic.main.eziot_ble_wifi_activity.*
import kotlinx.android.synthetic.main.eziot_ble_wifi_activity.logTv
import kotlinx.android.synthetic.main.eziot_ble_wifi_activity.wifiNameEt
import kotlinx.android.synthetic.main.eziot_ble_wifi_activity.wifiPwdEt
import java.lang.Exception
import java.lang.StringBuilder

class EZIoTBleWifiActivity : BaseActivity() {


    val sb = StringBuilder()

    lateinit var mEzIoTBleDevice : IEZIoTBleDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_ble_wifi_activity)
        addBackBtn(toolbar)
        val deviceSerial = intent.getStringExtra(IntentContent.DEVICE_ID)
        mEzIoTBleDevice = EZIoTBleManager.getBleDevice(this,deviceSerial!!)!!
        registerReceiver(object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val log = intent?.getStringExtra(IntentContent.BLE_SDK_LOG_DATA)
                if(!TextUtils.isEmpty(log)){
                    if(log!!.contains("request") || log.contains("response")){
                        printLog(log)
                    }
                }
            }

        }, IntentFilter(IntentContent.BLE_SDK_LOG_BROADCAST))

//        mEzIoTBleDevice.registerConnectionInfoChangeListener(object :
//            EZIoTBleConnectionInfoChangeListener {
//            override fun onConnectionInfoChangeListener(ezIoTConnectInfo: EZIoTConnectInfo) {
//                printLog("===================")
//                printLog("连接状态更新 ： " + JsonUtils.toJson(ezIoTConnectInfo))
//                printLog("===================")
//            }
//
//            override fun onRegisterInfoChangeListener(ezIoTRegisterInfo: EZIoTRegisterInfo) {
//                printLog("===================")
//                printLog("注册态更新 ： " + JsonUtils.toJson(ezIoTRegisterInfo))
//                printLog("===================")
//            }
//
//        })
    }


    fun onClickStartConfigWifi(view : View){
        val wifiName = wifiNameEt.text.toString()
        val wifiPwd = wifiPwdEt.text.toString()
        if(TextUtils.isEmpty(wifiName) || TextUtils.isEmpty(wifiPwd)){
            Utils.showToast(this,"请输入wifi名称和密码")
            return
        }
        if(ezIoTBleWifiInfos == null){
            Utils.showToast(this,"请先拉取wifi列表")
            return
        }
        var currentEzIoTBleWifiInfo : EZIoTBleWifiInfo? = null
        for (ezIoTBleWifiInfo in ezIoTBleWifiInfos!!){
            if(ezIoTBleWifiInfo.getSsid() == wifiName){
                currentEzIoTBleWifiInfo = ezIoTBleWifiInfo
                break
            }
        }
        printLog("开始配网")

        if(currentEzIoTBleWifiInfo == null){
            Utils.showToast(this,"未找到当前wifi")
            return
        }

        val values = EZIoTWifiEncryptType.values()
        var currentWifiEncryptType : EZIoTWifiEncryptType? = null
        for (ezIoTWifiEncryptType in values){
            if(currentEzIoTBleWifiInfo.getMode().toString() == ezIoTWifiEncryptType.type){
                currentWifiEncryptType = ezIoTWifiEncryptType
            }
        }

//        mEzIoTBleDevice.startFastApConfigWithToken(currentEzIoTBleWifiInfo.getSsid(),wifiPwd,
//            currentWifiEncryptType!!,"CN","","",currentEzIoTBleWifiInfo.getBssid(),object : IResultCallback {
//            override fun onSuccess() {
//                printLog("下發wifi成功")
//            }
//
//            override fun onError(errorCode: Int, errorDesc: String?) {
//                printLog("下發wifi失败 $errorCode")
//            }
//
//        })
    }

    private var ezIoTBleWifiInfos : List<EZIoTBleWifiInfo>? = null


    fun onClickGetWifiList(view : View){
        printLog("开始拉取wifi列表")
        mEzIoTBleDevice.getWifiList(object : IEZIoTResultCallback<List<EZIoTBleWifiInfo>>{
            override fun onSuccess(t: List<EZIoTBleWifiInfo>) {
                printLog("wifi列表拉取成功")
                ezIoTBleWifiInfos = t;
                printLog(JsonUtils.toJson(t))
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                printLog("wifi列表拉取失败 $errorCode")
            }

        })
    }

    fun onClickGetNetworkInfo(view : View){
        printLog("开始拉取网络状态")
//        mEzIoTBleDevice.getConnectionInfo(object : IEZIoTResultCallback<EZIoTConnectInfo>{
//            override fun onSuccess(t: EZIoTConnectInfo) {
//                try {
//                    printLog("拉取网络状态成功 : " + JsonUtils.toJson(t))
//                } catch (e : Exception){
//                    printLog("拉取网络状态成功 解析异常 ")
//                }
//            }
//
//            override fun onError(errorCode: Int, errorDesc: String?) {
//                printLog("拉取网络状态失败")
//            }
//
//        })
    }

    fun onClickGetBindState(view : View){
        printLog("开始拉取设备上线状态")
//        mEzIoTBleDevice.getRegisterInfo(object : IEZIoTResultCallback<EZIoTRegisterInfo> {
//            override fun onSuccess(t: EZIoTRegisterInfo) {
//                try {
//                    printLog("拉取设备上线状态成功 : " + JsonUtils.toJson(t))
//                } catch (e : Exception){
//                    printLog("拉取设备上线状态成功 解析异常 ")
//                }
//            }
//
//            override fun onError(errorCode: Int, errorDesc: String?) {
//                printLog("拉取设备上线状态失败")
//            }
//
//        })
    }

    fun onClickCopyLog(view : View){
        copyStr(logTv.text.toString())
        Utils.showToast(this,"已粘贴到剪切板")
    }

    private fun copyStr(copyStr : String) : Boolean{
        val cm =  getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val newPlainText = ClipData.newPlainText("Label", copyStr)
        cm.primaryClip = newPlainText
        return true
    }

    private fun printLog(log : String){
        runOnUiThread {
            sb.append(log)
            sb.append("\n")
            logTv.text = sb.toString()
        }
    }
}
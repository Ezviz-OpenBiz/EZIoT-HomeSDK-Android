package com.eziot.demo.ble

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.eziot.ble.EZIoTBleManager
import com.eziot.ble.callback.EZIoTBleOtaResultCallback
import com.eziot.ble.device.IEZIoTBleDevice
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.IntentContent
import com.eziot.iotsdkdemo.R
import kotlinx.android.synthetic.main.ble_main_activity.*
import kotlinx.android.synthetic.main.ble_main_activity.logTv
import kotlinx.android.synthetic.main.eziot_ble_update_activity.*
import java.io.File
import java.lang.StringBuilder

class EZIoTBleUpdateActivity : BaseActivity() {
    private val TAG = "EZIoTBleUpdateActivity"
    val sb = StringBuilder()

    lateinit var mEzIoTBleDevice : IEZIoTBleDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_ble_update_activity)
        val deviceSerial = intent.getStringExtra(IntentContent.DEVICE_ID)
        addBackBtn(toolbar)
        mEzIoTBleDevice = EZIoTBleManager.getBleDevice(this,deviceSerial!!)!!
        registerReceiver(object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val log = intent?.getStringExtra(IntentContent.BLE_SDK_LOG_DATA)
                if(!TextUtils.isEmpty(log)){
                    if(log!!.contains("response mEZIoTInternalBleSendDataCallback is finish")){
                        printLog("========================================")
                        return
                    }
                    if(log.contains("request messageId") || log.contains("response messageId")){
                        printLog(log)
                    }
                }
            }

        }, IntentFilter(IntentContent.BLE_SDK_LOG_BROADCAST))
    }

    var mcuDownFilePath : String? = "/storage/emulated/0/Android/data/com.example.iotsdkdemo/files/mcu.bin"
    var deviceDownFilePath : String? = "/storage/emulated/0/Android/data/com.example.iotsdkdemo/files/device.bin"

    private fun startDeviceOta(){
        val file = File(deviceDownFilePath)
        if(!file.exists()){
            printLog("请放置文件")
            return;
        }

        printLog("开始Ota")
        mEzIoTBleDevice.startOta(deviceDownFilePath!!, 0,object : EZIoTBleOtaResultCallback {
            override fun onSuccess() {
                printLog("ota成功")
                Log.i(TAG, "onSuccess: ")
            }

            override fun onError(type: Int, errorCode: Int, errorDes: String?) {
                printLog("ota失败")
                Log.i(TAG, "onError: type $type errorCode $errorCode errorDes $errorDes")
            }

            override fun onProgress(progress: Int) {
                Log.i(TAG, "onProgress: $progress")
            }

        })
    }

    private fun startSubDeviceOta(){
        val file = File(mcuDownFilePath)
        if(!file.exists()){
            printLog("请放置文件")
            return;
        }
        printLog("开始子模块Ota")
        mEzIoTBleDevice.startOta(mcuDownFilePath!!, 1,object : EZIoTBleOtaResultCallback {
            override fun onSuccess() {
                printLog("ota成功")
                Log.i(TAG, "onSuccess: ")
            }

            override fun onError(type: Int, errorCode: Int, errorDes: String?) {
                printLog("ota失败")
                Log.i(TAG, "onError: type $type errorCode $errorCode errorDes $errorDes")
            }

            override fun onProgress(progress: Int) {
                Log.i(TAG, "onProgress: $progress")
            }

        })
    }

    fun onClickStartDeviceUpgrade(view : View){
        startDeviceOta()
    }

    fun onClickStartSubDeviceUpgrade(view : View){
        startSubDeviceOta()
    }

    fun onClickClearLog(view: View){
        sb.clear()
        logTv.text = sb.toString()
    }

    private fun printLog(log : String){
        runOnUiThread {
            sb.append(log)
            sb.append("\n")
            logTv.text = sb.toString()
        }
    }

}
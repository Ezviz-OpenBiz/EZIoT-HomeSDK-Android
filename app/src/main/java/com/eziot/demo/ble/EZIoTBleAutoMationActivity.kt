package com.eziot.demo.ble

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.eziot.ble.EZIoTBleManager
import com.eziot.ble.callback.EZIoTBleConnectCallback
import com.eziot.ble.callback.EZIoTBleInitCallback
import com.eziot.ble.operate.bleinit.BleInitType
import com.eziot.ble.utils.LogUtil
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.IntentContent
import com.eziot.iotsdkdemo.R

class EZIoTBleAutoMationActivity : BaseActivity() {

    lateinit var deviceSerial : String

    val mainHandler = Handler(Looper.getMainLooper())

    var successTimes = 0

    var failTimes = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_ble_automation_activity)
        deviceSerial = intent.getStringExtra(IntentContent.DEVICE_ID)!!
        val bleDevice = EZIoTBleManager.getBleDevice(this, deviceSerial)
        bleDevice!!.disconnect()
    }

    fun onConnectAutoClick(view : View){
//        connectDevice()
        startConnectInit()
    }

    private fun connectDevice(){
        LogUtil.i("EZIoT_BLE_BleClientImpl", "成功次数 ： $successTimes 失败次数：$failTimes")
        val bleDevice = EZIoTBleManager.getBleDevice(this, deviceSerial)

        mainHandler.postDelayed({bleDevice!!.disconnect()},500)

        mainHandler.postDelayed({connectDevice()},700)

        bleDevice!!.connect(object : EZIoTBleConnectCallback{
            override fun connectSuccess() {
                successTimes++
//                mainHandler.postDelayed({bleDevice.disconnect()},1000)
//
//                mainHandler.postDelayed({connectDevice()},200)
            }

            override fun connectFailed(errorCode: Int, errMsg: String?) {
                failTimes++
//                mainHandler.postDelayed({bleDevice.disconnect()},1000)
//
//                mainHandler.postDelayed({connectDevice()},200)
            }

            override fun onConnectionStateChange(status: Int) {

            }

        })
    }

    private fun startConnectInit(){

        LogUtil.i("EZIoT_BLE_BleClientImpl", "成功次数 ： $successTimes 失败次数：$failTimes")
        val bleDevice = EZIoTBleManager.getBleDevice(this, deviceSerial)

        bleDevice!!.startBleConnectInit("1",object : EZIoTBleInitCallback{
            override fun onComplete() {

            }

            override fun onError(bleInitType: BleInitType, errorCode: Int, errorDes: String?) {

            }

            override fun process(bleInitType: BleInitType) {

            }

            override fun controlInitComplete() {

                successTimes++
                mainHandler.postDelayed({bleDevice!!.disconnect()},1000)

                mainHandler.postDelayed({startConnectInit()},2000)
            }

            override fun controlInitFail(
                bleInitType: BleInitType,
                errorCode: Int,
                errorDes: String?
            ) {

                failTimes++
                mainHandler.postDelayed({bleDevice!!.disconnect()},1000)

                mainHandler.postDelayed({startConnectInit()},2000)
            }

            override fun onConnectionStateChange(status: Int) {

            }

        })

    }


}
package com.eziot.demo.ble

import android.content.*
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.eziot.ble.EZIoTBleManager
import com.eziot.ble.callback.EZIoTBleConnectCallback
import com.eziot.ble.callback.EZIoTBleInitCallback
//import com.eziot.ble.callback.EZIoTBleLocalAuthCallback
import com.eziot.ble.callback.EZIoTBleSendDataCallback
import com.eziot.ble.device.IEZIoTBleDevice
import com.eziot.ble.http.core.callback.IEZIoTResultCallback
import com.eziot.ble.http.core.callback.IResultCallback
import com.eziot.ble.model.EZIoTBleAuthResp
import com.eziot.ble.model.EZIoTBleDeviceAbility
import com.eziot.ble.model.EZIoTBleDeviceInfo
import com.eziot.ble.model.EZIoTBleTimeZone
import com.eziot.ble.operate.bleinit.BleInitType
//import com.eziot.ble.operate.localauth.BleLocalAuthType
import com.eziot.ble.params.BLE_LOG_LEVEL
import com.eziot.ble.utils.JsonUtils
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.IntentContent
import com.eziot.demo.utils.Utils
import com.eziot.iotsdkdemo.R
import kotlinx.android.synthetic.main.ble_main_activity.*
import kotlinx.android.synthetic.main.eziot_family_list_activity.*
import java.lang.StringBuilder

class EZIoTBleBaseActivity : BaseActivity() {

    val sb = StringBuilder()

    lateinit var mEzIoTBleDevice : IEZIoTBleDevice

    lateinit var mDeviceSerial : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_ble_base_cmd_activity)
        addBackBtn(toolbar)
        mDeviceSerial = intent.getStringExtra(IntentContent.DEVICE_ID)!!
        mEzIoTBleDevice = EZIoTBleManager.getBleDevice(this,mDeviceSerial)!!
        registerReceiver(object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val log = intent?.getStringExtra(IntentContent.BLE_SDK_LOG_DATA)
                if(!TextUtils.isEmpty(log)){
                    if(log!!.contains("response mEZIoTInternalBleSendDataCallback is finish")){
                        printLog("========================================")
                        return
                    }
                    if(log.contains("request") || log.contains("response") ||  log.contains("HTTP")){
                        printLog(log)
                    }
                }
            }

        }, IntentFilter(IntentContent.BLE_SDK_LOG_BROADCAST))
    }

    fun onClickGetDeviceAbility(view : View){
        mEzIoTBleDevice.getDeviceAbility(object : IEZIoTResultCallback<EZIoTBleDeviceAbility> {
            override fun onSuccess(t: EZIoTBleDeviceAbility) {
                printLog("获取设备能力成功 ： " + com.eziot.ble.utils.JsonUtils.toJson(t))
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                printLog("获取设备能力失败")

            }

        })
    }

    fun onClickGetSubDeviceInfo(view : View){
        printLog("开始获取子设备")

        mEzIoTBleDevice.isConnected()

        mEzIoTBleDevice.getSubDeviceInfo(object : EZIoTBleSendDataCallback<EZIoTBleDeviceInfo> {
            override fun onError(errorCode: Int) {
                printLog("获取子设备信息失败 ： ")
            }

            override fun onSuccess(result: EZIoTBleDeviceInfo) {
                printLog("获取子设备信息成功 ： " + com.eziot.ble.utils.JsonUtils.toJson(result))
            }

        })
    }

    fun onClickGetSubDeviceInfo1(view : View){
        printLog("开始获取子设备1")
        Thread{
            mEzIoTBleDevice.connect(object : EZIoTBleConnectCallback{
                override fun connectSuccess() {

                }

                override fun connectFailed(errorCode: Int, errMsg: String?) {

                }

                override fun onConnectionStateChange(status: Int) {

                }

            })
        }.start()

//        mEzIoTBleDevice.getSubDeviceInfo(object : EZIoTBleSendDataCallback<EZIoTBleDeviceInfo> {
//            override fun onError(errorCode: Int) {
//                printLog("获取子设备信息失败 ： ")
//            }
//
//            override fun onSuccess(result: EZIoTBleDeviceInfo) {
//                printLog("获取子设备信息成功1 ： " + com.eziot.ble.utils.JsonUtils.toJson(result))
//            }
//
//        })
    }

    fun onClickClose(view : View){
        mEzIoTBleDevice.disconnect()
    }

    fun onClickGetSubDeviceInfo2(view : View){
        printLog("开始获取子设备2")
        mEzIoTBleDevice.getSubDeviceInfo(object : EZIoTBleSendDataCallback<EZIoTBleDeviceInfo> {
            override fun onError(errorCode: Int) {
                printLog("获取子设备信息失败 ： ")
            }

            override fun onSuccess(result: EZIoTBleDeviceInfo) {
                printLog("获取子设备信息成功2 ： " + com.eziot.ble.utils.JsonUtils.toJson(result))
            }

        })
    }

    fun onClickGetSubDeviceInfo3(view : View){
        printLog("开始获取子设备3")
        mEzIoTBleDevice.getSubDeviceInfo(object : EZIoTBleSendDataCallback<EZIoTBleDeviceInfo> {
            override fun onError(errorCode: Int) {
                printLog("获取子设备信息失败 ： ")
            }

            override fun onSuccess(result: EZIoTBleDeviceInfo) {
                printLog("获取子设备信息成功3 ： " + com.eziot.ble.utils.JsonUtils.toJson(result))
            }

        })
    }

    fun onClickGetDeviceInfo(view : View){
        printLog("开始获取设备信息")
        mEzIoTBleDevice.getDeviceInfo(object : EZIoTBleSendDataCallback<EZIoTBleDeviceInfo>{
            override fun onError(errorCode: Int) {
                printLog("获取设备信息失败 ")

            }

            override fun onSuccess(result: EZIoTBleDeviceInfo) {
                printLog("获取设备信息成功 ： " + com.eziot.ble.utils.JsonUtils.toJson(result))
            }

        })
    }

    fun onClickSetDeviceTime(view : View){
        printLog("开始设置设备时区")
        val ezIoTBleTimeZone = EZIoTBleTimeZone()
        ezIoTBleTimeZone.tzValue = "UTC-3:00"
        mEzIoTBleDevice.schoolHourDevice(ezIoTBleTimeZone,object : IEZIoTResultCallback<Long>{


            override fun onError(errorCode: Int, errorDesc: String?) {
                printLog("设置时区失败")
            }

            override fun onSuccess(t: Long) {
                printLog("设置时区成功")
            }

        })
    }

    fun onClickStartBleAuth(view : View){
        printLog("开始双向认证")
        mEzIoTBleDevice.startAuth(object : IEZIoTResultCallback<EZIoTBleAuthResp> {

            override fun onError(errorCode: Int, errorDesc: String?) {

                printLog("双向认证失败 $errorCode")
            }

            override fun onSuccess(t: EZIoTBleAuthResp) {
                printLog("双向认证成功")
            }

        })
    }

    fun onClickStartRuoBleAuth(view : View){
        printLog("开始带等级双向认证")
        mEzIoTBleDevice.startAuthWithGrade(object : IEZIoTResultCallback<EZIoTBleAuthResp>{
            override fun onSuccess(t: EZIoTBleAuthResp) {
                printLog("双向认证成功 : " + JsonUtils.toJson(t))
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                printLog("双向认证失败 $errorCode")
            }

        })
    }

    fun onClickSetBindStatus(view : View){
        printLog("开始下发绑定状态")
        mEzIoTBleDevice.setDeviceBindStatus(true,object : IResultCallback{
            override fun onSuccess() {
                printLog("下发绑定状态成功")

            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                printLog("下发绑定状态失败")
            }

        })
    }

    fun onClickSetUnBindStatus(view : View){
        printLog("开始下发解绑状态")
        mEzIoTBleDevice.setDeviceBindStatus(false,object : IResultCallback{
            override fun onSuccess() {
                printLog("下发解绑状态成功")
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                printLog("下发解绑状态失败")
            }

        })
    }

    fun onClickStartInit(view : View){
        printLog("开始初始化")
        mEzIoTBleDevice.startBleConnectInit("0",object : EZIoTBleInitCallback{
            override fun onComplete() {

            }

            override fun onError(bleInitType: BleInitType, errorCode: Int, errorDes: String?) {

            }

            override fun process(bleInitType: BleInitType) {

            }

            override fun controlInitComplete() {
                printLog("初始化成功")
            }

            override fun controlInitFail(bleInitType: BleInitType, errorCode: Int, errorDes: String?) {
                printLog("初始化失败 错误码 $errorCode")
            }

            override fun onConnectionStateChange(status: Int) {
            }

        })
    }

    fun onClickClearLog(view: View){
        sb.clear()
        logTv.text = sb.toString()
    }

    fun onClickCopyLog(view : View){
        copyStr(logTv.text.toString())
        Utils.showToast(this,"已粘贴到剪切板")
    }

    fun onClickBroadCastCheck(view : View){
        mEzIoTBleDevice.getDeviceInfo(object : EZIoTBleSendDataCallback<EZIoTBleDeviceInfo>{
            override fun onError(errorCode: Int) {

            }

            override fun onSuccess(result: EZIoTBleDeviceInfo) {

                if(result.deviceSerial == null){
                    printLog("请先实现获取设备信息接口")
                    return
                }
                if(mDeviceSerial == result.deviceSerial){
                    printLog("广播包格式校验通过  广播包序列号为 ： $mDeviceSerial")
                } else {
                    printLog("广播包格式校验不通过 广播包的 名称和pid组合为 $mDeviceSerial 请确认广播包格式是否按照协议解析")

                }

            }

        })
    }

    fun onClickGetDeviceLog(view : View){
        mEzIoTBleDevice.getLogFromDevice(100,object : IEZIoTResultCallback<String>{
            override fun onSuccess(t: String) {
                printLog("获取日志成功 ： $t")
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                printLog("获取日志失败 错误码 $errorCode")
            }

        })
    }

    fun onClickOpenLog(view : View){
        mEzIoTBleDevice.updateMultiFirmwareVersion("1",object:IResultCallback{

            override fun onSuccess() {

            }

            override fun onError(errorCode: Int, errorDesc: String?) {

            }

        })
//        mEzIoTBleDevice.setLogEnable(true,object : IResultCallback{
//            override fun onSuccess() {
//                printLog("下发日志开关成功")
//            }
//
//            override fun onError(errorCode: Int, errorDesc: String?) {
//                printLog("下发日志开关失败 $errorCode")
//            }
//
//        })
    }

    fun onClickSetLogLevel(view : View){
        mEzIoTBleDevice.setLogLevel(BLE_LOG_LEVEL.ERROR,object : IResultCallback{
            override fun onSuccess() {
                printLog("下发日志等级成功")
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                printLog("下发日志等级失败 错误码 $errorCode")
            }

        })
    }

    fun onClickLocalAuth(view: View){
//        mEzIoTBleDevice.startLocalAuth("GGBBAW", object : EZIoTBleLocalAuthCallback {
//            override fun onSuccess() {
//                printLog("本地认证成功")
//            }
//
//            override fun onError(bleLocalAuthType: Int, errorCode: Int, errorDes: String?) {
//                printLog("本地认证失败 错误码 ： $errorCode")
//            }
//
//            override fun process(bleLocalAuthType: BleLocalAuthType) {
//            }
//
//        })
    }

    fun onClickBound(view: View){
//        mEzIoTBleDevice.createBound(object : IResultCallback{
//            override fun onSuccess() {
//                printLog("配对成功")
//            }
//
//            override fun onError(errorCode: Int, errorDesc: String?) {
//                printLog("配对失败")
//            }
//
//        })
    }

    private fun printLog(log : String){
        runOnUiThread {
            sb.append(log)
            sb.append("\n")
            logTv.text = sb.toString()
        }
    }

    private fun copyStr(copyStr : String) : Boolean{
        val cm =  getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val newPlainText = ClipData.newPlainText("Label", copyStr)
        cm.primaryClip = newPlainText
        return true
    }

}
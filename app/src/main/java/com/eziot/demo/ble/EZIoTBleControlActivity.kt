package com.eziot.demo.ble

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import com.eziot.ble.EZIoTBleManager
import com.eziot.ble.callback.EZIoTBleTranFileResultCallback
import com.eziot.ble.device.IEZIoTBleDevice
import com.eziot.ble.http.core.callback.IEZIoTResultCallback
import com.eziot.ble.http.core.callback.IResultCallback
import com.eziot.ble.model.EZIoTBlePropsFeatureInfo
import com.eziot.ble.model.EZIoTStreamLineOtap
import com.eziot.ble.params.EZIoTBleValueType
import com.eziot.ble.utils.JsonUtils
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.IntentContent
import com.eziot.demo.utils.Utils
import com.eziot.iotsdkdemo.R
import kotlinx.android.synthetic.main.eziot_ble_control_activity.*
import kotlinx.android.synthetic.main.eziot_ble_control_activity.arrayTypeBtn
import kotlinx.android.synthetic.main.eziot_ble_control_activity.boolTypeBtn
import kotlinx.android.synthetic.main.eziot_ble_control_activity.identifierEt
import kotlinx.android.synthetic.main.eziot_ble_control_activity.intTypeBtn
import kotlinx.android.synthetic.main.eziot_ble_control_activity.localIndexEt
import kotlinx.android.synthetic.main.eziot_ble_control_activity.logTv
import kotlinx.android.synthetic.main.eziot_ble_control_activity.objTypeBtn
import kotlinx.android.synthetic.main.eziot_ble_control_activity.resourceIdentifierEt
import kotlinx.android.synthetic.main.eziot_ble_control_activity.stringTypeBtn
import kotlinx.android.synthetic.main.eziot_ble_control_activity.valueEt
import java.lang.StringBuilder

class EZIoTBleControlActivity : BaseActivity() {

    private val sb = StringBuilder()

    lateinit var mEzIoTBleDevice : IEZIoTBleDevice

    private val mainHandler = Handler(Looper.getMainLooper())

    private var autoEnable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_ble_control_activity)

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

    fun onClickClearLog(view: View){
        sb.clear()
        logTv.text = sb.toString()
    }

    fun onClickGetPropFeature(view : View){
        getPropFeature()
    }

    private fun getPropFeature(){
        val resourceIdentifier = resourceIdentifierEt.text.toString()
        val localIndex = localIndexEt.text.toString()
        val domainIdentifier = domainIdentifierEt.text.toString()
        val identifier = identifierEt.text.toString()
        if(TextUtils.isEmpty(resourceIdentifier) || TextUtils.isEmpty(localIndex)
            || TextUtils.isEmpty(domainIdentifier)){
            Utils.showToast(this,"请输入参数")
            return
        }
        printLog("开始获取属性")
        mEzIoTBleDevice.getPropFeature(resourceIdentifier.toInt(),localIndex.toInt(),domainIdentifier.toInt(),identifier.toInt(),object : IEZIoTResultCallback<EZIoTBlePropsFeatureInfo> {
            override fun onSuccess(t: EZIoTBlePropsFeatureInfo) {
                printLog("属性获取成功 ： " + JsonUtils.toJson(t))
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                printLog("属性获取失败 错误码 ： $errorCode")
            }

        })
    }

    fun onClickSetActionFeature(view : View){
        setActionFeature()
    }

    private fun setActionFeature(){
        val resourceIdentifier = resourceIdentifierEt.text.toString()
        val localIndex = localIndexEt.text.toString()
        val domainIdentifier = domainIdentifierEt.text.toString()
        val identifier = identifierEt.text.toString()
        val valueType = getValueType()
        val value = getValue()
        if(TextUtils.isEmpty(resourceIdentifier) || TextUtils.isEmpty(localIndex)
            || TextUtils.isEmpty(domainIdentifier) || TextUtils.isEmpty(identifier)
            || valueType == null || value == null){
            Utils.showToast(this,"请输入参数")
            return
        }

        printLog("开始下发操作")
        mEzIoTBleDevice.setActionFeature(resourceIdentifier.toInt(),localIndex.toInt(),domainIdentifier.toInt(),identifier.toInt(),valueType,value,object : IEZIoTResultCallback<String>{
            override fun onSuccess(t: String) {
                printLog("操作下发成功 返回值 ： $t")
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                printLog("操作下发失败 错误码 ： $errorCode")
            }

        })
    }

    fun onClickSetPropFeature(view : View){
        setPropFeature()
    }

    private fun setPropFeature(){
        val resourceIdentifier = resourceIdentifierEt.text.toString()
        val localIndex = localIndexEt.text.toString()
        val domainIdentifier = domainIdentifierEt.text.toString()
        val identifier = identifierEt.text.toString()
        val valueType = getValueType()
        val value = getValue()
        if(TextUtils.isEmpty(resourceIdentifier) || TextUtils.isEmpty(localIndex)
            || TextUtils.isEmpty(domainIdentifier) || TextUtils.isEmpty(identifier)
            || valueType == null || value == null){
            Utils.showToast(this,"请输入参数")
            return
        }
        printLog("开始下发属性")
        mEzIoTBleDevice.setPropFeature(resourceIdentifier.toInt(),localIndex.toInt(),domainIdentifier.toInt(),identifier.toInt(),valueType,value,object : IResultCallback{
            override fun onSuccess() {
                printLog("属性下发成功")
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                printLog("属性下发失败")
            }

        })
    }

    fun onClickSetPropFeaturePkg(view : View){
        val value = "[\"17f03550000global_ManualFeed_1\",\"17f15550000global_ManualFeed_1\",\"17f18550000global_ManualFeed_1\",\"17f19550000global_ManualFeed_1\",\"17f19570000global_ManualFeed_1\",\"17f20550000global_ManualFeed_1\",\"17f19580000global_ManualFeed_1\",\"17f19580000global_ManualFeed_2\"]"
        mEzIoTBleDevice.setPropFeature(0,0,65000,4,EZIoTBleValueType.ARRAY_TYPE,value,object : IResultCallback{
            override fun onSuccess() {

            }

            override fun onError(errorCode: Int, errorDesc: String?) {

            }

        })
    }

    fun onClickAutoSetPropFeature(view: View){
        autoEnable = true
        autoSetPropFeature()
    }

    private fun autoSetPropFeature(){
        val timeInter = auto_inter_time_et.text?.toString()
        if(TextUtils.isEmpty(timeInter)){
            Utils.showToast(this,"请输入自动化间隔参数")
            return
        }
        if(!autoEnable){
            return
        }
        mainHandler.postDelayed({
            setPropFeature()
            autoSetPropFeature()
        },timeInter!!.toLong())
    }

    fun onClickAutoSetActionFeature(view : View){
        autoEnable = true
        autoSetActionFeature()
    }

    private fun autoSetActionFeature(){
        val timeInter = auto_inter_time_et.text?.toString()
        if(TextUtils.isEmpty(timeInter)){
            Utils.showToast(this,"请输入自动化间隔参数")
            return
        }
        if(!autoEnable){
            return
        }
        mainHandler.postDelayed({
            setPropFeature()
            autoSetActionFeature()
        },timeInter!!.toLong())
    }

    fun onClickAutoGetPropFeature(view : View){
        autoGetPropFeature()
    }

    private fun autoGetPropFeature(){
        val timeInter = auto_inter_time_et.text?.toString()
        if(TextUtils.isEmpty(timeInter)){
            Utils.showToast(this,"请输入自动化间隔参数")
            return
        }
        if(!autoEnable){
            return
        }
        mainHandler.postDelayed({
            getPropFeature()
            autoGetPropFeature()
        },timeInter!!.toLong())
    }

    fun onClickStopAuto(view : View){
        autoEnable = false
    }

    fun onClickSendProfile(view : View){
        sendProfile()
    }

    override fun onDestroy() {
        super.onDestroy()
        autoEnable = false
    }

    private fun sendProfile(){
        showWaitDialog()
        mEzIoTBleDevice.getStreamLineOtap(object : IEZIoTResultCallback<EZIoTStreamLineOtap>{
            override fun onSuccess(t: EZIoTStreamLineOtap) {
                mEzIoTBleDevice.startTranFile(t.getProfile().toString(),object : EZIoTBleTranFileResultCallback {
                    override fun onSuccess() {
                        runOnUiThread{
                            Utils.showToast(this@EZIoTBleControlActivity,"传递成功")
                            dismissWaitDialog()
                        }
                    }

                    override fun onError(type: Int, errorCode: Int, errorDes: String?) {
                        runOnUiThread {
                            Utils.showToast(this@EZIoTBleControlActivity,"传递失败")
                            dismissWaitDialog()
                        }
                    }

                    override fun onProgress(progress: Int) {

                    }

                })
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                runOnUiThread {
                    Utils.showToast(this@EZIoTBleControlActivity, "获取profile失败 $errorDesc")
                    dismissWaitDialog()
                }
            }

        })
    }


    private fun getValueType() : EZIoTBleValueType? {
        if(intTypeBtn.isChecked){
            return EZIoTBleValueType.INTEGER_TYPE
        }
        if(boolTypeBtn.isChecked){
            return EZIoTBleValueType.BOOLEAN_TYPE
        }
        if(arrayTypeBtn.isChecked){
            return EZIoTBleValueType.ARRAY_TYPE
        }
        if(stringTypeBtn.isChecked){
            return EZIoTBleValueType.STRING_TYPE
        }
        if(objTypeBtn.isChecked){
            return EZIoTBleValueType.OBJECT_TYPE
        }
        return null
    }

    private fun getValue() : Any?{
        val value = valueEt.text.toString()
        if(TextUtils.isEmpty(value)){
            return null
        }
        return when(getValueType()){
            EZIoTBleValueType.INTEGER_TYPE -> value.toInt()
            EZIoTBleValueType.BOOLEAN_TYPE -> value.toBoolean()
            EZIoTBleValueType.DOUBLE_TYPE -> value.toInt()
            EZIoTBleValueType.STRING_TYPE -> value
            EZIoTBleValueType.ARRAY_TYPE -> value
            EZIoTBleValueType.OBJECT_TYPE -> value
            else -> null
        }
    }


    private fun printLog(log : String){
        runOnUiThread {
            sb.append(log)
            sb.append("\n")
            logTv.text = sb.toString()
        }
    }

}
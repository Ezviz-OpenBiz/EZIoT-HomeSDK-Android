package com.eziot.demo.ble

import android.bluetooth.le.ScanResult
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.eziot.ble.EZIoTBleManager
import com.eziot.ble.callback.EZIoTBleScanCallback
import com.eziot.ble.http.core.callback.IResultCallback
import com.eziot.ble.model.EZIoTBleTimeZone
import com.eziot.ble.model.EZIoTPeripheral
import com.eziot.common.utils.LocalInfo
import com.eziot.common.utils.LogUtil
import com.eziot.control.EZIoTDeviceControlConfig
import com.eziot.control.EZIoTDeviceControlPubParams
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.base.IntentContent
import com.eziot.demo.base.IntentContent.Companion.BLE_SDK_LOG_BROADCAST
import com.eziot.demo.ble.adapter.EZIoTBleListAdapter
import com.eziot.device.EZIoTDeviceManager
import com.eziot.iotsdkdemo.R
import com.eziot.user.EZIotUserManager
import kotlinx.android.synthetic.main.eziot_ble_list_activity.*
import kotlinx.android.synthetic.main.eziot_family_list_activity.toolbar
import java.util.ArrayList

class EZIoTBleListActivity : BaseActivity() {

    val scanEzIotPerherals = ArrayList<EZIoTPeripheral>()

    var ezIoTBleListAdapter : EZIoTBleListAdapter? = null;

    var isStartAutoTest = false


    var autoDeviceSerial = "C9310B8B:AC99A23C1"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_ble_list_activity)
        addBackBtn(toolbar)
        initControlSdk()
    }

    override fun onStart() {
        super.onStart()
        if(isStartAutoTest){
//            Handler(Looper.getMainLooper()).postDelayed({
//                ezIoTBleListAdapter!!.autoConnect(autoDeviceSerial)
//            },2000)
        }
    }


    private fun initControlSdk(){
        bleDeviceListRv.layoutManager = LinearLayoutManager(this)
        ezIoTBleListAdapter = EZIoTBleListAdapter(scanEzIotPerherals,this)
        bleDeviceListRv.adapter = ezIoTBleListAdapter
        EZIoTDeviceControlConfig.getInstance()
                .setDebug(true)
                .setEzIoTDeviceControlPubParams(object : EZIoTDeviceControlPubParams {
                    override fun getApiDomain(): String {
                        return LocalInfo.getInstance().apiDomain
                    }

                    override fun getSession(): String {
                        return LocalInfo.getInstance().session
                    }

                    override fun getHardwareCode(): String {
                        return LocalInfo.getInstance().hardwareCode
                    }

                    override fun getClientType(): String {
                        return LocalInfo.getInstance().clientType
                    }

                    override fun getClientVersion(): String {
                        return LocalInfo.getInstance().clientVersion
                    }

                    override fun getUserId(): String {
                        return EZIotUserManager.getUserInfo()?.userId ?: ""
                    }

                    override fun getAppId(): String {
                        return LocalInfo.getInstance().appId
                    }

                    override fun getLanguage(): String {
                        return LocalInfo.getInstance().language
                    }

                    override fun getProfile(deviceSerial: String): String? {
                        val familyInfo = BaseResDataManager.familyInfo
                        val ezIoTDeviceInstance =
                                EZIoTDeviceManager.getDeviceControl(familyInfo!!.id, deviceSerial)
                        val localDevice = ezIoTDeviceInstance.getLocalDevice()
                        return localDevice?.productInfo?.profile
                    }

                    override fun getDeviceSerialAndMacMap(): java.util.HashMap<String?, String?>? {
                        return null
                    }

                    override fun saveDeviceSerialAndMac(deviceSerialMacMap: java.util.HashMap<String?, String?>?) {

                    }

                    override fun log(tag: String, log: String) {
                        val intent = Intent(BLE_SDK_LOG_BROADCAST)
                        intent.putExtra(IntentContent.BLE_SDK_LOG_DATA,log)
                        sendBroadcast(intent)
                    }

                    override fun dclog(log: String?) {
                        LogUtil.i("EZIoTBleListActivity",log)
                    }

                    override fun getDeviceType(deviceSerial: String): String {
                        val familyInfo = BaseResDataManager.familyInfo
                        val ezIoTDeviceInstance =
                            EZIoTDeviceManager.getDeviceControl(familyInfo!!.id, deviceSerial)
                        val localDevice = ezIoTDeviceInstance.getLocalDevice()
                        return localDevice?.deviceType ?: ""
                    }

                    override fun getDeviceTimeZone(
                        deviceSerial: String,
                        localIndex: String
                    ): EZIoTBleTimeZone? {
                        val ezIoTBleTimeZone = EZIoTBleTimeZone()
                        ezIoTBleTimeZone.tzValue = "UTC+08:00"
                        return ezIoTBleTimeZone
                    }

                    override fun setCustomDeviceTimeZone(
                        deviceSerial: String,
                        localIndex: String,
                        iResultCallback: IResultCallback
                    ) {
                    }


                }).init(application)


        EZIoTBleManager.scanPeripherals(false,
            context = this,
            ezIoTBleScanCallback = object : EZIoTBleScanCallback {
                override fun onScanResult(
                    ezIoTPeripheral: EZIoTPeripheral,
                    callbackType: Int,
                    result: ScanResult
                ) {
                    if(containEZIoTPerheral(ezIoTPeripheral.address!!)){
                        return
                    }
                    if(TextUtils.isEmpty(ezIoTPeripheral.name)){
                        return
                    }
                    scanEzIotPerherals.add(ezIoTPeripheral)
                    val adapter = bleDeviceListRv.adapter
                    adapter!!.notifyDataSetChanged()
                }

                override fun onScanFail(errorCode: Int) {

                }

            })

    }


    fun onClickStartAuto(view : View){
        isStartAutoTest = true
        ezIoTBleListAdapter!!.autoConnect(autoDeviceSerial)
    }

    fun onClickStopAuto(view : View){
        isStartAutoTest = false
    }


    override fun onDestroy() {
        super.onDestroy()
//        EZIoTBleManager.stopScan()
    }


    private fun containEZIoTPerheral(mac : String) : Boolean{
        scanEzIotPerherals.forEach {
            if(it.address == mac){
                return true
            }
        }
        return false
    }

}
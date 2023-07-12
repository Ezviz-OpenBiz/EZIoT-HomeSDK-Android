package com.eziot.demo.device

import android.os.Bundle
import android.view.View
import com.eziot.common.http.callback.IResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.IntentContent
import com.eziot.device.EZIoTDeviceManager
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import kotlinx.android.synthetic.main.eziot_device_setting_activity.*
import kotlinx.android.synthetic.main.eziot_modify_device_name_activity.*
import kotlinx.android.synthetic.main.eziot_modify_device_name_activity.toolbar

class EZIoTDeviceModifyResourceActivity : BaseActivity() {

    private lateinit var deviceName : String;

    private lateinit var deviceSerial : String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_modify_device_name_activity)
        addBackBtn(toolbar)
        initData()
        initView()
    }

    private fun initData(){
        deviceSerial = intent.getStringExtra(IntentContent.DEVICE_ID)!!
        deviceName = intent.getStringExtra(IntentContent.RESOURCE_NAME)!!
    }

    private fun initView(){
        val deviceControl = EZIoTDeviceManager.getDeviceControl(getCurrentFamilyInfo()!!.id, deviceSerial)
        val resourceInfo = deviceControl.getLocalResources()[0]
        deviceNameEt.setText(deviceControl.getLocalResource(resourceInfo.localIndex)!!.name)
    }

    fun onClickModifyDeviceName(view : View){
        val deviceName = deviceNameEt.text.toString()
        showWaitDialog()
        val deviceControl = EZIoTDeviceManager.getDeviceControl(getCurrentFamilyInfo()!!.id, deviceSerial)
        val resourceInfo = deviceControl.getLocalResources()[0]
        val localResource = deviceControl.getLocalResource(resourceInfo.localIndex)
        deviceControl.modifyResourceName(localResource!!.resourceId,deviceName,object : IResultCallback{
            override fun onSuccess() {
                localResource.name = deviceName
                EZIoTDeviceManager.saveResource(getCurrentFamilyInfo()!!.id,localResource)
                dismissWaitDialog()
                Utils.showToast(this@EZIoTDeviceModifyResourceActivity,R.string.operateSuccess)
                finish()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTDeviceModifyResourceActivity,errorDesc)
            }

        })
    }


}
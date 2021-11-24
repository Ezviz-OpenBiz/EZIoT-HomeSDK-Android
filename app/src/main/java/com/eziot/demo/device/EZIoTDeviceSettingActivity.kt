package com.eziot.demo.device

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.eziot.common.http.callback.IResultCallback
import com.eziot.demo.EZIoTMainTabActivity
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.IntentContent
import com.eziot.demo.device.update.EZIoTDeviceUpdateInfoActivity
import com.eziot.device.EZIoTDeviceControl
import com.eziot.device.EZIoTDeviceManager
import com.eziot.device.model.DeviceInfo
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import kotlinx.android.synthetic.main.eziot_device_setting_activity.*

class EZIoTDeviceSettingActivity : BaseActivity() {

    private lateinit var deviceInfo : DeviceInfo

    private lateinit var deviceSerial : String

    private lateinit var ezIoTDeviceControl : EZIoTDeviceControl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_device_setting_activity)
        addBackBtn(toolbar)
        initData()
    }

    override fun onStart() {
        super.onStart()
        initView()
    }

    private fun initData(){
        deviceSerial = intent.getStringExtra(IntentContent.DEVICE_ID)!!
        ezIoTDeviceControl = EZIoTDeviceManager.createDeviceInstance(getCurrentFamilyInfo()!!.id,deviceSerial)
        deviceInfo = ezIoTDeviceControl.getLocalDevice()!!
    }

    private fun initView(){
        deviceInfo = ezIoTDeviceControl.getLocalDevice()!!
        deviceNameTv.text = deviceInfo.name

        val resource = ezIoTDeviceControl.getLocalResources()[0]
        resourceNameTv.text = resource.name

        if(!isHomeFamily()){
            deleteDeviceBtn.visibility = View.GONE
            deviceUpdateLayout.visibility = View.GONE
            resourceNameLayout.isEnabled = false
            deviceNameLayout.isEnabled = false
            deviceNameIv.visibility = View.INVISIBLE
            resourceNameIv.visibility = View.INVISIBLE
        } else {
            deviceNameIv.visibility = View.VISIBLE
            resourceNameIv.visibility = View.VISIBLE
        }
    }

    fun onClickDeleteDevice(view : View){
        AlertDialog.Builder(this).setMessage(R.string.delete_device_hint)
            .setPositiveButton(R.string.cancel
            ) { dialog, which -> }
            .setNegativeButton(R.string.sure
            ) { dialog, which ->
                showWaitDialog()
                ezIoTDeviceControl.deleteDevice(object : IResultCallback {
                    override fun onSuccess() {
                        dismissWaitDialog()
                        Utils.showToast(this@EZIoTDeviceSettingActivity, R.string.operateSuccess)
                        val intent = Intent(
                            this@EZIoTDeviceSettingActivity,
                            EZIoTMainTabActivity::class.java
                        )
                        startActivity(intent)
                    }

                    override fun onError(errorCode: Int, errorDesc: String?) {
                        Utils.showToast(this@EZIoTDeviceSettingActivity, errorDesc)
                        dismissWaitDialog()
                    }

                })
            }.show()
    }

    fun onClickModifyDeviceName(view : View){
        val intent = Intent(this,EZIoTDeviceModifyNameActivity::class.java)
        intent.putExtra(IntentContent.DEVICE_ID,deviceSerial)
        intent.putExtra(IntentContent.DEVICE_NAME,deviceInfo.name)
        startActivity(intent)
    }

    fun onClickUpdate(view : View){
        val intent = Intent(this,EZIoTDeviceUpdateInfoActivity::class.java)
        intent.putExtra(IntentContent.DEVICE_ID,deviceSerial)
        startActivity(intent)
    }

    fun onClickModifyResourceName(view : View){
        val intent = Intent(this,EZIoTDeviceModifyResourceActivity::class.java)
        intent.putExtra(IntentContent.DEVICE_ID,deviceSerial)
        intent.putExtra(IntentContent.RESOURCE_NAME, ezIoTDeviceControl.getLocalResources()[0].name)
        startActivity(intent)
    }

}
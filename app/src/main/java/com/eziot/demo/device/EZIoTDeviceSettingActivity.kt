package com.eziot.demo.device

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.common.http.callback.IResultCallback
import com.eziot.demo.EZIoTMainTabActivity
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.IntentContent
import com.eziot.demo.device.update.EZIoTDeviceUpdateInfoActivity
//import com.eziot.demo.ipc.play.EZIoTRealPlayActivity
//import com.eziot.demo.ipc.playback.EZIoTCloudPlaybackActivity
//import com.eziot.demo.ipc.playback.EZIoTLocalPlaybackActivity
import com.eziot.device.EZIoTDeviceControl
import com.eziot.device.EZIoTDeviceManager
import com.eziot.device.model.EZIoTDeviceInfo
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import kotlinx.android.synthetic.main.eziot_device_setting_activity.*
import org.json.JSONObject

class EZIoTDeviceSettingActivity : BaseActivity() {

    private lateinit var deviceInfo : EZIoTDeviceInfo

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
        ezIoTDeviceControl = EZIoTDeviceManager.getDeviceControl(getCurrentFamilyInfo()!!.id,deviceSerial)
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
        if(resource.isCamera == 1){
            ipcLayout.visibility = View.VISIBLE
        } else {
            ipcLayout.visibility = View.GONE
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

    fun onClickPreview(view : View){
//        val  intent = Intent(this, EZIoTRealPlayActivity::class.java)
//        intent.putExtra(IntentContent.DEVICE_ID,deviceSerial)
//        startActivity(intent)
    }

    fun onClickLocalPlayback(view : View){

        val deviceInfo =
            EZIoTDeviceManager.getDeviceControl(getCurrentFamilyInfo()!!.id, deviceSerial)

        val localIndex = deviceInfo.getLocalResources()[0].localIndex

        val resourceIdentifier = deviceInfo.getLocalResources()[0].resourceIdentifier

        val featureModel = deviceInfo.getLocalDevice()!!.featureModel

//        if(featureModel != null){
//            val actionFeature = deviceInfo.getLocalDevice()!!
//                .getActionFeature(resourceIdentifier, localIndex, "HDD", "GetHDDList")
//            deviceInfo.setActionFeature(actionFeature, null, object : IEZIoTResultCallback<String> {
//                override fun onSuccess(t: String) {
//                    val json = JSONObject(t)
//                    val jsonArray = json.optJSONArray("HDDList")
//                    if (jsonArray != null && jsonArray.length() > 0) {
//                        val intent = Intent(this@EZIoTDeviceSettingActivity, EZIoTLocalPlaybackActivity::class.java)
//                        intent.putExtra(IntentContent.DEVICE_ID, deviceSerial)
//                        startActivity(intent)
//                    } else {
//                        Utils.showToast(this@EZIoTDeviceSettingActivity,"没有sd卡")
//                    }
//                }
//
//                override fun onError(errorCode: Int, errorDesc: String?) {
//                    val intent = Intent(this@EZIoTDeviceSettingActivity, EZIoTLocalPlaybackActivity::class.java)
//                    intent.putExtra(IntentContent.DEVICE_ID, deviceSerial)
//                    startActivity(intent)
//                }
//
//            })
//        } else {
//            val intent = Intent(this@EZIoTDeviceSettingActivity, EZIoTLocalPlaybackActivity::class.java)
//            intent.putExtra(IntentContent.DEVICE_ID, deviceSerial)
//            startActivity(intent)
//        }

    }

    fun onClickCloudPlayback(view : View){
//        val intent = Intent(this, EZIoTCloudPlaybackActivity::class.java)
//        intent.putExtra(IntentContent.DEVICE_ID,deviceSerial)
//        startActivity(intent)
    }


}
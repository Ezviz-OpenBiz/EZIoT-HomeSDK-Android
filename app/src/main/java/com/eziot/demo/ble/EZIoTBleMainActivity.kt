package com.eziot.demo.ble

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.eziot.ble.EZIoTBleManager
import com.eziot.ble.http.core.callback.IEZIoTResultCallback
import com.eziot.ble.model.EZIoTBleAuthResp
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.IntentContent
import com.eziot.demo.utils.Utils
import com.eziot.iotsdkdemo.R
import kotlinx.android.synthetic.main.eziot_ble_main_activity.*

class EZIoTBleMainActivity : BaseActivity() {

    var deviceSerial : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_ble_main_activity)
        deviceSerial = intent.getStringExtra(IntentContent.DEVICE_ID)
        initListener()
    }

    override fun onStart() {
        super.onStart()
//        Handler(Looper.getMainLooper()).postDelayed({finish()},5000)
    }

    private fun initListener(){
        device_base_ly.setOnClickListener {
            val intent = Intent(this@EZIoTBleMainActivity,EZIoTBleBaseActivity::class.java)
            intent.putExtra(IntentContent.DEVICE_ID,deviceSerial)
            this@EZIoTBleMainActivity.startActivity(intent)
        }
        device_otap_ly.setOnClickListener {
            Utils.showWaitDialog(this@EZIoTBleMainActivity)
            val bleDevice = EZIoTBleManager.getBleDevice(this, deviceSerial)
            bleDevice!!.startAuthWithGrade(object : IEZIoTResultCallback<EZIoTBleAuthResp>{
                override fun onSuccess(t: EZIoTBleAuthResp) {
                    Utils.dismissWaitDialog()
                    val intent = Intent(this@EZIoTBleMainActivity,EZIoTBleControlActivity::class.java)
                    intent.putExtra(IntentContent.DEVICE_ID,deviceSerial)
                    this@EZIoTBleMainActivity.startActivity(intent)
                }

                override fun onError(errorCode: Int, errorDesc: String?) {
                    Utils.dismissWaitDialog()
                    Utils.showToast(this@EZIoTBleMainActivity,"双向认证失败，请先通过双向认证")
                }
            })
        }
        device_ota_ly.setOnClickListener {
            val intent = Intent(this@EZIoTBleMainActivity,EZIoTBleUpdateActivity::class.java)
            intent.putExtra(IntentContent.DEVICE_ID,deviceSerial)
            this@EZIoTBleMainActivity.startActivity(intent)
        }
        device_config_ly.setOnClickListener {
            val intent = Intent(this@EZIoTBleMainActivity,EZIoTBleWifiActivity::class.java)
            intent.putExtra(IntentContent.DEVICE_ID,deviceSerial)
            this@EZIoTBleMainActivity.startActivity(intent)
        }
        automation_lv.setOnClickListener {
            val intent = Intent(this@EZIoTBleMainActivity,EZIoTBleAutoMationActivity::class.java)
            intent.putExtra(IntentContent.DEVICE_ID,deviceSerial)
            this@EZIoTBleMainActivity.startActivity(intent)
        }
        skip_rope_lv.setOnClickListener {
            Utils.showWaitDialog(this@EZIoTBleMainActivity)
            val bleDevice = EZIoTBleManager.getBleDevice(this, deviceSerial)
            bleDevice!!.startAuthWithGrade(object : IEZIoTResultCallback<EZIoTBleAuthResp>{
                override fun onSuccess(t: EZIoTBleAuthResp) {
                    Utils.dismissWaitDialog()
                }

                override fun onError(errorCode: Int, errorDesc: String?) {
                    Utils.dismissWaitDialog()
                    Utils.showToast(this@EZIoTBleMainActivity,"双向认证失败，请先通过双向认证")
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EZIoTBleManager.getBleDevice(this,deviceSerial!!)!!.disconnect()
    }


}
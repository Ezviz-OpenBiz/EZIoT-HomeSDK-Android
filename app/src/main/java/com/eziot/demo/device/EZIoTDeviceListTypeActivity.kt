package com.eziot.demo.device

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.eziot.demo.base.BaseActivity
import com.eziot.iotsdkdemo.R
import kotlinx.android.synthetic.main.eziot_device_update_info_activity.*

class EZIoTDeviceListTypeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_device_list_type_activity)
        addBackBtn(toolbar)
    }

    fun onClickRemoteDevice(view : View){
        val intent = Intent(this,EZIoTDeviceListActivity::class.java)
        startActivity(intent)
    }

    fun onClickLocalDeviceByRoom(view : View){
        val intent = Intent(this,EZIoTLocalDeviceListByRoomActivity::class.java)
        startActivity(intent)
    }

    fun onClickLocalDeviceByFamily(view : View){
        val intent = Intent(this,EZIoTLocalDeviceListByFamilyActivity::class.java)
        startActivity(intent)
    }

    fun onClickLocalDeviceByRoomLimit(view : View){
        val intent = Intent(this,EZIoTLocalDeviceListByRoomLimitActivity::class.java)
        startActivity(intent)
    }

}
package com.eziot.demo.device

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.base.IntentContent
import com.eziot.demo.device.adapter.EZIoTDeviceFeatureAdapter
import com.eziot.demo.device.callback.EZIoTRefreshDeviceListCallback
import com.eziot.device.EZIoTDeviceControl
import com.eziot.device.EZIoTDeviceManager
import com.eziot.device.model.DeviceInfo
import com.eziot.iotsdkdemo.R
import kotlinx.android.synthetic.main.eziot_device_detail_activity.*
import kotlinx.android.synthetic.main.eziot_device_list_activity.toolbar

class EZIoTDeviceDetailActivity : BaseActivity() , EZIoTRefreshDeviceListCallback {

    private lateinit var deviceInfo : DeviceInfo

    private lateinit var ezIotDeviceControl : EZIoTDeviceControl

    private lateinit var ezIoTDeviceFeatureAdapter: EZIoTDeviceFeatureAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_device_detail_activity)
        addBackBtn(toolbar)
        initData()
    }

    private fun initData(){
        val deviceSerial = intent.getStringExtra(IntentContent.DEVICE_ID)
        val familyInfo = BaseResDataManager.familyInfo
        ezIotDeviceControl = EZIoTDeviceManager.createDeviceInstance(
            familyInfo!!.id,
            deviceSerial!!
        )
        deviceInfo = ezIotDeviceControl.getLocalDevice()!!
        val featureModel = deviceInfo.featureModel
        deviceFeatureRv.layoutManager = LinearLayoutManager(this)
        ezIoTDeviceFeatureAdapter = EZIoTDeviceFeatureAdapter(
            featureModel!!.getAllFeatureItems("0"),
            featureModel.getAllActionFeatureItems("0"),
            "0",
            deviceInfo,
            this,
            this
        )
        deviceFeatureRv.adapter = ezIoTDeviceFeatureAdapter
    }

    override fun onLocalRefreshCallback() {
        val familyInfo = BaseResDataManager.familyInfo
        val ezIotDevice = EZIoTDeviceManager.createDeviceInstance(
            familyInfo!!.id,
            deviceInfo.deviceSerial
        )
        val allFeatureItems = ezIotDevice.getLocalDevice()!!.featureModel!!.getAllFeatureItems("0")
        val actionItems = ezIotDevice.getLocalDevice()!!.featureModel!!.getAllActionFeatureItems("0")
        ezIoTDeviceFeatureAdapter.setData(allFeatureItems,actionItems)
    }

    override fun onRemoteRefreshCallback() {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_group_device_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_device_setting) {
            val intent = Intent(this,EZIoTDeviceSettingActivity::class.java)
            intent.putExtra(IntentContent.DEVICE_ID,deviceInfo.deviceSerial)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }


}
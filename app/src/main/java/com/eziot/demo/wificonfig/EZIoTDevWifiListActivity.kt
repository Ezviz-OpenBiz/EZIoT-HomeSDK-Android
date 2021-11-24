package com.eziot.demo.wificonfig

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.demo.wificonfig.adapter.DeviceWifiListAdapter
import com.eziot.iotsdkdemo.R
import com.eziot.demo.base.BaseActivity
import com.eziot.wificonfig.EZIoTNetConfigurator
import com.eziot.wificonfig.model.fastap.EZIoTWiFiItemInfo
import kotlinx.android.synthetic.main.eziot_family_list_activity.*

class EZIoTDevWifiListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eziot_dev_wifi_list)
        addBackBtn(toolbar)
        initView()
        initData()
    }

    private fun initView(){
        mDeviceWifiList = findViewById(R.id.device_wifi_recycler_list)
    }

    private fun initData(){
        showWaitDialog()
        EZIoTNetConfigurator.getAccessDeviceWifiList(object:
            IEZIoTResultCallback<List<EZIoTWiFiItemInfo>>{
            override fun onSuccess(t: List<EZIoTWiFiItemInfo>) {
                Handler(Looper.getMainLooper()).post(Runnable {
                    dismissWaitDialog()
                    var layoutManager = LinearLayoutManager(this@EZIoTDevWifiListActivity)
                    var adapter = DeviceWifiListAdapter(this@EZIoTDevWifiListActivity, t)
                    mDeviceWifiList?.layoutManager = layoutManager
                    mDeviceWifiList?.adapter = adapter
                })
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
            }
        })
    }

    override fun finish() {
        super.finish()
    }

    private var mDeviceWifiList: RecyclerView? = null
}
package com.eziot.demo.wificonfig

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.common.utils.LocalInfo
import com.eziot.demo.EZIoTMainTabActivity
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.iotsdkdemo.R
import com.eziot.demo.widge.WaitDialog
import com.eziot.wificonfig.EZIoTNetConfigurator
import com.eziot.wificonfig.fastap.callback.IQueryBindStatusCallback
import com.eziot.wificonfig.model.fastap.EZIoTUserDeviceInfo
import com.eziot.wificonfig.model.fastap.FastApConfigResult
import kotlinx.android.synthetic.main.eziot_family_list_activity.*

class EZIoTAPConfigSuccessActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ez_iot_ap_config_success)
        addBackBtn(toolbar)
        initView()
        initData()
    }

    private fun initView(){
        waitDialog = WaitDialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        mWifiConfigResult = findViewById(R.id.wifi_config_result)
    }

    private fun initData(){
        ssid = intent.getStringExtra("ssid")
        password = intent.getStringExtra("password")
        var map = HashMap<String, String>()
        map["areaCode"] = "CN"
        showWaitDialog()
        EZIoTNetConfigurator.startFastApConfigWithToken(BaseResDataManager.configTokenInfo, ssid, password, map, object:
            IEZIoTResultCallback<FastApConfigResult>{
            override fun onSuccess(t: FastApConfigResult) {
                var thread: Thread = Thread.currentThread()
                queryDevBindStatus(1)

            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                runOnUiThread(){
                    dismissWaitDialog()
                    wifiConfigFinish(1)
                    Toast.makeText(LocalInfo.getInstance().context, getString(R.string.wifiConfigFail), Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun queryDevBindStatus(num: Int) {
        EZIoTNetConfigurator.queryDeviceBindStatus(BaseResDataManager.APDevInfo?.devSubserial, BaseResDataManager.familyInfo?.id.toString(), object:
            IQueryBindStatusCallback<EZIoTUserDeviceInfo>{
            override fun onSuccess(t: EZIoTUserDeviceInfo) {
                wifiConfigFinish(0)
            }

            override fun onError(errorCode: Int, errorDesc: String?, supportGrab: Boolean) {
                Log.d("111111111", "aaa$num")
                if(num < 40){
                    Thread.sleep(5000)
                    queryDevBindStatus(num+1)
                }else{
                    wifiConfigFinish(1)
                }
            }
        })
    }

    private fun wifiConfigFinish(resultCode: Int){
        dismissWaitDialog()
        var str = getString(R.string.wifiConfigSuccess)
        if(resultCode == 1){
            Log.d("111111111", "失败")
            str = getString(R.string.wifiConfigFail)
        }else if(resultCode == 0){
            Log.d("111111111", "成功")
        }
        Handler(Looper.getMainLooper()).post(
            Runnable {
                mWifiConfigResult?.text = str

                if(resultCode == 0){
                    startActivity(Intent(this, EZIoTMainTabActivity::class.java))
                    Toast.makeText(LocalInfo.getInstance().context, getString(R.string.wifiConfigSuccess), Toast.LENGTH_LONG).show()
                }

            }
        )
    }

    interface ICallback{
        fun onSuccess()
        fun onError()
    }

    private var ssid: String? = null
    private var password : String? = null
    private var mWifiConfigResult: TextView? = null
}
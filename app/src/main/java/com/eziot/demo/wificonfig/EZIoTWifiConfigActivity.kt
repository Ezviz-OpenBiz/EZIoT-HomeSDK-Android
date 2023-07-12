package com.eziot.demo.wificonfig

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.family.model.family.EZIoTFamilyInfo
import com.eziot.family.model.group.EZIoTRoomInfo
import com.eziot.iotsdkdemo.R
import com.eziot.wificonfig.EZIoTNetConfigurator
import com.eziot.wificonfig.model.fastap.EZIoTAPDevInfo
import com.eziot.wificonfig.model.fastap.EZIoTConfigTokenInfo
import kotlinx.android.synthetic.main.eziot_family_list_activity.*
import kotlinx.android.synthetic.main.eziot_main_fragment.*

class EZIoTWifiConfigActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eziot_wifi_config)
        addBackBtn(toolbar)
        initView()
        initData()
    }

    private fun initView(){
        mDeviceWifiInfo = findViewById(R.id.indicatorStatus)
        mDeviceWifiInfo?.setOnClickListener(this)
    }

    private fun initData(){
        mFamilyInfo = BaseResDataManager.familyInfo
        mRoomInfo = BaseResDataManager.roomInfo

        EZIoTNetConfigurator.getConfigToken(mFamilyInfo?.id.toString(), mRoomInfo?.id.toString(), object:
            IEZIoTResultCallback<EZIoTConfigTokenInfo>{
            override fun onSuccess(t: EZIoTConfigTokenInfo) {
                BaseResDataManager.configTokenInfo = t
            }

            override fun onError(errorCode: Int, errorDesc: String?) {

            }

        })
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.indicatorStatus -> {
//                startActivityForResult(Intent(Settings.ACTION_WIFI_SETTINGS), 1)
                startActivity(Intent(this, EZIoTConfigDetailActivity::class.java))
//                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        EZIoTNetConfigurator.getAccessDeviceInfo(object: IEZIoTResultCallback<EZIoTAPDevInfo>{
            override fun onSuccess(t: EZIoTAPDevInfo) {
                mEZIoTAPDevInfo = t
                BaseResDataManager.APDevInfo = t

                Handler(Looper.getMainLooper()).post(Runnable {
                    var intent = Intent(this@EZIoTWifiConfigActivity, EZIoTDevWifiListActivity::class.java)
                    startActivity(intent)
//                    finish()
                })
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Log.d("aa", "bb")
            }
        })
    }

    private var mFamilyInfo : EZIoTFamilyInfo? = null
    private var mRoomInfo : EZIoTRoomInfo? = null
    private var mDeviceWifiInfo : Button? = null
    private var mEZIoTConfigTokenInfo: EZIoTConfigTokenInfo? = null
    private var mEZIoTAPDevInfo: EZIoTAPDevInfo? = null
}
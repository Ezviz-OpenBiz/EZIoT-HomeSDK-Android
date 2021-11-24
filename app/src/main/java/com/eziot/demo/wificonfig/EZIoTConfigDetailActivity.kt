package com.eziot.demo.wificonfig

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.iotsdkdemo.R
import com.eziot.wificonfig.EZIoTNetConfigurator
import com.eziot.wificonfig.model.fastap.EZIoTAPDevInfo
import kotlinx.android.synthetic.main.eziot_family_list_activity.*

class EZIoTConfigDetailActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eziot_config_detail)
        addBackBtn(toolbar)
        initView()
        initData()
    }

    private fun initView(){
        clickableTextView = findViewById(R.id.clickableTextView)
    }

    private fun initData(){

        clickableTextView?.apply {
            movementMethod = LinkMovementMethod.getInstance()
        }?.apply {
            append(SpannableString("，"+ getString(R.string.connectDeviceWifi)).apply {
                setSpan(object : ClickableSpan(){
                    override fun onClick(p0: View) {
                        startActivityForResult(Intent(Settings.ACTION_WIFI_SETTINGS), 1)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        // 去除下划线
                        ds.isUnderlineText = false
                    }

                }, 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.brand_color)),
                    0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE )
            })
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        EZIoTNetConfigurator.getAccessDeviceInfo(object: IEZIoTResultCallback<EZIoTAPDevInfo> {
            override fun onSuccess(t: EZIoTAPDevInfo) {
                mEZIoTAPDevInfo = t
                BaseResDataManager.APDevInfo = t

                Handler(Looper.getMainLooper()).post(Runnable {
                    var intent = Intent(this@EZIoTConfigDetailActivity, EZIoTDevWifiListActivity::class.java)
                    startActivity(intent)
//                    finish()
                })
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Log.d("aa", "bb")
            }
        })

    }

    private var mEZIoTAPDevInfo: EZIoTAPDevInfo? = null
    private var clickableTextView: TextView? = null
}
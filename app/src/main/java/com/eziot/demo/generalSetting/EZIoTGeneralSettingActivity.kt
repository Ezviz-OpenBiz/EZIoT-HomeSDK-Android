package com.eziot.demo.generalSetting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.eziot.demo.base.BaseActivity
import com.eziot.iotsdkdemo.R
import com.eziot.message.EZIoTMessageManager
import kotlinx.android.synthetic.main.activity_eziot_general_setting.*

class EZIoTGeneralSettingActivity : BaseActivity() {


    private var accountSecurityItem: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eziot_general_setting)
        addBackBtn(toolbar)
        initView()
    }

    private fun initView(){
        accountSecurityItem = findViewById(R.id.accountSecurityItem)
        accountSecurityItem?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, EZIoTAccountSecurityActivity::class.java)
            startActivity(intent)
        })
    }

    fun onClickMessageSwitch(view : View){
        val intent = Intent(this, EZIoTMsgSwitchActivity::class.java)
        startActivity(intent)
    }

}
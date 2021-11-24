package com.eziot.demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.eziot.common.model.BaseParams
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.user.EZIoTLoginActivity
import com.eziot.demo.user.EZIoTRegisterActivity
import com.eziot.iotsdkdemo.R
import com.eziot.user.EZIotUserManager

class EZIoTGuideActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_guide_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
    }

    fun onClickLogin(view : View){
        val intent = Intent(this,EZIoTLoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onClickRegister(view : View){
        val intent = Intent(this,EZIoTRegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

}
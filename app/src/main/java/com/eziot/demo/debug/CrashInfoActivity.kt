package com.eziot.demo.debug

import android.os.Bundle
//import com.eziot.common.logtool.EzLocalLog
import com.eziot.common.utils.GlobalVariable
import com.eziot.demo.base.BaseActivity
import com.eziot.iotsdkdemo.R
import kotlinx.android.synthetic.main.eziot_crash_activity.*

class CrashInfoActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_crash_activity)
        Thread{
//            val readFile = EzLocalLog.readFile("bleAuthLogFile")
            runOnUiThread {
//                crashInfo.text = readFile
            }
        }.start()
    }


}
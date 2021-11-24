package com.eziot.demo.about

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import com.eziot.demo.base.BaseActivity
import com.eziot.iotsdkdemo.R
import kotlinx.android.synthetic.main.eziot_about_activity.*

class EZIoTAboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_about_activity)
        versionNameTv.text = getVersionName()
    }

    private fun getVersionName() : String {
        val packageManager: PackageManager = application.packageManager
        val packInfo: PackageInfo = packageManager.getPackageInfo(application.getPackageName(), 0)
        return packInfo.versionName
    }


}
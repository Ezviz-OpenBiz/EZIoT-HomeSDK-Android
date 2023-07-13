package com.eziot.demo.about

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import com.eziot.common.model.BaseParams
import com.eziot.common.utils.LocalInfo
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.utils.ConfigInstance
import com.eziot.demo.utils.Utils
import com.eziot.iotsdkdemo.R
import kotlinx.android.synthetic.main.eziot_debug_activity.*


class EZIoTDebugActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_debug_activity)
        initView()
    }

    fun copyContentToClipboard(content: String?) {
        //获取剪贴板管理器：
        val cm: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 创建普通字符型ClipData
        val mClipData = ClipData.newPlainText("Label", content)
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData)
    }

    fun onAppIdChangeClick(view: View){
        val appId = appIdEt.text.toString().trim()
        ConfigInstance.setAppId(appId)
        Utils.showToast(this,"设置成功，请重启app")
    }

    fun onApiDomainClick(view : View){
        val apiDomain = apiDomainEt.text.toString().trim()
        ConfigInstance.setApiDomain(apiDomain)
        Utils.showToast(this,"设置成功，请重启app")
    }

    fun copyBaseAppIdClick(view : View){
        copyContentToClipboard("")
        Utils.showToast(this,"复制成功")
    }

    fun copyTest15ApiClick(view : View){
        copyContentToClipboard("")
        Utils.showToast(this,"复制成功")
    }

    fun copyLineApiClick(view : View){
        copyContentToClipboard("")
        Utils.showToast(this,"复制成功")
    }

    fun initView(){
        apiDomainTv.text = "当前的域名是 ： " + LocalInfo.getInstance().apiDomain
        appIdTv.text = "当前的AppId是 ： " + LocalInfo.getInstance().appId
    }



}
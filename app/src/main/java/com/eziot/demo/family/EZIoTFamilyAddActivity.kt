package com.eziot.demo.family

import android.os.Bundle
import android.view.View
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.family.EZIotFamilyManager
import com.eziot.family.model.family.EZIoTFamilyInfo
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import kotlinx.android.synthetic.main.eziot_family_add_activity.*

class EZIoTFamilyAddActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_family_add_activity)
        addBackBtn(toolbar)
        initView()
    }

    private fun initView(){
    }

    fun onClickAddFamily(view : View){
        val familyName = familyNameEt.text.toString()
        showWaitDialog()
        EZIotFamilyManager.createFamily(familyName,object : IEZIoTResultCallback<EZIoTFamilyInfo> {
            override fun onSuccess(t: EZIoTFamilyInfo) {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTFamilyAddActivity,R.string.addSuccess)
                finish()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTFamilyAddActivity,R.string.operateFail)
            }

        })
    }



}
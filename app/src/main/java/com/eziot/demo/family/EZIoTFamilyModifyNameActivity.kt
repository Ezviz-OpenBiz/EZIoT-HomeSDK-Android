package com.eziot.demo.family

import android.os.Bundle
import android.view.View
import com.eziot.common.http.callback.IResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.IntentContent
import com.eziot.family.EZIotFamilyManager
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import kotlinx.android.synthetic.main.eziot_family_add_activity.*

class EZIoTFamilyModifyNameActivity : BaseActivity() {


    private var familyId : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_family_modify_name_activity)
        addBackBtn(toolbar)
        initData()
    }

    fun initData(){
        familyId = intent.getLongExtra(IntentContent.FAMILY_ID,0)
    }

    fun onClickModifyFamilyName(view : View){
        val familyName = familyNameEt.text.toString()
        showWaitDialog()
        EZIotFamilyManager.modifyFamilyName(familyId,familyName,object : IResultCallback{
            override fun onSuccess() {
                Utils.showToast(this@EZIoTFamilyModifyNameActivity,R.string.operateSuccess)
                dismissWaitDialog()
                finish()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Utils.showToast(this@EZIoTFamilyModifyNameActivity,R.string.operateFail)
                dismissWaitDialog()
            }

        })
    }

    override fun finish() {
        setResult(IntentContent.REFRESH_CODE)
        super.finish()
    }



}
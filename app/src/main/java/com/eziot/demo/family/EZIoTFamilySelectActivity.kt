package com.eziot.demo.family

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.family.adapter.EZIoTFamilySelectAdapter
import com.eziot.family.EZIotFamilyManager
import com.eziot.family.model.family.EZIoTFamilyInfo
import com.eziot.family.model.family.EZIoTFamilyInfoResp
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import kotlinx.android.synthetic.main.eziot_family_list_activity.*

class EZIoTFamilySelectActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_family_select_activity)
        addBackBtn(toolbar)
        initData()
    }

    private fun initData(){
        showWaitDialog()
        val extFamilyListLocal = EZIotFamilyManager.getFamilyListLocal()
        if(extFamilyListLocal == null || extFamilyListLocal.isEmpty()){
            EZIotFamilyManager.getFamilyList(object : IEZIoTResultCallback<List<EZIoTFamilyInfo>> {

                override fun onError(errorCode: Int, errorDesc: String?) {
                    dismissWaitDialog()
                    Utils.showToast(this@EZIoTFamilySelectActivity,errorDesc)
                }

                override fun onSuccess(t: List<EZIoTFamilyInfo>) {
                    dismissWaitDialog()
                    val familyList = ArrayList<EZIoTFamilyInfo>()
                    familyList.addAll(t)
                    familyListRv.layoutManager = LinearLayoutManager(this@EZIoTFamilySelectActivity)
                    familyListRv.adapter = EZIoTFamilySelectAdapter(familyList)
                }

            })
        } else {
            dismissWaitDialog()
            val familyList = ArrayList<EZIoTFamilyInfo>()
            familyList.addAll(extFamilyListLocal)
            familyListRv.layoutManager = LinearLayoutManager(this@EZIoTFamilySelectActivity)
            familyListRv.adapter = EZIoTFamilySelectAdapter(familyList)
        }
    }

}
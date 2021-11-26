package com.eziot.demo.family

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.IntentContent
import com.eziot.demo.family.adapter.EZIoTFamilyListAdapter
import com.eziot.family.EZIotFamilyManager
import com.eziot.family.model.family.EZIoTFamilyInfoResp
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import kotlinx.android.synthetic.main.eziot_family_list_activity.*

class EZIoTFamilyListActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_family_list_activity)
        addBackBtn(toolbar)
        initData()
    }

    private fun initData(){
        showWaitDialog()
        EZIotFamilyManager.getExtFamilyList(object : IEZIoTResultCallback<EZIoTFamilyInfoResp> {
            override fun onSuccess(t: EZIoTFamilyInfoResp) {
                dismissWaitDialog()
                familyListRv.layoutManager = LinearLayoutManager(this@EZIoTFamilyListActivity)
                familyListRv.adapter = EZIoTFamilyListAdapter(t.ownFamilyInfos,this@EZIoTFamilyListActivity)

                joinFamilyListRv.layoutManager = LinearLayoutManager(this@EZIoTFamilyListActivity)
                joinFamilyListRv.adapter = EZIoTFamilyListAdapter(t.joinFamilyInfos,this@EZIoTFamilyListActivity)
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTFamilyListActivity,errorDesc)
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == IntentContent.REFRESH_CODE){
            initData()
        }
    }


}
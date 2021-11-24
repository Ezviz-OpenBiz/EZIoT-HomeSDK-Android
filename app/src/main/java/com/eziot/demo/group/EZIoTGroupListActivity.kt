package com.eziot.demo.group

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.base.IntentContent
import com.eziot.demo.group.adapter.EZIoTGroupListAdapter
import com.eziot.family.EZIoTGroupManager
import com.eziot.family.model.group.EZIoTGroupInfo
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import kotlinx.android.synthetic.main.eziot_family_list_activity.*

class EZIoTGroupListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_group_list_activity)
        addBackBtn(toolbar)
        initData()
    }

    private fun initData(){
        showWaitDialog()
        EZIoTGroupManager.getGroupList(BaseResDataManager.familyInfo!!.id,object : IEZIoTResultCallback<List<EZIoTGroupInfo>>{
            override fun onSuccess(t: List<EZIoTGroupInfo>) {
                dismissWaitDialog()
                familyListRv.layoutManager = LinearLayoutManager(this@EZIoTGroupListActivity)
                familyListRv.adapter = EZIoTGroupListAdapter(t,this@EZIoTGroupListActivity)
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTGroupListActivity,errorDesc)
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
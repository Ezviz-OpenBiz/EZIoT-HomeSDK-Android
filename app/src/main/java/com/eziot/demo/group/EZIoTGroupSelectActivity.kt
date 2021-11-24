package com.eziot.demo.group

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.group.adapter.EZIoTGroupSelectAdapter
import com.eziot.family.EZIoTGroupManager
import com.eziot.family.model.group.EZIoTGroupInfo
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import kotlinx.android.synthetic.main.eziot_group_select_activity.*

class EZIoTGroupSelectActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_group_select_activity)
        addBackBtn(toolbar)
        initData()
    }

    private fun initData(){
        showWaitDialog()
        EZIoTGroupManager.getGroupList(BaseResDataManager.familyInfo!!.id,object : IEZIoTResultCallback<List<EZIoTGroupInfo>>{
            override fun onSuccess(t: List<EZIoTGroupInfo>) {
                dismissWaitDialog()
                groupListRv.layoutManager = LinearLayoutManager(this@EZIoTGroupSelectActivity)
                groupListRv.adapter = EZIoTGroupSelectAdapter(t)
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTGroupSelectActivity,errorDesc)
            }

        })
    }

}
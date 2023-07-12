package com.eziot.demo.group

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.base.IntentContent
import com.eziot.demo.group.adapter.EZIoTGroupListAdapter
import com.eziot.family.EZIoTRoomManager
import com.eziot.family.model.group.EZIoTRoomInfo
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
        val roomListLocal = EZIoTRoomManager.getRoomListLocal(BaseResDataManager.familyInfo!!.id)
        if(roomListLocal == null || roomListLocal.isEmpty()){
            EZIoTRoomManager.getRoomList(BaseResDataManager.familyInfo!!.id,object : IEZIoTResultCallback<List<EZIoTRoomInfo>>{
                override fun onSuccess(t: List<EZIoTRoomInfo>) {
                    dismissWaitDialog()
                    familyListRv.layoutManager = LinearLayoutManager(this@EZIoTGroupListActivity)
                    familyListRv.adapter = EZIoTGroupListAdapter(t,this@EZIoTGroupListActivity)
                }

                override fun onError(errorCode: Int, errorDesc: String?) {
                    dismissWaitDialog()
                    Utils.showToast(this@EZIoTGroupListActivity,errorDesc)
                }

            })
        } else {
            dismissWaitDialog()
            familyListRv.layoutManager = LinearLayoutManager(this@EZIoTGroupListActivity)
            familyListRv.adapter = EZIoTGroupListAdapter(roomListLocal,this@EZIoTGroupListActivity)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == IntentContent.REFRESH_CODE){
            initData()
        }
    }


}
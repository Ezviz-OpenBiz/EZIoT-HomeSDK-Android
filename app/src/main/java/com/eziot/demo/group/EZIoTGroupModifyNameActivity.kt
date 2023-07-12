package com.eziot.demo.group

import android.os.Bundle
import android.view.View
import com.eziot.common.http.callback.IResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.IntentContent
import com.eziot.family.EZIoTRoomManager
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import kotlinx.android.synthetic.main.eziot_group_modify_name_activity.*

class EZIoTGroupModifyNameActivity : BaseActivity() {

    private var groupId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_group_modify_name_activity)
        addBackBtn(toolbar)
        initData()
    }

    fun initData(){
        groupId = intent.getIntExtra(IntentContent.GROUP_ID,0)
    }

    fun onClickModifyGroupName(view : View){
        val currentFamilyInfo = getCurrentFamilyInfo()
        val groupName = groupNameEt.text.toString()
        EZIoTRoomManager.modifyRoomName(currentFamilyInfo!!.id,groupId,groupName,object : IResultCallback{
            override fun onSuccess() {
                Utils.showToast(this@EZIoTGroupModifyNameActivity,R.string.operateSuccess)
                setResult(IntentContent.REFRESH_CODE)
                finish()
                dismissWaitDialog()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTGroupModifyNameActivity,R.string.operateFail)
            }

        })
    }


}
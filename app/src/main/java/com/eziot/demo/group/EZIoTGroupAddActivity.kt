package com.eziot.demo.group

import android.os.Bundle
import android.view.View
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.base.IntentContent
import com.eziot.family.EZIoTGroupManager
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import kotlinx.android.synthetic.main.eziot_group_add_activity.*

class EZIoTGroupAddActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_group_add_activity)
        addBackBtn(toolbar)
    }

    fun onClickAddGroup(view : View){
        val groupName = groupNameEt.text.toString()
        showWaitDialog()
//        EZIoTGroupManager.addNewGroup(BaseResDataManager.familyInfo!!.id,groupName,object : IEZIoTResultCallback<Int> {
//
//            override fun onSuccess(groupId : Int) {
//                dismissWaitDialog()
//                Utils.showToast(this@EZIoTGroupAddActivity,R.string.addSuccess)
//                setResult(IntentContent.REFRESH_CODE)
//                finish()
//            }
//
//            override fun onError(errorCode: Int, errorDesc: String?) {
//                dismissWaitDialog()
//                Utils.showToast(this@EZIoTGroupAddActivity,R.string.operateFail)
//            }
//
//        })
    }
}
package com.eziot.demo.family

import android.os.Bundle
import android.view.View
import com.eziot.common.http.callback.IResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.IntentContent
import com.eziot.family.EZIotFamilyManager
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import kotlinx.android.synthetic.main.eziot_family_invite_activity.*

class EZIoTFamilyInviteMemberActivity : BaseActivity() {

    private var familyId : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_family_invite_activity)
        addBackBtn(toolbar)
        initData()
    }

    fun initData(){
        familyId = intent.getLongExtra(IntentContent.FAMILY_ID,0)
    }

    fun onClickAddFamilyMember(view : View){
        val account = accountEt.text.toString()
        showWaitDialog()
        EZIotFamilyManager.inviteFamilyMember(familyId,account,object : IResultCallback{
            override fun onSuccess() {
                Utils.showToast(this@EZIoTFamilyInviteMemberActivity,R.string.inviteSuccess)
                dismissWaitDialog()
                setResult(IntentContent.REFRESH_CODE)
                finish()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Utils.showToast(this@EZIoTFamilyInviteMemberActivity,errorDesc ?: errorDesc)
                dismissWaitDialog()
            }

        })
    }


}
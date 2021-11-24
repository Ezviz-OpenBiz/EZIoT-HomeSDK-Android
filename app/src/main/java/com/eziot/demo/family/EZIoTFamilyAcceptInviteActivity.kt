package com.eziot.demo.family

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.common.http.callback.IResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.IntentContent
import com.eziot.demo.family.adapter.EZIoTFamilyMemberListAdapter
import com.eziot.family.EZIotFamilyManager
import com.eziot.family.model.family.EZIoTFamilyDetailInfo
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import kotlinx.android.synthetic.main.eziot_family_accept_activity.*
import kotlinx.android.synthetic.main.eziot_family_add_activity.*
import kotlinx.android.synthetic.main.eziot_family_add_activity.toolbar
import kotlinx.android.synthetic.main.eziot_family_info_activity.*
import kotlinx.android.synthetic.main.eziot_family_info_activity.familyNameTv
import kotlinx.android.synthetic.main.eziot_family_info_activity.memoryInfoRv

class EZIoTFamilyAcceptInviteActivity : BaseActivity() {

    var familyId : Long = 0

    lateinit var memberId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_family_accept_activity)
        addBackBtn(toolbar)
        initData()
    }

    private fun initData(){
        familyId = intent.getLongExtra(IntentContent.FAMILY_ID,0)
        memberId = intent.getStringExtra(IntentContent.MEMBER_ID)!!
        showWaitDialog()
        EZIotFamilyManager.getFamilyDetail(familyId,object :
            IEZIoTResultCallback<EZIoTFamilyDetailInfo> {
            override fun onSuccess(t: EZIoTFamilyDetailInfo) {
                initView(t)
                dismissWaitDialog()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                if(errorCode == 131035){
                    Utils.showToast(this@EZIoTFamilyAcceptInviteActivity,errorDesc)
                    finish()
                } else {
                    Utils.showToast(this@EZIoTFamilyAcceptInviteActivity,errorDesc)
                }
            }

        })
    }

    private fun initView(ezIotFamilyDetailInfo : EZIoTFamilyDetailInfo){
        when(ezIotFamilyDetailInfo.inviteStatus){
            0 -> {
                acceptHintTv.visibility = View.GONE
                acceptOperateLayout.visibility = View.VISIBLE
            }
            1 -> {
                acceptHintTv.setText(R.string.joined)
                acceptHintTv.visibility = View.VISIBLE
                acceptOperateLayout.visibility = View.GONE
            }
            2 -> {
                acceptHintTv.setText(R.string.refuse_join)
                acceptHintTv.visibility = View.VISIBLE
                acceptOperateLayout.visibility = View.GONE
            }
            3 -> {
                acceptHintTv.setText(R.string.expire)
                acceptHintTv.visibility = View.VISIBLE
                acceptOperateLayout.visibility = View.GONE
            }
        }
        familyNameTv.text = ezIotFamilyDetailInfo.familyName
        memoryInfoRv.layoutManager = LinearLayoutManager(this)
        memoryInfoRv.adapter = EZIoTFamilyMemberListAdapter(ezIotFamilyDetailInfo.ezIoTFamilyMemberInfos,this,false)
    }

    fun onClickAccept(view : View){
        showWaitDialog()
        EZIotFamilyManager.acceptMemberInvite(familyId,memberId,object : IResultCallback{
            override fun onSuccess() {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTFamilyAcceptInviteActivity,R.string.operateSuccess)
                finish()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTFamilyAcceptInviteActivity,errorDesc)
            }

        })
    }

    fun onClickRefuse(view : View){
        showWaitDialog()
        EZIotFamilyManager.refuseMemberInvite(familyId,memberId,object : IResultCallback{
            override fun onSuccess() {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTFamilyAcceptInviteActivity,R.string.operateSuccess)
                finish()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTFamilyAcceptInviteActivity,errorDesc)
            }

        })
    }

}
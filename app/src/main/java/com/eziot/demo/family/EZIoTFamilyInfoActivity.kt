package com.eziot.demo.family

import android.content.Intent
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
import com.eziot.family.model.family.EZIoTFamilyMemberInfo
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import com.eziot.user.EZIotUserManager
import com.eziot.user.http.bean.GetUserInfoResp
import com.eziot.user.model.user.EZIotBaseUserInfo
import kotlinx.android.synthetic.main.eziot_family_add_activity.toolbar
import kotlinx.android.synthetic.main.eziot_family_info_activity.*

class EZIoTFamilyInfoActivity : BaseActivity() {

    var familyId : Long = 0

    private var isNeedRefresh = false

    lateinit var ezIotBaseUserInfo : EZIotBaseUserInfo

    lateinit var ezIotFamilyDetailInfo : EZIoTFamilyDetailInfo

    override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_family_info_activity)
        addBackBtn(toolbar)
        initData()
    }

    private fun initData(){
        familyId = intent.getLongExtra(IntentContent.FAMILY_ID,0)
        showWaitDialog()
        EZIotUserManager.getUserProfile(object : IEZIoTResultCallback<GetUserInfoResp>{
            override fun onSuccess(t: GetUserInfoResp) {
                ezIotBaseUserInfo = t.userInfo
                EZIotFamilyManager.getFamilyDetail(familyId,object : IEZIoTResultCallback<EZIoTFamilyDetailInfo>{
                    override fun onSuccess(t: EZIoTFamilyDetailInfo) {
                        initView(t)
                        dismissWaitDialog()
                    }

                    override fun onError(errorCode: Int, errorDesc: String?) {
                        dismissWaitDialog()
                        Utils.showToast(this@EZIoTFamilyInfoActivity,errorDesc)
                    }

                })
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTFamilyInfoActivity,errorDesc)
            }

        })

    }

    private fun initView(ezIotFamilyDetailInfo : EZIoTFamilyDetailInfo){
        this. ezIotFamilyDetailInfo = ezIotFamilyDetailInfo
        val hostMem = isHostMem(ezIotFamilyDetailInfo, ezIotBaseUserInfo)
        if(hostMem){
            exitFamily.visibility = View.GONE
            deleteFamilyBtn.visibility = View.VISIBLE
            inviteFamilyBtn.visibility = View.VISIBLE
        } else {
            exitFamily.visibility = View.VISIBLE
            deleteFamilyBtn.visibility = View.GONE
            inviteFamilyBtn.visibility = View.GONE
        }

        familyNameTv.text = ezIotFamilyDetailInfo.familyName
        roomNum.text = ezIotFamilyDetailInfo.roomNum.toString()
        memoryInfoRv.layoutManager = LinearLayoutManager(this)
        memoryInfoRv.adapter = EZIoTFamilyMemberListAdapter(ezIotFamilyDetailInfo.ezIoTFamilyMemberInfos,this,hostMem)
    }

    fun onClickDeleteFamily(view : View){
        EZIotFamilyManager.deleteFamily(familyId,object : IResultCallback{
            override fun onSuccess() {
                isNeedRefresh = true
                finish()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {

                Utils.showToast(this@EZIoTFamilyInfoActivity,errorDesc)
            }

        })
    }


    fun onClickInviteFamily(view : View){
        val intent = Intent(this,EZIoTFamilyInviteMemberActivity::class.java)
        intent.putExtra(IntentContent.FAMILY_ID,familyId)
        startActivityForResult(intent,IntentContent.REFRESH_CODE)
    }

    fun onClickExitFamily(view : View){
        showWaitDialog()
        EZIotFamilyManager.memberSignOut(familyId,object : IResultCallback{
            override fun onSuccess() {
                Utils.showToast(this@EZIoTFamilyInfoActivity,R.string.operateSuccess)
                dismissWaitDialog()
                isNeedRefresh = true
                finish()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Utils.showToast(this@EZIoTFamilyInfoActivity,errorDesc)
                dismissWaitDialog()
            }

        })
    }

    fun onClickModifyFamilyName(view : View){
        if(isHostMem(ezIotFamilyDetailInfo, ezIotBaseUserInfo)){
            val intent = Intent(this , EZIoTFamilyModifyNameActivity::class.java)
            intent.putExtra(IntentContent.FAMILY_ID,familyId)
            startActivityForResult(intent,2)
        } else {
            Utils.showToast(this,R.string.family_home_operate)
        }
    }

    private fun isHostMem(ezIotFamilyDetailInfo: EZIoTFamilyDetailInfo,baseUserInfo: EZIotBaseUserInfo) : Boolean{
        EZIotUserManager.getUserInfo()
        val ezIoTFamilyMemberInfos = ezIotFamilyDetailInfo.ezIoTFamilyMemberInfos
        var familyHostMemInfo : EZIoTFamilyMemberInfo? = null
        for (ezIotFamilyMemberInfo in ezIoTFamilyMemberInfos){
            if(ezIotFamilyMemberInfo.type == 0){
                familyHostMemInfo = ezIotFamilyMemberInfo
                break
            }
        }
        return familyHostMemInfo!!.phone.equals(baseUserInfo.phone) || familyHostMemInfo.phone.equals(baseUserInfo.email)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == IntentContent.REFRESH_CODE){
            isNeedRefresh = true
            showWaitDialog()
            EZIotUserManager.getUserProfile(object : IEZIoTResultCallback<GetUserInfoResp>{
                override fun onSuccess(t: GetUserInfoResp) {
                    ezIotBaseUserInfo = t.userInfo
                    EZIotFamilyManager.getFamilyDetail(familyId,object : IEZIoTResultCallback<EZIoTFamilyDetailInfo>{
                        override fun onSuccess(t: EZIoTFamilyDetailInfo) {
                            initView(t)
                            dismissWaitDialog()
                        }

                        override fun onError(errorCode: Int, errorDesc: String?) {
                            dismissWaitDialog()
                            Utils.showToast(this@EZIoTFamilyInfoActivity,errorDesc)
                        }

                    })
                }

                override fun onError(errorCode: Int, errorDesc: String?) {
                    dismissWaitDialog()
                    Utils.showToast(this@EZIoTFamilyInfoActivity,errorDesc)
                }

            })
        }
    }

    override fun finish() {
        if(isNeedRefresh){
            setResult(IntentContent.REFRESH_CODE)
        }
        super.finish()
    }



}
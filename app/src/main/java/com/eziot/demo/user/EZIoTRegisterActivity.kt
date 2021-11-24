package com.eziot.demo.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.common.http.callback.IResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.utils.CommonUtils
import com.eziot.iotsdkdemo.R
import com.eziot.user.EZIotUserManager
import com.eziot.user.model.account.EZIoTUserBizType
import com.eziot.user.model.area.EZIotUserAreaInfo
import com.eziot.user.model.user.EZIoTUserSessionInfo
import kotlinx.android.synthetic.main.eziot_user_register_activity.*

class EZIoTRegisterActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_user_register_activity)
        addBackBtn(toolbar)
    }

    fun onClickGetVerCode(view: View) {
        val countryCode = phoneCodeEt.text.toString()
        val userName = userNameEt.text.toString()
        if (countryCode.isEmpty() || userName.isEmpty()) {
            CommonUtils.showToast(this, resources.getString(R.string.inputErrorHint))
            return
        }
        EZIotUserManager.getSMSCode(
            userName,
            countryCode.toInt(),
            EZIoTUserBizType.USER_REGISTRATION,
            object : IResultCallback {
                override fun onSuccess() {
                    CommonUtils.showToast(this@EZIoTRegisterActivity,resources.getString(R.string.getVerCodeSuccess))
                }

                override fun onError(errorCode: Int, errorDesc: String?) {
                    CommonUtils.showToast(this@EZIoTRegisterActivity,errorDesc)
                }

            })
    }

    fun onClickRegister(view: View) {
        val phoneCode = phoneCodeEt.text.toString()
        val userName = userNameEt.text.toString()
        val verCode = verCodeEt.text.toString()
        val password = passwordEt.text.toString()
        if (phoneCode.isEmpty() || userName.isEmpty() || verCode.isEmpty() || password.isEmpty()) {
            CommonUtils.showToast(this, resources.getString(R.string.inputErrorHint))
            return
        }
        showWaitDialog()
        EZIotUserManager.getAreaList(object : IEZIoTResultCallback<List<EZIotUserAreaInfo>>{
            override fun onSuccess(t: List<EZIotUserAreaInfo>) {

                var areaId = 0

                for (ezIotUserAreaInfo in t){
                    if(ezIotUserAreaInfo.telephoneCode == phoneCode.toInt()){
                        areaId = ezIotUserAreaInfo.id
                        break
                    }
                }

                EZIotUserManager.registerWithAccount(
                    userName,
                    phoneCode.toInt(),
                    password,
                    areaId,
                    verCode,
                    object : IEZIoTResultCallback<EZIoTUserSessionInfo> {
                        override fun onSuccess(t: EZIoTUserSessionInfo) {
                            dismissWaitDialog()
                            CommonUtils.showToast(this@EZIoTRegisterActivity,resources.getString(R.string.registerSuccess))
                            startActivity(Intent(this@EZIoTRegisterActivity,EZIoTLoginActivity::class.java))
                            finish()
                        }

                        override fun onError(errorCode: Int, errorDesc: String?) {
                            dismissWaitDialog()
                            CommonUtils.showToast(this@EZIoTRegisterActivity,errorDesc)
                        }

                    }
                )

            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                CommonUtils.showToast(this@EZIoTRegisterActivity,errorDesc)
            }

        })
    }


}
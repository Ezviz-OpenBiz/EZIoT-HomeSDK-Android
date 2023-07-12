package com.eziot.demo.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.eziot.common.http.callback.IResultCallback
import com.eziot.demo.EZIoTMainTabActivity
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.utils.CommonUtils
import com.eziot.iotsdkdemo.R
import com.eziot.user.EZIotUserManager
import com.eziot.user.http.bean.LoginResp
import com.eziot.user.http.callback.IEzIoTLoginResultCallback
import com.eziot.user.model.account.EZIoTUserBizType
import com.eziot.user.model.account.EZIoTUserLoginParam
import com.eziot.user.model.account.EZIotLoginResp
import kotlinx.android.synthetic.main.eziot_user_register_activity.*

class EZIoTResetPwdActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_user_forget_password_activity)
        addBackBtn(toolbar)
    }

    fun onClickGetVerCode(view : View){
        val countryCode = phoneCodeEt.text.toString()
        val userName = userNameEt.text.toString()
        if (countryCode.isEmpty() || userName.isEmpty()) {
            CommonUtils.showToast(this, resources.getString(R.string.inputErrorHint))
            return
        }
        showWaitDialog()
        EZIotUserManager.getSMSCode(userName,countryCode.toInt(), EZIoTUserBizType.RETRIEVE_PASSWORD, null, null,  object : IResultCallback{
            override fun onSuccess() {
                dismissWaitDialog()
                CommonUtils.showToast(this@EZIoTResetPwdActivity,resources.getString(R.string.getVerCodeSuccess))
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                CommonUtils.showToast(this@EZIoTResetPwdActivity,errorDesc)
            }

        })

    }

    fun onClickReset(view : View){
        val phoneCode = phoneCodeEt.text.toString()
        val userName = userNameEt.text.toString()
        val verCode = verCodeEt.text.toString()
        val password = passwordEt.text.toString()
        if (phoneCode.isEmpty() || userName.isEmpty() || verCode.isEmpty() || password.isEmpty()) {
            CommonUtils.showToast(this, resources.getString(R.string.inputErrorHint))
            return
        }
        showWaitDialog()
        EZIotUserManager.resetPasswordWithAccount(
            userName,
            phoneCode.toInt(),
            password,
            verCode,
            object : IResultCallback {
                override fun onSuccess() {
                    dismissWaitDialog()
                    CommonUtils.showToast(
                        this@EZIoTResetPwdActivity,
                        resources.getString(R.string.resetSuccess)
                    )
                    val ezIoTUserLoginParam = EZIoTUserLoginParam()
                    ezIoTUserLoginParam.password = password
                    ezIoTUserLoginParam.account = userName
                    ezIoTUserLoginParam.phoneCode = phoneCode
                    EZIotUserManager.login(ezIoTUserLoginParam, object :
                        IEzIoTLoginResultCallback<EZIotLoginResp> {
                        override fun onSuccess(t: EZIotLoginResp) {
                            val intent = Intent(this@EZIoTResetPwdActivity, EZIoTMainTabActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        override fun onError(
                            errorCode: Int,
                            errorDesc: String?,
                            loginResp: LoginResp?
                        ) {
                            CommonUtils.showToast(this@EZIoTResetPwdActivity, errorDesc)
                        }

                    })
                }

                override fun onError(errorCode: Int, errorDesc: String?) {
                    dismissWaitDialog()
                    CommonUtils.showToast(this@EZIoTResetPwdActivity, errorDesc)
                }

            })
    }


}
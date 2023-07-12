package com.eziot.demo.user

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.eziot.common.http.callback.IResultCallback
import com.eziot.common.utils.LocalInfo
import com.eziot.demo.base.BaseResDataManager
import com.eziot.iotsdkdemo.R
import com.eziot.user.EZIotUserManager
import com.eziot.user.model.account.EZIoTUserBizType
import com.eziot.user.model.account.EZIoTUserContactModifyParam

class EZIoTVerifyPhoneOrEmailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eziot_verify_phone_or_email)

        initView()
        initData()
    }
    fun initView(){
        inputEdit = findViewById(R.id.inputVerifyCodeEdit)
        inputButton = findViewById(R.id.inputVerifyCodeButton)
        verifyPhoneOrEmailPage = findViewById(R.id.verifyPhoneOrEmailPage)
        currentAccount = findViewById(R.id.inputVerifyCodeTip)
        verifyPhoneOrEmailPage?.visibility = View.GONE
        inputButton?.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                if(inputEdit?.text.toString().isNullOrEmpty()){
                    Toast.makeText(LocalInfo.getInstance().context, "请输入验证码", Toast.LENGTH_SHORT).show()
                }
                else{
                    BaseResDataManager.mEZIoTUserContactModifyParam?.oldContactSMScode = inputEdit?.text.toString()
                    EZIotUserManager.verifySmsCode(oldAccount!!, null,
                        BaseResDataManager.mEZIoTUserContactModifyParam?.oldContactSMScode!!, BaseResDataManager.oldBizType!!, object: IResultCallback{
                            override fun onSuccess() {
                                var intent = Intent(this@EZIoTVerifyPhoneOrEmailActivity, EZIoTInputNewAccountActivity::class.java)
                                intent.putExtra("operationType", operationType)
                                startActivity(intent)
                            }
                            override fun onError(errorCode: Int, errorDesc: String?) {
                                Toast.makeText(LocalInfo.getInstance().context, "$errorDesc", Toast.LENGTH_SHORT).show()
                            }
                        })
                }
            }

        })
    }

    fun initData(){
        BaseResDataManager.mEZIoTUserContactModifyParam = EZIoTUserContactModifyParam()
        mPhone = if(!intent.getStringExtra("phone").isNullOrEmpty()) intent.getStringExtra("phone")!! else ""
        mEmail = if(!intent.getStringExtra("email").isNullOrEmpty()) intent.getStringExtra("email")!! else ""
        operationType = intent.getIntExtra("operationType", 1)

        if(operationType == 1){
            //修改手机号
            if(!mPhone.isNullOrEmpty() && !mEmail.isNullOrEmpty()){
                verifyUserPhone(mPhone, mEmail)
            }else{
                verifyUserPhone()
            }
        }else if(operationType == 3){
            //修改邮箱号
            if(!mPhone.isNullOrEmpty() && !mEmail.isNullOrEmpty()){
                verifyUserEmail(mPhone, mEmail!!)
            }else{
                verifyUserEmail()
            }
        }
    }

    private fun verifyUserPhone(){
        if(mPhone.isNullOrEmpty()){
            BaseResDataManager.oldBizType = EZIoTUserBizType.UPDATE_PHONE_VALIDATE_OLD_EMAIL
            BaseResDataManager.mEZIoTUserContactModifyParam?.oldSMSType = 3
            oldAccount = LocalInfo.getInstance().account
        }else{
            BaseResDataManager.oldBizType = EZIoTUserBizType.UPDATE_PHONE_VALIDATE_OLD_PHONE
            BaseResDataManager.mEZIoTUserContactModifyParam?.oldSMSType = 1
            oldAccount = LocalInfo.getInstance().countryCode + LocalInfo.getInstance().account
        }
        BaseResDataManager.newBizType = EZIoTUserBizType.UPDATE_PHONE_VALIDATE_NEW_PHONE
        sendSmsVerifyDialog()
    }

    private fun verifyUserPhone(phone: String, email: String){
        var build: AlertDialog.Builder = AlertDialog.Builder(this).
        setTitle("选择验证方式").
        setNeutralButton("取消", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                finish()
            }
        }).
        setNegativeButton("短信", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                oldAccount = phone
                BaseResDataManager.oldBizType = EZIoTUserBizType.UPDATE_PHONE_VALIDATE_OLD_PHONE
                BaseResDataManager.newBizType = EZIoTUserBizType.UPDATE_PHONE_VALIDATE_NEW_PHONE
                BaseResDataManager.mEZIoTUserContactModifyParam?.oldSMSType = 1
                sendSmsVerify()

            }
        }).
        setPositiveButton("邮箱", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                oldAccount = email
                BaseResDataManager.oldBizType = EZIoTUserBizType.UPDATE_PHONE_VALIDATE_OLD_EMAIL
                BaseResDataManager.newBizType = EZIoTUserBizType.UPDATE_PHONE_VALIDATE_NEW_PHONE
                BaseResDataManager.mEZIoTUserContactModifyParam?.oldSMSType = 3
                sendSmsVerify()
            }
        })
        build.create().show()
    }
    private fun verifyUserEmail(){

        if(mPhone.isNullOrEmpty()){
            BaseResDataManager.oldBizType = EZIoTUserBizType.UPDATE_EMAIL_VALIDATE_OLD_EMAIL
            BaseResDataManager.mEZIoTUserContactModifyParam?.oldSMSType = 3
            oldAccount = LocalInfo.getInstance().account
        }else{
            BaseResDataManager.oldBizType = EZIoTUserBizType.UPDATE_EMAIL_VALIDATE_OLD_PHONE
            BaseResDataManager.mEZIoTUserContactModifyParam?.oldSMSType = 1
            oldAccount = LocalInfo.getInstance().countryCode + LocalInfo.getInstance().account
        }
        BaseResDataManager.newBizType = EZIoTUserBizType.UPDATE_EMAIL_VALIDATE_NEW_EMAIL

        sendSmsVerifyDialog()
    }

    private fun verifyUserEmail(phone: String, email: String){

        var build: AlertDialog.Builder = AlertDialog.Builder(this).
        setTitle("选择验证方式").
        setNeutralButton("取消", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                finish()
            }
        }).
        setNegativeButton("短信", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                oldAccount = phone
                BaseResDataManager.oldBizType = EZIoTUserBizType.UPDATE_EMAIL_VALIDATE_OLD_PHONE
                BaseResDataManager.newBizType = EZIoTUserBizType.UPDATE_EMAIL_VALIDATE_NEW_EMAIL
                BaseResDataManager.mEZIoTUserContactModifyParam?.oldSMSType = 1
                sendSmsVerify()
            }
        }).
        setPositiveButton("邮箱", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                oldAccount = email
                BaseResDataManager.oldBizType = EZIoTUserBizType.UPDATE_EMAIL_VALIDATE_OLD_EMAIL
                BaseResDataManager.newBizType = EZIoTUserBizType.UPDATE_EMAIL_VALIDATE_NEW_EMAIL
                BaseResDataManager.mEZIoTUserContactModifyParam?.oldSMSType = 3
                sendSmsVerify()
            }
        })
        build.create().show()
    }

    private fun sendSmsVerifyDialog(){
        var build: AlertDialog.Builder = AlertDialog.Builder(this).
        setTitle("发送短信验证").
        setNegativeButton("取消", null).
        setPositiveButton("确认", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                sendSmsVerify()
            }
        })
        build.create().show()
    }

    private fun sendSmsVerify(){
        verifyPhoneOrEmailPage?.visibility = View.VISIBLE
        currentAccount?.text = oldAccount!!
        EZIotUserManager.getSMSCode(oldAccount!!, null,
            BaseResDataManager.oldBizType!!, null, null, object: IResultCallback {
            override fun onSuccess() {
//                inputSmsVerifyDialog()
            }
            override fun onError(errorCode: Int, errorDesc: String?) {
                Toast.makeText(LocalInfo.getInstance().context, "$errorDesc", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private var oldAccount: String? = null

    private var operationType: Int? = null
    private var mPhone: String = ""
    private var mEmail: String = ""
    private var inputEdit: EditText? = null
    private var currentAccount: TextView? = null
    private var inputButton: Button? = null
    private var verifyPhoneOrEmailPage: ViewGroup? = null
}
package com.eziot.demo.generalSetting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.eziot.common.http.callback.IResultCallback
import com.eziot.common.utils.LocalInfo
import com.eziot.demo.EZIoTMainTabActivity
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.utils.CommonUtils
import com.eziot.iotsdkdemo.R
import com.eziot.user.EZIotUserManager
import com.eziot.user.http.bean.LoginResp
import com.eziot.user.http.callback.IEzIoTLoginResultCallback
import com.eziot.user.model.account.EZIoTUserBizType
import com.eziot.user.model.account.EZIotLoginResp

class EZIoTInputVerifyCodeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eziot_input_verify_code)

        initView()
        initData()
    }

    private fun initView(){
        inputText = findViewById(R.id.InputVerifyCodeText)
        inputButton = findViewById(R.id.InputVerifyCodeButton)
        goBack = findViewById(R.id.input_verifyCode_go_back)
        goBack?.setOnClickListener(View.OnClickListener {
            finish()
        })
        inputButton?.setOnClickListener(View.OnClickListener {
            if(inputText?.text.toString().isNullOrEmpty()){
                Toast.makeText(LocalInfo.getInstance().context, "验证码不能为空", Toast.LENGTH_SHORT).show()
            }else{
                verifySms(inputText?.text.toString())
            }
        })
    }

    private fun initData(){
        operationType = intent.getIntExtra("operationType", 2)
        if(operationType == 0 || operationType == 2){
            bizType = EZIoTUserBizType.TERMINAL_BIND
        }else if(operationType == 1 || operationType == 3){
            bizType = EZIoTUserBizType.TERMINAL_DELETE
        }
    }

    private fun verifySms(smsCode: String){
        BaseResDataManager.ezIotLoginParam?.smsCode = smsCode
        EZIotUserManager.verifySmsCode(LocalInfo.getInstance().account, LocalInfo.getInstance().countryCode.toInt(), smsCode,
            bizType!!, object: IResultCallback{
                override fun onSuccess() {
                    if(operationType == 2){
                        finish()
                    }
                    else if(operationType == 1 || operationType == 3){
                        intent.putExtra("smsCode", smsCode)
                        setResult(operationType!!, intent)
                        finish()
                    }
                    else if(operationType == 0 ){
                        userLogin()
                    }
                }

                override fun onError(errorCode: Int, errorDesc: String?) {
                    Toast.makeText(LocalInfo.getInstance().context, "验证短信验证码失败", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun userLogin(){
        BaseResDataManager.ezIotLoginParam?.bizType = bizType
        EZIotUserManager.login(BaseResDataManager.ezIotLoginParam!!, object :
            IEzIoTLoginResultCallback<EZIotLoginResp> {
            override fun onSuccess(t: EZIotLoginResp) {
                dismissWaitDialog()
                CommonUtils.showToast(this@EZIoTInputVerifyCodeActivity, resources.getString(R.string.loginSuccess))
                val intent = Intent(this@EZIoTInputVerifyCodeActivity, EZIoTMainTabActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onError(errorCode: Int, errorDesc: String?, loginResp: LoginResp?) {
                dismissWaitDialog()
                CommonUtils.showToast(this@EZIoTInputVerifyCodeActivity, errorDesc)
            }
        })
    }

    private var inputText: EditText? = null
    private var inputButton: Button? = null
    //0表示登录时终端验证，1.表示登录时终端删除，2.表示开启终端绑定，3.终端删除
    private var operationType: Int? = null
    private var goBack: ImageView? = null
    private var bizType: EZIoTUserBizType? = null
}
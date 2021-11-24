package com.eziot.demo.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.eziot.common.http.callback.IResultCallback
import com.eziot.common.utils.LocalInfo
import com.eziot.demo.base.BaseResDataManager
import com.eziot.iotsdkdemo.R
import com.eziot.user.EZIotUserManager

class EZIoTVerifyNewAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eziot_verify_new_account)

        initView()
        initData()
    }

    private fun initView(){
        verifyEdit = findViewById(R.id.newAccountVerifyText)
        verifyButton = findViewById(R.id.newAccountVerifyButton)
        verifyButton?.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                if(verifyEdit?.text.toString().isNullOrEmpty()){
                    Toast.makeText(LocalInfo.getInstance().context, "请输入验证码", Toast.LENGTH_SHORT).show()
                }else{
                    if(operationType == 3){
                        BaseResDataManager.mEZIoTUserContactModifyParam?.theNewEmailSMSkcode = verifyEdit?.text.toString()
                        EZIotUserManager.modifyUserEmailAddress(BaseResDataManager.mEZIoTUserContactModifyParam!!, object:
                            IResultCallback {
                            override fun onSuccess() {
                                var intent = Intent(this@EZIoTVerifyNewAccountActivity, EZIoTUserInfoActivity::class.java)
                                startActivity(intent)
                            }
                            override fun onError(errorCode: Int, errorDesc: String?) {
                                Toast.makeText(LocalInfo.getInstance().context, "$errorDesc", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }else if(operationType == 1){
                        BaseResDataManager.mEZIoTUserContactModifyParam?.theNewPhoneSMScode = verifyEdit?.text.toString()
                        EZIotUserManager.modifyUserPhoneNumber(BaseResDataManager.mEZIoTUserContactModifyParam!!, object:
                            IResultCallback {
                            override fun onSuccess() {
                                var intent = Intent(this@EZIoTVerifyNewAccountActivity, EZIoTUserInfoActivity::class.java)
                                startActivity(intent)
                            }

                            override fun onError(errorCode: Int, errorDesc: String?) {
                                Toast.makeText(LocalInfo.getInstance().context, "$errorDesc", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }

        })
    }

    private fun initData(){
        operationType = intent.getIntExtra("operationType", 1)
    }

    private var operationType: Int? = null
    private var verifyEdit: EditText? = null
    private var verifyButton: Button? = null
}
package com.eziot.demo.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.TestLooperManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.eziot.common.http.callback.IResultCallback
import com.eziot.common.utils.LocalInfo
import com.eziot.demo.base.BaseResDataManager
import com.eziot.iotsdkdemo.R
import com.eziot.user.EZIotUserManager

class EZIoTInputNewAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eziot_input_new_account)

        initView()
        initData()
    }

    private fun initView(){
        countryCodeText = findViewById(R.id.newCountryCodeText)
        newAccountButton = findViewById(R.id.newAccountButton)
        newAccountText = findViewById(R.id.newAccountText)
        newAccountButton?.setOnClickListener(object: View.OnClickListener{

            override fun onClick(p0: View?) {
                accountName = newAccountText?.text.toString()

                if(operationType == 1){
                    countryCode = countryCodeText?.text.toString()

                    if(newAccountText?.text.toString().isEmpty() || countryCodeText?.text.toString().isNullOrEmpty()){
                        Toast.makeText(LocalInfo.getInstance().context, "账号名或国家码不能为空", Toast.LENGTH_SHORT).show()
                    }else{
                        sendVerifyCodeWithNewAccount()
                    }
                }else if(operationType == 3){
                    if(newAccountText?.text.toString().isNullOrEmpty()){
                        Toast.makeText(LocalInfo.getInstance().context, "账号名不能为空", Toast.LENGTH_SHORT).show()
                    }else{
                        sendVerifyCodeWithNewAccount()
                    }
                }
            }

        })
    }

    private fun initData(){
        operationType = intent.getIntExtra("operationType", 1)
        if(operationType==3){
            countryCodeText?.visibility = View.GONE
        }
    }

    private fun sendVerifyCodeWithNewAccount(){
        EZIotUserManager.getSMSCode(accountName!!, countryCode.toInt(),
            BaseResDataManager.newBizType!!, null, null, object: IResultCallback {
                override fun onSuccess() {
                    if(operationType == 1){
                        BaseResDataManager.mEZIoTUserContactModifyParam?.theNewPhone = accountName
                    }else if(operationType == 3){
                        BaseResDataManager.mEZIoTUserContactModifyParam?.theNewEmail = accountName
                    }
                    var intent = Intent(this@EZIoTInputNewAccountActivity, EZIoTVerifyNewAccountActivity::class.java)
                    intent.putExtra("operationType", operationType)
                    startActivity(intent)
                }

                override fun onError(errorCode: Int, errorDesc: String?) {
                    Toast.makeText(LocalInfo.getInstance().context, "$errorDesc", Toast.LENGTH_SHORT).show()
                }

            })
    }


    private var operationType: Int? = null
    private var countryCodeText: EditText? = null
    private var newAccountText: EditText? = null
    private var newAccountButton: Button? = null
    private var accountName : String? = null
    private var countryCode : String = "86"
}
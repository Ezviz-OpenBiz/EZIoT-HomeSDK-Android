package com.eziot.demo.generalSetting

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.common.http.callback.IResultCallback
import com.eziot.common.utils.LocalInfo
import com.eziot.demo.base.BaseActivity
import com.eziot.iotsdkdemo.R
import com.eziot.user.EZIotUserManager
import com.eziot.user.model.account.EZIoTUserBizType
import com.eziot.user.model.terminal.EZIoTUserTerminalStatus
import java.util.regex.Pattern

class EZIoTAccountSecurityActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eziot_account_security)
        initView()
        iniData()
    }

    private fun initView(){
        terminalButton = findViewById(R.id.terminalBindButton)
        terminalButton?.setOnClickListener(this)

        terminalManage = findViewById(R.id.terminalManageLayout)
        terminalManage?.setOnClickListener(this)

        goBack = findViewById(R.id.account_security_go_back)
        goBack?.setOnClickListener(this)
    }

    private fun iniData(){
        showWaitDialog()
        EZIotUserManager.getTerminalEnableStatus(object :
            IEZIoTResultCallback<EZIoTUserTerminalStatus>{
            override fun onSuccess(t: EZIoTUserTerminalStatus) {
                dismissWaitDialog()
                mEZIoTUserTerminalStatus = t
                terminalStatus = t.terminalOpened.toInt() != 0
                Handler(Looper.getMainLooper()).post { Runnable {
                    if(terminalStatus as Boolean){
                        terminalButton?.text = getString(R.string.terminalStartStatus)
                    }else{
                        terminalButton?.text = getString(R.string.terminalCloseStatus)
                    }
                } }
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                terminalStatus = true
            }
        })
    }

    private var terminalStatus: Boolean? = null
    private var terminalButton : Button? = null
    private var mEZIoTUserTerminalStatus: EZIoTUserTerminalStatus? = null
    private var terminalManage: ViewGroup? = null
    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.terminalBindButton -> {
                terminalStatus = !terminalStatus!!

                if(mEZIoTUserTerminalStatus?.terminalBinded?.equals("0")!!){
                    sendSmsVerifyDialog()
                }else{
                    switchTerminalManage("")
                }
            }
            R.id.terminalManageLayout -> {
                var intent = Intent(this, EZIoTTerminalManageActivity::class.java)
                intent.putExtra("operationType", 3)
                startActivity(intent)
            }
            R.id.account_security_go_back -> {
                finish()
            }
        }
    }

    private fun switchTerminalManage(smsCode: String){

        var smsType = 1
        if(isEmailAddress(LocalInfo.getInstance().account)){
            smsType = 3
        }

        mEZIoTUserTerminalStatus?.let {
            EZIotUserManager.setTerminalStatus(terminalStatus!!, it, smsCode, 1, object :
                    IResultCallback{
                override fun onSuccess() {

                    Handler(Looper.getMainLooper()).post(Runnable {
                        if(terminalStatus as Boolean){
                            terminalButton?.text = getString(R.string.terminalStartStatus)
                        }else{
                            terminalButton?.text = getString(R.string.terminalCloseStatus)
                        }
                    })
                }

                override fun onError(errorCode: Int, errorDesc: String?) {
                    Log.d("aaaa", "dddddd")
                }

            })
        }
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
        EZIotUserManager.getSMSCode(LocalInfo.getInstance().account, LocalInfo.getInstance().countryCode.toInt(),
                EZIoTUserBizType.TERMINAL_BIND, null, null, object: IResultCallback{
            override fun onSuccess() {
                verifySmsDialog()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Toast.makeText(LocalInfo.getInstance().context, "获取短信验证码失败", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun verifySmsDialog(){

        val layoutView: View = LayoutInflater.from(this).inflate(R.layout.layout_input_dialog_edittext, null)
        val inputSecurityAreaName =
                layoutView.findViewById<View>(R.id.dialog_edit_text) as EditText
        inputSecurityAreaName.hint  = "短信验证码"

        var builder: AlertDialog.Builder = AlertDialog.Builder(this).setTitle("输入短信验证码").setView(layoutView).
                setNegativeButton("取消", null).
                setPositiveButton("确认", object: DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        verifySms(inputSecurityAreaName.text.toString())

                    }
                })

        builder.create().show()
    }

    private fun verifySms(smsCode: String){
        EZIotUserManager.verifySmsCode(LocalInfo.getInstance().account, LocalInfo.getInstance().countryCode.toInt(), smsCode,
                EZIoTUserBizType.TERMINAL_BIND, object: IResultCallback{
            override fun onSuccess() {
                switchTerminalManage(smsCode)
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Toast.makeText(LocalInfo.getInstance().context, "验证短信验证码失败", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isEmailAddress(text: String): Boolean{
        val regexStr = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*\$"
        return Pattern.matches(regexStr, text)
    }

    private var goBack: ImageView? = null
}
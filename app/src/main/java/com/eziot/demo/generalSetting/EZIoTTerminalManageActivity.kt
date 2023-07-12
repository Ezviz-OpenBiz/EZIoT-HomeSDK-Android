package com.eziot.demo.generalSetting

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.common.http.callback.IResultCallback
import com.eziot.common.utils.LocalInfo
import com.eziot.demo.EZIoTMainTabActivity
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.generalSetting.adapter.TerminalManageAdapter
import com.eziot.demo.utils.CommonUtils
import com.eziot.iotsdkdemo.R
import com.eziot.user.EZIotUserManager
import com.eziot.user.http.bean.LoginResp
import com.eziot.user.http.callback.IEzIoTLoginResultCallback
import com.eziot.user.model.account.EZIoTUserBizType
import com.eziot.user.model.account.EZIotLoginResp
import com.eziot.user.model.terminal.EZIoTDelTerminalParams
import com.eziot.user.model.terminal.EZIoTUserTerminal
import com.eziot.user.model.terminal.EZIoTUserTerminalStatus
import java.util.regex.Pattern

class EZIoTTerminalManageActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eziot_terminal_manage)
        initView()
        initData()
    }

    private fun initView(){
        mRecyclerView = findViewById(R.id.terminalManageRecycler)
        mCurrentTerminalNum = findViewById(R.id.currentTerminalNum)
        goBack = findViewById(R.id.terminal_manage_list_go_back)
        goBack?.setOnClickListener(View.OnClickListener {
            if(operationType == 1){
                //1表示登录时终端删除
                userLogin()
            }else if(operationType == 3){
                finish()
            }
        })
    }

    private fun initData(){
        operationType = intent.getIntExtra("operationType", 3)
        EZIotUserManager.getTerminalsList(10, 1, object: IEZIoTResultCallback<List<EZIoTUserTerminal>>{
            override fun onSuccess(t: List<EZIoTUserTerminal>) {
                mEZIoTUserTerminals = t as ArrayList<EZIoTUserTerminal>

                getTerminalBindState()

            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Toast.makeText(LocalInfo.getInstance().context, "获取终端列表失败", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getTerminalBindState(){
        EZIotUserManager.getTerminalEnableStatus(object :
                IEZIoTResultCallback<EZIoTUserTerminalStatus>{
            override fun onSuccess(t: EZIoTUserTerminalStatus) {
                mEZIoTUserTerminalStatus = t
                showTerminalManageList()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Toast.makeText(LocalInfo.getInstance().context, "获取终端绑定状态失败", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showTerminalManageList(){
        var layoutManager = LinearLayoutManager(this@EZIoTTerminalManageActivity)
        mRecyclerView?.layoutManager = layoutManager
        adapter = TerminalManageAdapter(this@EZIoTTerminalManageActivity, mEZIoTUserTerminals!!, object: TerminalManageAdapter.TerminalManageInterface{
            override fun terminalDelete(terminalInfo: EZIoTUserTerminal) {
                terminalObj = terminalInfo
                if(mEZIoTUserTerminalStatus?.terminalBinded.equals("0")){
                    sendSmsVerifyDialog()
                }else{
                    deleteTerminalList("")
                }
            }
        })
        mRecyclerView?.adapter = adapter
        mCurrentTerminalNum?.text = "当前终端数为${mEZIoTUserTerminals?.size}个"
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
            EZIoTUserBizType.TERMINAL_DELETE, null,null,  object: IResultCallback {
            override fun onSuccess() {
//                verifySmsDialog()
                var intent = Intent(this@EZIoTTerminalManageActivity, EZIoTInputVerifyCodeActivity::class.java)
                intent.putExtra("operationType", operationType)
                startActivityForResult(intent, operationType)
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Toast.makeText(LocalInfo.getInstance().context, "$errorDesc", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        deleteTerminalList(data?.getStringExtra("smsCode")!!)
    }

    private fun deleteTerminalList(smsCode: String){
        var terminalInfos = ArrayList<EZIoTUserTerminal>()
        terminalInfos.add(terminalObj!!)
        var params = EZIoTDelTerminalParams()
        params.terminals = terminalInfos
        params.terminalStatus = mEZIoTUserTerminalStatus
        params.smsCode = smsCode
        params.smsType = if(isEmailAddress(LocalInfo.getInstance().account)) 3 else 1
        params.tempId = ""
        EZIotUserManager.delTerminalsList(params, object: IResultCallback{
            override fun onSuccess() {
                mEZIoTUserTerminals?.remove(terminalObj!!)
                adapter?.notifyDataSetChanged()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Toast.makeText(LocalInfo.getInstance().context, "$errorDesc", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun userLogin(){
        BaseResDataManager.ezIotLoginParam?.bizType = EZIoTUserBizType.TERMINAL_DELETE
        EZIotUserManager.login(BaseResDataManager.ezIotLoginParam!!, object :
            IEzIoTLoginResultCallback<EZIotLoginResp> {
            override fun onSuccess(t: EZIotLoginResp) {
                dismissWaitDialog()
                CommonUtils.showToast(this@EZIoTTerminalManageActivity, resources.getString(R.string.loginSuccess))
                val intent = Intent(this@EZIoTTerminalManageActivity, EZIoTMainTabActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onError(errorCode: Int, errorDesc: String?, loginResp: LoginResp?) {
                dismissWaitDialog()
                CommonUtils.showToast(this@EZIoTTerminalManageActivity, errorDesc)
            }
        })
    }

    fun isEmailAddress(text: String): Boolean{
        val regexStr = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*\$"
        return Pattern.matches(regexStr, text)
    }
    private var mRecyclerView: RecyclerView? = null
    private var mCurrentTerminalNum: TextView? = null
    private var mEZIoTUserTerminalStatus: EZIoTUserTerminalStatus? = null
    private var mEZIoTUserTerminals: ArrayList<EZIoTUserTerminal>? = null
    private var terminalObj: EZIoTUserTerminal? = null
    private var adapter: TerminalManageAdapter? = null
    private var goBack: ImageView? = null
    private var operationType: Int = 1
}
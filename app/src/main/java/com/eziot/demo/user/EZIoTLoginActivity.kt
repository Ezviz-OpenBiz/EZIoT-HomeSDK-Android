package com.eziot.demo.user

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.eziot.common.data.SystemConfigRepository
import com.eziot.common.exception.EZIoTException
import com.eziot.common.http.callback.IResultCallback
import com.eziot.common.model.configuration.SystemConfigInfo
import com.eziot.common.utils.GlobalVariable
import com.eziot.common.utils.LocalInfo
import com.eziot.common.utils.MD5Util
import com.eziot.demo.EZIoTMainTabActivity
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.generalSetting.EZIoTInputVerifyCodeActivity
import com.eziot.demo.generalSetting.EZIoTTerminalManageActivity
import com.eziot.demo.utils.CommonUtils
import com.eziot.iotsdkdemo.R
import com.eziot.push.EzIoTPush
import com.eziot.push.core.IPushOptions
import com.eziot.user.EZIotUserManager
import com.eziot.user.http.bean.LoginResp
import com.eziot.user.http.callback.IEzIoTLoginResultCallback
import com.eziot.user.model.account.EZIoTUserBizType
import com.eziot.user.model.account.EZIoTUserLoginParam
import com.eziot.user.model.account.EZIotLoginResp
import com.eziot.wificonfig.EZIoTNetConfigurator
import com.ezdatasource.simple.AsyncListener
import com.ezdatasource.simple.From
import kotlinx.android.synthetic.main.eziot_user_login_activity.*

class EZIoTLoginActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_user_login_activity)
        addBackBtn(toolbar)
        EzIoTPush.init(this, object : IPushOptions {

            override fun getAppType(): String? {
                return LocalInfo.getInstance().appId
            }

            override fun getServerAddress(): String? {
                val systemConfigInfo: SystemConfigInfo? =
                    SystemConfigRepository.getSystemConfig().local()
                return systemConfigInfo?.pushDasDomain
            }

            override fun getServerPort(): Int {
                val systemConfigInfo = SystemConfigRepository.getSystemConfig().local()
                return systemConfigInfo?.pushDasPort ?: 8666
            }

            override fun getSession(): String? {
                return MD5Util.getMD5String(LocalInfo.getInstance().session)
            }

            override fun getUserId(): String? {
                return GlobalVariable.USER_ID.get()
            }

            override fun getPhoneId(): String? {
                return LocalInfo.getInstance().hardwareCode;
            }

            override fun getConfigPath(): String? {
                return getExternalFilesDir(null).toString() + "/"
            }

            override fun isEnableLog(): Boolean {
                return true
            }

        })
    }

    fun onClickLogin(view : View){
        val countryCode = phoneCodeEt.text.toString()
        val userName = userNameEt.text.toString()
        val pwd = passwordEt.text.toString()

        if(userName.isEmpty() || countryCode.isEmpty() || pwd.isEmpty()){
            CommonUtils.showToast(this,resources.getString(R.string.inputErrorHint))
            return
        }

        BaseResDataManager.ezIotLoginParam = EZIoTUserLoginParam()
        BaseResDataManager.ezIotLoginParam?.account = userName
        BaseResDataManager.ezIotLoginParam?.phoneCode = countryCode
        BaseResDataManager.ezIotLoginParam?.password = pwd
        BaseResDataManager.ezIotLoginParam?.pushRegisterJson = "[{channel:99}]"

        LocalInfo.getInstance().countryCode = countryCode
        LocalInfo.getInstance().account = userName
        showWaitDialog()
        EZIotUserManager.login(BaseResDataManager.ezIotLoginParam!!,object : IEzIoTLoginResultCallback<EZIotLoginResp>{
            override fun onSuccess(t: EZIotLoginResp) {
                dismissWaitDialog()
                getSystemConfig()
                CommonUtils.showToast(this@EZIoTLoginActivity,resources.getString(R.string.loginSuccess))
                val intent = Intent(this@EZIoTLoginActivity,EZIoTMainTabActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onError(errorCode: Int, errorDesc: String?, loginResp: LoginResp?) {
                dismissWaitDialog()
                CommonUtils.showToast(this@EZIoTLoginActivity,errorDesc)
            }

        })
    }

    private fun sendSmsVerifyDialog(title: String, code: Int){
        var build: AlertDialog.Builder = AlertDialog.Builder(this).
        setTitle(title).
        setNegativeButton("取消", null).
        setPositiveButton("确认", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                if(code == 106002){
                    sendSmsVerify()
                }else if(code == 101069){
                    var intent = Intent(this@EZIoTLoginActivity, EZIoTTerminalManageActivity::class.java)
                    intent.putExtra("operationType", 1)
                    startActivity(intent)
                }

            }
        })

        build.create().show()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == 101069){
//            userLogin()
//        }
//    }

    private fun sendSmsVerify(){
        BaseResDataManager.ezIotLoginParam?.bizType = EZIoTUserBizType.TERMINAL_BIND
        EZIotUserManager.getSMSCode(LocalInfo.getInstance().account, LocalInfo.getInstance().countryCode.toInt(),
                EZIoTUserBizType.TERMINAL_BIND, null, null, object: IResultCallback {
            override fun onSuccess() {
                var intent = Intent(this@EZIoTLoginActivity, EZIoTInputVerifyCodeActivity::class.java)
                intent.putExtra("operationType", 0)
                startActivity(intent)
            }
            override fun onError(errorCode: Int, errorDesc: String?) {
                Toast.makeText(LocalInfo.getInstance().context, "$errorDesc", Toast.LENGTH_SHORT).show()
            }
        })
    }




    fun onClickForgetPwd(view : View){
        val intent = Intent(this,EZIoTResetPwdActivity::class.java)
        startActivity(intent)
    }

    private fun getSystemConfig() {
        SystemConfigRepository.getSystemConfig().asyncRemote(object :
            AsyncListener<SystemConfigInfo, EZIoTException>() {

            override fun onResult(p0: SystemConfigInfo?, p1: From) {
                EzIoTPush.turnOnPush()
            }

            override fun onError(error: EZIoTException) {

            }

        })

    }

}
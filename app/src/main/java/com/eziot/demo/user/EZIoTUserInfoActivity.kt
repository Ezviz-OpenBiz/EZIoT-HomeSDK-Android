package com.eziot.demo.user

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.common.http.callback.IResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import com.eziot.user.EZIotUserManager
import com.eziot.user.http.bean.GetUserInfoResp
import com.eziot.user.model.user.EZIotBaseUserInfo
import kotlinx.android.synthetic.main.eziot_user_info_activity.*
import kotlinx.android.synthetic.main.eziot_user_info_activity.toolbar
import kotlinx.android.synthetic.main.eziot_user_login_activity.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EZIoTUserInfoActivity : BaseActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_user_info_activity)
        addBackBtn(toolbar)
        initData()
    }

    override fun addBackBtn(toolbar: Toolbar){

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener {
            intent.putExtra("homeTitle", userNameValueTv.text.toString())
            setResult(0, intent)
            finish()
        }
    }

    private fun initData(){
        showWaitDialog()
        EZIotUserManager.getUserProfile(object : IEZIoTResultCallback<GetUserInfoResp>{
            override fun onSuccess(t: GetUserInfoResp) {
                dismissWaitDialog()
                initView(t.userInfo)
                userInfo = t.userInfo
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTUserInfoActivity,"获取失败")
            }
        })
    }

    private fun initView(userInfo : EZIotBaseUserInfo){
        userNameValueTv.text = userInfo.homeTitle
        genderValueTv.text =  if(userInfo.gender == 1) "男" else "女"
        emailValueTv.text = userInfo.email
        phoneValueTv.text = userInfo.phone
        registerDateValueTv.text = formatTime(userInfo.regDate)
        lastLoginDeviceValueTv.text = userInfo.lastLoginDevice
        lastLoginTimeValueTv.text = userInfo.lastLoginTime
        locationValueTv.text = userInfo.location
        birthValueTv.text = userInfo.birth


        findViewById<ViewGroup>(R.id.userNameLayout).setOnClickListener(this)
        findViewById<ViewGroup>(R.id.emailLayout).setOnClickListener(this)
        findViewById<ViewGroup>(R.id.phoneLayout).setOnClickListener(this)
        findViewById<ViewGroup>(R.id.genderLayout).setOnClickListener(this)
        findViewById<ViewGroup>(R.id.birthLayout).setOnClickListener(this)
        findViewById<ViewGroup>(R.id.passwordLayout).setOnClickListener(this)
    }

    private fun verifyUserInfoWithDialog(category: String, hint: String){
        showDialogForInputWifiPassword(category, hint)
    }

    private fun showDialogForInputWifiPassword(category: String, hint: String) {
        val layoutView: View = LayoutInflater.from(this).inflate(R.layout.layout_input_dialog_edittext, null)
        val inputSecurityAreaName =
            layoutView.findViewById<View>(R.id.dialog_edit_text) as EditText
        inputSecurityAreaName.hint  = hint
        var build: AlertDialog.Builder = AlertDialog.Builder(this).setView(layoutView)
            .setTitle(category).setNegativeButton(R.string.cancel, null)

        build.setPositiveButton(R.string.sure, object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                Handler(Looper.getMainLooper()).post(Runnable {
                    when(category){
                        getString(R.string.inputUserName) -> {
                            updateNickName(inputSecurityAreaName.text.toString())
                        }
                        getString(R.string.inputGender) -> {
                            updateGender(inputSecurityAreaName.text.toString())
                        }
                        getString(R.string.birthFormat) -> {
                            updateBirth(inputSecurityAreaName.text.toString())
                        }
                    }
                })

            }
        }).create().show()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.userNameLayout -> {
                verifyUserInfoWithDialog(getString(R.string.inputUserName), getString(R.string.userNameFormat))
            }

            R.id.emailLayout -> {
                var intent : Intent? = Intent(this, EZIoTVerifyPhoneOrEmailActivity::class.java)
                intent?.putExtra("operationType", 3)
                intent?.putExtra("email", userInfo?.email)
                intent?.putExtra("phone", userInfo?.phone)
                startActivity(intent)
            }

            R.id.phoneLayout -> {
                var intent : Intent? = Intent(this, EZIoTVerifyPhoneOrEmailActivity::class.java)
                intent?.putExtra("operationType", 1)
                intent?.putExtra("email", userInfo?.email)
                intent?.putExtra("phone", userInfo?.phone)
                startActivity(intent)
            }

            R.id.genderLayout -> {
                verifyUserInfoWithDialog(getString(R.string.inputGender), getString(R.string.genderFormat))
            }

            R.id.birthLayout -> {
                verifyUserInfoWithDialog(getString(R.string.birthFormat), getString(R.string.birthFormat))
            }

            R.id.passwordLayout -> {
                var intent: Intent = Intent(this, EZIoTModifyPwdActivity::class.java)
                startActivityForResult(intent, 4)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 1){
            phoneValueTv.text = data?.getStringExtra("newPhone")
        }else if(resultCode == 3){
            emailValueTv.text = data?.getStringExtra("newEmail")
        }else{

        }
    }

    private fun updateNickName(userName: String){
        if(userName.isEmpty() || userName.length <= 1){
            Toast.makeText(this, "请输入正确的用户名", Toast.LENGTH_SHORT).show()
            return
        }
        EZIotUserManager.updateNickName(userName, object: IResultCallback{
            override fun onSuccess() {
                Handler(Looper.getMainLooper()).post(Runnable {
                    userNameValueTv.text = userName
                })
                Toast.makeText(this@EZIoTUserInfoActivity, "修改用户名称成功", Toast.LENGTH_SHORT).show()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Toast.makeText(this@EZIoTUserInfoActivity, "修改用户名称失败", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun updateGender(gender: String){
        if(gender != "男" && gender != "女"){
            Toast.makeText(this, "请输入正确的用户性别", Toast.LENGTH_SHORT).show()
            return
        }
        var genderInt = if(gender == "男") 1 else 2
        EZIotUserManager.updateGender(genderInt, object : IResultCallback{
            override fun onSuccess() {
                Handler(Looper.getMainLooper()).post(Runnable {
                    genderValueTv.text = gender
                })
                Toast.makeText(this@EZIoTUserInfoActivity, "修改用户性别成功", Toast.LENGTH_SHORT).show()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Toast.makeText(this@EZIoTUserInfoActivity, "修改用户性别失败", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun updateBirth(birth : String){

        if(!isBirthDate(birth)){
            Toast.makeText(this, "请输入正确的日期格式", Toast.LENGTH_SHORT).show()
            return
        }

        EZIotUserManager.updateBirth(birth, object: IResultCallback{
            override fun onSuccess() {
                Handler(Looper.getMainLooper()).post(Runnable {
                    birthValueTv.text = birth
                })
                Toast.makeText(this@EZIoTUserInfoActivity, "修改用户生日信息成功", Toast.LENGTH_SHORT).show()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Toast.makeText(this@EZIoTUserInfoActivity, "修改用户生日信失败", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isLetterDigit(str: String): Boolean{
        var regex = Regex("^[\u4e00-\u9fa5_a-z0-9A-Z]+$")
        return str.matches(regex)
    }

    private fun isBirthDate(str: String): Boolean{
        var convertSuccess = true
        var format = SimpleDateFormat("yyyy-MM-dd")
        try{
            format.isLenient = false
            format.parse(str)
        }catch(e: ParseException){
            convertSuccess = false
        }
        return convertSuccess
    }

    private fun formatTime(time : Long) : String{
        val date = Date(time)
        val format = SimpleDateFormat("yyyy-MM-dd HH-mm-ss")
        return format.format(date)
    }

    private var userInfo: EZIotBaseUserInfo? = null

}
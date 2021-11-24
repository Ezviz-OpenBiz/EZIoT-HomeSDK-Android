package com.eziot.demo.user

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eziot.common.http.callback.IResultCallback
import com.eziot.common.utils.LocalInfo
import com.eziot.iotsdkdemo.R
import com.eziot.user.EZIotUserManager
import java.util.regex.Pattern

class EZIoTModifyPwdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eziot_modify_pwd)

        initView()
        initData()
    }

    private fun initView(){
        oldInput = findViewById(R.id.inputOldPwdEditText)
        newInput = findViewById(R.id.inputNewPwdEditText)
        ensureButton = findViewById(R.id.ensureButton)
        ensureButton?.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                var oldPwd = oldInput?.text.toString()
                var newPwd = newInput?.text.toString()

                val isStrong = getPwdRank(newPwd, null) >= 3
                val isValidLen = newPwd.length in 8..16
                if(oldInput?.text.toString().isNullOrEmpty() || newInput?.text.toString().isNullOrEmpty()){
                    Toast.makeText(LocalInfo.getInstance().context, "密码不能为空", Toast.LENGTH_SHORT).show()
                }
                else if(!isStrong || !isValidLen){
                    Toast.makeText(LocalInfo.getInstance().context, "密码必须包含字符，数字和特殊字符", Toast.LENGTH_SHORT).show()
                }
                else{
                    var oldPwd = oldInput?.text.toString()
                    var newPwd = newInput?.text.toString()
                    EZIotUserManager.modifyUserAccountPassword(oldPwd, newPwd, object:
                        IResultCallback{
                        override fun onSuccess() {
                            Toast.makeText(LocalInfo.getInstance().context, "修改密码成功", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        override fun onError(errorCode: Int, errorDesc: String?) {
                            Toast.makeText(LocalInfo.getInstance().context, "$errorDesc", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

        })
    }

    private fun initData(){

    }

    fun getPwdRank(szPwd: String, szUser: String?): Int {
        var iRank = 0
        if (hasLowerLetter(szPwd)) {
            iRank++
        }
        if (hasCapitalLetter(szPwd)) {
            iRank++
        }
        if (hasDigit(szPwd)) {
            iRank++
        }
        if (hasSpecialChar(szPwd)) {
            iRank++
        }
        iRank = if (iRank > 3) 3 else iRank
        if (szPwd.length < 8 || isAlike(szPwd, szUser)) {
            iRank = 0
        }

        return iRank
    }

    private fun isAlike(psw: String?, userName: String?): Boolean {
        var userNameReversal: String? = null
        if (!TextUtils.isEmpty(userName)) {
            userNameReversal = StringBuffer(userName).reverse().toString()
        }
        return TextUtils.equals(psw, userName) || TextUtils.equals(psw, userNameReversal)
    }

    private fun hasSpecialChar(content: String?): Boolean {
        var flag = false
        val p = Pattern.compile(".*[^a-zA-Z0-9].*")
        val m = p.matcher(content)
        if (m.matches()) {
            flag = true
        }
        return flag
    }

    private fun hasDigit(content: String?): Boolean {
        var flag = false
        val p = Pattern.compile(".*\\d+.*")
        val m = p.matcher(content)
        if (m.matches()) {
            flag = true
        }
        return flag
    }

    private fun hasLowerLetter(content: String?): Boolean {
        var flag = false
        val p = Pattern.compile(".*[a-z].*")
        val m = p.matcher(content)
        if (m.matches()) {
            flag = true
        }
        return flag
    }

    private fun hasCapitalLetter(content: String?): Boolean {
        var flag = false
        val p = Pattern.compile(".*[A-Z].*")
        val m = p.matcher(content)
        if (m.matches()) flag = true
        return flag
    }

    private var oldInput: EditText? = null
    private var newInput: EditText? = null
    private var ensureButton: Button? = null
}
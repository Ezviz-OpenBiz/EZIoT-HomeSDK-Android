package com.eziot.demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.user.EZIoTLoginActivity
import com.eziot.demo.user.EZIoTRegisterActivity
import com.eziot.iotsdkdemo.R
import com.eziot.user.EZIotUserManager
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException

class EZIoTGuideActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_guide_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        showWaitDialog()
        Thread {
            try {
                val autoLogin = EZIotUserManager.autoLogin()
                val intent = Intent(this@EZIoTGuideActivity, EZIoTMainTabActivity::class.java)
                startActivity(intent)
                finish()
                dismissWaitDialog()
            } catch (e: Exception) {
                dismissWaitDialog()
            }
        }.start()

    }

    fun onClickLogin(view : View){
        val intent = Intent(this, EZIoTLoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onClickRegister(view : View){
        val intent = Intent(this,EZIoTRegisterActivity::class.java)
        startActivity(intent)
        finish()
    }


    fun getUserIv(): ByteArray? {
        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val random = Random()
            val iv = ByteArray(cipher.blockSize)
            random.nextBytes(iv)
            return iv
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        return ByteArray(16)
    }


}
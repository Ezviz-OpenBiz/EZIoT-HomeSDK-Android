package com.eziot.demo.generalSetting

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.common.http.callback.IResultCallback
import com.eziot.common.utils.LocalInfo
import com.eziot.demo.generalSetting.adapter.TerminalManageAdapter
import com.eziot.iotsdkdemo.R
import com.eziot.user.EZIotUserManager
import com.eziot.user.model.account.EZIoTUserBizType
import com.eziot.user.model.terminal.EZIoTDelTerminalParams
import com.eziot.user.model.terminal.EZIoTUserTerminal
import kotlinx.android.synthetic.main.activity_eziot_terminal_delete.*
import java.util.regex.Pattern

class EZIoTTerminalDeleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eziot_terminal_delete)

        initView()
        initData()
    }

    private fun initView(){
        mTerminalDeleteRecycler = findViewById(R.id.terminalManageRecycler)
    }

    private fun initData(){
        EZIotUserManager.getTerminalsList(10, 1,
                object: IEZIoTResultCallback<List<EZIoTUserTerminal>>{
                    override fun onSuccess(t: List<EZIoTUserTerminal>) {
                        var manager = LinearLayoutManager(this@EZIoTTerminalDeleteActivity)
                        var adapter = TerminalManageAdapter(this@EZIoTTerminalDeleteActivity, t,
                                object: TerminalManageAdapter.TerminalManageInterface{
                                    override fun terminalDelete(terminalInfo: EZIoTUserTerminal) {

                                    }
                                })
                        terminalDeleteRecycler.layoutManager = manager
                        terminalDeleteRecycler.adapter = adapter
                    }

                    override fun onError(errorCode: Int, errorDesc: String?) {
                        Toast.makeText(this@EZIoTTerminalDeleteActivity, "获取终端列表失败", Toast.LENGTH_SHORT).show()
                    }
                })
    }


    fun isEmailAddress(text: String): Boolean{
        val regexStr = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*\$"
        return Pattern.matches(regexStr, text)
    }

    private var mTerminalDeleteRecycler: RecyclerView? = null
}
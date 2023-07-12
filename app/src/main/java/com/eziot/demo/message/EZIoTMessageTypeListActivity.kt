package com.eziot.demo.message

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.message.adapter.EZIoTMessageTypeListAdapter
import com.eziot.iotsdkdemo.R
import com.eziot.message.EZIoTMessageManager
import com.eziot.message.model.EZIoTGetMsgCategoriesResp
import com.eziot.message.model.EZIoTMsgCategoryInfo
import com.eziot.message.model.EZIoTMsgListResp
import kotlinx.android.synthetic.main.activity_eziot_general_setting.*

class EZIoTMessageTypeListActivity : BaseActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eziot_message_type_list)
        addBackBtn(toolbar)
        initView()
        initData()
    }

    private fun initView(){
        messageType = intent.getIntExtra("messageType", 0)
        mMessageListPageTitle = findViewById(R.id.toolbar)
        mMessageListNull = findViewById(R.id.message_list_null)
        if(messageType == 1){
            mMessageListPageTitle?.title = getString(R.string.noticeMessage)
            mType = R.string.noticeMessage
        }
        mRecyclerView = findViewById(R.id.message_list_recyclerView)
    }

    private fun initData(){
        val messageCategoriesLocal = EZIoTMessageManager.getMessageCategoriesLocal()
        if(messageCategoriesLocal.isNotEmpty()){
            for (t1 in messageCategoriesLocal){
                if(t1.name.equals(getString(mType))){
                    getMessageList(t1)
                    break
                }
            }
        } else {
            EZIoTMessageManager.getMessageCategories(object: IEZIoTResultCallback<EZIoTGetMsgCategoriesResp>{
                override fun onSuccess(t: EZIoTGetMsgCategoriesResp) {
                    for (t1 in t.card){
                        if(t1.name.equals(getString(mType))){
                            getMessageList(t1)
                            break
                        }
                    }
                }
                override fun onError(errorCode: Int, errorDesc: String?) {
                    Log.d("aa0", "cc")
                }

            })
        }
    }

    fun getMessageList(ezIoTMsgCategoryInfo: EZIoTMsgCategoryInfo){
        val messageListLocal = EZIoTMessageManager.getMessageListLocal(
            ezIoTMsgCategoryInfo,
            System.currentTimeMillis(),
            20
        )

        if(messageListLocal.isNotEmpty()){
            mMessageListNull?.visibility = View.GONE
            mRecyclerView?.visibility = View.VISIBLE
            val layoutManager = LinearLayoutManager(this@EZIoTMessageTypeListActivity)
            mDdapter = EZIoTMessageTypeListAdapter(this@EZIoTMessageTypeListActivity, messageListLocal)
            mRecyclerView?.layoutManager = layoutManager
            mRecyclerView?.adapter = mDdapter
        } else {
            EZIoTMessageManager.getMessageList(ezIoTMsgCategoryInfo, 20, 0, "", "",object:
                IEZIoTResultCallback<EZIoTMsgListResp>{
                override fun onSuccess(t: EZIoTMsgListResp) {
                    if(t.message != null && t.message.size > 0){
                        mMessageListNull?.visibility = View.GONE
                        mRecyclerView?.visibility = View.VISIBLE
                        val layoutManager = LinearLayoutManager(this@EZIoTMessageTypeListActivity)
                        mDdapter = EZIoTMessageTypeListAdapter(this@EZIoTMessageTypeListActivity, t.message)
                        mRecyclerView?.layoutManager = layoutManager
                        mRecyclerView?.adapter = mDdapter
                    }else{
                        mMessageListNull?.visibility = View.VISIBLE
                        mRecyclerView?.visibility = View.GONE
                    }
                }

                override fun onError(errorCode: Int, errorDesc: String?) {

                }
            })
        }



    }


    private var messageType: Int? = null
    private var mDdapter: EZIoTMessageTypeListAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private var mMessageListPageTitle: Toolbar? = null
    private var mMessageListNull: TextView? = null
    private var mType: Int = R.string.alarmMessage

}
package com.eziot.demo.generalSetting

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.common.http.callback.IResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.generalSetting.adapter.EZIoTMsgSwitchAdapter
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import com.eziot.message.EZIoTMessageManager
import com.eziot.message.model.EZIoTGetMsgCategoriesResp
import com.eziot.message.model.EZIoTMsgCategoryInfo
import kotlinx.android.synthetic.main.eziot_msg_switch_activity.*
import kotlinx.android.synthetic.main.eziot_msg_switch_adapter.*

class EZIoTMsgSwitchActivity : BaseActivity() {

    val switchMsgMap = HashMap<Int,Boolean>()

    lateinit var ezIoTMsgCategoryInfos : List<EZIoTMsgCategoryInfo>

    lateinit var adapter : EZIoTMsgSwitchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_msg_switch_activity)
        addBackBtn(toolbar)
        initData()
    }

    private fun initData(){
        showWaitDialog()
        EZIoTMessageManager.getMessageCategories(object : IEZIoTResultCallback<EZIoTGetMsgCategoriesResp>{
            override fun onSuccess(ezIoTGetMsgCategoriesResp : EZIoTGetMsgCategoriesResp) {
                this@EZIoTMsgSwitchActivity.ezIoTMsgCategoryInfos = ezIoTGetMsgCategoriesResp.card
                var calculateTypeColumn = 0;
                ezIoTMsgCategoryInfos.forEach {
                    EZIoTMessageManager.getMessageNodisturbingStatus(it.type,object : IEZIoTResultCallback<Boolean>{
                        override fun onSuccess(t: Boolean) {
                            calculateTypeColumn++
                            switchMsgMap[it.type] = !t
                            if(calculateTypeColumn == ezIoTMsgCategoryInfos.size){
                                initView()
                                dismissWaitDialog()
                            }
                        }

                        override fun onError(errorCode: Int, errorDesc: String?) {
                            dismissWaitDialog()
                            Utils.showToast(this@EZIoTMsgSwitchActivity,R.string.operateFail)
                        }

                    })
                }

            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTMsgSwitchActivity,R.string.operateFail)
            }

        })
    }

    private fun initView(){
        msgTypeSwitchRv.layoutManager = LinearLayoutManager(this)
        msgTypeSwitchRv.adapter = EZIoTMsgSwitchAdapter(this,ezIoTMsgCategoryInfos,switchMsgMap,object : EZIoTMsgSwitchAdapter.EZIoTMsgSwitchListener{
            override fun ezIotMsgSwitchClick(categoryType: Int, isOpen: Boolean) {
                showWaitDialog()
                EZIoTMessageManager.setMessageNodisturbingStatus(categoryType,!isOpen,object : IResultCallback{

                    override fun onSuccess() {
                        dismissWaitDialog()
                        switchMsgMap[categoryType] = isOpen
                        msgTypeSwitchRv.adapter!!.notifyDataSetChanged()
                    }

                    override fun onError(errorCode: Int, errorDesc: String?) {
                        dismissWaitDialog()
                        Utils.showToast(this@EZIoTMsgSwitchActivity,errorDesc)
                    }

                })
            }


        })
    }


}
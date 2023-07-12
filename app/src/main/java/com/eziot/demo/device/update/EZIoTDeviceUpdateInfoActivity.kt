package com.eziot.demo.device.update

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.base.IntentContent
import com.eziot.demo.device.adapter.EZIoTDeviceUpdateInfoAdapter
import com.eziot.device.EZIoTDeviceControl
import com.eziot.device.EZIoTDeviceManager
import com.eziot.device.model.EZIoTDeviceInfo
import com.eziot.device.model.update.EZIoTUpdateInfo
import com.eziot.device.model.update.EZIoTUpdateStateInfo
import com.eziot.device.update.IUpdateProgressCallback
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import kotlinx.android.synthetic.main.eziot_device_update_info_activity.*

class EZIoTDeviceUpdateInfoActivity : BaseActivity() {

    private lateinit var deviceInfo: EZIoTDeviceInfo

    private lateinit var ezIotDeviceControl: EZIoTDeviceControl

    private var ezIotUpdateInfos = ArrayList<EZIoTUpdateInfo>()

    private lateinit var adapter: EZIoTDeviceUpdateInfoAdapter

    private var ezIotUpdateShowInfos = ArrayList<EZIoTUpdateShowInfo>()

    companion object {
        const val UPDATE_INIT = 0;

        const val UPDATE_FAIL = 8;

        const val UPDATE_SUCCESS = 7;

        const val UPDATING = 3;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_device_update_info_activity)
        addBackBtn(toolbar)
        initData()
    }

    private fun initData() {
        val deviceSerial = intent.getStringExtra(IntentContent.DEVICE_ID)
        val familyInfo = BaseResDataManager.familyInfo
        ezIotDeviceControl = EZIoTDeviceManager.getDeviceControl(
            familyInfo!!.id,
            deviceSerial!!
        )
        deviceInfo = ezIotDeviceControl.getLocalDevice()!!
        showWaitDialog()
        ezIotDeviceControl.searchUpdateInfo(object : IEZIoTResultCallback<List<EZIoTUpdateInfo>> {
            override fun onSuccess(t: List<EZIoTUpdateInfo>) {
                if (t.isEmpty()) {
                    finish()
                } else {
                    t.forEach {
                        if (it.packageInfo != null) {
                            val updateShowInfo = EZIoTUpdateShowInfo()
                            updateShowInfo.updateInfo = it
                            ezIotUpdateShowInfos.add(updateShowInfo)
                            ezIotUpdateInfos.add(it)
                        }
                    }
                    initView()
                    var finishSearchIndex = 0
                    val columnSearch = ezIotUpdateShowInfos.size
                    ezIotUpdateShowInfos.forEach {
                        ezIotDeviceControl.searchUpdateState(it.updateInfo,object : IEZIoTResultCallback<EZIoTUpdateStateInfo>{
                            override fun onSuccess(t: EZIoTUpdateStateInfo) {
                                finishSearchIndex++
                                if(t.status == 1 || t.status == 2 || t.status == 3 || t.status == 4 || t.status == 5 || t.status == 6){
                                    it.state = UPDATING
                                } else {
                                    it.state = t.status
                                }
                                adapter.updateStateAndProgress(it.updateInfo.productModel,it.state,t.progress)
                                if(finishSearchIndex == columnSearch){
                                    dismissWaitDialog()
                                    checkStateComplete()
                                }
                            }

                            override fun onError(errorCode: Int, errorDesc: String?) {
                                finishSearchIndex++
                                if(finishSearchIndex == columnSearch){
                                    dismissWaitDialog()
                                    checkStateComplete()
                                }
                            }

                        })
                    }
                }
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Utils.showToast(this@EZIoTDeviceUpdateInfoActivity, errorDesc ?: this@EZIoTDeviceUpdateInfoActivity.getString(R.string.operateFail))
                finish()
            }

        })
    }

    private fun checkStateComplete(){
        val ezIotUpdatingInfos = ArrayList<EZIoTUpdateInfo>()
        ezIotUpdateShowInfos.forEach {
            if(it.state == UPDATING){
                ezIotUpdatingInfos.add(it.updateInfo)
            }
        }
        if(ezIotUpdatingInfos.size > 0){
            startUpdate(ezIotUpdateInfos)
        }
    }

    private fun initView() {
        updateInfoRv.layoutManager = LinearLayoutManager(this)
        adapter = EZIoTDeviceUpdateInfoAdapter(ezIotUpdateShowInfos, this)
        updateInfoRv.adapter = adapter
    }

    fun onClickUpdate(view: View) {
        showWaitDialog()
        startUpdate(ezIotUpdateInfos)
    }

    private fun startUpdate(ezIotUpdateInfos : ArrayList<EZIoTUpdateInfo>){
        updateBtn.visibility = View.GONE
        ezIotDeviceControl.startUpdate(ezIotUpdateInfos, object : IUpdateProgressCallback {
            override fun startOtaUpdate() {
            }

            override fun onUpdateFail(productModel: String?, errorCode: Int) {
                dismissWaitDialog()
                adapter.updateState(productModel, UPDATE_FAIL)

            }

            override fun onProgress(productModel: String, status: Int, progress: Int) {
                dismissWaitDialog()
                adapter.updateProgress(productModel, progress)
            }

            override fun onUpdateSuccess(productModel: String) {
                dismissWaitDialog()
                adapter.updateState(productModel, UPDATE_SUCCESS)
            }

        })
    }



    override fun onDestroy() {
        super.onDestroy()
//        ezIotDeviceControl.stopUpdate()
    }

}
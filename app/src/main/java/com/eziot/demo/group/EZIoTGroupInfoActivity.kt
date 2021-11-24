package com.eziot.demo.group

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.common.http.callback.IResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.base.IntentContent
import com.eziot.demo.group.adapter.EZIoTGroupAddDeviceListDialogAdapter
import com.eziot.demo.group.adapter.EZIoTGroupDeviceListAdapter
import com.eziot.device.EZIoTDeviceManager
import com.eziot.device.model.DeviceInfo
import com.eziot.family.EZIoTGroupManager
import com.eziot.family.model.group.EZIoTGroupDeviceInfo
import com.eziot.family.model.group.EZIoTGroupInfo
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import kotlinx.android.synthetic.main.eziot_family_list_activity.*
import kotlinx.android.synthetic.main.eziot_group_info_activity.*
import kotlinx.android.synthetic.main.eziot_group_info_activity.toolbar

class EZIoTGroupInfoActivity : BaseActivity() {


    private var groupId : Int = 0

    private var defaultGroupId : Int = 0

    private var defaultGroup : Boolean = false

    private  var ezIoTGroupInfo: EZIoTGroupInfo? = null

    private var targetEzIoTGroupInfo: EZIoTGroupInfo? = null

    private var otherGroupDeviceList  = ArrayList<DeviceInfo>()

    private var thisGroupDeviceList  = ArrayList<DeviceInfo>()

    private lateinit var adapter : EZIoTGroupDeviceListAdapter

    private var isNeedRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_group_info_activity)
        addBackBtn(toolbar)
        initData()
    }

    private fun initData(){
        groupId = intent.getIntExtra(IntentContent.GROUP_ID,0)
        val familyInfo = BaseResDataManager.familyInfo
        val id = familyInfo!!.id
        showWaitDialog()
        EZIoTGroupManager.getGroupList(id,object : IEZIoTResultCallback<List<EZIoTGroupInfo>>{
            override fun onSuccess(t: List<EZIoTGroupInfo>) {
                dismissWaitDialog()
                t.forEach {
                    if(it.id == groupId){
                        ezIoTGroupInfo = it
                    }
                    if(it.isDefaultGroup){
                        defaultGroupId = it.id
                        targetEzIoTGroupInfo = it
                        if(groupId == it.id){
                            defaultGroup = true
                        }
                    }
                }
                initView()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTGroupInfoActivity,errorDesc)
            }

        })
    }

    private fun initView(){
        thisGroupDeviceList.clear()
        otherGroupDeviceList.clear()
        familyNameTv.text = ezIoTGroupInfo!!.name
        val localDeviceList = EZIoTDeviceManager.getLocalDeviceList(getCurrentFamilyInfo()!!.id)
        localDeviceList.forEach {
            if(it.resourceInfos.size > 0 && it.resourceInfos[0].groupId == groupId){
                thisGroupDeviceList.add(it)
            } else {
                otherGroupDeviceList.add(it)
            }
        }
        deviceListRv.layoutManager = LinearLayoutManager(this)
        adapter =  EZIoTGroupDeviceListAdapter(thisGroupDeviceList,this, object : EZIoTGroupDeviceListAdapter.EZIoTGroupDeviceListener{
            override fun ezIotGroupRemoveDeviceClick(deviceInfo: DeviceInfo) {
                removeDeviceFromGroup(deviceInfo)
                isNeedRefresh = true
            }

        },isHomeFamily())
        deviceListRv.adapter = adapter

        if(!isHomeFamily()){
            addGroupDeviceBtn.visibility = View.GONE
            deleteGroup.visibility = View.GONE
            groupInfoLayout.isEnabled = false
            arrorRightIv.visibility = View.GONE
        }
    }


    fun onClickDeleteGroup(view : View){
        if(targetEzIoTGroupInfo == null){
            Utils.showToast(this@EZIoTGroupInfoActivity,R.string.finalRoomNotDelete)
            return
        }
        val familyInfo = BaseResDataManager.familyInfo
        showWaitDialog()
        EZIoTGroupManager.deleteGroup(familyInfo!!.id,groupId,targetEzIoTGroupInfo!!.id,object : IResultCallback{
            override fun onSuccess() {
                Utils.showToast(this@EZIoTGroupInfoActivity,R.string.operateSuccess)
                isNeedRefresh = true
                finish()
                dismissWaitDialog()
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTGroupInfoActivity,R.string.operateFail)
            }

        })
    }

    fun onClickModifyGroupName(view : View){
        val intent = Intent(this,EZIoTGroupModifyNameActivity::class.java)
        intent.putExtra(IntentContent.GROUP_ID,groupId)
        startActivityForResult(intent,IntentContent.REFRESH_CODE)
    }

    fun onClickAddGroupDevice(view : View){
        val layoutView: View = LayoutInflater.from(this).inflate(R.layout.layout_group_device_dialog, null)
        val recyclerView = layoutView.findViewById<RecyclerView>(R.id.listRv)
        val cancelTv = layoutView.findViewById<TextView>(R.id.cancelTv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        var enumDialog : Dialog? = null
        recyclerView.adapter = EZIoTGroupAddDeviceListDialogAdapter(otherGroupDeviceList,this,object : EZIoTGroupAddDeviceListDialogAdapter.EZIoTGroupDeviceListener{
            override fun ezIotGroupDeviceClick(deviceInfo: DeviceInfo) {
                enumDialog!!.dismiss()
                addDeviceFromGroup(deviceInfo)
            }

        })
        cancelTv.setOnClickListener {
            enumDialog!!.dismiss()
        }

        try {
            enumDialog = AlertDialog.Builder(this)
                .setView(layoutView)
                .setCancelable(false)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeDeviceFromGroup(deviceInfo: DeviceInfo){
        val ezIotGroupDeviceInfo = EZIoTGroupDeviceInfo()
        ezIotGroupDeviceInfo.groupId = defaultGroupId
        ezIotGroupDeviceInfo.familyId = getCurrentFamilyInfo()!!.id
        ezIotGroupDeviceInfo.deviceSerial = deviceInfo.deviceSerial
        val ezIotGroupDeviceInfos = ArrayList<EZIoTGroupDeviceInfo>()
        ezIotGroupDeviceInfos.add(ezIotGroupDeviceInfo)
        showWaitDialog()
        EZIoTGroupManager.groupDevicesOperation(groupId,ezIotGroupDeviceInfos,object : IResultCallback{

            override fun onSuccess() {
                dismissWaitDialog()
                otherGroupDeviceList.add(deviceInfo)
                thisGroupDeviceList.remove(deviceInfo)
                deviceInfo.resourceInfos.forEach {
                    it.groupId = defaultGroupId
                    EZIoTDeviceManager.saveResource(getCurrentFamilyInfo()!!.id,it)
                }
                adapter.notifyDataSetChanged()
                Utils.showToast(this@EZIoTGroupInfoActivity,R.string.operateSuccess)
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTGroupInfoActivity,errorDesc)

            }

        })
    }

    private fun addDeviceFromGroup(deviceInfo: DeviceInfo){
        val ezIotGroupDeviceInfo = EZIoTGroupDeviceInfo()
        ezIotGroupDeviceInfo.groupId = groupId
        ezIotGroupDeviceInfo.familyId = getCurrentFamilyInfo()!!.id
        ezIotGroupDeviceInfo.deviceSerial = deviceInfo.deviceSerial
        val ezIotGroupDeviceInfos = ArrayList<EZIoTGroupDeviceInfo>()
        ezIotGroupDeviceInfos.add(ezIotGroupDeviceInfo)
        showWaitDialog()
        EZIoTGroupManager.groupDevicesOperation(groupId,ezIotGroupDeviceInfos,object : IResultCallback{

            override fun onSuccess() {
                dismissWaitDialog()
                otherGroupDeviceList.remove(deviceInfo)
                thisGroupDeviceList.add(deviceInfo)
                deviceInfo.resourceInfos.forEach {
                    it.groupId = groupId
                    EZIoTDeviceManager.saveResource(getCurrentFamilyInfo()!!.id,it)
                }
                adapter.notifyDataSetChanged()
                isNeedRefresh = true
                Utils.showToast(this@EZIoTGroupInfoActivity,R.string.operateSuccess)
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                Utils.showToast(this@EZIoTGroupInfoActivity,errorDesc)

            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == IntentContent.REFRESH_CODE){
            isNeedRefresh = true
            val familyInfo = BaseResDataManager.familyInfo
            val id = familyInfo!!.id
            showWaitDialog()
            EZIoTGroupManager.getGroupList(id,object : IEZIoTResultCallback<List<EZIoTGroupInfo>>{
                override fun onSuccess(t: List<EZIoTGroupInfo>) {
                    dismissWaitDialog()
                    t.forEach {
                        if(it.id == groupId){
                            ezIoTGroupInfo = it
                        }
                        if(it.isDefaultGroup){
                            defaultGroupId = it.id
                            targetEzIoTGroupInfo = it
                            if(groupId == it.id){
                                defaultGroup = true
                            }
                        }
                    }
                    initView()
                }

                override fun onError(errorCode: Int, errorDesc: String?) {
                    dismissWaitDialog()
                    Utils.showToast(this@EZIoTGroupInfoActivity,errorDesc)
                }

            })
        }
    }

    override fun finish() {
        if(isNeedRefresh){
            setResult(IntentContent.REFRESH_CODE)
        }
        super.finish()
    }


}
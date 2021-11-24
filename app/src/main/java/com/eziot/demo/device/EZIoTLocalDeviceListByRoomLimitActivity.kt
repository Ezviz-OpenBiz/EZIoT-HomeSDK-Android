package com.eziot.demo.device

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.device.adapter.EZIoTDeviceListAdapter
import com.eziot.demo.widget.pulltorefresh.*
import com.eziot.device.EZIoTDeviceManager
import com.eziot.iotsdkdemo.R
import kotlinx.android.synthetic.main.eziot_device_list_activity.*
import kotlinx.android.synthetic.main.eziot_family_list_activity.toolbar

class EZIoTLocalDeviceListByRoomLimitActivity : BaseActivity() {

    private var offset = 0

    private var limit = 3

    private lateinit var adapter : EZIoTDeviceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_device_list_activity)
        addBackBtn(toolbar)
        initData()
    }

    private fun initData(){
        val familyId = BaseResDataManager.familyInfo!!.id
        val groupId = BaseResDataManager.groupInfo!!.id
        val getDeviceListResp = EZIoTDeviceManager.getLocalDeviceList(familyId,groupId,limit,offset)
        offset = getDeviceListResp.deviceInfos.size
        deviceListPullRv.setLoadingLayoutCreator(object : PullToRefreshBase.LoadingLayoutCreator() {

            override fun create(context: Context, headerOrFooter: Boolean, orientation: PullToRefreshBase.Orientation): LoadingLayout {
                return if (headerOrFooter) {
                    PullToRefreshHeader(context)
                } else {
                    PullToRefreshFooter(context, PullToRefreshFooter.Style.MORE)
                }
            }
        })

        deviceListPullRv.setOnRefreshListener { _, headerOrFooter ->
            if(headerOrFooter){
                offset = 0
            }
            val getDeviceListResp = EZIoTDeviceManager.getLocalDeviceList(familyId,groupId,limit,offset)
            if(offset == 0){
                adapter.setData(getDeviceListResp.deviceInfos)
            } else {
                adapter.addData(getDeviceListResp.deviceInfos)
            }
            offset += getDeviceListResp.deviceInfos.size
            deviceListPullRv.onRefreshComplete()
            deviceListPullRv.setFooterRefreshEnabled(getDeviceListResp.isHasNext)
        }
        deviceListPullRv.mode = IPullToRefresh.Mode.BOTH
        deviceListPullRv.setFooterRefreshEnabled(true)

        deviceListPullRv.refreshableView.layoutManager = LinearLayoutManager(this)
        adapter = EZIoTDeviceListAdapter(getDeviceListResp.deviceInfos,this)
        deviceListPullRv.refreshableView.adapter = adapter
    }

}
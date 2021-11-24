package com.eziot.demo.device

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.device.adapter.EZIoTDeviceListAdapter
import com.eziot.demo.widget.pulltorefresh.*
import com.eziot.device.EZIoTDeviceManager
import com.eziot.device.model.DeviceInfo
import com.eziot.iotsdkdemo.R
import kotlinx.android.synthetic.main.eziot_device_list_activity.*
import kotlinx.android.synthetic.main.eziot_family_list_activity.toolbar

class EZIoTLocalDeviceListByFamilyActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_device_list_activity)
        addBackBtn(toolbar)
        initData()
    }

    private fun initData(){
        val familyId = BaseResDataManager.familyInfo!!.id
        val localDeviceList = EZIoTDeviceManager.getLocalDeviceList(familyId)
        deviceListPullRv.setLoadingLayoutCreator(object : PullToRefreshBase.LoadingLayoutCreator() {

            override fun create(context: Context, headerOrFooter: Boolean, orientation: PullToRefreshBase.Orientation): LoadingLayout {
                return if (headerOrFooter) {
                    PullToRefreshHeader(context)
                } else {
                    PullToRefreshFooter(context, PullToRefreshFooter.Style.EMPTY_NO_MORE)
                }
            }
        })
        deviceListPullRv.mode = IPullToRefresh.Mode.DISABLED
        deviceListPullRv.setFooterRefreshEnabled(false)

        deviceListPullRv.refreshableView.layoutManager = LinearLayoutManager(this)
        deviceListPullRv.refreshableView.adapter  = EZIoTDeviceListAdapter(localDeviceList as MutableList<DeviceInfo>,this)
    }

}
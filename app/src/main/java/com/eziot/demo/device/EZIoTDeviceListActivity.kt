package com.eziot.demo.device

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.device.adapter.EZIoTDeviceListAdapter
import com.eziot.demo.widget.pulltorefresh.*
import com.eziot.device.EZIoTDeviceManager
import com.eziot.device.model.DeviceInfo
import com.eziot.device.model.GetDeviceListResp
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import kotlinx.android.synthetic.main.eziot_device_list_activity.*
import kotlinx.android.synthetic.main.eziot_family_list_activity.*
import kotlinx.android.synthetic.main.eziot_family_list_activity.toolbar

class EZIoTDeviceListActivity : BaseActivity() {

    private lateinit var adapter : EZIoTDeviceListAdapter

    private var offset = 0

    private var limit = 5

    private var mNoMoreView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_device_list_activity)
        addBackBtn(toolbar)
        initData()
        initPullRecyclerView(ArrayList<DeviceInfo>())
    }

    private fun initData(){
        getDeviceListServer()
    }

    private fun getDeviceListServer(){
        val familyId = BaseResDataManager.familyInfo!!.id
        val groupId = BaseResDataManager.groupInfo!!.id
        showWaitDialog()
        EZIoTDeviceManager.getDeviceList(familyId,groupId,limit,offset,object : IEZIoTResultCallback<GetDeviceListResp>{
            override fun onSuccess(t: GetDeviceListResp) {
                dismissWaitDialog()
                deviceListPullRv.onRefreshComplete()
                if(offset == 0){
                    if(t.deviceInfos == null ||  t.deviceInfos.size == 0){
                        Utils.showToast(this@EZIoTDeviceListActivity,R.string.dataIsNull)
                    } else {
                        adapter.setData(t.deviceInfos)
                        offset += t.deviceInfos.size
                    }
                } else {
                    if(t.deviceInfos == null ||  t.deviceInfos.size == 0){
                        deviceListPullRv.setFooterRefreshEnabled(false)
                    } else {
                        if(t.deviceInfos.size < limit){
                            deviceListPullRv.setFooterRefreshEnabled(false)
                        }
                        adapter.addData(t.deviceInfos)
                        offset += t.deviceInfos.size
                    }
                }
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                dismissWaitDialog()
                deviceListPullRv.onRefreshComplete()
                Utils.showToast(this@EZIoTDeviceListActivity,errorDesc)
            }

        })
    }


    private fun initPullRecyclerView(deviceList: ArrayList<DeviceInfo>){
        mNoMoreView = layoutInflater.inflate(R.layout.no_more_footer, null)
        deviceListPullRv.setLoadingLayoutCreator(object : PullToRefreshBase.LoadingLayoutCreator() {

            override fun create(context: Context, headerOrFooter: Boolean, orientation: PullToRefreshBase.Orientation): LoadingLayout {
                return if (headerOrFooter) {
                    PullToRefreshHeader(context)
                } else {
                    PullToRefreshFooter(context, PullToRefreshFooter.Style.MORE)
                }
            }
        })
        deviceListPullRv.mode = IPullToRefresh.Mode.BOTH
        deviceListPullRv.setFooterRefreshEnabled(true)
        deviceListPullRv.setOnRefreshListener { _, headerOrFooter ->
            if(headerOrFooter){
                offset = 0
                deviceListPullRv.setFooterRefreshEnabled(true)
                deviceListPullRv.refreshableView
            }
            getDeviceListServer()
        }
        deviceListPullRv.refreshableView.layoutManager = LinearLayoutManager(this)
        adapter = EZIoTDeviceListAdapter(deviceList,this@EZIoTDeviceListActivity)
        deviceListPullRv.refreshableView.adapter  = adapter
    }


}
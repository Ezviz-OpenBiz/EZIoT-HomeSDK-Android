package com.eziot.demo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.common.http.callback.IResultCallback
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.utils.Utils
import com.eziot.device.EZIoTDeviceManager
import com.eziot.iotsdkdemo.R
import com.eziot.wificonfig.EZIoTNetConfigurator
import kotlinx.android.synthetic.main.eziot_main_tab_activity.*

class EZIoTMainTabActivity : BaseActivity() {

    companion object{
        const val MAIN = "main"

        const val SMART = "smart"

        const val MY = "my"
    }

    private val fragmentMap = HashMap<String , Fragment>()

    private var currentKey = MAIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_main_tab_activity)
        initData()
        initView()
    }

    private fun initData(){
        fragmentMap[MAIN] = EZIoTMainFragment()
        fragmentMap[SMART] = EZIoTSmartFragment()
        fragmentMap[MY] = EZIoTMineFragment()
    }

    private fun initView(){
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, fragmentMap[MAIN]!!)
            .commit();
        changeBottomTab(MAIN)
    }

    fun onClickMain(view : View){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView,fragmentMap[MAIN]!!)
            .commit()
        changeBottomTab(MAIN)
    }

    fun onClickMine(view : View){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView,fragmentMap[MY]!!)
            .commit()
        changeBottomTab(MY)


    }

    fun onClickSmart(view : View){
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragmentContainerView,fragmentMap[SMART]!!)
//            .commit()
//        changeBottomTab(SMART)



        val getDeviceControl = EZIoTDeviceManager.getDeviceControl(
            BaseResDataManager.familyInfo!!.id,
            "G0EBC76F79203480F816BC:869951041000002"
        )
        getDeviceControl.deleteDevice(object : IResultCallback{
            override fun onSuccess() {
                Utils.showToast(this@EZIoTMainTabActivity,"删除成功")
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Utils.showToast(this@EZIoTMainTabActivity,"删除失败")

            }

        })
//        EZIoTNetConfigurator.addDeviceNB(BaseResDataManager.groupInfo!!.id,"869951041362123",object : IEZIoTResultCallback<String>{
//            override fun onSuccess(t: String) {
//
//            }
//
//            override fun onError(errorCode: Int, errorDesc: String?) {
//            }
//
//        })



    }

    private fun changeBottomTab(key : String){
        currentKey = key
        when(currentKey){
            MAIN -> {
                mainIv.isEnabled = false
                smartIv.isEnabled = true
                myIv.isEnabled = true
            }
            SMART -> {
                mainIv.isEnabled = true
                smartIv.isEnabled = false
                myIv.isEnabled = true
            }
            MY -> {
                mainIv.isEnabled = true
                smartIv.isEnabled = true
                myIv.isEnabled = false
            }

        }

    }


}
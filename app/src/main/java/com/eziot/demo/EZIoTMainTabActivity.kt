package com.eziot.demo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.eziot.demo.base.BaseActivity
import com.eziot.iotsdkdemo.R
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
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView,fragmentMap[SMART]!!)
            .commit()
        changeBottomTab(SMART)
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
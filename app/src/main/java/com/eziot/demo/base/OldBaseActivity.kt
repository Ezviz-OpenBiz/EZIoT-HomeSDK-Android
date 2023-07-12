package com.eziot.demo.base

import android.R
import android.app.Activity
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.eziot.common.utils.GlobalVariable
import com.eziot.family.model.family.EZIoTFamilyInfo
import com.eziot.family.model.group.EZIoTRoomInfo
import com.eziot.demo.widge.WaitDialog

open class OldBaseActivity : Activity() {

    open var waitDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        waitDialog = WaitDialog(this, R.style.Theme_Translucent_NoTitleBar)
    }

    public fun showWaitDialog(){
        waitDialog!!.show()
    }

    public fun dismissWaitDialog(){
        waitDialog!!.dismiss()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Android.R.id.home对应应用程序图标的id
        if (item.itemId === R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    public open fun addBackBtn(toolbar: Toolbar){
        toolbar.setNavigationOnClickListener { finish() }
    }

    fun getCurrentFamilyInfo() : EZIoTFamilyInfo?{
        return BaseResDataManager.familyInfo
    }

    fun getCurrentGroupInfo() :  EZIoTRoomInfo? {
        return BaseResDataManager.roomInfo
    }

    fun isHomeFamily() : Boolean{
        return BaseResDataManager.familyInfo?.creator.equals(GlobalVariable.USER_ID.get())
    }

}
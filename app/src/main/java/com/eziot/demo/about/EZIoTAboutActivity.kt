package com.eziot.demo.about

import android.Manifest
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eziot.demo.base.BaseActivity
import com.eziot.demo.ble.EZIoTBleListActivity
import com.eziot.demo.debug.CrashInfoActivity
import com.eziot.demo.utils.Utils
import com.eziot.iotsdkdemo.R
import kotlinx.android.synthetic.main.eziot_about_activity.*

class EZIoTAboutActivity : BaseActivity() ,ActivityCompat.OnRequestPermissionsResultCallback{

    companion object{
        const val REQUEST_LOCATE = 1;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eziot_about_activity)
        versionNameTv.text = getVersionName()
    }

    private fun getVersionName() : String {
        val packageManager: PackageManager = application.packageManager
        val packInfo: PackageInfo = packageManager.getPackageInfo(application.packageName, 0)
        return packInfo.versionName
    }

    fun onGoToMultiBleManager(view: View){
        if(requestPermission()) {
            val intent = Intent(this, EZIoTBleListActivity::class.java)
            startActivity(intent)
        }
    }

    var name : String? = null

    fun onGoToNetLogManager(view: View){
    }

    fun onGoToGroupManager(view: View){
//        val intent = Intent(this, GroupManagerActivity::class.java)
//        startActivity(intent)
        name!!.substring(3)
    }

    fun onChangeConfig(view: View){
        val intent = Intent(this, EZIoTDebugActivity::class.java)
        startActivity(intent)
    }

    fun onGoToCrash(view: View){
        val intent = Intent(this, CrashInfoActivity::class.java)
        startActivity(intent)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty() && grantResults[0] == 0){
            val intent = Intent(this, EZIoTBleListActivity::class.java)
            startActivity(intent)
        } else {
            Utils.showToast(this, "请打开定位权限")
        }
    }

    private fun requestPermission() : Boolean{
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATE
                )
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATE
                )
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false
        }
        return true
    }

}
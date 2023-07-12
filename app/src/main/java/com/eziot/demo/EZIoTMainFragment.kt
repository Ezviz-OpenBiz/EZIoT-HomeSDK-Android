package com.eziot.demo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eziot.demo.about.EZIoTAboutActivity
import com.eziot.demo.base.BaseFragment
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.ble.EZIoTBleListActivity
import com.eziot.demo.device.EZIoTDeviceListTypeActivity
import com.eziot.demo.family.EZIoTFamilyAddActivity
import com.eziot.demo.family.EZIoTFamilyListActivity
import com.eziot.demo.family.EZIoTFamilySelectActivity
import com.eziot.demo.group.EZIoTGroupAddActivity
import com.eziot.demo.group.EZIoTGroupListActivity
import com.eziot.demo.group.EZIoTGroupSelectActivity
import com.eziot.demo.wificonfig.EZIoTWifiConfigActivity
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils

class EZIoTMainFragment : BaseFragment() , View.OnClickListener{

    lateinit var currentFamilyTv : TextView

    lateinit var currentGroupTv : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.eziot_main_fragment,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ViewGroup>(R.id.addFamilyLayout).setOnClickListener(this)
        view.findViewById<ViewGroup>(R.id.selectFamilyLayout).setOnClickListener(this)
        view.findViewById<ViewGroup>(R.id.familyLayout).setOnClickListener(this)
        view.findViewById<ViewGroup>(R.id.addGroup).setOnClickListener(this)
        view.findViewById<ViewGroup>(R.id.groupList).setOnClickListener(this)
        view.findViewById<ViewGroup>(R.id.selectGroup).setOnClickListener(this)
        view.findViewById<ViewGroup>(R.id.deviceGroup).setOnClickListener(this)
        view.findViewById<ViewGroup>(R.id.wifiModule).setOnClickListener(this)
        view.findViewById<ViewGroup>(R.id.bleModule).setOnClickListener(this)
        currentFamilyTv = view.findViewById(R.id.currentFamilyTv)
        currentGroupTv = view.findViewById(R.id.currentGroupTv)
    }

    override fun onResume() {
        super.onResume()
        val familyInfo = BaseResDataManager.familyInfo
        val groupInfo = BaseResDataManager.roomInfo
        if(familyInfo != null){
            currentFamilyTv.text = familyInfo.familyName
        } else {
            currentFamilyTv.text = ""
        }
        if(groupInfo != null){
            currentGroupTv.text = groupInfo.name
        } else {
            currentGroupTv.text = ""
        }
        view?.findViewById<ViewGroup>(R.id.selectFamilyLayout)?.setOnClickListener(this)
        view?.findViewById<ViewGroup>(R.id.familyLayout)?.setOnClickListener(this)

        view?.findViewById<ViewGroup>(R.id.addGroup)?.setOnClickListener(this)
        view?.findViewById<ViewGroup>(R.id.groupList)?.setOnClickListener(this)
        view?.findViewById<ViewGroup>(R.id.selectGroup)?.setOnClickListener(this)
        view?.findViewById<ViewGroup>(R.id.deviceGroup)?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val familyInfo = BaseResDataManager.familyInfo
        val groupInfo = BaseResDataManager.roomInfo
        when(v!!.id){
            R.id.addFamilyLayout -> {
                startActivity(Intent(activity,EZIoTFamilyAddActivity::class.java))
            }
            R.id.selectFamilyLayout -> {

                startActivity(Intent(activity,EZIoTFamilySelectActivity::class.java))
            }
            R.id.familyLayout -> {
                startActivity(Intent(activity,EZIoTFamilyListActivity::class.java))
            }
            R.id.addGroup -> {
                if(familyInfo == null){
                    Utils.showToast(context,R.string.selectFamilyHint)
                } else {
                    if(isHomeFamily()){
                        startActivity(Intent(activity,EZIoTGroupAddActivity::class.java))
                    } else {
                        Utils.showToast(context,R.string.family_home_operate)
                    }
                }
            }
            R.id.groupList -> {
                if(familyInfo == null){
                    Utils.showToast(context,R.string.selectFamilyHint)
                } else {
                    startActivity(Intent(activity,EZIoTGroupListActivity::class.java))
                }
            }
            R.id.selectGroup -> {
                if(familyInfo == null){
                    Utils.showToast(context,R.string.selectFamilyHint)
                } else {
                    startActivity(Intent(activity,EZIoTGroupSelectActivity::class.java))
                }
            }
            R.id.deviceGroup -> {
                if(familyInfo == null || groupInfo == null){
                    Utils.showToast(context,R.string.selectFamilyOrRoomHint)
                } else {
                    startActivity(Intent(activity,EZIoTDeviceListTypeActivity::class.java))
                }
            }
            R.id.wifiModule -> {
                if(familyInfo == null || groupInfo == null){
                    Utils.showToast(context, R.string.selectFamilyOrRoomHint)
                }else{
                    if(isHomeFamily()){
                        startActivity(Intent(activity, EZIoTWifiConfigActivity::class.java))
                    } else {
                        Utils.showToast(context, R.string.family_home_add_device_hint)
                    }
                }
            }
            R.id.bleModule -> {
                if(requestPermission()){
                    startActivity(Intent(activity, EZIoTBleListActivity::class.java))
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty() && grantResults[0] == 0){
            val intent = Intent(activity, EZIoTBleListActivity::class.java)
            startActivity(intent)
        } else {
            Utils.showToast(activity, "请打开定位权限")
        }
    }


    private fun requestPermission() : Boolean{
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    EZIoTAboutActivity.REQUEST_LOCATE
                )
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    EZIoTAboutActivity.REQUEST_LOCATE
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
package com.eziot.demo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.common.http.callback.IResultCallback
import com.eziot.demo.about.EZIoTAboutActivity
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.generalSetting.EZIoTGeneralSettingActivity
import com.eziot.demo.message.EZIoTMessageListActivity
import com.eziot.demo.user.EZIoTUserInfoActivity
import com.eziot.family.EZIotFamilyManager
import com.eziot.family.model.family.EZIoTFamilyInfo
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import com.eziot.user.EZIotUserManager
import com.eziot.user.http.bean.GetUserInfoResp

class EZIoTMineFragment : Fragment(), View.OnClickListener{


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(mView == null){
            mView = inflater.inflate(R.layout.eziot_my_fragment,container,false)
        }

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ViewGroup>(R.id.view_click_settings).setOnClickListener(this)
        view.findViewById<ViewGroup>(R.id.view_click_message_center).setOnClickListener(this)
        view.findViewById<Button>(R.id.logoutBtn).setOnClickListener(this)
        view.findViewById<ViewGroup>(R.id.view_click_about).setOnClickListener(this)
        view.findViewById<View>(R.id.view_click_debug).setOnClickListener(this)

        mUserName = view.findViewById(R.id.user_tv_nickname)
        initData()

        mUserName?.setOnClickListener(this)


    }



    private fun initData(){
        EZIotUserManager.getUserProfile(object: IEZIoTResultCallback<GetUserInfoResp>{
            override fun onSuccess(t: GetUserInfoResp) {
                mUserName?.text = t.userInfo.homeTitle
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                Utils.showToast(activity,errorDesc)
            }

        })

        EZIotFamilyManager.getFamilyList(object : IEZIoTResultCallback<List<EZIoTFamilyInfo>>{
            override fun onSuccess(t: List<EZIoTFamilyInfo>) {

            }

            override fun onError(errorCode: Int, errorDesc: String?) {

            }

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val text = data?.getStringExtra("homeTitle")
        if(text == null || text.isEmpty()){
            initData()
        }else{
            mUserName?.text = data?.getStringExtra("homeTitle")
        }

    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.user_tv_nickname -> {
                startActivityForResult(Intent(activity, EZIoTUserInfoActivity::class.java), 0)
            }
            R.id.view_click_settings -> {
                startActivity(Intent(activity, EZIoTGeneralSettingActivity::class.java))
            }
            R.id.view_click_message_center -> {
                startActivity(Intent(activity, EZIoTMessageListActivity::class.java))
            }
            R.id.logoutBtn -> {
                EZIotUserManager.logout(object : IResultCallback{
                    override fun onSuccess() {
                        BaseResDataManager.roomInfo = null
                        BaseResDataManager.familyInfo = null
                        val intent = Intent(activity!!,EZIoTGuideActivity::class.java)
                        startActivity(intent)
                        activity!!.finish()
                    }

                    override fun onError(errorCode: Int, errorDesc: String?) {
                        BaseResDataManager.roomInfo = null
                        BaseResDataManager.familyInfo = null
                        val intent = Intent(activity!!,EZIoTGuideActivity::class.java)
                        startActivity(intent)
                        activity!!.finish()
                    }

                })
            }
            R.id.view_click_about -> {
                val intent = Intent(activity,EZIoTAboutActivity::class.java)
                startActivity(intent)
            }
            R.id.view_click_debug -> {
                val intent = Intent(activity,EZIoTAboutActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private var mView: View? = null
    private var mUserName: TextView? = null

}
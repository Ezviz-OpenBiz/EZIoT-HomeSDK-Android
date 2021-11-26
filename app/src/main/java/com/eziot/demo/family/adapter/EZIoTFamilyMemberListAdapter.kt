package com.eziot.demo.family.adapter

import android.app.Activity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eziot.common.http.callback.IResultCallback
import com.eziot.family.EZIotFamilyManager
import com.eziot.family.model.family.EZIoTFamilyMemberInfo
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils

class EZIoTFamilyMemberListAdapter(private val memberInfoList: List<EZIoTFamilyMemberInfo>,val activity : Activity, val isFamilyHost : Boolean) :
    RecyclerView.Adapter<EZIoTFamilyMemberListAdapter.EZIoTFamilyListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EZIoTFamilyListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.eziot_family_member_list_adapter, parent, false)
        return EZIoTFamilyListViewHolder(view)
    }

    override fun onBindViewHolder(holder: EZIoTFamilyListViewHolder, position: Int) {
        val ezIotFamilyMemberInfo = memberInfoList[position]
        holder.removeMemberBtn.visibility = View.GONE
        holder.memberListLayout.setOnClickListener(null)
        holder.numberNameTv.text = if(!TextUtils.isEmpty(ezIotFamilyMemberInfo.nick)) ezIotFamilyMemberInfo.nick else ezIotFamilyMemberInfo.phone
        if(ezIotFamilyMemberInfo.type == 0){
            holder.memberTypeTv.text = activity.getString(R.string.familyCreator)
        } else {
            holder.memberTypeTv.text = activity.getString(R.string.familyMember)
            when(ezIotFamilyMemberInfo.status){
                0 -> holder.memberTypeTv.text = activity.getString(R.string.waiting_join)
                2 -> holder.memberTypeTv.text = activity.getString(R.string.refuse_join)
                3 -> holder.memberTypeTv.text = activity.getString(R.string.expire)
            }
            if(isFamilyHost){
                holder.removeMemberBtn.visibility = View.VISIBLE
                holder.removeMemberBtn.setOnClickListener {
                    EZIotFamilyManager.removeFamilyMember(ezIotFamilyMemberInfo.familyId,
                        ezIotFamilyMemberInfo.id.toString(), object : IResultCallback{
                        override fun onSuccess() {
                            Utils.showToast(activity,R.string.operateSuccess)
                            activity.finish()
                        }

                        override fun onError(errorCode: Int, errorDesc: String?) {
                            Utils.showToast(activity,errorDesc)
                        }

                    })
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return memberInfoList.size
    }

    class EZIoTFamilyListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val numberNameTv: TextView = view.findViewById(R.id.numberNameTv)
        val memberTypeTv: TextView = view.findViewById(R.id.memberTypeTv)
        val memberListLayout: ViewGroup = view.findViewById(R.id.memberListLayout)
        val removeMemberBtn : Button = view.findViewById(R.id.removeMemberBtn)
    }


}
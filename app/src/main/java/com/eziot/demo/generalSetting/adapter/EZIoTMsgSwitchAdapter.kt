package com.eziot.demo.generalSetting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eziot.iotsdkdemo.R
import com.eziot.message.model.EZIoTMsgCategoryInfo

class EZIoTMsgSwitchAdapter(val context: Context,
                            private val ezIoTMsgCategoryInfos : List<EZIoTMsgCategoryInfo>, private val switchMsgMap : HashMap<Int,Boolean>,val ezIotMsgSwitchListener : EZIoTMsgSwitchListener) : RecyclerView.Adapter<EZIoTMsgSwitchAdapter.EZIoTMsgSwitchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EZIoTMsgSwitchViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.eziot_msg_switch_adapter,  parent, false)
        return EZIoTMsgSwitchViewHolder(view)
    }

    override fun onBindViewHolder(holder: EZIoTMsgSwitchViewHolder, position: Int) {
        holder.msgTypeNameTv.text = ezIoTMsgCategoryInfos[position].name
        holder.msgSwitchCb.isChecked = switchMsgMap[ezIoTMsgCategoryInfos[position].type] ?: false
        holder.msgSwitchLayout.setOnClickListener {
            ezIotMsgSwitchListener.ezIotMsgSwitchClick(ezIoTMsgCategoryInfos[position].type , !holder.msgSwitchCb.isChecked)
        }
    }

    override fun getItemCount(): Int {
        return ezIoTMsgCategoryInfos.size
    }

    class EZIoTMsgSwitchViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val msgTypeNameTv = view.findViewById<TextView>(R.id.msgTypeNameTv)
        val msgSwitchCb = view.findViewById<CheckBox>(R.id.msgSwitchCb)
        val msgSwitchLayout = view.findViewById<ViewGroup>(R.id.msgSwitchLayout)
    }

    interface EZIoTMsgSwitchListener {

        fun ezIotMsgSwitchClick(categoryType : Int , isOpen : Boolean)

    }

}
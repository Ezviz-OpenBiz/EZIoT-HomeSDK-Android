package com.eziot.demo.generalSetting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eziot.iotsdkdemo.R
import com.eziot.user.model.terminal.EZIoTUserTerminal

class TerminalManageAdapter(var context: Context, var userTerminals: List<EZIoTUserTerminal>, var terminalManage: TerminalManageInterface):
    RecyclerView.Adapter<TerminalManageAdapter.TerminalManageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TerminalManageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.eziot_terminal_manage_adapter, null)
        return TerminalManageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userTerminals.size
    }

    override fun onBindViewHolder(holder: TerminalManageViewHolder, position: Int) {
        holder.deviceName.text = userTerminals[position].name
        holder.deviceBindDate.text = userTerminals[position].addTime
        holder.terminalDeviceDelete.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                terminalManage.terminalDelete(userTerminals[position])
            }

        })
    }

    interface TerminalManageInterface{
        fun terminalDelete(terminalInfo: EZIoTUserTerminal)
    }

    class TerminalManageViewHolder(view: View): RecyclerView.ViewHolder(view){
        var deviceName = view.findViewById<TextView>(R.id.terminalDeviceName)
        var deviceBindDate = view.findViewById<TextView>(R.id.terminalDeviceBindDate)
        var terminalDeviceDelete = view.findViewById<TextView>(R.id.terminalDeviceDelete)
    }
}
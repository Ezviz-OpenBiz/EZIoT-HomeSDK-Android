package com.eziot.demo.device.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eziot.iotsdkdemo.R

class EZIoTEnumListDialogAdapter(private val data : MutableList<Any>, context : Context ,val enumDialogItemClickListener : EnumDialogItemClickListener): RecyclerView.Adapter<EZIoTEnumListDialogAdapter.EZIoTListDialogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EZIoTListDialogViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_dialog_adapter, parent, false)
        return EZIoTListDialogViewHolder(view)
    }

    override fun onBindViewHolder(holder: EZIoTListDialogViewHolder, position: Int) {
        holder.nameTv.text = data[position].toString()
        holder.enumItemLayout.setOnClickListener {
            enumDialogItemClickListener.onEnumDialogItemClick(data[position])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class EZIoTListDialogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTv: TextView = view.findViewById(R.id.nameTv)
        val enumItemLayout : ViewGroup = view.findViewById(R.id.enumItemLayout)
    }


    interface EnumDialogItemClickListener{
        fun onEnumDialogItemClick(view : Any)
    }

}
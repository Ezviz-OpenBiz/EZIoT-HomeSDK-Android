package com.eziot.demo.ble.skip

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.eziot.demo.ble.skip.GetOfflineResp.OfflineItem
import com.eziot.iotsdkdemo.R

class OfflineListAdapter(var data: List<OfflineItem>,var context : Context) : Adapter<OfflineListAdapter.OfflineViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfflineViewHolder {
        return OfflineViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_eziot_skip_rose,null))
    }

    override fun onBindViewHolder(holder: OfflineViewHolder, position: Int) {
        val offlineItem = data[position]
        holder.costTimeTv.text = offlineItem.t.toString()
        holder.columnTv.text = offlineItem.c.toString()
        holder.timeTv.text = offlineItem.d.toString()
        holder.deleteBtn.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return data.size;
    }


    class OfflineViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val costTimeTv: TextView = view.findViewById(R.id.costTimeTv)
        var columnTv: TextView =    view.findViewById(R.id.columnTv)
        var timeTv: TextView =    view.findViewById(R.id.timeTv)
        var deleteBtn: TextView =    view.findViewById(R.id.deleteBtn)
    }

}
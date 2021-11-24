package com.eziot.demo.device.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eziot.demo.device.update.EZIoTDeviceUpdateInfoActivity
import com.eziot.demo.device.update.EZIoTUpdateShowInfo
import com.eziot.device.model.update.EZIoTUpdateInfo
import com.eziot.iotsdkdemo.R

class EZIoTDeviceUpdateInfoAdapter(private val ezIotUpdateInfos : List<EZIoTUpdateShowInfo>,val context: Context) :
    RecyclerView.Adapter<EZIoTDeviceUpdateInfoAdapter.EZIoTDeviceUpdateInfoViewHolder>() {

    private val ezIotUpdateInfoArray = ArrayList<EZIoTUpdateShowInfo>()

    init {
        ezIotUpdateInfoArray.clear()
        ezIotUpdateInfoArray.addAll(ezIotUpdateInfos)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EZIoTDeviceUpdateInfoViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.eziot_update_info_adapter, parent, false)
        return EZIoTDeviceUpdateInfoViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EZIoTDeviceUpdateInfoViewHolder, position: Int) {
        val ezIoTUpdateInfo = ezIotUpdateInfoArray[position]
        holder.currentVersionTv.text = ezIoTUpdateInfo.updateInfo.version
        holder.updateVersionTv.text = if(ezIoTUpdateInfo.updateInfo.packageInfo != null) ezIoTUpdateInfo.updateInfo.packageInfo.firmwareVersion else ""
        holder.updateContentTv.text = if(ezIoTUpdateInfo.updateInfo.packageDescInfo != null) ezIoTUpdateInfo.updateInfo.packageDescInfo.packageDesc else ""
        when (ezIoTUpdateInfo.state) {
            EZIoTDeviceUpdateInfoActivity.UPDATE_INIT -> {
                holder.updateStateLayout.visibility = View.GONE
            }
            EZIoTDeviceUpdateInfoActivity.UPDATING -> {
                holder.updateStateLayout.visibility = View.VISIBLE
                holder.updateStateTv.text =  "${ezIoTUpdateInfo.progress}%"
            }
            EZIoTDeviceUpdateInfoActivity.UPDATE_FAIL -> {
                holder.updateStateLayout.visibility = View.VISIBLE
                holder.updateStateTv.text = context.getText(R.string.updateFail)
            }
            EZIoTDeviceUpdateInfoActivity.UPDATE_SUCCESS -> {
                holder.updateStateLayout.visibility = View.VISIBLE
                holder.updateStateTv.text = context.getText(R.string.updateSuccess)
            }
        }
    }

    override fun getItemCount(): Int {
        return ezIotUpdateInfoArray.size
    }

    fun updateProgress(productModel: String, progress: Int){
        for (ezIotUpdateShowInfo in ezIotUpdateInfoArray){
            if(productModel == ezIotUpdateShowInfo.updateInfo.productModel){
                ezIotUpdateShowInfo.state = EZIoTDeviceUpdateInfoActivity.UPDATING
                ezIotUpdateShowInfo.progress = progress
                notifyDataSetChanged()
                break
            }
        }
    }

    fun updateState(productModel: String?, state: Int){
        for (ezIotUpdateShowInfo in ezIotUpdateInfoArray){
            if(productModel == null){
                ezIotUpdateShowInfo.state = state
                notifyDataSetChanged()
            } else {
                if(productModel == ezIotUpdateShowInfo.updateInfo.productModel){
                    ezIotUpdateShowInfo.state = state
                    notifyDataSetChanged()
                    break
                }
            }
        }
    }

    fun updateStateAndProgress(productModel: String?, state: Int , progress: Int){
        for (ezIotUpdateShowInfo in ezIotUpdateInfoArray){
            if(productModel == null){
                ezIotUpdateShowInfo.state = state
                ezIotUpdateShowInfo.progress = progress
                notifyDataSetChanged()
            } else {
                if(productModel == ezIotUpdateShowInfo.updateInfo.productModel){
                    ezIotUpdateShowInfo.state = state
                    ezIotUpdateShowInfo.progress = progress
                    notifyDataSetChanged()
                    break
                }
            }
        }
    }

    class EZIoTDeviceUpdateInfoViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val currentVersionTv: TextView = view.findViewById(R.id.currentVersionTv)
        val updateVersionTv: TextView = view.findViewById(R.id.updateVersionTv)
        val updateContentTv: TextView = view.findViewById(R.id.updateContentTv)
        val updateStateLayout: ViewGroup = view.findViewById(R.id.updateStateLayout)
        val updateStateTv: TextView = view.findViewById(R.id.updateStateTv)
    }

}
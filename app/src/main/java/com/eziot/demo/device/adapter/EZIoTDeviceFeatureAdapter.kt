package com.eziot.demo.device.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.*
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.eziot.common.http.callback.IEZIoTResultCallback
import com.eziot.common.http.callback.IResultCallback
import com.eziot.demo.base.BaseResDataManager
import com.eziot.demo.device.callback.EZIoTRefreshDeviceListCallback
import com.eziot.device.EZIoTDeviceManager
import com.eziot.device.model.EZIoTDeviceInfo
import com.eziot.device.model.FeatureModel
import com.eziot.iotsdkdemo.R
import com.eziot.demo.utils.Utils
import com.google.android.material.slider.Slider
import java.lang.StringBuilder

class EZIoTDeviceFeatureAdapter(
    private val featureItems: MutableList<FeatureModel.FeatureItem>,
    private val actionItems: MutableList<FeatureModel.ActionItem>,
    private val localIndex: String,
    private val deviceInfo: EZIoTDeviceInfo,
    private val context: Context,
    private val ezIoTRefreshDeviceListCallback: EZIoTRefreshDeviceListCallback
) : RecyclerView.Adapter<EZIoTDeviceFeatureAdapter.EZIoTDeviceFeatureViewHolder>() {

    private val datas = ArrayList<Any>()

    init {
        datas.addAll(featureItems)
        datas.addAll(actionItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EZIoTDeviceFeatureViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.eziot_device_feature_adapter,
            parent,
            false
        )
        return EZIoTDeviceFeatureViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EZIoTDeviceFeatureViewHolder, position: Int) {
        val data = datas[position]
        holder.controlTv.visibility = View.VISIBLE
        if(data is FeatureModel.FeatureItem){
            holder.featureKeyTv.text = "Key：" + data.key.substring(
                0,
                data.key.length - 2
            )
            holder.featureNameTv.text = "名称：" +  data.title
            holder.featureValueTv.text = "值：[" + data.dataValue.toString() +"]"
            if(data.isEnum){
                holder.featureTypeTv.text = "类型：[ enum-" + data.dataType + "]"
            } else {
                holder.featureTypeTv.text = "类型：[" + data.dataType + "]"
            }

            holder.deviceItemLayout.setOnClickListener {
                showCommonFeatureInfoDialog(data)
            }

            val access = data.rawData.optString("access")
            holder.featureViewLayout.removeAllViews()

            if(access.equals("r") || !deviceInfo.isOnline){
                holder.controlTv.visibility = View.GONE
                return
            }
            when {
                isSlider(data) -> {
                    sliderItem(holder, data)
                }
                data.dataType.equals("boolean") -> {
                    boolItem(holder, data)
                }
                data.isEnum -> {
                    enumItem(holder, data)
                }
                data.dataType.equals("integer") -> {
                    integerItem(holder, data)
                }
                data.dataType.equals("string") -> {
                    stringItem(holder, data)
                }
                data.dataType.equals("number") -> {
                    numberItem(holder, data)
                }
                data.dataType.equals("array") -> {
                    arrayItem(holder, data)
                }
                data.dataType.equals("object") -> {
                    objectItem(holder, data)
                }
            }
        } else if (data is FeatureModel.ActionItem){
            holder.featureKeyTv.text = "Key：" + data.key.substring(
                0,
                data.key.length - 2
            )
            holder.featureNameTv.text = "名称：" +  data.title
            holder.featureTypeTv.text = "[action]"
            holder.featureValueTv.text = ""
            holder.deviceItemLayout.setOnClickListener {
                showCommonFeatureInfoDialog(data)
            }
            holder.featureViewLayout.removeAllViews()
            val access = data.rawData.optString("access")
            if(access.equals("r")){
                holder.controlTv.visibility = View.GONE
                return
            }
            if(!deviceInfo.isOnline){
                holder.controlTv.visibility = View.GONE
                return
            }
            actionItem(holder, data)
        }

    }

    public fun setData(featureItems: List<FeatureModel.FeatureItem>,actionItems: List<FeatureModel.ActionItem>){
        this.datas.clear()
        this.datas.addAll(featureItems)
        this.datas.addAll(actionItems)
        notifyDataSetChanged()
    }

    private fun isSlider(featureItem: FeatureModel.FeatureItem) : Boolean{
        if(featureItem.rawData.has("schema")){
            val jsonObject = featureItem.rawData.getJSONObject("schema")
            return jsonObject.has("maximum") && jsonObject.has("minimum")
        }
        return false
    }

    private fun boolItem(
        holder: EZIoTDeviceFeatureViewHolder,
        featureItem: FeatureModel.FeatureItem
    ){
        val checkBox = CheckBox(context)
        checkBox.buttonDrawable = null
        checkBox.setBackgroundResource(R.drawable.common_check2_selector)
        holder.featureViewLayout.addView(checkBox)
        if(featureItem.dataValue != null && featureItem.dataValue is Boolean){
            checkBox.isChecked = featureItem.dataValue as Boolean
        }
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            val familyInfo = BaseResDataManager.familyInfo
            val ezIotDevice = EZIoTDeviceManager.getDeviceControl(
                familyInfo!!.id,
                deviceInfo.deviceSerial
            )
            ezIotDevice.setPropFeature(featureItem, isChecked, object : IResultCallback {
                override fun onSuccess() {
                    deviceInfo.setFeature(featureItem.key, localIndex, isChecked)
                    EZIoTDeviceManager.saveDeviceInfo(familyInfo.id, deviceInfo)
                    ezIoTRefreshDeviceListCallback.onLocalRefreshCallback()
                }

                override fun onError(errorCode: Int, errorDesc: String?) {

                }

            })
        }
    }

    private fun enumItem(
        holder: EZIoTDeviceFeatureViewHolder,
        featureItem: FeatureModel.FeatureItem
    ){

        val textView = getTextView()
        holder.featureViewLayout.addView(textView)
        textView.setOnClickListener {
            showEnumDialog(featureItem.enumData,object : EnumDialogCallback{
                override fun onEnumDialogItemClick(value: Any) {
                    setDeviceFeatureProp(featureItem, value)
                }

            })
        }
    }

    private fun integerItem(
        holder: EZIoTDeviceFeatureViewHolder,
        featureItem: FeatureModel.FeatureItem
    ){

        val textView = getTextView()
        holder.featureViewLayout.addView(textView)
        textView.setOnClickListener {
            showInputDialog(featureItem.dataValue?.toString(),object : InputDialogCallback{
                override fun onSureClick(value: String) {
                    if(TextUtils.isEmpty(value)){
                        Utils.showToast(context,R.string.not_empty)
                        return
                    }
                    setDeviceFeatureProp(featureItem, value.toInt())
                }

            })
        }
    }

    private fun objectItem(
        holder: EZIoTDeviceFeatureViewHolder,
        featureItem: FeatureModel.FeatureItem
    ){

        val textView = getTextView()
        holder.featureViewLayout.addView(textView)
        textView.setOnClickListener {
            showInputDialog(featureItem.dataValue?.toString(),object : InputDialogCallback{
                override fun onSureClick(value: String) {
                    if(TextUtils.isEmpty(value)){
                        Utils.showToast(context,R.string.not_empty)
                        return
                    }
                    try {
                        val jsonObject = JSONObject.parseObject(value)
                        setDeviceFeatureProp(featureItem, jsonObject)
                    } catch (e : java.lang.Exception){
                        Utils.showToast(context,R.string.value_formate_error)
                    }
                }

            })
        }
    }

    private fun stringItem(
        holder: EZIoTDeviceFeatureViewHolder,
        featureItem: FeatureModel.FeatureItem
    ){
        val textView = getTextView()
        holder.featureViewLayout.addView(textView)
        textView.setOnClickListener {
            showInputDialog(featureItem.dataValue?.toString(),object : InputDialogCallback{
                override fun onSureClick(value: String) {
                    if(TextUtils.isEmpty(value)){
                        Utils.showToast(context,R.string.not_empty)
                        return
                    }
                    setDeviceFeatureProp(featureItem, value)
                }

            })
        }
    }

    private fun arrayItem(
        holder: EZIoTDeviceFeatureViewHolder,
        featureItem: FeatureModel.FeatureItem
    ){
        val textView = getTextView()
        holder.featureViewLayout.addView(textView)
        textView.setOnClickListener {
            showInputDialog(featureItem.dataValue?.toString(),object : InputDialogCallback{
                override fun onSureClick(value: String) {
                    if(TextUtils.isEmpty(value)){
                        Utils.showToast(context,R.string.not_empty)
                        return
                    }
                    try {
                        val jsonArray = JSONObject.parseArray(value)
                        setDeviceFeatureProp(featureItem, jsonArray)
                    } catch (e : java.lang.Exception){
                        Utils.showToast(context,R.string.value_formate_error)
                    }
                }

            })
        }
    }

    private fun numberItem(
        holder: EZIoTDeviceFeatureViewHolder,
        featureItem: FeatureModel.FeatureItem
    ){
        val textView = getTextView()
        holder.featureViewLayout.addView(textView)
        textView.setOnClickListener {
            showInputDialog(featureItem.dataValue?.toString(),object : InputDialogCallback{
                override fun onSureClick(value: String) {
                    if(TextUtils.isEmpty(value)){
                        Utils.showToast(context,R.string.not_empty)
                        return
                    }
                    setDeviceFeatureProp(featureItem,value.toInt())
                }

            })
        }
    }

    private fun sliderItem(holder: EZIoTDeviceFeatureViewHolder,
                           featureItem: FeatureModel.FeatureItem){
        val jsonObject = featureItem.rawData.getJSONObject("schema")
        val max = jsonObject.getInt("maximum");
        val min = jsonObject.getInt("minimum")
        val slider = Slider(context)
        slider.valueTo = max.toFloat()
        slider.valueFrom = min.toFloat()
        slider.stepSize = 1f
        if(featureItem.dataValue != null){
            slider.value = (featureItem.dataValue as Int).toFloat()
        } else {
            slider.value = min.toFloat()
        }
        slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: Slider) {

            }

            override fun onStopTrackingTouch(slider: Slider) {
                Handler(Looper.getMainLooper()).postDelayed({
                    setDeviceFeatureProp(featureItem,slider.value.toInt())
                },200)
            }

        })
        holder.featureViewLayout.addView(slider)
    }

    private fun actionItem(holder : EZIoTDeviceFeatureViewHolder, actionItem: FeatureModel.ActionItem){
        val textView = getTextView()
        holder.featureViewLayout.addView(textView)
        textView.setOnClickListener {
            showInputDialog("",object : InputDialogCallback{
                override fun onSureClick(value: String) {
                    var request : Any? = null
                    if(!TextUtils.isEmpty(value)){
                        try {
                            val inputType = actionItem.rawData.optJSONObject("input")?.optJSONObject("schema")?.optString("type")
                            when(inputType){
                                "boolean" -> {
                                    if(value == "true" || value == "false"){
                                        request = value.toBoolean()
                                    } else {
                                        throw java.lang.Exception()
                                    }
                                }
                                "integer" -> {
                                    request = value.toInt()
                                }
                                "number" -> {
                                    request = value.toInt()
                                }
                                "object" -> {
                                    request = org.json.JSONObject(value)
                                }
                                "array" -> {
                                    request =  org.json.JSONArray(value)
                                }
                            }
                        } catch (e : java.lang.Exception){
                            Utils.showToast(context,R.string.value_formate_error)
                            return
                        }
                    }
                    setDeviceActionProp(actionItem,request)
                }

            })
        }
    }

    private fun setDeviceFeatureProp(featureItem: FeatureModel.FeatureItem , value : Any , delayDismiss : Boolean){
        Utils.showWaitDialog(context)
        val familyInfo = BaseResDataManager.familyInfo
        val ezIotDevice = EZIoTDeviceManager.getDeviceControl(
            familyInfo!!.id,
            deviceInfo.deviceSerial
        )

        ezIotDevice.setPropFeature(featureItem, value, object : IResultCallback {
            override fun onSuccess() {
                Utils.showToast(context,R.string.operateSuccess)
                deviceInfo.setFeature(featureItem.key, localIndex, value)
                EZIoTDeviceManager.saveDeviceInfo(familyInfo.id, deviceInfo)
                ezIoTRefreshDeviceListCallback.onLocalRefreshCallback()
                if(delayDismiss){
                    Handler(Looper.getMainLooper()).postDelayed({
                        Utils.dismissWaitDialog()
                    },1000)
                } else {
                    Utils.dismissWaitDialog()
                }
            }

            override fun onError(errorCode: Int, errorDesc: String?) {
                if(errorCode == 131030){
                    Utils.showToast(context,context.getString(R.string.no_permission_operate_device))
                } else {
                    Utils.showToast(context,errorDesc ?: (context.getString(R.string.operateFail) + errorCode.toString()))
                }
                if(delayDismiss){
                    Handler(Looper.getMainLooper()).postDelayed({
                        Utils.dismissWaitDialog()
                    },1000)
                } else {
                    Utils.dismissWaitDialog()
                }
            }

        })
    }


    private fun setDeviceFeatureProp(featureItem: FeatureModel.FeatureItem , value : Any){
        setDeviceFeatureProp(featureItem, value,false)
    }


    private fun setDeviceActionProp(actionItem: FeatureModel.ActionItem , value : Any?){
        val familyInfo = BaseResDataManager.familyInfo
        val ezIotDevice = EZIoTDeviceManager.getDeviceControl(
            familyInfo!!.id,
            deviceInfo.deviceSerial
        )
        ezIotDevice.setActionFeature(actionItem,value , object : IEZIoTResultCallback<String>{


            override fun onError(errorCode: Int, errorDesc: String?) {
                if(errorCode == 131030){
                    Utils.showToast(context,context.getString(R.string.no_permission_operate_device))
                } else {
                    Utils.showToast(context,errorDesc ?: (context.getString(R.string.operateFail) + errorCode.toString()))
                }
            }

            override fun onSuccess(t: String) {
                val operateSuccess = context.getString(R.string.operateSuccess)
                Utils.showToast(context, "$operateSuccess  返回数据：$t")
            }

        })
    }


    private fun getTextView() : TextView{
        val textView = TextView(context)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            textView.setTextColor(context.getColor(android.R.color.holo_blue_dark))
        }
        textView.text = context.getString(R.string.clickSend)
        return textView
    }

    private fun showInputDialog(value : String? ,inputDialogCallback: InputDialogCallback){
        val layoutView: View = LayoutInflater.from(context).inflate(R.layout.layout_input_dialog_edittext, null)
        val editTextView = layoutView.findViewById<EditText>(R.id.dialog_edit_text)
        editTextView.setText(value);
        try {
            val inputDialog : Dialog = AlertDialog.Builder(context)
                .setView(layoutView)
                .setPositiveButton(R.string.sure,
                    DialogInterface.OnClickListener { dialog, which ->
                        inputDialogCallback.onSureClick(editTextView.text.toString())
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, which ->

                    })
                .setCancelable(false)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showEnumDialog(data : MutableList<Any>,enumDialogCallback: EnumDialogCallback){
        val layoutView: View = LayoutInflater.from(context).inflate(R.layout.layout_list_dialog, null)
        val recyclerView = layoutView.findViewById<RecyclerView>(R.id.listRv)
        val cancelTv = layoutView.findViewById<TextView>(R.id.cancelTv)
        recyclerView.layoutManager = LinearLayoutManager(context)
        var enumDialog : Dialog? = null
        recyclerView.adapter = EZIoTEnumListDialogAdapter(data,context,object : EZIoTEnumListDialogAdapter.EnumDialogItemClickListener{
            override fun onEnumDialogItemClick(value: Any) {
                enumDialogCallback.onEnumDialogItemClick(value)
                enumDialog!!.dismiss()
            }

        })
        cancelTv.setOnClickListener {
            enumDialog!!.dismiss()
        }
        try {
            enumDialog = AlertDialog.Builder(context)
                .setView(layoutView)
                .setCancelable(false)
                .show()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showCommonFeatureInfoDialog(featureItem: FeatureModel.FeatureItem){
        val message = StringBuilder()
        message.append("key : " + featureItem.key.substring(0,featureItem.key.length - 2))
        message.append("\n").append("\n")
        message.append("type : " + featureItem.dataType)
        message.append("\n").append("\n")
        message.append("value : " + featureItem.dataValue)
        message.append("\n").append("\n")
        message.append("version : " + featureItem.rawData.optString("version"))
        message.append("\n").append("\n")
        message.append("access : " + featureItem.rawData.optString("access"))
        message.append("\n").append("\n")
        message.append("schema : " + featureItem.rawData.optString("schema"))
        AlertDialog.Builder(context)
            .setMessage(message)
            .setCancelable(true)
            .show()
    }

    private fun showCommonFeatureInfoDialog(actionItem : FeatureModel.ActionItem){
        val message = StringBuilder()
        message.append("key : " + actionItem.key.substring(0,actionItem.key.length - 2))
        message.append("\n").append("\n")
        message.append("version : " + actionItem.rawData.optString("version"))
        message.append("\n").append("\n")
        message.append("access : " + actionItem.rawData.optString("access"))
        message.append("\n").append("\n")
        message.append("input : " + actionItem.rawData.optString("input"))
        message.append("\n").append("\n")
        message.append("output : " + actionItem.rawData.optString("output"))
        AlertDialog.Builder(context)
            .setMessage(message)
            .setCancelable(true)
            .show()
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    class EZIoTDeviceFeatureViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val featureNameTv : TextView = view.findViewById(R.id.featureNameTv)
        val featureKeyTv : TextView = view.findViewById(R.id.featureKeyTv)
        val featureValueTv : TextView = view.findViewById(R.id.featureValueTv)
        val featureTypeTv : TextView = view.findViewById(R.id.featureTypeTv)
        val featureViewLayout : ViewGroup = view.findViewById(R.id.featureViewLayout)
        val deviceItemLayout : ViewGroup = view.findViewById(R.id.deviceItemLayout)
        val controlTv : TextView = view.findViewById(R.id.controlTv)
    }

    interface InputDialogCallback{

        fun onSureClick(value : String)

    }

    interface EnumDialogCallback{
        fun onEnumDialogItemClick(value : Any)
    }

}
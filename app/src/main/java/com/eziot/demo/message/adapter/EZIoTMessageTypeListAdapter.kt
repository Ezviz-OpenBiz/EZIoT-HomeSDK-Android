package com.eziot.demo.message.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.eziot.demo.base.IntentContent
import com.eziot.demo.family.EZIoTFamilyAcceptInviteActivity
import com.eziot.demo.message.EZIoTMessageDetailActivity
import com.eziot.iotsdkdemo.R
import com.eziot.message.model.EZIoTMsgInfo
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.logging.Handler

class EZIoTMessageTypeListAdapter(
    private var context: Context,
    private var messageInfos: List<EZIoTMsgInfo>
) :
    RecyclerView.Adapter<EZIoTMessageTypeListAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.eziot_message_list_adapter, null)
        return MessageViewHolder(view)

    }

    override fun getItemCount(): Int {
        return messageInfos.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.textTitle.text = messageInfos[position].title
        holder.textContent.text = messageInfos[position].detail
        returnBitmap(holder, messageInfos[position].defaultPic)

        holder.view.setOnClickListener {
            val ezIoTMsgInfo = messageInfos[position]
            if (ezIoTMsgInfo.detailLink != null && ezIoTMsgInfo.detailLink.contains("FamilyDetailPage")) {
                val jsonObject = org.json.JSONObject(ezIoTMsgInfo.detailLink)
                val familyId = jsonObject.getInt("familyId")
                val memberId = jsonObject.getString("memberId")
                val intent = Intent(context, EZIoTFamilyAcceptInviteActivity::class.java)
                intent.putExtra(IntentContent.FAMILY_ID, familyId)
                intent.putExtra(IntentContent.MEMBER_ID, memberId)
                context.startActivity(intent)
            } else {
                var intent = Intent(context, EZIoTMessageDetailActivity::class.java)
                intent.putExtra("viewUrl", messageInfos[position].defaultPic)
                intent.putExtra("viewTitle", messageInfos[position].title)
                intent.putExtra("viewContent", messageInfos[position].detail)
                intent.putExtra("timeInfo", ezIoTMsgInfo.timeStr)
                intent.putExtra("deviceInfo", ezIoTMsgInfo.from)
                context.startActivity(intent)
            }
        }
    }

    fun returnBitmap(holder: MessageViewHolder, url: String) {
        Thread(Runnable {
            var myFileUrl: URL? = null
            var bitmap: Bitmap? = null
            myFileUrl = URL(url)
            var conn: HttpURLConnection = myFileUrl.openConnection() as HttpURLConnection
            conn.doInput = true
            conn.connect()
            var input: InputStream = conn.inputStream
            bitmap = BitmapFactory.decodeStream(input)
            input.close()
            android.os.Handler(Looper.getMainLooper()).post(Runnable {
                holder.imageView.setImageBitmap(bitmap)
            })
        }).start()
    }

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textTitle: TextView = view.findViewById<TextView>(R.id.message_item_title)
        var textContent: TextView = view.findViewById<TextView>(R.id.message_item_content)
        var imageView: ImageView = view.findViewById<ImageView>(R.id.image_view_uri)
        var view: View = view.findViewById<LinearLayout>(R.id.message_item)
    }
}
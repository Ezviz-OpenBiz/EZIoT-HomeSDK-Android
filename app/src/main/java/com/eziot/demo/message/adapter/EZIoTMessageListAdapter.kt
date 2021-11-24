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
import com.eziot.demo.message.EZIoTMessageTypeListActivity
import com.eziot.iotsdkdemo.R
import com.eziot.message.model.EZIoTMsgCategoryInfo
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class EZIoTMessageListAdapter(private var context: Context, private var messageTypes: List<EZIoTMsgCategoryInfo>):
    RecyclerView.Adapter<EZIoTMessageListAdapter.TypeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.eziot_message_type_adapter, null)
        return TypeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messageTypes.size
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        holder.textView.text = messageTypes[position].name
        returnBitmap(holder, messageTypes[position].pic)
        holder.view.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(p0?.id == R.id.message_item){
                    val intent = Intent(context, EZIoTMessageTypeListActivity::class.java)
                    if(messageTypes[position].type == 24){
                        intent.putExtra("messageType", 0)
                    }else if(messageTypes[position].type == 21){
                        intent.putExtra("messageType", 1)
                    }
                    context.startActivity(intent)
                }
            }

        })
    }

    private fun returnBitmap(holder: TypeViewHolder, url: String) {
        Thread(Runnable {
            var myFileUrl: URL? = null
            var bitmap: Bitmap? = null
            myFileUrl = URL(url)
            var conn: HttpURLConnection = myFileUrl.openConnection() as HttpURLConnection
            conn.doInput = true
            conn.connect()
            var input : InputStream = conn.inputStream
            bitmap = BitmapFactory.decodeStream(input)
            input.close()
            android.os.Handler(Looper.getMainLooper()).post(Runnable {
                holder.imageView.setImageBitmap(bitmap)
            })
        }).start()
    }

    class TypeViewHolder(view: View): RecyclerView.ViewHolder(view){
        var imageView: ImageView = view.findViewById(R.id.image_type_view_uri)
        var textView: TextView = view.findViewById(R.id.message_type_item_content)
        var view: LinearLayout = view.findViewById(R.id.message_item)
    }
}
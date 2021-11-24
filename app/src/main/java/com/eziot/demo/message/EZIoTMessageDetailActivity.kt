package com.eziot.demo.message

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.eziot.demo.message.adapter.EZIoTMessageTypeListAdapter
import com.eziot.iotsdkdemo.R
import org.w3c.dom.Text
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class EZIoTMessageDetailActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eziot_message_detail)
        initView()
        initData()
    }

    private fun initView(){
        imageView = findViewById(R.id.message_item_detail_image)
        textViewTitle = findViewById(R.id.message_item_detail_title)
        textViewContent = findViewById(R.id.message_item_detail_content)
        mTimeInfo = findViewById(R.id.message_item_time_info)
        mDeviceInfo = findViewById(R.id.message_item_device_info)
        findViewById<View>(R.id.message_detail_go_back).setOnClickListener(this)
    }

    private fun initData(){
        textViewTitle?.text = intent.getStringExtra("viewTitle")
        textViewContent?.text = intent.getStringExtra("viewContent")
        if(textViewContent?.text.toString().isEmpty()){
            textViewContent?.visibility = View.GONE
        }
        if(!intent.getStringExtra("timeInfo")?.isEmpty()!!){
            mTimeInfo?.text = intent.getStringExtra("timeInfo")
        }
        if(!intent.getStringExtra("deviceInfo")?.isEmpty()!!){
            mDeviceInfo?.text = intent.getStringExtra("deviceInfo")
        }
        returnBitmap(imageView, intent.getStringExtra("viewUrl"))
    }

    private fun returnBitmap(imageView: ImageView?, url: String?) {
        Thread(Runnable {
            var myFileUrl: URL?
            var bitmap: Bitmap? = null
            myFileUrl = URL(url)
            var conn: HttpURLConnection = myFileUrl.openConnection() as HttpURLConnection
            conn.doInput = true
            conn.connect()
            var input : InputStream = conn.inputStream
            bitmap = BitmapFactory.decodeStream(input)
            input.close()
            android.os.Handler(Looper.getMainLooper()).post(Runnable {
                imageView?.setImageBitmap(bitmap)
            })
        }).start()
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.message_detail_go_back -> {
                finish()
            }
        }
    }

    private var imageView: ImageView? = null
    private var textViewTitle: TextView? = null
    private var textViewContent: TextView? = null
    private var mTimeInfo: TextView? = null
    private var mDeviceInfo: TextView? = null

}
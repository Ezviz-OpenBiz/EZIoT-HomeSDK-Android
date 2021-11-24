package com.eziot.demo.utils

import android.content.Context
import android.text.TextUtils
import android.widget.Toast

object CommonUtils {


    fun showToast(context: Context?, text: CharSequence?) {
        showToast(context, text, Toast.LENGTH_LONG)
    }

    fun showToast(context: Context?, text: CharSequence?, duration: Int) {
        if (context == null) {
            return
        }
        if (!TextUtils.isEmpty(text)) {
            Toast.makeText(context, text, duration).show()
        }
    }


}
package com.eziot.demo.base;

import android.app.Application;

import androidx.annotation.NonNull;

import com.eziot.demo.utils.EZIoTGlobalVariable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    /** 系统默认的UncaughtException处理类 */
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public void init(Application application){
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        String result = info.toString();
        printWriter.close();
        EZIoTGlobalVariable.LOCAL_CATCH.set(result);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {

        }
        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());

    }

}

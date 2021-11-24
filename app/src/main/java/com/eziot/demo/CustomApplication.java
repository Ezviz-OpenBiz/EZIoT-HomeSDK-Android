package com.eziot.demo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.eziot.common.http.callback.IEZIoTLoginAuthCallback;
import com.eziot.common.http.callback.IEZIoTRefreshSessionCallback;
import com.eziot.common.model.BaseParams;
import com.eziot.demo.base.CrashHandler;
import com.eziot.sdk.EZIoTSDK;
import com.eziot.user.EZIotUserManager;

public class CustomApplication extends Application {

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        CrashHandler crashHandler = new CrashHandler();
        crashHandler.init(this);
        BaseParams baseParams = new BaseParams();
//        EZIoTSDK.INSTANCE.init(this,baseParams);
        EZIotUserManager.INSTANCE.initWithParams(this, baseParams);

        EZIotUserManager.INSTANCE.setLoginAuthListener(new IEZIoTLoginAuthCallback() {
            @Override
            public void onAuthException() {

            }
        });

        EZIotUserManager.INSTANCE.setSessionUpdateListener(new IEZIoTRefreshSessionCallback() {
            @Override
            public void onRefreshSession(@NonNull String session, @NonNull String rfSession) {

            }
        });
    }


}

package com.eziot.demo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;


import com.ezdatasource.simple.EZDataSource;
import com.eziot.common.http.callback.IEZIoTLoginAuthCallback;
import com.eziot.common.http.callback.IEZIoTRefreshSessionCallback;
import com.eziot.common.model.BaseParams;
import com.eziot.demo.base.CrashHandler;
import com.eziot.demo.utils.ConfigInstance;
import com.eziot.demo.utils.EZIoTGlobalVariable;
//import com.eziot.demo.ysmp.EZIoTModelCommonImpl;
//import com.eziot.rnmodule.EZIoTRnManager;
import com.eziot.sdk.EZIoTSDK;
import com.eziot.user.EZIotUserManager;

import io.realm.Realm;

public class CustomApplication extends Application {

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        EZIoTGlobalVariable.init(this);
        CrashHandler crashHandler = new CrashHandler();
        crashHandler.init(this);
        BaseParams baseParams = new BaseParams();
        baseParams.setAppId(ConfigInstance.INSTANCE.getAppId());
        baseParams.setApiDomain(ConfigInstance.INSTANCE.getApiDomain());
        EZIoTSDK.INSTANCE.init(this,baseParams);
        EZIoTSDK.INSTANCE.setDebug(true);
//        EZIoTRnManager.initReactNative(this,new EZIoTModelCommonImpl());
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

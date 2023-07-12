package com.eziot.demo.utils

object ConfigInstance {

    fun getAppId() : String{
        return EZIoTGlobalVariable.APP_ID.get();
    }

    fun setAppId(appId : String){
        EZIoTGlobalVariable.APP_ID.set(appId)
    }

    fun setApiDomain(apiDomain : String){
        EZIoTGlobalVariable.API_DOMAIN.set(apiDomain)
    }

    fun getApiDomain() : String{
        return EZIoTGlobalVariable.API_DOMAIN.get()
    }

}
package com.eziot.demo.base

import com.eziot.family.model.family.EZIoTFamilyInfo
import com.eziot.family.model.group.EZIoTRoomInfo
import com.eziot.user.model.account.EZIoTUserBizType
import com.eziot.user.model.account.EZIoTUserContactModifyParam
import com.eziot.user.model.account.EZIoTUserLoginParam
import com.eziot.wificonfig.model.fastap.EZIoTAPDevInfo
import com.eziot.wificonfig.model.fastap.EZIoTConfigTokenInfo

object BaseResDataManager {

    var familyInfo : EZIoTFamilyInfo? = null

    var roomInfo : EZIoTRoomInfo? = null

    var configTokenInfo : EZIoTConfigTokenInfo? = null

    var APDevInfo: EZIoTAPDevInfo? = null


    var oldBizType: EZIoTUserBizType? = null
    var newBizType: EZIoTUserBizType? = null
    var mEZIoTUserContactModifyParam: EZIoTUserContactModifyParam? = null

    var ezIotLoginParam: EZIoTUserLoginParam? = null

}
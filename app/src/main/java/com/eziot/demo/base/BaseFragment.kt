package com.eziot.demo.base

import androidx.fragment.app.Fragment
import com.eziot.common.utils.GlobalVariable

open class BaseFragment : Fragment() {

    fun isHomeFamily() : Boolean{
        return BaseResDataManager.familyInfo?.creator.equals(GlobalVariable.USER_ID.get())
    }

}
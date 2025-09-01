package com.film.television.utils

import android.content.Context
import com.umeng.commonsdk.UMConfigure

object UMUtil {

    const val APP_KEY = "66545d82cac2a664de3d6445"
    const val CHANNEL = "baidu"

    fun preInit(appContext: Context) {
        UMConfigure.preInit(appContext, UMUtil.APP_KEY, UMUtil.CHANNEL)
    }

    fun init(appContext: Context) {
        UMConfigure.init(
            appContext,
            APP_KEY,
            CHANNEL,
            UMConfigure.DEVICE_TYPE_PHONE,
            ""
        )
    }

}
package com.film.television

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.film.television.activity.SplashActivity
import com.film.television.model.SdkReportConfigBody
import com.film.television.repository.ContentRepository
import com.film.television.utils.AdUtil
import com.film.television.utils.ApplicationData
import com.film.television.utils.Constants
import com.film.television.utils.DataStoreUtil
import com.film.television.utils.DeviceUtil
import com.film.television.utils.RouteUtil
import com.film.television.utils.TokenUtil
import com.film.television.utils.UMUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class MyApplication : Application() {
    private var activityCount = 0
    private var isAppInForeground = true
    private val activityRefList = mutableListOf<WeakReference<Activity>>()

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        UMUtil.preInit(this)
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(
                activity: Activity,
                savedInstanceState: Bundle?
            ) {
                activityRefList.add(WeakReference(activity))
            }

            override fun onActivityStarted(activity: Activity) {
                if (activityCount == 0 && !isAppInForeground) {
                    onAppForeground()
                    isAppInForeground = true
                }
                activityCount += 1
            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
                activityCount -= 1
                if (activityCount == 0) {
                    isAppInForeground = false
                }
            }

            override fun onActivitySaveInstanceState(
                activity: Activity,
                outState: Bundle
            ) {

            }

            override fun onActivityDestroyed(activity: Activity) {
                val newActivityRefList = mutableListOf<WeakReference<Activity>>()
                for (activityRef in activityRefList) {
                    val act = activityRef.get()
                    if (act != null && act != activity) {
                        newActivityRefList.add(WeakReference(act))
                    }
                }
                activityRefList.clear()
                activityRefList.addAll(newActivityRefList)
            }
        })
        mainScope.launch {
            var token = TokenUtil.getToken()
            if (token != null) {
                onGetToken(token)
            } else {
                delay(800L)
                token = TokenUtil.getToken()
                if (token != null) {
                    onGetToken(token)
                } else {
                    throw RuntimeException("Unable to get token.")
                }
            }
        }
    }

    private suspend fun onGetToken(token: String) {
        val body = SdkReportConfigBody(
            Constants.SDK_REPORT_CONFIG_QUERY,
            DeviceUtil.getPackageName(),
            token
        )
        val resp = ContentRepository.querySdkReportConfig(body)
        if (resp.data == "true") {
            AdUtil.init(this)
            ApplicationData.setAdSdkInitNeeded(false)
        } else {
            Constants.IS_IN_REVIEW = true
            if (DataStoreUtil.getGranted()) {
                AdUtil.init(this)
                UMUtil.init(this)
                ApplicationData.setAdSdkInitNeeded(false)
            } else {
                ApplicationData.setAdSdkInitNeeded(true)
            }
        }
    }

    private fun onAppForeground() {
        val lastActivity = activityRefList.lastOrNull()?.get()
        if (lastActivity != null && lastActivity !is SplashActivity) {
            RouteUtil.restartApp(lastActivity)
        }
    }

    companion object {
        lateinit var INSTANCE: MyApplication
    }

}
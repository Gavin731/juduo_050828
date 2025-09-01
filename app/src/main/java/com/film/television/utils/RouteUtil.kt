package com.film.television.utils

import android.util.Log
import android.app.Activity
import android.content.Context
import android.content.Intent
import com.film.television.activity.SplashActivity
import com.film.television.activity.TeenModeContentActivity
import com.film.television.activity.VideoDetailActivity
import com.film.television.activity.WebActivity
import com.film.television.model.VideoBean
import kotlin.system.exitProcess

object RouteUtil {

    fun goToVideoDetailActivity(context: Context, videoBean: VideoBean) {
        val intent = Intent(context, VideoDetailActivity::class.java)
        intent.putExtra(Constants.KEY_VIDEO_BEAN, videoBean)
        context.startActivity(intent)
    }

    fun goToWebActivity(context: Context, title: String?, url: String) {
        val intent = Intent(context, WebActivity::class.java)
        intent.putExtra(Constants.KEY_TITLE, title)
        intent.putExtra(Constants.KEY_URL, url)
        context.startActivity(intent)
    }

    fun goToTeenModeContentActivity(context: Context) {
        context.startActivity(Intent(context, TeenModeContentActivity::class.java))
    }

    fun restartApp(activity: Activity) {
        try {
            activity.finishAffinity()
            val intent = Intent(activity, SplashActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            activity.startActivity(intent)
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(0)
        } catch (e: Exception) {
            Log.e("lytest", "restartApp throws exception: $e")
        }
    }

}
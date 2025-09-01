package com.film.television.utils

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.core.widget.NestedScrollView
import com.film.television.MyApplication
import kotlin.math.roundToInt

object UIUtil {

    fun dp2px(context: Context, dp: Float): Int {
        return (context.resources.displayMetrics.density * dp).roundToInt()
    }

    fun px2dp(context: Context, px: Int): Float {
        return px / context.resources.displayMetrics.density
    }

    fun getScreenWidthHeightWithoutInsets(context: Context): Pair<Int, Int> {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds
            bounds.width() to bounds.height()
        } else {
            val displayMetrics = context.resources.displayMetrics
            displayMetrics.widthPixels to displayMetrics.heightPixels
        }
    }

    val feedsWidth: Int by lazy {
        getScreenWidthHeightWithoutInsets(MyApplication.INSTANCE).first - dp2px(
            MyApplication.INSTANCE,
            32f
        )
    }
    val feedsHeight: Int by lazy { (feedsWidth * 6f / 17).toInt() }

}
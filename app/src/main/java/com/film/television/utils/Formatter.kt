package com.film.television.utils

import android.content.Context
import android.text.format.DateUtils
import android.text.format.Formatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Formatter {

    fun formatFileSize(context: Context, sizeBytes: Long): String {
        return Formatter.formatFileSize(context, sizeBytes)
    }

    /**
     * @param time millisecond
     */
    fun formatTime(time: Long): String {
        val timeSeconds = time / 1000

        val hours = timeSeconds / 3600
        val minutes = (timeSeconds % 3600) / 60
        val seconds = timeSeconds % 60

        return if (hours > 0) {
            String.format(Locale.CHINA, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.CHINA, "%02d:%02d", minutes, seconds)
        }
    }

    fun formatCalendar(calendar: Calendar): String {
        return "${calendar[Calendar.YEAR]}-${calendar[Calendar.MONTH] + 1}-${calendar[Calendar.DAY_OF_MONTH]}"
    }

}
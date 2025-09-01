package com.film.television.utils

import android.content.Context
import android.os.Environment
import android.text.format.Formatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object CacheUtil {

    suspend fun getCacheSize(context: Context): Long = withContext(Dispatchers.IO) {
        var size: Long = 0
        size += getFileSize(context.cacheDir)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            size += getFileSize(context.externalCacheDir!!)
        }
        size
    }

    private fun getFileSize(file: File): Long {
        if (file.isDirectory) {
            var size: Long = 0
            val files = file.listFiles() ?: return 0L
            for (f in files) {
                size += getFileSize(f)
            }
            return size
        } else {
            return file.length()
        }
    }

    suspend fun clearCache(context: Context): Boolean = withContext(Dispatchers.IO) {
        if (!deleteFile(context.cacheDir)) {
            return@withContext false
        }
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            if (!deleteFile(context.externalCacheDir!!)) {
                return@withContext false
            }
        }
        true
    }

    private fun deleteFile(file: File): Boolean {
        if (file.isDirectory) {
            val files = file.listFiles()
            if (files != null) {
                for (f in files) {
                    if (!deleteFile(f)) {
                        return false
                    }
                }
            }
        }
        return file.delete()
    }

}
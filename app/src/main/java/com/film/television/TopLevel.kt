package com.film.television

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.film.television.utils.DataStoreUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "juduo")

fun showShortToast(text: String) {
    Toast.makeText(MyApplication.INSTANCE, text, Toast.LENGTH_SHORT).show()
}

fun showLongToast(text: String) {
    Toast.makeText(MyApplication.INSTANCE, text, Toast.LENGTH_LONG).show()
}

suspend fun isTeenModeEnabled(): Boolean {
    return !DataStoreUtil.getPassword().isNullOrEmpty()
}

val mainHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
package com.film.television.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.film.television.MyApplication
import com.film.television.dataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

object DataStoreUtil {
    private val appContext by lazy { MyApplication.INSTANCE }
    private val SEARCH_HISTORY = stringPreferencesKey("search_history")
    private val TOKEN_AND_EXPIRED_TIME = stringPreferencesKey("token_and_expired_time")
    private val GRANTED = booleanPreferencesKey("granted")
    private val SUPPORTED = booleanPreferencesKey("supported")
    private val PASSWORD = stringPreferencesKey("password")
    private val ENV_INFO_POSTED = booleanPreferencesKey("env_info_posted")
    private val HOT_SCROLL_VIEW_INTERSTITIAL_AD_SHOWN =
        booleanPreferencesKey("hot_scroll_view_interstitial_ad_shown")
    private val gson by lazy { Gson() }
    private val stringListToken by lazy { object : TypeToken<List<String>>() {} }

    fun getSearchHistoryFlow(): Flow<List<String>> {
        return appContext.dataStore.data.map { prefs ->
            gson.fromJson(prefs[SEARCH_HISTORY], stringListToken) ?: emptyList()
        }
    }

    suspend fun addSearchHistory(input: String) {
        appContext.dataStore.edit { prefs ->
            val history = gson.fromJson(prefs[SEARCH_HISTORY], stringListToken) ?: emptyList()
            if (input in history) {
                val inputIndex = history.indexOf(input)
                if (inputIndex != 0) {
                    val newHistory = mutableListOf<String>()
                    newHistory.addAll(history)
                    newHistory.removeAt(inputIndex)
                    newHistory.add(0, input)
                    prefs[SEARCH_HISTORY] = gson.toJson(newHistory)
                }
            } else {
                val newHistory = mutableListOf<String>()
                newHistory.add(input)
                newHistory.addAll(history)
                if (newHistory.size <= 10) {
                    prefs[SEARCH_HISTORY] = gson.toJson(newHistory)
                } else {
                    prefs[SEARCH_HISTORY] = gson.toJson(newHistory.subList(0, 10))
                }
            }
        }
    }

    suspend fun clearSearchHistory() {
        appContext.dataStore.edit { prefs ->
            prefs[SEARCH_HISTORY] = ""
        }
    }

    suspend fun getTokenAndExpiredTime(): String? {
        return appContext.dataStore.data.map { prefs ->
            prefs[TOKEN_AND_EXPIRED_TIME]
        }.firstOrNull()
    }

    suspend fun setTokenAndExpiredTime(s: String) {
        appContext.dataStore.edit { prefs ->
            prefs[TOKEN_AND_EXPIRED_TIME] = s
        }
    }

    suspend fun getGranted(): Boolean {
        return appContext.dataStore.data.map { prefs ->
            prefs[GRANTED]
        }.firstOrNull() == true
    }

    suspend fun setGranted() {
        appContext.dataStore.edit { prefs ->
            prefs[GRANTED] = true
        }
    }

    suspend fun getSupported(): Boolean {
        return appContext.dataStore.data.map { prefs ->
            prefs[SUPPORTED]
        }.firstOrNull() == true
    }

    suspend fun setSupported() {
        appContext.dataStore.edit { prefs ->
            prefs[SUPPORTED] = true
        }
    }

    suspend fun getPassword(): String? {
        return appContext.dataStore.data.map { prefs ->
            prefs[PASSWORD]
        }.firstOrNull()
    }

    suspend fun setPassword(password: String) {
        appContext.dataStore.edit { prefs ->
            prefs[PASSWORD] = password
        }
    }

    suspend fun setEverydayTeenModeUsableTime(dateString: String, time: Long) {
        appContext.dataStore.edit { prefs ->
            prefs[longPreferencesKey(dateString)] = time
        }
    }

    suspend fun getEverydayTeenModeUsableTime(dateString: String): Long {
        return appContext.dataStore.data.map { prefs ->
            prefs[longPreferencesKey(dateString)] ?: Constants.TEEN_MODE_MAX_USE_TIME
        }.first()
    }

    suspend fun getEnvInfoPosted(): Boolean {
        return appContext.dataStore.data.map { prefs ->
            prefs[ENV_INFO_POSTED] == true
        }.first()
    }

    suspend fun setEnvInfoPosted() {
        appContext.dataStore.edit { prefs ->
            prefs[ENV_INFO_POSTED] = true
        }
    }

    suspend fun getHotScrollViewInterstitialAdShown(): Boolean {
        return appContext.dataStore.data.map { prefs ->
            prefs[HOT_SCROLL_VIEW_INTERSTITIAL_AD_SHOWN] == true
        }.first()
    }

    suspend fun setHotScrollViewInterstitialAdShown() {
        appContext.dataStore.edit { prefs ->
            prefs[HOT_SCROLL_VIEW_INTERSTITIAL_AD_SHOWN] = true
        }
    }

}
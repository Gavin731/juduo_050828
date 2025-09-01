package com.film.television.utils

import com.film.television.repository.ContentRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TokenUtil {
    private val mutex = Mutex()

    suspend fun getToken(): String? {
        mutex.withLock {
            val tokenAndExpiredTime = DataStoreUtil.getTokenAndExpiredTime()
            var token: String? = null
            if (tokenAndExpiredTime == null) {
                token = getAndSetToken()
            } else {
                val temp = tokenAndExpiredTime.split(Constants.DELIMITER)
                token = temp[0]
                val expiredTime = temp[1]
                val chinaFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.CHINA)
                val expiredDate = chinaFormat.parse(expiredTime)
                val currentDate = Date()
                if (currentDate.after(expiredDate)) {
                    token = getAndSetToken()
                }
            }
            return token
        }
    }

    private suspend fun getAndSetToken(): String? {
        val tokenResp = ContentRepository.getToken()
        if (tokenResp.code == 200 && tokenResp.data != null) {
            DataStoreUtil.setTokenAndExpiredTime("${tokenResp.data.appToken}${Constants.DELIMITER}${tokenResp.data.expiredTime}")
            return tokenResp.data.appToken
        }
        return null
    }
}
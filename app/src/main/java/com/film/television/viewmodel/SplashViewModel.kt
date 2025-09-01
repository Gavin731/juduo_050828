package com.film.television.viewmodel

import androidx.lifecycle.ViewModel
import com.film.television.model.ConfigQueryBody
import com.film.television.model.ConfigQueryResp
import com.film.television.repository.ContentRepository
import com.film.television.utils.Constants
import com.film.television.utils.DeviceUtil

class SplashViewModel : ViewModel() {

    suspend fun queryConfig(token: String): ConfigQueryResp {
        return ContentRepository.queryConfig(
            ConfigQueryBody(
                Constants.CONFIG_QUERY,
                DeviceUtil.getPackageName(),
                token,
                ConfigQueryBody.Params(null, DeviceUtil.getSimState())
            )
        )
    }
}
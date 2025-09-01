package com.film.television.viewmodel

import androidx.lifecycle.ViewModel
import com.film.television.model.GeneralVideoInfoBody
import com.film.television.model.GeneralVideoInfoResp
import com.film.television.model.HomepageVideoBody
import com.film.television.model.HomepageVideoResp
import com.film.television.repository.ContentRepository
import com.film.television.utils.Constants
import com.film.television.utils.DeviceUtil

class HomeViewModel : ViewModel() {

    suspend fun queryGeneralVideoInfo(token: String): GeneralVideoInfoResp {
        return ContentRepository.queryGeneralVideoInfo(
            GeneralVideoInfoBody(
                Constants.GENERAL_VIDEO_INFO,
                DeviceUtil.getPackageName(),
                token
            )
        )
    }

    suspend fun queryHomepageVideo(
        token: String,
        moduleType: String = "POPULAR"
    ): HomepageVideoResp {
        return ContentRepository.queryHomepageVideo(
            HomepageVideoBody(
                Constants.HOMEPAGE_VIDEO_QUERY,
                DeviceUtil.getPackageName(),
                token,
                HomepageVideoBody.Params(moduleType)
            )
        )
    }

}
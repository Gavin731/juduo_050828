package com.film.television.repository

import com.film.television.model.ConfigQueryBody
import com.film.television.model.ConfigQueryResp
import com.film.television.model.EnvInfoBody
import com.film.television.model.GeneralVideoInfoBody
import com.film.television.model.HomepageVideoBody
import com.film.television.model.PagedVideoQueryBody
import com.film.television.model.SdkReportConfigBody
import com.film.television.model.TokenBody
import com.film.television.model.VideoSearchBody
import com.film.television.network.RetrofitNetwork
import com.film.television.utils.Constants
import com.film.television.utils.DeviceUtil

object ContentRepository {

    suspend fun getToken() = RetrofitNetwork.contentService.getToken(
        TokenBody(
            Constants.TOKEN,
            DeviceUtil.getPackageName()
        )
    )

    suspend fun postEnvInfo(
        token: String,
        deviceId: String,
        osVersion: String,
        imei: String?,
        androidId: String?,
        oaid: String?,
        meid: String?,
        mac: String?,
        systemInfo: String?,
        ipAddress: String?,
        simState: String?,
        manufacture: String?,
        model: String?,
        brand: String?,
        board: String?,
        device: String?,
        hardware: String?,
        sdkInt: String?
    ) = RetrofitNetwork.contentService.postEnvInfo(
        EnvInfoBody(
            Constants.ENV_INFO,
            DeviceUtil.getPackageName(),
            token,
            EnvInfoBody.Params(
                "ystj",
                deviceId,
                osVersion,
                imei,
                androidId,
                oaid,
                meid,
                mac,
                systemInfo,
                ipAddress,
                simState,
                manufacture,
                model,
                brand,
                board,
                device,
                hardware,
                osVersion,
                sdkInt
            )
        )
    )

    suspend fun queryConfig(body: ConfigQueryBody) =
        RetrofitNetwork.contentService.queryConfig(body)

    suspend fun queryGeneralVideoInfo(body: GeneralVideoInfoBody) =
        RetrofitNetwork.contentService.queryGeneralVideoInfo(body)

    suspend fun queryHomepageVideo(body: HomepageVideoBody) =
        RetrofitNetwork.contentService.queryHomepageVideo(body)

    suspend fun queryPagedVideo(body: PagedVideoQueryBody) =
        RetrofitNetwork.contentService.queryPagedVideo(body)

    suspend fun searchVideo(body: VideoSearchBody) =
        RetrofitNetwork.contentService.searchVideo(body)

    suspend fun querySdkReportConfig(body: SdkReportConfigBody) =
        RetrofitNetwork.contentService.querySdkReportConfig(body)
}
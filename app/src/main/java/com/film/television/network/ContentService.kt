package com.film.television.network

import com.film.television.model.ConfigQueryBody
import com.film.television.model.ConfigQueryResp
import com.film.television.model.EnvInfoBody
import com.film.television.model.EnvInfoResp
import com.film.television.model.GeneralVideoInfoBody
import com.film.television.model.GeneralVideoInfoResp
import com.film.television.model.HomepageVideoBody
import com.film.television.model.HomepageVideoResp
import com.film.television.model.PagedVideoQueryBody
import com.film.television.model.PagedVideoQueryResp
import com.film.television.model.SdkReportConfigBody
import com.film.television.model.SdkReportConfigResp
import com.film.television.model.TokenBody
import com.film.television.model.TokenResp
import com.film.television.model.VideoSearchBody
import com.film.television.model.VideoSearchResp
import retrofit2.http.Body
import retrofit2.http.POST

interface ContentService {

    @POST(ConnectionConstants.CONTENT_API)
    suspend fun getToken(
        @Body body: TokenBody
    ): TokenResp

    @POST(ConnectionConstants.CONTENT_API)
    suspend fun postEnvInfo(@Body body: EnvInfoBody): EnvInfoResp

    @POST(ConnectionConstants.CONTENT_API)
    suspend fun queryConfig(@Body body: ConfigQueryBody): ConfigQueryResp

    @POST(ConnectionConstants.CONTENT_API)
    suspend fun queryGeneralVideoInfo(@Body body: GeneralVideoInfoBody): GeneralVideoInfoResp

    @POST(ConnectionConstants.CONTENT_API)
    suspend fun queryHomepageVideo(@Body body: HomepageVideoBody): HomepageVideoResp

    @POST(ConnectionConstants.CONTENT_API)
    suspend fun queryPagedVideo(@Body body: PagedVideoQueryBody): PagedVideoQueryResp

    @POST(ConnectionConstants.CONTENT_API)
    suspend fun searchVideo(@Body body: VideoSearchBody): VideoSearchResp

    @POST(ConnectionConstants.CONTENT_API)
    suspend fun querySdkReportConfig(@Body body: SdkReportConfigBody): SdkReportConfigResp
}
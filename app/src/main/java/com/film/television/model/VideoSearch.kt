package com.film.television.model

import com.google.gson.annotations.SerializedName

data class VideoSearchBody(
    @SerializedName("methodType") val methodType: String,
    @SerializedName("appName") val appName: String,
    @SerializedName("appToken") val appToken: String,
    @SerializedName("params") val params: Params
) {
    data class Params(
        @SerializedName("title") val title: String,
        @SerializedName("pageNum") val pageNum: Int,
        @SerializedName("pageSize") val pageSize: Int
    )
}

data class VideoSearchResp(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: Data
) {
    data class Data(
        @SerializedName("pageNum") val pageNum: Int,
        @SerializedName("pageSize") val pageSize: Int,
        @SerializedName("totalPages") val totalPages: Int,
        @SerializedName("total") val total: Int,
        @SerializedName("records") val records: List<VideoBean>
    )
}
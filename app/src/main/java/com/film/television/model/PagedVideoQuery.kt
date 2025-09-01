package com.film.television.model

import com.google.gson.annotations.SerializedName

data class PagedVideoQueryBody(
    @SerializedName("methodType") val methodType: String,
    @SerializedName("appName") val appName: String,
    @SerializedName("appToken") val appToken: String,
    @SerializedName("params") val params: Params
) {
    data class Params(
        @SerializedName("pageNum") val pageNum: Int?,
        @SerializedName("pageSize") val pageSize: Int?,
        @SerializedName("category") val category: String?,
        @SerializedName("genre") val genre: String?,
        @SerializedName("region") val region: String?,
        @SerializedName("releaseYear") val releaseYear: String?
    )
}

data class PagedVideoQueryResp(
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
package com.film.television.model

import com.google.gson.annotations.SerializedName

data class HomepageVideoBody(
    @SerializedName("methodType") val methodType: String,
    @SerializedName("appName") val appName: String,
    @SerializedName("appToken") val appToken: String,
    @SerializedName("params") val params: Params
) {
    data class Params(@SerializedName("moduleType") val moduleType: String)
}

data class HomepageVideoResp(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: Data
) {
    data class Data(
        @SerializedName("TV") val tv: List<VideoBean>,
        @SerializedName("MOVIE") val movie: List<VideoBean>,
        @SerializedName("VARIETY") val variety: List<VideoBean>,
        @SerializedName("ANIME") val anime: List<VideoBean>
    )
}
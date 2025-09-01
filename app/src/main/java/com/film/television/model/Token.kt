package com.film.television.model

import com.google.gson.annotations.SerializedName

data class TokenBody(
    @SerializedName("methodType") val methodType: String,
    @SerializedName("appName") val appName: String
)

data class TokenResp(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: Data?
) {
    data class Data(
        @SerializedName("appId") val appId: String,
        @SerializedName("appToken") val appToken: String,
        @SerializedName("expiredTime") val expiredTime: String
    )
}
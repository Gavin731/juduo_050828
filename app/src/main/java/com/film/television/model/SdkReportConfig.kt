package com.film.television.model

import com.google.gson.annotations.SerializedName

data class SdkReportConfigBody(
    @SerializedName("methodType") val methodType: String,
    @SerializedName("appName") val appName: String,
    @SerializedName("appToken") val appToken: String
)

data class SdkReportConfigResp(@SerializedName("data") val data: String?)
package com.film.television.model

import com.google.gson.annotations.SerializedName

data class ConfigQueryBody(
    @SerializedName("methodType") val methodType: String,
    @SerializedName("appName") val appName: String,
    @SerializedName("appToken") val appToken: String,
    @SerializedName("params") val params: Params
) {
    data class Params(
        @SerializedName("ipAddress") val ipAddress: String?,
        @SerializedName("simState") val simState: String
    )
}

data class ConfigQueryResp(
    @SerializedName("code") val code: String,
    @SerializedName("data") val data: List<ConfigItem>
) {
    data class ConfigItem(
        @SerializedName("configKey") val configKey: String,
        @SerializedName("configStatus") val configStatus: Boolean
    )
}
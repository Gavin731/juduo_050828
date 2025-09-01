package com.film.television.model

import com.google.gson.annotations.SerializedName

data class EnvInfoBody(
    @SerializedName("methodType") val methodType: String,
    @SerializedName("appName") val appName: String,
    @SerializedName("appToken") val appToken: String,
    @SerializedName("params") val params: Params
) {
    data class Params(
        @SerializedName("tjType") val tjType: String,
        @SerializedName("deviceId") val deviceId: String,
        @SerializedName("osVersion") val osVersion1: String?,
        @SerializedName("imei") val imei: String?,
        @SerializedName("androidId") val androidId: String?,
        @SerializedName("oaid") val oaid: String?,
        @SerializedName("meid") val meid: String?,
        @SerializedName("mac") val mac: String?,
        @SerializedName("systemInfo") val systemInfo: String?,
        @SerializedName("ipAddress") val ipAddress: String?,
        @SerializedName("simState") val simState: String?,
        @SerializedName("MANUFACTURER") val manufacture: String?,
        @SerializedName("MODEL") val model: String?,
        @SerializedName("BRAND") val brand: String?,
        @SerializedName("BOARD") val board: String?,
        @SerializedName("DEVICE") val device: String?,
        @SerializedName("HARDWARE") val hardware: String?,
        @SerializedName("OS_VERSION") val osVersion2: String?,
        @SerializedName("SDK_INT") val sdkInt: String?
    )
}

data class EnvInfoResp(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: String?
)
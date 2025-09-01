package com.film.television.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class GeneralVideoInfoBody(
    @SerializedName("methodType") val methodType: String,
    @SerializedName("appName") val appName: String,
    @SerializedName("appToken") val appToken: String
)

data class GeneralVideoInfoResp(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: Data
) {
    data class Data(
        @SerializedName("categories") val categories: JsonObject,
        @SerializedName("orders") val orders: JsonObject,
        @SerializedName("genres") val genres: JsonObject,
        @SerializedName("regions") val regions: JsonObject,
        @SerializedName("yearCategories") val yearCategories: JsonObject
    )
}
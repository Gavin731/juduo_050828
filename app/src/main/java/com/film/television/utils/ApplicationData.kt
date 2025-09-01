package com.film.television.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.film.television.model.GeneralVideoInfoResp

object ApplicationData {
    private val _generalVideoInfoData = MutableLiveData<GeneralVideoInfoResp.Data>()

    val generalVideoInfoData: LiveData<GeneralVideoInfoResp.Data> = _generalVideoInfoData

    fun setGeneralVideoInfoData(data: GeneralVideoInfoResp.Data) {
        _generalVideoInfoData.value = data
    }

    private val _initStatus = MutableLiveData<Boolean>()

    val initStatus: LiveData<Boolean> = _initStatus

    fun setInitStatus(status: Boolean) {
        _initStatus.value = status
    }

    fun setInitStatusAsync(status: Boolean) {
        _initStatus.postValue(status)
    }

    private val _isAdSdkInitNeeded = MutableLiveData<Boolean>()

    val isAdSdkInitNeeded: LiveData<Boolean> = _isAdSdkInitNeeded

    fun setAdSdkInitNeeded(needed: Boolean) {
        _isAdSdkInitNeeded.value = needed
    }
}
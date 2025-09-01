package com.film.television.viewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.film.television.model.EnvInfoResp
import com.film.television.repository.ContentRepository
import com.film.television.utils.DeviceUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    init {
        if (!savedStateHandle.contains("position")) {
            savedStateHandle["position"] = 0
        }
    }

    val position: LiveData<Int> = savedStateHandle.getLiveData("position")

    fun setPosition(position: Int) {
        savedStateHandle["position"] = position
    }

    private val _hotCategory: MutableLiveData<String> = MutableLiveData()

    val hotCategory: LiveData<String> = _hotCategory.distinctUntilChanged()

    fun setHotCategory(hotCategory: String) {
        _hotCategory.value = hotCategory
    }

    @RequiresPermission("android.permission.READ_PRIVILEGED_PHONE_STATE")
    fun postEnvInfo(
        context: Context,
        lifecycleScope: CoroutineScope,
        token: String,
        onResp: suspend (EnvInfoResp) -> Unit
    ) {
        DeviceUtil.getOaid(context) { oaid ->
            lifecycleScope.launch {
                val resp = ContentRepository.postEnvInfo(
                    token,
                    DeviceUtil.getDeviceId(),
                    DeviceUtil.getOsVersion(),
                    DeviceUtil.getImei(),
                    DeviceUtil.getAndroidId(),
                    oaid,
                    DeviceUtil.getMeid(),
                    DeviceUtil.getMac(),
                    null,
                    DeviceUtil.getIpAddress(),
                    DeviceUtil.getSimState(),
                    Build.MANUFACTURER,
                    Build.MODEL,
                    Build.BRAND,
                    Build.BOARD,
                    Build.DEVICE,
                    Build.HARDWARE,
                    Build.VERSION.SDK_INT.toString()
                )
                onResp(resp)
            }
        }
    }

    private val _showSupportDialog = MutableLiveData<Boolean>()

    val showSupportDialog: LiveData<Boolean> = _showSupportDialog

    fun setShowSupportDialog() {
        _showSupportDialog.value = true
    }
}
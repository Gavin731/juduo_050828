package com.film.television.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.film.television.BuildConfig
import com.film.television.MyApplication
import com.umeng.commonsdk.UMConfigure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Inet6Address
import java.net.NetworkInterface
import java.net.URL
import java.util.Collections
import java.util.Locale

object DeviceUtil {
    private var deviceId: String = ""
    private var androidId: String? = null
    private var androidIdInitialized: Boolean = false
    private val telephonyManager: TelephonyManager by lazy {
        MyApplication.INSTANCE.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }
    private val wifiManager: WifiManager by lazy {
        MyApplication.INSTANCE.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    fun getPackageName(): String {
        return BuildConfig.APPLICATION_ID
    }

    @SuppressLint("HardwareIds")
    fun getAndroidId(): String? {
        if (!androidIdInitialized) {
            androidId = Settings.Secure.getString(
                MyApplication.INSTANCE.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            androidIdInitialized = true
        }
        return androidId
    }

    fun getDeviceId(): String {
        if (deviceId.isEmpty()) {
            deviceId =
                DigestUtil.getMD5("${Build.MODEL}${Build.DEVICE}${getAndroidId()}")
        }
        return deviceId
    }

    fun getOsVersion(): String {
        return "Android ${Build.VERSION.SDK_INT}"
    }

    fun getAndroidVersion(): String {
        val sdkVersion = Build.VERSION.SDK_INT
        val release = Build.VERSION.RELEASE
        return "Android SDK: $sdkVersion ($release)"
    }

    @SuppressLint("HardwareIds")
    @RequiresPermission("android.permission.READ_PRIVILEGED_PHONE_STATE")
    fun getImei(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return null
        }
        val granted = ContextCompat.checkSelfPermission(MyApplication.INSTANCE, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            return null
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyManager.getImei(0) ?: telephonyManager.getImei(1)
        } else {
            telephonyManager.deviceId
        }
    }

    fun getOaid(context: Context, onGetOaid: (String?) -> Unit) {
        UMConfigure.getOaid(context, onGetOaid)
    }

    @SuppressLint("HardwareIds")
    @RequiresPermission("android.permission.READ_PRIVILEGED_PHONE_STATE")
    fun getMeid(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return null
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyManager.meid
        } else {
            telephonyManager.getDeviceId(2)
        }
    }

    @SuppressLint("HardwareIds")
    fun getMac(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return null
        }
        val mac = wifiManager.connectionInfo.macAddress
        if (mac != null && mac != "02:00:00:00:00:00") {
            return mac
        }
        return null
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun getIpAddress(): String? {
        // 首先尝试获取Wifi的IP地址
        getWifiIpAddress()?.let { return it }
        // 再尝试获取活动网络IP地址
        getActiveNetworkIpAddress()?.let { return it }
        // 再遍历所有网络接口
        val allIps = getAllIpAddresses()
        if (allIps.isNotEmpty()) {
            // 优先返回非127.0.0.1的地址
            return allIps.firstOrNull {
                it != "127.0.0.1"
            }
        }
        return null
    }

    private fun getWifiIpAddress(): String? {
        val wifiInfo = wifiManager.connectionInfo
        val ipAddress = wifiInfo.ipAddress
        return String.format(
            Locale.CHINA,
            "%d.%d.%d.%d",
            ipAddress and 0xFF,
            ipAddress shr 8 and 0xFF,
            ipAddress shr 16 and 0xFF,
            ipAddress shr 24 and 0xFF
        )
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun getActiveNetworkIpAddress(): String? {
        val connectivityManager =
            MyApplication.INSTANCE.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return null
        val linkProperties = connectivityManager.getLinkProperties(network) ?: return null
        return linkProperties.linkAddresses.firstOrNull()?.address?.hostAddress
    }

    private fun getAllIpAddresses(): List<String> {
        val ipAddresses = mutableListOf<String>()
        val networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (networkInterface in networkInterfaces) {
            if (networkInterface.isLoopback || networkInterface.isUp) continue
            val addresses = Collections.list(networkInterface.inetAddresses)
            for (address in addresses) {
                if (address is Inet6Address) continue
                val hostAddress = address.hostAddress
                hostAddress?.let { ipAddresses.add(it) }
            }
        }
        return ipAddresses
    }

    suspend fun getPublicIp(): String? = withContext(Dispatchers.IO) {
        try {
            URL("https://ipinfo.io/ip").openStream()
                .bufferedReader()
                .use { it.readLine() }
        } catch (e: Exception) {
            null
        }
    }

    fun getSimState(): String {
        return telephonyManager.simState.toString()
    }

}
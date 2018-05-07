package com.pape.utils

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.provider.Settings
import android.support.annotation.RequiresPermission
import android.telephony.TelephonyManager
import android.util.Log
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.net.UnknownHostException

/**
 * 功能描述：
 * Created by Administrator on 2018/5/7.
 */

enum class NetworkType {
    NETWORK_WIFI,
    NETWORK_4G,
    NETWORK_3G,
    NETWORK_2G,
    NETWORK_UNKNOWN,
    NETWORK_NO
}

object NetworkUtils {

    fun openWirelessSettings() {
        Utils.getApp()?.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)
                .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    fun isConnected(): Boolean {
        val info = getActiveNetworkInfo()
        return info != null && info.isConnected
    }

    @RequiresPermission(INTERNET)
    fun isAvailableByPing(ip: String? = null): Boolean {
        val pingIp = ip?.let { if (ip.isEmpty()) "223.5.5.5" else ip }
        val result: CommandResult = ShellExt.execCmd(commands = "ping -c 1 $pingIp", isRoot = false)
        result.errorMsg?.also { Log.d("NetworkUtils", "isAvailableByPing() called" + it) }
        result.successMsg?.also { Log.d("NetworkUtils", "isAvailableByPing() called" + it) }
        return result.result == 0
    }

    fun setMobileDataEnabled(enabled: Boolean) {
        try {
            val tm: TelephonyManager? = Utils.getApp()?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager ?: return
            val setMobileDataEnabledMethod = tm?.javaClass?.getDeclaredMethod("setDataEnabled", Boolean::class.javaPrimitiveType)
            setMobileDataEnabledMethod?.invoke(tm, enabled)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    fun isMobileData(): Boolean {
        val info = getActiveNetworkInfo()
        return (null != info
                && info.isAvailable
                && info.type == ConnectivityManager.TYPE_MOBILE)
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    fun is4G(): Boolean {
        val info = getActiveNetworkInfo()
        return (info != null
                && info.isAvailable
                && info.subtype == TelephonyManager.NETWORK_TYPE_LTE)
    }

    @RequiresPermission(ACCESS_WIFI_STATE)
    fun getWifiEnabled(): Boolean? {
        @SuppressLint("WifiManagerLeak")
        val manager: WifiManager = Utils.getApp()?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager ?: return false
        return manager.isWifiEnabled
    }

    fun getMobileDataEnabled(): Boolean {
        try {
            val tm: TelephonyManager? = Utils.getApp()?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager ?: return false
            @SuppressLint("PrivateApi")
            val getMobileDataEnabledMethod = tm?.javaClass?.getDeclaredMethod("getDataEnabled")
            if (null != getMobileDataEnabledMethod) {
                return getMobileDataEnabledMethod.invoke(tm) as Boolean
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    private fun getActiveNetworkInfo(): NetworkInfo? {
        val manager = Utils.getApp()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager ?: return null
        return manager.activeNetworkInfo
    }

    @SuppressLint("MissingPermission")
    fun setWifiEnabled(enabled: Boolean) {
        @SuppressLint("WifiManagerLeak")
        val manager = Utils.getApp()?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager ?: return
        if (enabled) {
            if (!manager.isWifiEnabled) {
                manager.isWifiEnabled = true
            }
        } else {
            if (manager.isWifiEnabled) {
                manager.isWifiEnabled = false
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun isWifiConnected(): Boolean {
        val cm: ConnectivityManager? = Utils.getApp()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager ?: return false
        return cm?.activeNetworkInfo != null
                && cm.activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
    }

    @RequiresPermission(allOf = arrayOf(ACCESS_WIFI_STATE, INTERNET))
    fun isWifiAvailable(): Boolean {
        return getWifiEnabled()?.and(isAvailableByPing()) ?: false
    }

    fun getNetworkOperatorName(): String? {
        val tm: TelephonyManager? = Utils.getApp()?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm?.networkOperatorName
    }

    private val NETWORK_TYPE_GSM = 16
    private val NETWORK_TYPE_TD_SCDMA = 17
    private val NETWORK_TYPE_IWLAN = 18

    @RequiresPermission(ACCESS_NETWORK_STATE)
    fun getNetworkType(): NetworkType {
        var netType = NetworkType.NETWORK_NO
        val info = getActiveNetworkInfo()
        if (info != null && info.isAvailable) {
            when {
                info.type == ConnectivityManager.TYPE_WIFI -> netType = NetworkType.NETWORK_WIFI
                info.type == ConnectivityManager.TYPE_MOBILE -> when (info.subtype) {
                    NETWORK_TYPE_GSM,
                    TelephonyManager.NETWORK_TYPE_GPRS,
                    TelephonyManager.NETWORK_TYPE_CDMA,
                    TelephonyManager.NETWORK_TYPE_EDGE,
                    TelephonyManager.NETWORK_TYPE_1xRTT,
                    TelephonyManager.NETWORK_TYPE_IDEN -> netType = NetworkType.NETWORK_2G

                    NETWORK_TYPE_TD_SCDMA,
                    TelephonyManager.NETWORK_TYPE_EVDO_A,
                    TelephonyManager.NETWORK_TYPE_UMTS,
                    TelephonyManager.NETWORK_TYPE_EVDO_0,
                    TelephonyManager.NETWORK_TYPE_HSDPA,
                    TelephonyManager.NETWORK_TYPE_HSUPA,
                    TelephonyManager.NETWORK_TYPE_HSPA,
                    TelephonyManager.NETWORK_TYPE_EVDO_B,
                    TelephonyManager.NETWORK_TYPE_EHRPD,
                    TelephonyManager.NETWORK_TYPE_HSPAP -> netType = NetworkType.NETWORK_3G

                    NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> netType = NetworkType.NETWORK_4G
                    else -> {
                        val subtypeName = info.subtypeName
                        netType = if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                                || subtypeName.equals("WCDMA", ignoreCase = true)
                                || subtypeName.equals("CDMA2000", ignoreCase = true)) {
                            NetworkType.NETWORK_3G
                        } else {
                            NetworkType.NETWORK_UNKNOWN
                        }
                    }
                }
                else -> netType = NetworkType.NETWORK_UNKNOWN
            }
        }
        return netType
    }

    @RequiresPermission(INTERNET)
    fun getIPAddress(useIPv4: Boolean): String? {
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp) continue
                val addresses = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    val inetAddress = addresses.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        val hostAddress = inetAddress.hostAddress
                        val isIPv4 = hostAddress.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return hostAddress
                        } else {
                            if (!isIPv4) {
                                val index = hostAddress.indexOf('%')
                                return if (index < 0)
                                    hostAddress.toUpperCase()
                                else
                                    hostAddress.substring(0, index).toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return null
    }

    @RequiresPermission(INTERNET)
    fun getDomainAddress(domain: String): String? {
        val inetAddress: InetAddress
        return try {
            inetAddress = InetAddress.getByName(domain)
            inetAddress.hostAddress
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            null
        }

    }

}
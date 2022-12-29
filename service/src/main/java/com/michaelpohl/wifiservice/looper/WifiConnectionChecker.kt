package com.michaelpohl.wifiservice.looper

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build

class WifiConnectionChecker(private val connectivityManager: ConnectivityManager, private val wifiManager: WifiManager) {

    fun isWifiOn(): Boolean {
        return wifiManager.isWifiEnabled
    }

    fun isConnectedToAnyWifi(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network: Network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected && networkInfo.type == ConnectivityManager.TYPE_WIFI
        }
    }
}

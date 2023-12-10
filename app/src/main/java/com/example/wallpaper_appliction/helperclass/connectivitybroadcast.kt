package com.example.wallpaper_appliction.helperclass

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build

class connectivitybroadcast:BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context!=null)
        {
            isonline(context)
        }

    }
    protected fun isonline(context: Context?) {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val isConnected = networkCapabilities != null
            notifyConnectivityStatus(context, isConnected)
        } else {
            @Suppress("DEPRECATION")
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            val isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected
            notifyConnectivityStatus(context, isConnected)
        }

    }

    private fun notifyConnectivityStatus(context: Context, isConnected: Boolean) {
        val intent = Intent("internet")
        intent.putExtra("is_connected", isConnected)
        context.sendBroadcast(intent)
    }
}
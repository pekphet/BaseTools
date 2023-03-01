// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.pekphet.base.components.ZInfoRecorder

object NetworkStateHelper {
    private const val _TAG = "CONNECTIVITY"
    private var mCallback: (Boolean) -> Unit = {}
    private var mAvailable = false
    lateinit var mConnMgr: ConnectivityManager

    fun init(ctx: Context) {
        mConnMgr = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        mConnMgr.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    ZInfoRecorder.e("net_state", "onAvailable")
                    if (!mAvailable) {
                        mAvailable = true
                        mCallback(true)
                    }
                }


                override fun onLost(network: Network) {
                    super.onLost(network)
                    ZInfoRecorder.e("net_state", "onLost")
                    if (mAvailable) {
                        mAvailable = false
                        mCallback(false)
                    }
                }

                override fun onUnavailable() {
                    ZInfoRecorder.e("net_state", "onUnavailable")
                    super.onUnavailable()
                    if (mAvailable) {
                        mAvailable = false
                        mCallback(false)
                    }
                }

                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    ZInfoRecorder.e("net_state", "onCapabilitiesChanged: ${networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)}")
                    if (mAvailable != networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                        mAvailable = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                        mCallback(mAvailable)
                    }
                }
            })
        mAvailable = mConnMgr.activeNetwork != null
    }

    fun setOnNetworkStateChanged(onChanged: (Boolean) -> Unit) {
        mCallback = onChanged
    }

    fun isNetworkAvailable(): Boolean {
        ZInfoRecorder.e("net_state", "isNetworkAvailable: $mAvailable")
        return mAvailable
    }

    fun reset() {
        ZInfoRecorder.e("net_state", "onUnavailable")
        try {
            throw RuntimeException("RESET NETWORK STATE HELPER!!!!")
        } catch (ex: Exception) {
//            ex.printStackTrace()
        }
        mAvailable = false
    }
}
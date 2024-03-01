package com.dataxow.helper

import java.net.InetAddress
import java.net.NetworkInterface

class NetHelper {
    companion object {
        fun getLocalIPAddresses(): List<String> {
            return NetworkInterface.getNetworkInterfaces().asSequence().flatMap { ni ->
                ni.inetAddresses.asSequence().filter { it.isSiteLocalAddress && it is InetAddress }
                    .map { it.hostAddress }
            }.toList()
        }
    }
}

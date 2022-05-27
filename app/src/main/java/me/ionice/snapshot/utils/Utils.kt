package me.ionice.snapshot.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import java.time.format.DateTimeFormatter

object Utils {
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")

    fun checkIfOnline(context: Context): Boolean {
        val cm = ContextCompat.getSystemService(context, ConnectivityManager::class.java)

        val capabilities = cm?.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
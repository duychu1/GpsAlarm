package com.ruicomp.gpsalarm.utils

import android.location.Address

object AddressUtils {
    fun handleAddressLine(address: Address): String {
        address.getAddressLine(0)?.takeIf { it.isNotEmpty() }?.let { return it }

        return buildString {
            address.featureName?.takeIf { it.isNotEmpty() && it != address.thoroughfare }?.let { append(it) }

            address.thoroughfare?.takeIf { it.isNotEmpty() }?.let {
                if (isNotEmpty()) append(", ")
                append(it)
                address.subThoroughfare?.takeIf { it.isNotEmpty() }?.let { append(" ").append(it) }
            }

            address.locality?.takeIf { it.isNotEmpty() }?.let {
                if (isNotEmpty()) append(", ")
                append(it)
            }

            address.adminArea?.takeIf { it.isNotEmpty() }?.let {
                if (isNotEmpty()) append(", ")
                append(it)
            }

            address.postalCode?.takeIf { it.isNotEmpty() }?.let {
                if (isNotEmpty()) append(" ")
                append(it)
            }

            address.countryName?.takeIf { it.isNotEmpty() }?.let {
                if (isNotEmpty()) append(", ")
                append(it)
            }
        }.ifEmpty { "Address unavailable" }
    }
}
package com.ruicomp.gpsalarm.model

data class Language(
    val code: String,
    val name: String,
    val flagResId: Int,
//    val languageToLoad: String,
    var isSelected: Boolean = false,
    var colorBackground: String? = null,
)

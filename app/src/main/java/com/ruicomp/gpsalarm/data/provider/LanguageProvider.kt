package com.ruicomp.gpsalarm.data.provider

import com.ruicomp.gpsalarm.R
import com.ruicomp.gpsalarm.model.Language

object LanguageProvider {
    val languages = listOf(
        Language("pl", "Polska", R.drawable.flag_pl),
        Language("en", "English", R.drawable.flag_en),
        Language("hi", "हिंदी", R.drawable.flag_hi),
        Language("it", "Italia", R.drawable.flag_it),
        Language("de", "Deutsch", R.drawable.flag_germany),
        Language("fr", "Français", R.drawable.flag_france),
        Language("es", "Español", R.drawable.flag_spain_es),
        Language("ru", "Русский", R.drawable.flag_russia),
        Language("zh-CN", "简体中文", R.drawable.flag_china),
        Language("zh-TW", "繁體中文", R.drawable.flag_taiwan),
        Language("ja", "日本語", R.drawable.flag_japan),
        Language("ko", "한국어", R.drawable.flag_south_korea),


    )

    val defaultLanguageCode = languages.first().code

    const val KEY_LANGUAGE_CODE = "KEY_LANGUAGE_CODE"
}
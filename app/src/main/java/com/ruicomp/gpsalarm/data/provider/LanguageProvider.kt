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
    )

    val defaultLanguageCode = languages.first().code

    const val KEY_LANGUAGE_CODE = "KEY_LANGUAGE_CODE"
}
package com.ruicomp.gpsalarm.feature.outer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.utils.InternetUtil
import com.google.android.gms.ads.nativead.NativeAd
import com.ruicomp.gpsalarm.AdCache
import com.ruicomp.gpsalarm.AdIds
import com.ruicomp.gpsalarm.AppSession
import com.ruicomp.gpsalarm.model.Language

// Extend BaseLanguageActivity
class Lfo1Activity : BaseLanguageActivity() {

    private val lfo1NativeAds = arrayListOf(AdIds.lfo1_native_high, AdIds.lfo1_native)
    private val ob1NativeAds = arrayListOf(AdIds.ob1_native_high, AdIds.ob1_native)
    private val lfo2NativeAds = arrayListOf(AdIds.lfo2_native_high2, AdIds.lfo2_native)
    private var isFirstResume = true

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Call super.onCreate first
        AdCache.getInstance().lfo1Native?.let {
            AdmobManager.getInstance().showNative(
                this,
                it,
                binding.frNativeAd,
                AdmobManager.NativeAdType.BIG
            )
        } ?: run {
            isFirstResume = false
        }
        if (InternetUtil.isNetworkAvailable(this)) {
            if (AdCache.getInstance().lfo2NativeHigh == null && AdCache.getInstance().lfo2NativeHigh1 == null) {
                AdmobManager.getInstance()
                    .preloadAlternateNative(this, lfo2NativeAds, object : AdCallback() {
                        override fun onNativeAds(nativeAd: NativeAd?) {
                            super.onNativeAds(nativeAd)
                            AdCache.getInstance().lfo2NativeHigh2 = nativeAd
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()
//                            logEvent("language2_ad_native_view")
                        }

                        override fun onAdClicked() {
                            super.onAdClicked()
//                            logEvent("language2_ad_native_click")

                        }
                    })
            }
        }

        if (InternetUtil.isNetworkAvailable(this) && AdCache.getInstance().ob1Native == null) {
            AdmobManager.getInstance()
                .preloadAlternateNative(this, ob1NativeAds, object : AdCallback() {
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        AdCache.getInstance().ob1Native = nativeAd
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
//                        logEvent("onboard1_native_view")
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
//                        logEvent("onboard1_native_click")

                    }
                })
        }
    }

    override fun setupToolbar() {
        binding.btnConfirm.visibility = View.GONE
        binding.btnBack.visibility = View.VISIBLE

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun setInitialLanguageSelection() {
        // No initial selection needed for LanguageSelectionActivity
        // as the user chooses a language to navigate away
    }

    override fun onLanguageSelected(language: Language) {
        // When a language is selected, navigate to LanguageActivity
        binding.btnBack.visibility = View.INVISIBLE
        navigateNext(language.code)
    }

    private fun navigateNext(languageCode: String) {
        Lfo2Activity.startActivity(this, languageCode)
        overridePendingTransition(0, 0) // Disable transition animation
        finish() // Close this activity
    }

    override fun onResume() {
        super.onResume()
        if (isFirstResume) {
            isFirstResume = false
        } else if (AppSession.isCompletedInterSplash) {
            loadAlternateNativeReload()
        }
    }

    private fun loadAlternateNativeReload() {
        AdmobManager.getInstance()
            .preloadAlternateNative(this, lfo1NativeAds, object : AdCallback() {
                override fun onNativeAds(nativeAd: NativeAd?) {
                    super.onNativeAds(nativeAd)
                    AdmobManager.getInstance().showNative(
                        this@Lfo1Activity,
                        nativeAd,
                        binding.frNativeAd,
                        AdmobManager.NativeAdType.BIG
                    )
                }

                override fun onAdClicked() {
                    super.onAdClicked()
    //                        logEvent("language_ad_native_click")
                }

                override fun onAdImpression() {
                    super.onAdImpression()
    //                        logEvent("language_ad_native_view")

                }
            })
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, Lfo1Activity::class.java)
            context.startActivity(intent)
        }
    }
}
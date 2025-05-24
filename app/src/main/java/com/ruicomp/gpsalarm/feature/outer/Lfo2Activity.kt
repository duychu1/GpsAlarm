package com.ruicomp.gpsalarm.feature.outer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.utils.InternetUtil
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.ruicomp.gpsalarm.AdCache
import com.ruicomp.gpsalarm.AdIds
import com.ruicomp.gpsalarm.data.provider.LanguageProvider
import com.ruicomp.gpsalarm.datastore2.DataStoreKeys
import com.ruicomp.gpsalarm.datastore2.DataStorePreferences
import com.ruicomp.gpsalarm.model.Language
import com.ruicomp.gpsalarm.remote_config.RemoteConfigManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class Lfo2Activity : BaseLanguageActivity() {

    private val dataStorePref: DataStorePreferences by inject()

    private var currentLanguageCode: String? = null
    private val onb2NativeAds = arrayListOf(AdIds.ob2_native_high, AdIds.ob2_native)
    private val lfo2NativeAdsReload = arrayListOf(AdIds.lfo2_native_high, AdIds.lfo2_native)
    private val onb3NativeFull = AdIds.ob3_native_high1
    private val onb3InterAds by lazy { arrayListOf(AdIds.ob3_inter_high, AdIds.ob3_inter) }
    private val onb4NativeAds = arrayListOf(AdIds.ob4_native_high, AdIds.ob4_native)
    private var isFirstResume = true

    override fun onCreate(savedInstanceState: Bundle?) {
        currentLanguageCode = intent.getStringExtra(LanguageProvider.KEY_LANGUAGE_CODE)
        super.onCreate(savedInstanceState)

        val cachedNativeAd = AdCache.getInstance().lfo2NativeHigh
            ?: AdCache.getInstance().lfo2NativeHigh1
            ?: AdCache.getInstance().lfo2NativeHigh2

        cachedNativeAd?.let { nativeAd ->
            AdmobManager.getInstance().showNative(
                this@Lfo2Activity,
                nativeAd, binding.frNativeAd, AdmobManager.NativeAdType.BIG
            )
        }

        preloadNativeOb2()
        preloadNativeFullOb3()

    }

    override fun setupToolbar() {
        binding.btnConfirm.visibility = View.VISIBLE // Ensure confirm button is visible in this activity
        binding.btnBack.visibility = View.GONE

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnConfirm.setOnClickListener {
            // Save the selected language
            selectedLanguage?.let { language ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStorePref.saveString(DataStoreKeys.LANGUAGE_STRING, language.code)
                    onNavigateNext()
                }
            }
        }
    }

    private fun onNavigateNext() {
        val intent = Intent(this@Lfo2Activity, OnboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun setInitialLanguageSelection() {
        // Set initial selection based on currentLanguageCode
        LanguageProvider.languages.find { it.code == currentLanguageCode }?.isSelected = true
        selectedLanguage = LanguageProvider.languages.find { it.isSelected }
    }

    override fun onLanguageSelected(language: Language) {
        // Update the selected language when a language is clicked
        selectedLanguage = language
    }

    private fun loadAlternateNativeReload() {
        Log.d("Refresh", "Create Refresh")
        AdmobManager.getInstance().preloadAlternateNative(
            this,
            lfo2NativeAdsReload,
            object : AdCallback() {
                override fun onNativeAds(nativeAd: NativeAd?) {
                    super.onNativeAds(nativeAd)
                    AdmobManager.getInstance().showNative(
                        this@Lfo2Activity,
                        nativeAd,
                        binding.frNativeAd,
                        AdmobManager.NativeAdType.BIG
                    )
                    Log.d("Refresh", "ShowRefreshHowToUse")
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                }
            }
        )
    }
    private fun preloadNativeOb2() {
        if (InternetUtil.isNetworkAvailable(this)) {
            AdmobManager.getInstance()
                .preloadAlternateNative(this, onb2NativeAds, object : AdCallback() {
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        AdCache.getInstance().ob2NativeHigh = nativeAd
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
    private fun preloadNativeFullOb3() {
        if (RemoteConfigManager.instance?.isShowInterOverFullScreenNativeOnboard == true) {
            if (AdCache.getInstance().ob3Inter != null) return
            if (InternetUtil.isNetworkAvailable(this)) {
                AdmobManager.getInstance()
                    .loadAlternateInter(this, onb3InterAds, object : AdCallback() {
                        override fun onResultInterstitialAd(interstitialAd: InterstitialAd?) {
                            super.onResultInterstitialAd(interstitialAd)
                            AdCache.getInstance().ob3Inter = interstitialAd
                        }

                        override fun onAdFailedToLoad(i: LoadAdError) {
                            super.onAdFailedToLoad(i)
                        }
                    })
            } else {
                Log.d(
                    "devLogger: loadAlternateInter",
                    "No network available, skipping interstitial load."
                )
            }
        } else {
            if (InternetUtil.isNetworkAvailable(this)) {
                AdmobManager.getInstance()
                    .preloadFullScreenNative(this, onb3NativeFull, object : AdCallback() {
                        override fun onNativeAds(nativeAd: NativeAd?) {
                            super.onNativeAds(nativeAd)
                            AdCache.getInstance().ob3NativeHigh1 = nativeAd
                        }
                    })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isFirstResume) {
            isFirstResume = false
        } else {
            loadAlternateNativeReload()
        }
    }

    companion   object {
        val TAG = this.javaClass.simpleName

        fun startActivity(context: Context, languageCode: String) {
            val intent = Intent(context, Lfo2Activity::class.java).apply {
                putExtra(LanguageProvider.KEY_LANGUAGE_CODE, languageCode)
            }
            context.startActivity(intent)
        }

    }
}
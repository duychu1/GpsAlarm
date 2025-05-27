package com.ruicomp.gpsalarm.feature.outer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.utils.InternetUtil
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.ruicomp.gpsalarm.AdIds
import com.ruicomp.gpsalarm.databinding.ActivitySplashBinding
import com.ruicomp.gpsalarm.datastore2.DataStoreKeys
import com.ruicomp.gpsalarm.datastore2.DataStorePreferences
import com.ruicomp.gpsalarm.AdCache
import com.ruicomp.gpsalarm.AppSession
import com.ruicomp.gpsalarm.R
import com.ruicomp.gpsalarm.remote_config.RemoteConfigManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import kotlin.math.min

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivityNonBinding() {

    private val dataStorePreferences: DataStorePreferences by inject()

    private lateinit var binding: ActivitySplashBinding

    private var isNativeAdLoaded = false
    private var listNativeSplash = arrayListOf(AdIds.native_splash_high, AdIds.native_splash)
    private var onb5NativePermissionAds = arrayListOf(AdIds.ob5_native_high, AdIds.ob5_native)
    private var appOpenAdid = AdIds.app_open_splash
    private var lfo1NativeAds = arrayListOf(AdIds.lfo1_native_high, AdIds.lfo1_native)
    private var lfo2NativeHighAds = arrayListOf(AdIds.lfo2_native_high, AdIds.lfo2_native_high1)
    private var onb1NativeAds = arrayListOf(AdIds.ob1_native_high, AdIds.ob1_native)
    private var onb3NativeFull = AdIds.ob3_native_high
    private var onb3InterAds = arrayListOf(AdIds.ob3_inter_high, AdIds.ob3_inter)
    private var progressStatus = 0
    private val totalDurationSplashMillis = 10000L // Total time for splash in milliseconds
    private val updateIntervalMillis = 100L // How often to update progress
    private var languageCode: String? = "abc"
    private var isOnboardComplete = true
    private var isPermissionComplete = true
    private var isFirstResume = true
    private var job: Job? = null
//    private var intervalShowPremium = 5*60*1000
    private var intervalShowPremium = 24 * 60 * 60 * 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.bg_splash)
// Inside your Activity's onCreate or a relevant lifecycle method
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
// For dark icons (when status bar background is light)
        windowInsetsController.isAppearanceLightStatusBars = true

        lifecycleScope.launch(Dispatchers.IO) {
            languageCode = dataStorePreferences.getString(DataStoreKeys.LANGUAGE_STRING)
            isOnboardComplete = dataStorePreferences.getBoolean(DataStoreKeys.IS_ONBOARD_COMPLETE) ?: false
            isPermissionComplete = dataStorePreferences.getBoolean(DataStoreKeys.IS_PERMISSION_COMPLETE) ?: false
            launch {
                val lastShowPremium =  dataStorePreferences.getLong(DataStoreKeys.LAST_PREMIUM_SHOWN_AT) ?: 0L
                AppSession.isDismissPremium = (System.currentTimeMillis() - lastShowPremium) <= (intervalShowPremium)
            }

            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < totalDurationSplashMillis) {
                delay(200)
                if (RemoteConfigManager.instance?.getIsLoading() == false) break
            }

            AdIds.updateIdAdsWithRemoteConfig()
            updateAdIdProperties()
            withContext(Dispatchers.Main.immediate) {
                loadAlternateNativeSplash()
                loadAppOpenAdSplash()
                handlePreloadAds()
            }
            while (System.currentTimeMillis() - startTime < 10000) {
                delay(200)
                if (isNativeAdLoaded) {
                    withContext(Dispatchers.Main) {
                        onLoadingFinished()
                    }
                    break
                }
            }
        }

        binding.pbLoading.progress = 0
        binding.pbLoading.max = 100

        job = lifecycleScope.launch {
            // Simulate loading progress
            val progressSteps = (totalDurationSplashMillis / updateIntervalMillis).toInt()
            for (i in 0..progressSteps) {
                progressStatus = min((100 * i / progressSteps), 100)
                // Update the progress bar on the main thread
                withContext(Dispatchers.Main) {
                    binding.tvLoadingStatus.text =
                        getString(R.string.loading_with_percent, progressStatus)
                }
                delay(updateIntervalMillis) // Non-blocking delay
            }

            // When loading is complete, update UI on the main thread
            withContext(Dispatchers.Main) {
                onLoadingFinished()
            }
        }

//        lifecycleScope.launch {
//            languageCode = dataStorePreferences.getString(DataStoreKeys.LANGUAGE_STRING)
//            isOnboardComplete = dataStorePreferences.getBoolean(DataStoreKeys.IS_ONBOARD_COMPLETE) ?: false
//            isPermissionComplete = dataStorePreferences.getBoolean(DataStoreKeys.IS_PERMISSION_COMPLETE) ?: false
//
//            // You can now use the retrieved values (languageCode, isOnboardComplete, isPermissionComplete)
//            // if needed for conditional ad loading or logging.
//            Log.d("DataStore", "Language Code: $languageCode, Onboard Complete: $isOnboardComplete, Permission Complete: $isPermissionComplete")
//
//            // Once the values are retrieved, run the preload ads function
//            handlePreloadAds()
//        }

        // Set listener for the button
        binding.btnContinue.setOnClickListener {
            showAppOpenAdSplashAndGotoNext()
        }
    }

    private fun updateAdIdProperties() {
        listNativeSplash = arrayListOf(AdIds.native_splash_high, AdIds.native_splash)
        onb5NativePermissionAds = arrayListOf(AdIds.ob5_native_high, AdIds.ob5_native)
        appOpenAdid = AdIds.app_open_splash
        lfo1NativeAds = arrayListOf(AdIds.lfo1_native_high, AdIds.lfo1_native)
        lfo2NativeHighAds = arrayListOf(AdIds.lfo2_native_high, AdIds.lfo2_native_high1)
        onb1NativeAds = arrayListOf(AdIds.ob1_native_high, AdIds.ob1_native)
        onb3NativeFull = AdIds.ob3_native_high // Assuming this is how it's defined in AdIds
        onb3InterAds = arrayListOf(AdIds.ob3_inter_high, AdIds.ob3_inter)
    }

    private fun onLoadingFinished() {
        binding.btnContinue.visibility = View.VISIBLE
        binding.tvLoadingStatus.text =
            getString(R.string.loading_complete) // Assuming you add this string resource
        job?.cancel()
    }

    private fun navigateToNextScreen() {
        if (languageCode == null) {
            val intent = Intent(this, Lfo1Activity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else if (!isOnboardComplete) {
            val intent = Intent(this, OnboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else if (!isPermissionComplete) {
            val intent = Intent(this, PermissionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            val intent = Intent(this, PremiumActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (isFirstResume) {
            isFirstResume = false
        } else if (AppSession.isCompletedInterSplash) {
            loadAlternateNativeSplash()
        }
    }

    private fun loadAppOpenAdSplash() {
        AdmobManager.getInstance().loadAppOpenAd(this, appOpenAdid, object: AdCallback() {
            override fun onResultOpenAppAd(appOpenAd: AppOpenAd?) {
                super.onResultOpenAppAd(appOpenAd)
                AdCache.getInstance().appOpenAd = appOpenAd
            }
            override fun onAdFailedToLoad(i: LoadAdError) {
                super.onAdFailedToLoad(i)
            }
        })
    }

    private fun showAppOpenAdSplashAndGotoNext() {
        if (AdCache.getInstance().appOpenAd != null ) {
            AdmobManager.getInstance()
                .showAppOpenAd(this, AdCache.getInstance().appOpenAd, object : AdCallback() {
                    override fun onNextScreen() {
                        super.onNextScreen()
                        navigateToNextScreen()
                    }
                })
        } else {
            AdmobManager.getInstance().loadAndShowSplashOpenApp(this, appOpenAdid, object : AdCallback() {
                override fun onNextScreen() {
                    super.onNextScreen()
                    navigateToNextScreen()
                }
            })
        }
    }

    private fun loadAlternateNativeSplash() {
        Log.d("Refresh","Create Refresh")
        AdmobManager.getInstance().preloadAlternateNative(
            this,
            listNativeSplash,
            object : AdCallback(){
                override fun onNativeAds(nativeAd: NativeAd?) {
                    super.onNativeAds(nativeAd)
                    isNativeAdLoaded = true
                    AdmobManager.getInstance().showNative(
                        this@SplashActivity,
                        nativeAd,
                        binding.frNativeAd,
                        AdmobManager.NativeAdType.BIG
                    )
                    Log.d("Refresh","ShowRefreshHowToUse")
                }

                override fun onAdFailedToLoad(i: LoadAdError) {
                    super.onAdFailedToLoad(i)
                    binding.frNativeAd.visibility = View.GONE
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

    private fun handlePreloadAds() {
        if (languageCode == null) {
            preloadNativeLanguage1()
            preloadNativeLanguage2()
        } else if (!isOnboardComplete) {
            preloadNativeOb1()
            preloadNativeFullOb3()
        } else if (!isPermissionComplete) {
            preloadNativePermission()
        }
    }

    private fun preloadNativePermission() {
        if (InternetUtil.isNetworkAvailable(this)) {
            AdmobManager.getInstance()
                .preloadAlternateNative(this, onb5NativePermissionAds, object : AdCallback() {
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        AdCache.getInstance().ob5NativePermissionHigh = nativeAd
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        //                            logEvent("onboard6_native_view")
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        //                            logEvent("onboard6_native_click")

                    }
                })
        }
    }

    private fun preloadNativeLanguage1() {
        if (InternetUtil.isNetworkAvailable(this)) {
            AdmobManager.getInstance()
                .preloadAlternateNative(this, lfo1NativeAds, object : AdCallback() {
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        AdCache.getInstance().lfo1Native = nativeAd
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
//                        logEvent("language2_ad_native_view")
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
//                        logEvent("language2_ad_native_click")

                    }
                })
        }
    }

    private fun preloadNativeLanguage2() {
        if (InternetUtil.isNetworkAvailable(this)) {
            AdmobManager.getInstance()
                .preloadAlternateNative(this, lfo2NativeHighAds, object : AdCallback() {
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        AdCache.getInstance().lfo2NativeHigh = nativeAd
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
//                        logEvent("language2_ad_native_view")
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
//                        logEvent("language2_ad_native_click")

                    }
                })
        }
    }

    private fun preloadNativeOb1() {
        if (InternetUtil.isNetworkAvailable(this)) {
            AdmobManager.getInstance()
                .preloadAlternateNative(this, onb1NativeAds, object : AdCallback() {
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

    private fun preloadNativeFullOb3() {
        if (RemoteConfigManager.instance?.isShowInterOverFullScreenNativeOnboard == true) {
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
                            AdCache.getInstance().ob3NativeHigh = nativeAd
                        }
                    })
            }
        }
    }
}
package com.ruicomp.gpsalarm.feature.outer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.utils.InternetUtil
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.ruicomp.gpsalarm.AdCache
import com.ruicomp.gpsalarm.AdIds
import com.ruicomp.gpsalarm.R
import com.ruicomp.gpsalarm.databinding.ActivityOnboardBinding
import com.ruicomp.gpsalarm.remote_config.RemoteConfigManager
import com.ruicomp.gpsalarm.utils.dlog
import com.ruicomp.gpsalarm.utils.hideNavigationBar
import com.ruicomp.gpsalarm.utils.hideSystemBar

class OnboardActivity() : AppCompatActivity(), OnboardFragment.OnEventClickListener {

    private lateinit var binding: ActivityOnboardBinding
    private lateinit var onboardingPagerAdapter: OnboardingPagerAdapter
    private val totalPages = 4
    private val onb2NativeAds = arrayListOf(AdIds.ob2_native_high, AdIds.ob2_native)
    private val onb3NativeFull = arrayListOf(AdIds.ob3_native_high2, AdIds.ob3_native)
    private val onb3InterAds by lazy { arrayListOf(AdIds.ob3_inter_high, AdIds.ob3_inter) }
    private val onb4NativeAds = arrayListOf(AdIds.ob4_native_high, AdIds.ob4_native)
    private val onb5NativePermissionAds = arrayListOf(AdIds.ob5_native_high, AdIds.ob5_native)
    private var isHandleAds34 = false
    private var isHandleAds4 = false
    private var isHandleAds2 = false
    private var isHandleAdsPermission = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardBinding.inflate(layoutInflater)
        setContentView(binding.root)



        setupViewPager()
//        setupButton()
    }

    private fun setupViewPager() {
        onboardingPagerAdapter = OnboardingPagerAdapter(this, totalPages)
        binding.viewPager.adapter = onboardingPagerAdapter.apply {
            addFragment(
                OnboardFragment(
                    0,
                    R.drawable.ic_ob11,
                    R.string.onboard_title_1,
                    R.string.onboard_desc_1
                )
            )
            addFragment(
                OnboardFragment(
                    1,
                    R.drawable.ic_ob21,
                    R.string.onboard_title_2,
                    R.string.onboard_desc_2
                )
            )
            addFragment(OnboardFragment(2))
            addFragment(
                OnboardFragment(
                    3,
                    R.drawable.ic_ob41,
                    R.string.onboard_title_3,
                    R.string.onboard_desc_3
                )
            )
        }

//        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

//        binding.viewPager.offscreenPageLimit = 0
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when (position) {
                    0 -> handleAds2()
                    1 -> handleAds34()
                    2 -> {
                        handleAds4()
                        if (
                            RemoteConfigManager.instance?.isShowInterOverFullScreenNativeOnboard == true &&
                            RemoteConfigManager.instance?.isShowInterOnboard3 == true
                        ) {
                            showInter(AdCache.getInstance().ob3Inter)
                            return
                        }
                    }
                    3 -> handleAdsPermission()
                }

            }
        })

    }

    fun handleAds34() {
        if (!isHandleAds34) {
            isHandleAds34 = true
            preloadNativeFullOb3()
            preloadNativeOb4()
        }
    }

    fun handleAds2() {
        if (!isHandleAds2) {
            preloadNativeOb2()
            isHandleAds2 = true
        }
    }

    fun handleAds4() {
        if (!isHandleAds4) {
            isHandleAds4 = true
            preloadNativeOb4()
        }
    }

    fun handleAdsPermission() {
        if (!isHandleAdsPermission) {
            isHandleAdsPermission = true
            preloadNativePermission()
        }
    }

    private fun preloadNativeOb2() {
        if (AdCache.getInstance().ob2NativeHigh != null) return
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
            if (AdCache.getInstance().ob3NativeHigh == null && AdCache.getInstance().ob3NativeHigh1 == null) {
                if (InternetUtil.isNetworkAvailable(this)) {
                    AdmobManager.getInstance()
                        .preloadFullScreenAlternateNative(
                            this,
                            onb3NativeFull,
                            object : AdCallback() {
                                override fun onNativeAds(nativeAd: NativeAd?) {
                                    super.onNativeAds(nativeAd)
                                    AdCache.getInstance().ob3NativeHigh2AndNative = nativeAd
                                }

                                override fun onAdImpression() {
                                    super.onAdImpression()
                                }

                                override fun onAdClicked() {
                                    super.onAdClicked()
                                }
                            })
                }
            }
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

    private fun preloadNativeOb4() {
        if (AdCache.getInstance().ob4NativeHigh != null) return
        if (InternetUtil.isNetworkAvailable(this)) {
            AdmobManager.getInstance()
                .preloadAlternateNative(this, onb4NativeAds, object : AdCallback() {
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        AdCache.getInstance().ob4NativeHigh = nativeAd
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
    //                            logEvent("onboard5_native_view")
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
    //                            logEvent("onboard5_native_click")

                    }
                })
        }
    }

    private fun showInter(interstitialAd: InterstitialAd?) {
        if (interstitialAd == null) return
        AdmobManager.getInstance().showInterstitial(
            this,
            interstitialAd,
            object : AdCallback() {
                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
//                    logEvent("splash_ad_inter_view")
                }
                override fun onNextScreen() {
                    super.onNextScreen()
                    AdCache.getInstance().ob3Inter = null
                    binding.viewPager.currentItem += 1
                }
                override fun onClickClose() {
                    super.onClickClose()
//                    logEvent("splash_ad_inter_close_click")
                }
                override fun onAdClicked() {
                    super.onAdClicked()
//                    logEvent("splash_ad_inter_click")
                }
            })

    }


    override fun onButtonClicked(nPage: Int) {
        val currentItem = binding.viewPager.currentItem
        dlog("onButtonClicked: click next page: $currentItem")
        if (currentItem < totalPages - 1) {
            binding.viewPager.currentItem = currentItem + 1
        } else {
            gotoNext()
        }
    }

    private fun gotoNext() {
        val intent = Intent(this, PermissionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        window.hideSystemBar()
        window.statusBarColor = getColor(R.color.status_bar_below30)
    }

    private fun setupButton() {
//        binding.buttonNext.setOnClickListener {
//            val currentItem = binding.viewPager.currentItem
//            if (currentItem < totalPages - 1) {
//                binding.viewPager.currentItem = currentItem + 1
//            } else {
//                Toast.makeText(this, "Goto Home", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun updateButtonText(position: Int) {
//        if (position == totalPages - 1) {
//            binding.buttonNext.setText(R.string.get_started)
//        } else {
//            binding.buttonNext.setText(R.string.next)
//        }
    }
}

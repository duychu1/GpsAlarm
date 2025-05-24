package com.ruicomp.gpsalarm.feature.outer

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.utils.InternetUtil
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.ruicomp.gpsalarm.AdCache
import com.ruicomp.gpsalarm.AdIds
import com.ruicomp.gpsalarm.AppSession
import com.ruicomp.gpsalarm.R
import com.ruicomp.gpsalarm.utils.dlog
import com.ruicomp.gpsalarm.databinding.FragmentOnboardContentBinding
import com.ruicomp.gpsalarm.remote_config.RemoteConfigManager

class OnboardFragment(
    val position: Int = 0,
    @DrawableRes val imageRes: Int = R.drawable.ic_placeholder1,
    @StringRes val titleRes: Int = R.string.onboard_title_1,
    @StringRes val descriptionRes: Int = R.string.onboard_desc_1,
) : Fragment() {

    private var _binding: FragmentOnboardContentBinding? = null
    private val binding get() = _binding!!
    private val positionAdFullscreen = 2
    private val onb1NativeAds = arrayListOf(AdIds.ob1_native_high, AdIds.ob1_native)
    private val onb2NativeAds = arrayListOf(AdIds.ob2_native_high, AdIds.ob2_native)
    private val onb3NativeFull = arrayListOf(AdIds.ob3_native_high, AdIds.ob3_native)
    private val onb3InterAds by lazy { arrayListOf(AdIds.ob3_inter_high, AdIds.ob3_inter) }
    private val onb4NativeAds = arrayListOf(AdIds.ob4_native_high, AdIds.ob4_native)
    private var isFirstResume = true

    interface OnEventClickListener {
        fun onButtonClicked(nPage: Int) // Define method(s) the Activity must implement
        // fun onOtherEvent()
    }

    // 2. Hold a reference to the listener
    private var listener: OnEventClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 3. Check if the hosting Activity implements the interface
        if (context is OnEventClickListener) {
            listener = context
        } else {
            Log.w("OnboardContentFragment", "Hosting Activity does not implement OnEventClickListener")
            // Optional: Throw an exception if the Activity doesn't implement it
             throw RuntimeException("$context must implement OnEventClickListener")
            // Or just log a warning
        }
    }

    override fun onDetach() {
        super.onDetach()
        // 5. Release the listener reference to prevent memory leaks
        listener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getMaxHeightScreen(): Int {
        return try {
            val displayMetrics = DisplayMetrics()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                requireContext().display.getRealMetrics(displayMetrics)
            } else {
                @Suppress("DEPRECATION")
                requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            }
            val screenHeight = displayMetrics.heightPixels
            (screenHeight*0.6).toInt()
        } catch (e: Exception) {
            val contentHeight = requireContext().resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._350sdp)
            contentHeight
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dlog("onViewCreated onboardFragment: position=$position")

        binding.imageViewOnboard.setImageResource(imageRes)
        binding.textViewTitle.setText(titleRes)
        binding.textViewDescription.setText(descriptionRes)

        if (position > positionAdFullscreen) {
            binding.llindicator.getChildAt(position - 1).isSelected = true
        } else {
            binding.llindicator.getChildAt(position).isSelected = true
        }

        if (position == 3) {
            binding.buttonNext.setText(R.string.get_started)
        } else {
            binding.buttonNext.setText(R.string.next)
        }

        binding.buttonNext.setOnClickListener {
            listener?.onButtonClicked(position) //not use param
        }

        when (position) {
            0 -> {
                AdmobManager.getInstance().showNative(
                    context,
                    AdCache.getInstance().ob1Native,
                    binding.frNativeAd,
                    AdmobManager.NativeAdType.BIG,
                    true
                )
                Log.d("nativeOB", "ob1NativeHigh")
            }

            1 -> {
                AdmobManager.getInstance().showNative(
                    context,
                    AdCache.getInstance().ob2NativeHigh,
                    binding.frNativeAd,
                    AdmobManager.NativeAdType.BIG,
                    true
                )
                Log.d("nativeOB", "ob2NativeHigh")
            }

            2 -> {
                run {
                    binding.ctMain.visibility = View.INVISIBLE
                    binding.ctAd.visibility = View.VISIBLE
//                    if(AppSession.clickNext && (position == AppSession.posClickNext+2)) {
//                        AppSession.clickNext = false
//                        return
//                    }

                    val ob3NativeFullAd = AdCache.getInstance().ob3NativeHigh
                        ?: AdCache.getInstance().ob3NativeHigh1
                        ?: AdCache.getInstance().ob3NativeHigh2AndNative

                    if (RemoteConfigManager.instance!!.isShowNativeFullScreenOnboard &&
                        InternetUtil.isNetworkAvailable(requireContext()) &&
                        ob3NativeFullAd != null
                        ) {
                        Log.d("showFullNative", "Showing full-screen native ad")
                        ob3NativeFullAd.let {
                            AdmobManager.getInstance().showNative(
                                context,
                                it,
                                binding.frNativeFull,
                                AdmobManager.NativeAdType.FULLSCREEN
                            )
                        }
                    }
                }
            }

            3 -> {
                AdmobManager.getInstance().showNative(
                    context,
                    AdCache.getInstance().ob4NativeHigh,
                    binding.frNativeAd,
                    AdmobManager.NativeAdType.BIG,
                    true
                )
                Log.d("nativeOB", "ob4NativeHigh")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isFirstResume) {
            isFirstResume = false
        } else {
            when (position) {
                0 -> {
                    context?.let { safeContext ->
                        AdmobManager.getInstance().preloadAlternateNative(
                            safeContext,
                            onb1NativeAds,
                            object : AdCallback() {
                                override fun onNativeAds(nativeAd: NativeAd?) {
                                    super.onNativeAds(nativeAd)
                                    if (isAdded) {
                                        AdmobManager.getInstance().showNative(
                                            safeContext,
                                            nativeAd,
                                            binding.frNativeAd,
                                            AdmobManager.NativeAdType.BIG,
                                            true
                                        )
                                    }
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
                1 -> {
                    context?.let { safeContext ->
                        AdmobManager.getInstance().preloadAlternateNative(
                            safeContext,
                            onb2NativeAds,
                            object : AdCallback() {
                                override fun onNativeAds(nativeAd: NativeAd?) {
                                    super.onNativeAds(nativeAd)
                                    if (isAdded) {
                                        AdmobManager.getInstance().showNative(
                                            safeContext,
                                            nativeAd,
                                            binding.frNativeAd,
                                            AdmobManager.NativeAdType.BIG,
                                            true
                                        )
                                    }
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
                2 -> {
                    context?.let { safeContext ->
                        AdmobManager.getInstance().preloadFullScreenAlternateNative(
                            safeContext,
                            onb3NativeFull,
                            object : AdCallback() {
                                override fun onNativeAds(nativeAd: NativeAd?) {
                                    super.onNativeAds(nativeAd)
                                    if (isAdded) {
                                        AdmobManager.getInstance().showNative(
                                            safeContext,
                                            nativeAd,
                                            binding.frNativeFull,
                                            AdmobManager.NativeAdType.FULLSCREEN
                                        )
                                    }
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
                3 -> {
                    context?.let { safeContext ->
                        AdmobManager.getInstance().preloadAlternateNative(
                            safeContext,
                            onb4NativeAds,
                            object : AdCallback() {
                                override fun onNativeAds(nativeAd: NativeAd?) {
                                    super.onNativeAds(nativeAd)
                                    if (isAdded) {
                                        AdmobManager.getInstance().showNative(
                                            safeContext,
                                            nativeAd,
                                            binding.frNativeAd,
                                            AdmobManager.NativeAdType.BIG,
                                            true
                                        )
                                    }
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
    }
}

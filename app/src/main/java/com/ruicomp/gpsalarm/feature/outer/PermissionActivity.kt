package com.ruicomp.gpsalarm.feature.outer // Change to your actual package name

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.google.android.gms.ads.nativead.NativeAd
import com.ruicomp.gpsalarm.AdCache
import com.ruicomp.gpsalarm.AdIds
import com.ruicomp.gpsalarm.R
import com.ruicomp.gpsalarm.databinding.ActivityPermissionBinding // Import generated binding class
import com.ruicomp.gpsalarm.datastore2.DataStoreKeys
import com.ruicomp.gpsalarm.datastore2.DataStorePreferences
import com.ruicomp.gpsalarm.utils.PermissionUtils
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PermissionActivity : BaseActivityNonBinding() {
    private val dataStorePreferences: DataStorePreferences by inject()

    private lateinit var binding: ActivityPermissionBinding
    private var isCameraPermissionGranted = false
    private var isLocationPermissionGranted = false
    private var isFirstResume = true
    private val nativePermissionList = arrayListOf(AdIds.ob5_native_high, AdIds.ob5_native)

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            isCameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false
            isLocationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            updateUi()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.f0f0f0)
        AdmobManager.getInstance().showNative(
            this,
            AdCache.getInstance().ob5NativePermissionHigh,
            binding.frNativeAd,
            AdmobManager.NativeAdType.MEDIUM
        )
        checkInitialPermissions()
        updateUi()
        setupListeners()
        lifecycleScope.launch {
            dataStorePreferences.saveBoolean(DataStoreKeys.IS_ONBOARD_COMPLETE, true)
        }

    }

    private fun checkInitialPermissions() {
        isCameraPermissionGranted = PermissionUtils.hasPermissions(this, Manifest.permission.CAMERA)
        isLocationPermissionGranted = PermissionUtils.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun updateUi() {
        binding.switchCameraPermission.isChecked = isCameraPermissionGranted
        binding.switchLocationPermission.isChecked = isLocationPermissionGranted
//        binding.btnGetStarted.isEnabled = isCameraPermissionGranted && isLocationPermissionGranted
    }

    private fun setupListeners() {
        binding.cardCameraPermission.setOnClickListener {
            handlePermissionsRequest(Manifest.permission.CAMERA)
        }
        binding.cardLocationPermission.setOnClickListener {
            handlePermissionsRequest(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        binding.btnGetStarted.setOnClickListener {
            lifecycleScope.launch {
                dataStorePreferences.saveBoolean(DataStoreKeys.IS_PERMISSION_COMPLETE, true)
            }
            navigateToNextScreen()
        }
    }

    private fun handlePermissionsRequest(vararg permissions: String) {
        PermissionUtils.requestPermissionsWithRationale(
            this@PermissionActivity,
            arrayOf(*permissions),
            requestPermissionsLauncher,
            "These permissions are required for the app to function properly.",
            onPermissionsGranted = { updateUi() }
        )
    }

    private fun navigateToNextScreen() {
        startActivity(Intent(this, PremiumActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        finish()
    }


    private fun loadAlternateNative() {
        Log.d("Refresh","Create Refresh")
        AdmobManager.getInstance().preloadAlternateNative(
            this,
            nativePermissionList,
            object : AdCallback(){
                override fun onNativeAds(nativeAd: NativeAd?) {
                    super.onNativeAds(nativeAd)
                    AdmobManager.getInstance().showNative(
                        this@PermissionActivity,
                        nativeAd,
                        binding.frNativeAd,
                        AdmobManager.NativeAdType.BIG
                    )
                    Log.d("Refresh","ShowRefreshHowToUse")
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

    override fun onResume() {
        super.onResume()
        if (isFirstResume) {
            isFirstResume = false
        } else {
            loadAlternateNative()
        }
    }
}

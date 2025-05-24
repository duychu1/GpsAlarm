package com.ruicomp.gpsalarm.remote_config

import android.app.Activity
import android.os.Build
import android.util.Log
import com.common.control.utils.InternetUtil
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.ruicomp.gpsalarm.R

class RemoteConfigManager {
    private var remoteConfig: FirebaseRemoteConfig? = null
    private var isLoading = false
    var isShowNativeFullScreenOnboard: Boolean = true
    var isShowInterOverFullScreenNativeOnboard: Boolean = true
    var isShowInterOnboard3: Boolean = true

    var _101_splash_n_native_high: Boolean = true
    var _101_splash_n_native: Boolean = true
    var _102_splash_n_open_app: Boolean = true
    var _201_lfo_n_native_high: Boolean = true
    var _201_lfo_n_native: Boolean = true
    var _202_lfo_n_native_high: Boolean = true
    var _202_lfo_n_native_high_1: Boolean = true
    var _202_lfo_n_native_high_2: Boolean = true
    var _202_lfo_n_native: Boolean = true
    var _301_ob1_n_native_high: Boolean = true
    var _301_ob1_n_native: Boolean = true
    var _302_ob2_n_native_high: Boolean = true
    var _302_ob2_n_native: Boolean = true
    var _303_ob3_n_inter_high: Boolean = true
    var _303_ob3_n_inter: Boolean = true
    var _303_ob3_n_native_full_high: Boolean = true
    var _303_ob3_n_native_full_high_1: Boolean = true
    var _303_ob3_n_native_full_high_2: Boolean = true
    var _303_ob3_n_native_full: Boolean = true
    var _304_ob4_n_native_high: Boolean = true
    var _304_ob4_n_native: Boolean = true
    var _305_ob5_n_native_high: Boolean = true
    var _305_ob5_n_native: Boolean = true
    var _401_app_o_reopen: Boolean = true

    var _501_home_o_native: Boolean = true
    var _501_home_o_native_high: Boolean = true
    var _502_home_o_banner: Boolean = true
    var _502_home_o_banner_high: Boolean = true

    // Added based on AdIds.kt
    var _601_camera_o_banner: Boolean = true
    var _601_camera_o_banner_high: Boolean = true
    var _602_camera_o_inter: Boolean = true
    var _602_camera_o_inter_high: Boolean = true

    var _701_direction_o_banner: Boolean = true
    var _701_direction_o_banner_high: Boolean = true

    var _801_template_o_banner: Boolean = true
    var _801_template_o_banner_high: Boolean = true

    var _901_manual_entry_o_banner: Boolean = true
    var _901_manual_entry_o_banner_high: Boolean = true
    var _902_manual_entry_o_inter: Boolean = true 
    var _902_manual_entry_o_inter_high: Boolean = true 

    var _1001_automatic_o_banner: Boolean = true
    var _1001_automatic_o_banner_high: Boolean = true
    var _1002_automatic_o_inter: Boolean = true
    var _1002_automatic_o_inter_high: Boolean = true 

    var _1101_setting_o_native: Boolean = true
    var _1101_setting_o_native_high: Boolean = true

    var _1201_language_o_native: Boolean = true
    var _1201_language_o_native_high: Boolean = true

    var _1301_rate_o_native: Boolean = true
    var _1301_rate_o_native_high: Boolean = true

    var _1401_share_o_native: Boolean = true
    var _1401_share_o_native_high: Boolean = true

    var _1501_image_screen_o_native: Boolean = true
    var _1501_image_screen_o_native_high: Boolean = true

    var _702_direction_o_inter: Boolean = true
    var _702_direction_o_inter_high: Boolean = true

    var _802_template_o_native: Boolean = true
    var _802_template_o_native_high: Boolean = true

    var _1601_rewarded_premium: Boolean = true
    var _1601_rewarded_premium_high: Boolean = true

    var time_load_banner: Long = 0

    fun getIsLoading(): Boolean = isLoading

    fun loadRemote() {
        if (isLoading) {
            return
        }
        isLoading = true
        val config = FirebaseRemoteConfig.getInstance()
        val configSettings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(0).build()
        config.setConfigSettingsAsync(configSettings)
        config.setDefaultsAsync(R.xml.default_config)
        config.fetchAndActivate().addOnCompleteListener { task ->
            isLoading = false
            if (task.isSuccessful) {
                remoteConfig = FirebaseRemoteConfig.getInstance()
                //load ads remote
                loadAdsRemote(config)

                time_load_banner = config.getLong("time_load_banner")
                Log.d("remotconfigLogger: ", "time_load_banner - $time_load_banner")

                isShowNativeFullScreenOnboard =
                    _303_ob3_n_native_full_high || _303_ob3_n_native_full_high_1 || _303_ob3_n_native_full_high_2 || _303_ob3_n_native_full

                isShowInterOnboard3 = _303_ob3_n_inter_high || _303_ob3_n_inter

            } else {
                loadRemote()
            }
        }
    }

    private fun loadAdsRemote(config: FirebaseRemoteConfig) {
        _101_splash_n_native_high = config.getBoolean("_101_splash_n_native_high")
        Log.d("remotconfigLogger: ", "_101_splash_n_native_high - $_101_splash_n_native_high")
        _101_splash_n_native = config.getBoolean("_101_splash_n_native")
        Log.d("remotconfigLogger: ", "_101_splash_n_native - $_101_splash_n_native")
        _102_splash_n_open_app = config.getBoolean("_102_splash_n_open_app")
        Log.d("remotconfigLogger: ", "_102_splash_n_open_app - $_102_splash_n_open_app")
        _201_lfo_n_native_high = config.getBoolean("_201_lfo_n_native_high")
        Log.d("remotconfigLogger: ", "_201_lfo_n_native_high - $_201_lfo_n_native_high")
        _201_lfo_n_native = config.getBoolean("_201_lfo_n_native")
        Log.d("remotconfigLogger: ", "_201_lfo_n_native - $_201_lfo_n_native")
        _202_lfo_n_native_high = config.getBoolean("_202_lfo_n_native_high")
        Log.d("remotconfigLogger: ", "_202_lfo_n_native_high - $_202_lfo_n_native_high")
        _202_lfo_n_native_high_1 = config.getBoolean("_202_lfo_n_native_high_1")
        Log.d("remotconfigLogger: ", "_202_lfo_n_native_high_1 - $_202_lfo_n_native_high_1")
        _202_lfo_n_native_high_2 = config.getBoolean("_202_lfo_n_native_high_2")
        Log.d("remotconfigLogger: ", "_202_lfo_n_native_high_2 - $_202_lfo_n_native_high_2")
        _202_lfo_n_native = config.getBoolean("_202_lfo_n_native")
        Log.d("remotconfigLogger: ", "_202_lfo_n_native - $_202_lfo_n_native")
        _301_ob1_n_native_high = config.getBoolean("_301_ob1_n_native_high")
        Log.d("remotconfigLogger: ", "_301_ob1_n_native_high - $_301_ob1_n_native_high")
        _301_ob1_n_native = config.getBoolean("_301_ob1_n_native")
        Log.d("remotconfigLogger: ", "_301_ob1_n_native - $_301_ob1_n_native")
        _302_ob2_n_native_high = config.getBoolean("_302_ob2_n_native_high")
        Log.d("remotconfigLogger: ", "_302_ob2_n_native_high - $_302_ob2_n_native_high")
        _302_ob2_n_native = config.getBoolean("_302_ob2_n_native")
        Log.d("remotconfigLogger: ", "_302_ob2_n_native - $_302_ob2_n_native")
        _303_ob3_n_inter_high = config.getBoolean("_303_ob3_n_inter_high")
        Log.d("remotconfigLogger: ", "_303_ob3_n_inter_high - $_303_ob3_n_inter_high")
        _303_ob3_n_inter = config.getBoolean("_303_ob3_n_inter")
        Log.d("remotconfigLogger: ", "_303_ob3_n_inter - $_303_ob3_n_inter")
        _303_ob3_n_native_full_high = config.getBoolean("_303_ob3_n_native_full_high")
        Log.d("remotconfigLogger: ", "_303_ob3_n_native_full_high - $_303_ob3_n_native_full_high")
        _303_ob3_n_native_full_high_1 = config.getBoolean("_303_ob3_n_native_full_high_1")
        Log.d("remotconfigLogger: ", "_303_ob3_n_native_full_high_1 - $_303_ob3_n_native_full_high_1")
        _303_ob3_n_native_full_high_2 = config.getBoolean("_303_ob3_n_native_full_high_2")
        Log.d("remotconfigLogger: ", "_303_ob3_n_native_full_high_2 - $_303_ob3_n_native_full_high_2")
        _303_ob3_n_native_full = config.getBoolean("_303_ob3_n_native_full")
        Log.d("remotconfigLogger: ", "_303_ob3_n_native_full - $_303_ob3_n_native_full")
        _304_ob4_n_native_high = config.getBoolean("_304_ob4_n_native_high")
        Log.d("remotconfigLogger: ", "_304_ob4_n_native_high - $_304_ob4_n_native_high")
        _304_ob4_n_native = config.getBoolean("_304_ob4_n_native")
        Log.d("remotconfigLogger: ", "_304_ob4_n_native - $_304_ob4_n_native")
        _305_ob5_n_native_high = config.getBoolean("_305_ob5_n_native_high")
        Log.d("remotconfigLogger: ", "_305_ob5_n_native_high - $_305_ob5_n_native_high")
        _305_ob5_n_native = config.getBoolean("_305_ob5_n_native")
        Log.d("remotconfigLogger: ", "_305_ob5_n_native - $_305_ob5_n_native")
        _401_app_o_reopen = config.getBoolean("_401_app_o_reopen")
        Log.d("remotconfigLogger: ", "_401_app_o_reopen - $_401_app_o_reopen")

        _501_home_o_native = config.getBoolean("_501_home_o_native")
        Log.d("remotconfigLogger: ", "_501_home_o_native - $_501_home_o_native")
        _501_home_o_native_high = config.getBoolean("_501_home_o_native_high")
        Log.d("remotconfigLogger: ", "_501_home_o_native_high - $_501_home_o_native_high")
        _502_home_o_banner = config.getBoolean("_502_home_o_banner")
        Log.d("remotconfigLogger: ", "_502_home_o_banner - $_502_home_o_banner")
        _502_home_o_banner_high = config.getBoolean("_502_home_o_banner_high")
        Log.d("remotconfigLogger: ", "_502_home_o_banner_high - $_502_home_o_banner_high")

        _601_camera_o_banner = config.getBoolean("_601_camera_o_banner")
        Log.d("remotconfigLogger: ", "_601_camera_o_banner - $_601_camera_o_banner")
        _601_camera_o_banner_high = config.getBoolean("_601_camera_o_banner_high")
        Log.d("remotconfigLogger: ", "_601_camera_o_banner_high - $_601_camera_o_banner_high")
        _602_camera_o_inter = config.getBoolean("_602_camera_o_inter")
        Log.d("remotconfigLogger: ", "_602_camera_o_inter - $_602_camera_o_inter")
        _602_camera_o_inter_high = config.getBoolean("_602_camera_o_inter_high")
        Log.d("remotconfigLogger: ", "_602_camera_o_inter_high - $_602_camera_o_inter_high")

        _701_direction_o_banner = config.getBoolean("_701_direction_o_banner")
        Log.d("remotconfigLogger: ", "_701_direction_o_banner - $_701_direction_o_banner")
        _701_direction_o_banner_high = config.getBoolean("_701_direction_o_banner_high")
        Log.d("remotconfigLogger: ", "_701_direction_o_banner_high - $_701_direction_o_banner_high")

        _801_template_o_banner = config.getBoolean("_801_template_o_banner")
        Log.d("remotconfigLogger: ", "_801_template_o_banner - $_801_template_o_banner")
        _801_template_o_banner_high = config.getBoolean("_801_template_o_banner_high")
        Log.d("remotconfigLogger: ", "_801_template_o_banner_high - $_801_template_o_banner_high")

        _901_manual_entry_o_banner = config.getBoolean("_901_manual_entry_o_banner")
        Log.d("remotconfigLogger: ", "_901_manual_entry_o_banner - $_901_manual_entry_o_banner")
        _901_manual_entry_o_banner_high = config.getBoolean("_901_manual_entry_o_banner_high")
        Log.d("remotconfigLogger: ", "_901_manual_entry_o_banner_high - $_901_manual_entry_o_banner_high")
        _902_manual_entry_o_inter = config.getBoolean("_902_manual_entry_o_inter")
        Log.d("remotconfigLogger: ", "_902_manual_entry_o_inter - $_902_manual_entry_o_inter")
        _902_manual_entry_o_inter_high = config.getBoolean("_902_manual_entry_o_inter_high")
        Log.d("remotconfigLogger: ", "_902_manual_entry_o_inter_high - $_902_manual_entry_o_inter_high")

        _1001_automatic_o_banner = config.getBoolean("_1001_automatic_o_banner")
        Log.d("remotconfigLogger: ", "_1001_automatic_o_banner - $_1001_automatic_o_banner")
        _1001_automatic_o_banner_high = config.getBoolean("_1001_automatic_o_banner_high")
        Log.d("remotconfigLogger: ", "_1001_automatic_o_banner_high - $_1001_automatic_o_banner_high")
        _1002_automatic_o_inter = config.getBoolean("_1002_automatic_o_inter")
        Log.d("remotconfigLogger: ", "_1002_automatic_o_inter - $_1002_automatic_o_inter")
        _1002_automatic_o_inter_high = config.getBoolean("_1002_automatic_o_inter_high")
        Log.d("remotconfigLogger: ", "_1002_automatic_o_inter_high - $_1002_automatic_o_inter_high")

        _1101_setting_o_native = config.getBoolean("_1101_setting_o_native")
        Log.d("remotconfigLogger: ", "_1101_setting_o_native - $_1101_setting_o_native")
        _1101_setting_o_native_high = config.getBoolean("_1101_setting_o_native_high")
        Log.d("remotconfigLogger: ", "_1101_setting_o_native_high - $_1101_setting_o_native_high")

        _1201_language_o_native = config.getBoolean("_1201_language_o_native")
        Log.d("remotconfigLogger: ", "_1201_language_o_native - $_1201_language_o_native")
        _1201_language_o_native_high = config.getBoolean("_1201_language_o_native_high")
        Log.d("remotconfigLogger: ", "_1201_language_o_native_high - $_1201_language_o_native_high")

        _1301_rate_o_native = config.getBoolean("_1301_rate_o_native")
        Log.d("remotconfigLogger: ", "_1301_rate_o_native - $_1301_rate_o_native")
        _1301_rate_o_native_high = config.getBoolean("_1301_rate_o_native_high")
        Log.d("remotconfigLogger: ", "_1301_rate_o_native_high - $_1301_rate_o_native_high")

        _1401_share_o_native = config.getBoolean("_1401_share_o_native")
        Log.d("remotconfigLogger: ", "_1401_share_o_native - $_1401_share_o_native")
        _1401_share_o_native_high = config.getBoolean("_1401_share_o_native_high")
        Log.d("remotconfigLogger: ", "_1401_share_o_native_high - $_1401_share_o_native_high")

        _1501_image_screen_o_native = config.getBoolean("_1501_image_screen_o_native")
        Log.d("remotconfigLogger: ", "_1501_image_screen_o_native - $_1501_image_screen_o_native")
        _1501_image_screen_o_native_high = config.getBoolean("_1501_image_screen_o_native_high")
        Log.d("remotconfigLogger: ", "_1501_image_screen_o_native_high - $_1501_image_screen_o_native_high")

        _702_direction_o_inter = config.getBoolean("_702_direction_o_inter")
        Log.d("remotconfigLogger: ", "_702_direction_o_inter - $_702_direction_o_inter")
        _702_direction_o_inter_high = config.getBoolean("_702_direction_o_inter_high")
        Log.d("remotconfigLogger: ", "_702_direction_o_inter_high - $_702_direction_o_inter_high")

        _802_template_o_native = config.getBoolean("_802_template_o_native")
        Log.d("remotconfigLogger: ", "_802_template_o_native - $_802_template_o_native")
        _802_template_o_native_high = config.getBoolean("_802_template_o_native_high")
        Log.d("remotconfigLogger: ", "_802_template_o_native_high - $_802_template_o_native_high")

        _1601_rewarded_premium = config.getBoolean("_1601_rewarded_premium")
        Log.d("remotconfigLogger: ", "_1601_rewarded_premium - $_1601_rewarded_premium")
        _1601_rewarded_premium_high = config.getBoolean("_1601_rewarded_premium_high")
        Log.d("remotconfigLogger: ", "_1601_rewarded_premium_high - $_1601_rewarded_premium_high")
    }

    fun loadIsShowConsent(activity: Activity, callback: BooleanCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!InternetUtil.isNetworkAvailable(activity)) {
                callback.onResult(false)
                return
            }
        }
        if (isLoading && remoteConfig == null) {
            Thread {
                while (isLoading || remoteConfig == null) {
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                activity.runOnUiThread {
                    if (remoteConfig != null) {
                        callback.onResult(remoteConfig!!.getBoolean(IS_SHOW_CONSENT))
                    }
                }
            }.start()
        } else {
            if (remoteConfig != null) {
                callback.onResult(remoteConfig!!.getBoolean(IS_SHOW_CONSENT))
            }
        }
    }

    fun loadLimitFunctionInAppCount(activity: Activity, callback: NumberCallback) {
        if (isLoading && remoteConfig == null) {
            Thread {
                while (isLoading && remoteConfig == null) {
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                activity.runOnUiThread {
                    callback.onResult(
                        remoteConfig!!.getLong(
                            LIMIT_FUNCTION_IN_APP
                        )
                    )
                }
            }.start()
        } else {
            callback.onResult(remoteConfig!!.getLong(LIMIT_FUNCTION_IN_APP))
        }
    }

    fun loadReshowGDPRSplashCount(activity: Activity, callback: NumberCallback) {
        if (isLoading && remoteConfig == null) {
            Thread {
                while (isLoading || remoteConfig == null) {
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                activity.runOnUiThread { callback.onResult(remoteConfig!!.getLong(RESHOW_GDPR_SPLASH)) }
            }.start()
        } else {
            callback.onResult(remoteConfig!!.getLong(RESHOW_GDPR_SPLASH))
        }
    }

    val isShowConsent: Boolean
        get() = remoteConfig != null && remoteConfig!!.getBoolean(IS_SHOW_CONSENT)

    fun limitFunctionClickCount(): Long {
        return if (remoteConfig == null) {
            0
        } else {
            remoteConfig!!.getLong(LIMIT_FUNCTION_IN_APP)
        }
    }

    interface BooleanCallback {
        fun onResult(value: Boolean)
    }

    interface NumberCallback {
        fun onResult(value: Long)
    }

    interface StringCallback {
        fun onResult(value: String?)
    }

    fun fetchAndActivate(callback: () -> Unit) {
        remoteConfig?.let { config ->
            config.fetchAndActivate()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback.invoke()
                    }
                }
        } ?: run {
            Log.e("RemoteConfig", "FirebaseRemoteConfig is not initialized.")
        }
    }

    fun getLanguageOrder(): String {
        return remoteConfig?.getString("language_order") ?: ""
    }

    companion object {
        private const val IS_SHOW_CONSENT = "is_show_consent"
        private const val LIMIT_FUNCTION_IN_APP = "limit_function_in_app"
        private const val RESHOW_GDPR_SPLASH = "reshow_gdpr_splash"
        private var INSTANCE: RemoteConfigManager? = null

        @JvmStatic
        val instance: RemoteConfigManager?
            get() {
                if (INSTANCE == null) {
                    INSTANCE = RemoteConfigManager()
                }
                return INSTANCE
            }
    }
}
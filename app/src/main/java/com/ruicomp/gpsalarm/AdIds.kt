package com.ruicomp.gpsalarm

import com.common.control.manager.AppOpenManager
import com.ruicomp.gpsalarm.remote_config.RemoteConfigManager
import com.ruicomp.gpsalarm.utils.dlog

object AdIds {
    fun updateIdAdsWithRemoteConfig() {
        native_splash_high = if (RemoteConfigManager.instance!!._101_splash_n_native_high) BuildConfig.native_splash_high else ""
        native_splash = if (RemoteConfigManager.instance!!._101_splash_n_native) BuildConfig.native_splash else ""
        app_open_splash = if (RemoteConfigManager.instance!!._102_splash_n_open_app) BuildConfig.app_open_splash else ""
        dlog("updateIdAdsWithRemoteConfig: app_open_splash=$app_open_splash")
        lfo1_native_high =
            if (RemoteConfigManager.instance!!._201_lfo_n_native_high) BuildConfig.lfo1_native_high else ""
        lfo1_native =
            if (RemoteConfigManager.instance!!._201_lfo_n_native) BuildConfig.lfo1_native else ""
        lfo2_native_high =
            if (RemoteConfigManager.instance!!._202_lfo_n_native_high) BuildConfig.lfo2_native_high else ""
        lfo2_native_high1 =
            if (RemoteConfigManager.instance!!._202_lfo_n_native_high_1) BuildConfig.lfo2_native_high1 else ""
        lfo2_native_high2 =
            if (RemoteConfigManager.instance!!._202_lfo_n_native_high_2) BuildConfig.lfo2_native_high2 else ""
        lfo2_native =
            if (RemoteConfigManager.instance!!._202_lfo_n_native) BuildConfig.lfo2_native else ""
        ob1_native_high =
            if (RemoteConfigManager.instance!!._301_ob1_n_native_high) BuildConfig.ob1_native_high else ""
        ob1_native =
            if (RemoteConfigManager.instance!!._301_ob1_n_native) BuildConfig.ob1_native else ""
        ob2_native_high =
            if (RemoteConfigManager.instance!!._302_ob2_n_native_high) BuildConfig.ob2_native_high else ""
        ob2_native =
            if (RemoteConfigManager.instance!!._302_ob2_n_native) BuildConfig.ob2_native else ""
        ob3_inter_high = if (RemoteConfigManager.instance!!._303_ob3_n_inter_high) BuildConfig.ob3_inter_high else ""
        ob3_inter = if (RemoteConfigManager.instance!!._303_ob3_n_inter) BuildConfig.ob3_inter else ""
        ob3_native_high =
            if (RemoteConfigManager.instance!!._303_ob3_n_native_full_high) BuildConfig.ob3_native_high else ""
        ob3_native_high1 =
            if (RemoteConfigManager.instance!!._303_ob3_n_native_full_high_1) BuildConfig.ob3_native_high1 else ""
        ob3_native_high2 =
            if (RemoteConfigManager.instance!!._303_ob3_n_native_full_high_2) BuildConfig.ob3_native_high2 else ""
        ob3_native =
            if (RemoteConfigManager.instance!!._303_ob3_n_native_full) BuildConfig.ob3_native else ""
        ob4_native_high =
            if (RemoteConfigManager.instance!!._304_ob4_n_native_high) BuildConfig.ob4_native_high else ""
        ob4_native =
            if (RemoteConfigManager.instance!!._304_ob4_n_native) BuildConfig.ob4_native else ""
        ob5_native_high =
            if (RemoteConfigManager.instance!!._305_ob5_n_native_high) BuildConfig.ob5_native_high else ""
        ob5_native =
            if (RemoteConfigManager.instance!!._305_ob5_n_native) BuildConfig.ob5_native else ""
        app_reopen =
            if (RemoteConfigManager.instance!!._401_app_o_reopen) BuildConfig.app_reopen else ""
        AppOpenManager.getInstance().appResumeAdId = app_reopen

        native_home =  if (RemoteConfigManager.instance!!._501_home_o_native) BuildConfig.native_home else ""
        native_home_high =  if (RemoteConfigManager.instance!!._501_home_o_native_high) BuildConfig.native_home_high else ""
        banner_home =  if (RemoteConfigManager.instance!!._502_home_o_banner) BuildConfig.banner_home else ""
        banner_home_high =  if (RemoteConfigManager.instance!!._502_home_o_banner_high) BuildConfig.banner_home_high else ""

//        rewarded_premium = if (RemoteConfigManager.instance!!._1601_rewarded_premium) BuildConfig.rewarded_premium else ""
//        rewarded_premium_high = if (RemoteConfigManager.instance!!._1601_rewarded_premium_high) BuildConfig.rewarded_premium_high else ""


    }

    var app_open_splash = BuildConfig.app_open_splash
    var app_reopen = BuildConfig.app_reopen
    var native_splash_high = BuildConfig.native_splash_high
    var native_splash = BuildConfig.native_splash
    var lfo1_native_high = BuildConfig.lfo1_native_high
    var lfo1_native = BuildConfig.lfo1_native
    var lfo2_native_high = BuildConfig.lfo2_native_high
    var lfo2_native_high1 = BuildConfig.lfo2_native_high1
    var lfo2_native_high2 = BuildConfig.lfo2_native_high2
    var lfo2_native = BuildConfig.lfo2_native
    var ob1_native_high = BuildConfig.ob1_native_high
    var ob1_native = BuildConfig.ob1_native
    var ob2_native_high = BuildConfig.ob2_native_high
    var ob2_native = BuildConfig.ob2_native
    var ob3_native_high = BuildConfig.ob3_native_high
    var ob3_native_high1 = BuildConfig.ob3_native_high1
    var ob3_native_high2 = BuildConfig.ob3_native_high2
    var ob3_native = BuildConfig.ob3_native
    var ob3_inter_high = BuildConfig.ob3_inter_high
    var ob3_inter = BuildConfig.ob3_inter
    var ob4_native_high = BuildConfig.ob4_native_high
    var ob4_native = BuildConfig.ob4_native
    var ob5_native_high = BuildConfig.ob5_native_high
    var ob5_native = BuildConfig.ob5_native

    var native_home = BuildConfig.native_home
    var native_home_high = BuildConfig.native_home_high
    var banner_home = BuildConfig.banner_home
    var banner_home_high = BuildConfig.banner_home_high

//    var rewarded_premium = BuildConfig.rewarded_premium
//    var rewarded_premium_high = BuildConfig.rewarded_premium_high
}
package com.common.control.manager;

import android.os.Bundle;

import com.common.control.model.AdType;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.reyun.solar.engine.SolarEngineManager;
import com.reyun.solar.engine.infos.SEAdImpEventModel;

public class TrackRevenueSolar {
    public void trackRevenueRewardAd(AdValue adValue, RewardedAd rewardedAd) {
        // Extract the impression-level ad revenue data.
        double valueMicros = adValue.getValueMicros();
        String currencyCode = adValue.getCurrencyCode();
        int precision = adValue.getPrecisionType();

        // Get the ad unit ID.
        String adUnitId = rewardedAd.getAdUnitId();

        AdapterResponseInfo loadedAdapterResponseInfo = rewardedAd.getResponseInfo().getLoadedAdapterResponseInfo();
        String adSourceName = loadedAdapterResponseInfo.getAdSourceName();
        String adSourceId = loadedAdapterResponseInfo.getAdSourceId();
        String adSourceInstanceName = loadedAdapterResponseInfo.getAdSourceInstanceName();
        String adSourceInstanceId = loadedAdapterResponseInfo.getAdSourceInstanceId();

        Bundle extras = rewardedAd.getResponseInfo().getResponseExtras();
        //SE SDK processing logic
        SEAdImpEventModel seAdImpEventModel = new SEAdImpEventModel();
        //Monetization Platform Name
        seAdImpEventModel.setAdNetworkPlatform(adSourceName);
        //Mediation Platform Name (e.g. admob SDK as "admob")
        seAdImpEventModel.setMediationPlatform("admob");
        //Displayed Ad Type (Taking Rewarded Ad as an example, adType = 1)
        seAdImpEventModel.setAdType(AdType.REWARDED_VIDEO);
        //Monetization Platform App ID
        seAdImpEventModel.setAdNetworkAppID(adSourceId);
        //Monetization Platform Ad Unit ID
        seAdImpEventModel.setAdNetworkADID(adUnitId);
        //Ad eCPM
        seAdImpEventModel.setEcpm(valueMicros / 1000);
        //Monetization Platform Currency Type
        seAdImpEventModel.setCurrencyType(currencyCode);
        //True: rendered success
        seAdImpEventModel.setRenderSuccess(true);
        //You can add custom properties as needed. Here we do not give examples.
        SolarEngineManager.getInstance().trackAdImpression(seAdImpEventModel);
    }

    public void trackRevenueOpenAppAd(AdValue adValue, AppOpenAd openAd) {
        // Extract the impression-level ad revenue data.
        double valueMicros = adValue.getValueMicros();
        String currencyCode = adValue.getCurrencyCode();
        int precision = adValue.getPrecisionType();

        // Get the ad unit ID.
        String adUnitId = openAd.getAdUnitId();

        AdapterResponseInfo loadedAdapterResponseInfo = openAd.getResponseInfo().getLoadedAdapterResponseInfo();
        String adSourceName = loadedAdapterResponseInfo.getAdSourceName();
        String adSourceId = loadedAdapterResponseInfo.getAdSourceId();
        String adSourceInstanceName = loadedAdapterResponseInfo.getAdSourceInstanceName();
        String adSourceInstanceId = loadedAdapterResponseInfo.getAdSourceInstanceId();

        Bundle extras = openAd.getResponseInfo().getResponseExtras();
        //SE SDK processing logic
        SEAdImpEventModel seAdImpEventModel = new SEAdImpEventModel();
        //Monetization Platform Name
        seAdImpEventModel.setAdNetworkPlatform(adSourceName);
        //Mediation Platform Name (e.g. admob SDK as "admob")
        seAdImpEventModel.setMediationPlatform("admob");
        //Displayed Ad Type (Taking Rewarded Ad as an example, adType = 1)
        seAdImpEventModel.setAdType(AdType.OPEN_APP);
        //Monetization Platform App ID
        seAdImpEventModel.setAdNetworkAppID(adSourceId);
        //Monetization Platform Ad Unit ID
        seAdImpEventModel.setAdNetworkADID(adUnitId);
        //Ad eCPM
        seAdImpEventModel.setEcpm(valueMicros / 1000);
        //Monetization Platform Currency Type
        seAdImpEventModel.setCurrencyType(currencyCode);
        //True: rendered success
        seAdImpEventModel.setRenderSuccess(true);
        //You can add custom properties as needed. Here we do not give examples.
        SolarEngineManager.getInstance().trackAdImpression(seAdImpEventModel);
    }
    public void trackRevenueInterSolar(AdValue adValue, InterstitialAd mInterstitialAd) {
        // Extract the impression-level ad revenue data.
        double valueMicros = adValue.getValueMicros();
        String currencyCode = adValue.getCurrencyCode();
        int precision = adValue.getPrecisionType();

        // Get the ad unit ID.
        String adUnitId = mInterstitialAd.getAdUnitId();

        AdapterResponseInfo loadedAdapterResponseInfo = mInterstitialAd.getResponseInfo().getLoadedAdapterResponseInfo();
        String adSourceName = loadedAdapterResponseInfo.getAdSourceName();
        String adSourceId = loadedAdapterResponseInfo.getAdSourceId();
        String adSourceInstanceName = loadedAdapterResponseInfo.getAdSourceInstanceName();
        String adSourceInstanceId = loadedAdapterResponseInfo.getAdSourceInstanceId();
        //SE SDK processing logic
        SEAdImpEventModel seAdImpEventModel = new SEAdImpEventModel();
        //Monetization Platform Name
        seAdImpEventModel.setAdNetworkPlatform(adSourceName);
        //Mediation Platform Name (e.g. admob SDK as "admob")
        seAdImpEventModel.setMediationPlatform("admob");
        //Displayed Ad Type (Taking Rewarded Ad as an example, adType = 1)
        seAdImpEventModel.setAdType(AdType.INTERSTITIAL);
        //Monetization Platform App ID
        seAdImpEventModel.setAdNetworkAppID(adSourceId);
        //Monetization Platform Ad Unit ID
        seAdImpEventModel.setAdNetworkADID(adUnitId);
        //Ad eCPM
        seAdImpEventModel.setEcpm(valueMicros / 1000);
        //Monetization Platform Currency Type
        seAdImpEventModel.setCurrencyType(currencyCode);
        //True: rendered success
        seAdImpEventModel.setRenderSuccess(true);
        //You can add custom properties as needed. Here we do not give examples.
        SolarEngineManager.getInstance().trackAdImpression(seAdImpEventModel);
        //end
    }

    public void trackRevenueBannerSolar(AdValue adValue, AdView adView, String adUnitId) {
        // Extract the impression-level ad revenue data.
        double valueMicros = adValue.getValueMicros();
        String currencyCode = adValue.getCurrencyCode();
        int precision = adValue.getPrecisionType();

        AdapterResponseInfo loadedAdapterResponseInfo = adView.getResponseInfo().getLoadedAdapterResponseInfo();
        String adSourceName = loadedAdapterResponseInfo.getAdSourceName();
        String adSourceId = loadedAdapterResponseInfo.getAdSourceId();
        String adSourceInstanceName = loadedAdapterResponseInfo.getAdSourceInstanceName();
        String adSourceInstanceId = loadedAdapterResponseInfo.getAdSourceInstanceId();

        //SE SDK processing logic
        SEAdImpEventModel seAdImpEventModel = new SEAdImpEventModel();
        //Monetization Platform Name
        seAdImpEventModel.setAdNetworkPlatform(adSourceName);
        //Mediation Platform Name (e.g. admob SDK as "admob")
        seAdImpEventModel.setMediationPlatform("admob");
        //Displayed Ad Type (Taking Rewarded Ad as an example, adType = 1)
        seAdImpEventModel.setAdType(AdType.BANNER);
        //Monetization Platform App ID
        seAdImpEventModel.setAdNetworkAppID(adSourceId);
        //Monetization Platform Ad Unit ID
        seAdImpEventModel.setAdNetworkADID(adUnitId);
        //Ad eCPM
        seAdImpEventModel.setEcpm(valueMicros / 1000);
        //Monetization Platform Currency Type
        seAdImpEventModel.setCurrencyType(currencyCode);
        //True: rendered success
        seAdImpEventModel.setRenderSuccess(true);
        //You can add custom properties as needed. Here we do not give examples.
        SolarEngineManager.getInstance().trackAdImpression(seAdImpEventModel);
    }

    public void trackRevenueNativeSolar(AdValue adValue, NativeAd nativeAd, String adUnitId) {
        // Extract the impression-level ad revenue data.
        double valueMicros = adValue.getValueMicros();
        String currencyCode = adValue.getCurrencyCode();
        int precision = adValue.getPrecisionType();

        AdapterResponseInfo loadedAdapterResponseInfo = nativeAd.getResponseInfo().getLoadedAdapterResponseInfo();
        String adSourceName = loadedAdapterResponseInfo.getAdSourceName();
        String adSourceId = loadedAdapterResponseInfo.getAdSourceId();
        String adSourceInstanceName = loadedAdapterResponseInfo.getAdSourceInstanceName();
        String adSourceInstanceId = loadedAdapterResponseInfo.getAdSourceInstanceId();
        //SE SDK processing logic
        SEAdImpEventModel seAdImpEventModel = new SEAdImpEventModel();
        //Monetization Platform Name
        seAdImpEventModel.setAdNetworkPlatform(adSourceName);
        //Mediation Platform Name (e.g. admob SDK as "admob")
        seAdImpEventModel.setMediationPlatform("admob");
        //Displayed Ad Type (Taking Rewarded Ad as an example, adType = 1)
        seAdImpEventModel.setAdType(AdType.NATIVE);
        //Monetization Platform App ID
        seAdImpEventModel.setAdNetworkAppID(adSourceId);
        //Monetization Platform Ad Unit ID
        seAdImpEventModel.setAdNetworkADID(adUnitId);
        //Ad eCPM
        seAdImpEventModel.setEcpm(valueMicros / 1000);
        //Monetization Platform Currency Type
        seAdImpEventModel.setCurrencyType(currencyCode);
        //True: rendered success
        seAdImpEventModel.setRenderSuccess(true);
        //You can add custom properties as needed. Here we do not give examples.
        SolarEngineManager.getInstance().trackAdImpression(seAdImpEventModel);
    }
}

package com.common.control.interfaces;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.rewarded.RewardItem;

abstract public class AdCallback {
    public AdCallback() {
    }

    @Deprecated
    public void onAdClosed() {
    }

    public void onNextScreen() {
    }

    public void onAdShowedFullScreenContent() {
    }


    public void onAdFailedToLoad(@NonNull LoadAdError i) {
    }

    public void onAdLoaded() {
    }


    public void onResultInterstitialAd(InterstitialAd interstitialAd) {
    }

    public void onNativeAds(NativeAd nativeAd) {

    }

    public void onResultOpenAppAd(AppOpenAd appOpenAd) {}

    public void onAdFailedToShowFullScreenContent(LoadAdError errAd) {

    }

    public void onUserEarnedReward(RewardItem rewardItem) {

    }


    public void onAdClicked() {

    }

    public void onAdImpression() {

    }

    public void onClickClose(){

    }

}

package com.ruicomp.gpsalarm;

import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;

public class AdCache {

    private static AdCache instance;

    public AppOpenAd appOpenAd = null;
    public NativeAd lfo1Native = null;
    public NativeAd lfo2NativeHigh = null;
    public NativeAd lfo2NativeHigh1 = null;
    public NativeAd lfo2NativeHigh2 = null;
    public NativeAd ob1Native = null;
    public NativeAd ob2NativeHigh = null;
    public NativeAd ob3NativeHigh = null;
    public NativeAd ob3NativeHigh1 = null;
    public NativeAd ob3NativeHigh2AndNative = null;

    public InterstitialAd ob3Inter = null;
    public NativeAd ob4NativeHigh = null;
    public NativeAd ob5NativePermissionHigh = null;



    private AdCache() {
    }

    public static AdCache getInstance() {
        if (instance == null) {
            instance = new AdCache();
        }
        return instance;
    }

}

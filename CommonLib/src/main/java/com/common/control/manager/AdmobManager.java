package com.common.control.manager;

//import static com.common.control.BuildConfig.DEBUG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.common.control.R;
import com.common.control.dialog.PrepareLoadingAdsDialog;
import com.common.control.interfaces.AdCallback;
import com.facebook.ads.AudienceNetworkAds;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MediaAspectRatio;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;

public class AdmobManager {
    public static final String ACTION_CLOSE_NATIVE_ADS = "ACTION_CLOSE_NATIVE_ADS";
    public static final String ACTION_OPEN_NATIVE_ADS = "ACTION_OPEN_NATIVE_ADS";
    private static AdmobManager instance;
    private final LoadAdError errAd = new LoadAdError(2, "No Ad", "", null, null);
    private boolean hasAds = true;
    private boolean isShowLoadingDialog;
    private long customTimeLoadingDialog = 1500;
    private boolean hasAdjust;
    private boolean hasLog;
    TrackRevenueSolar trackRevenueSolar = new TrackRevenueSolar();

    private AdmobManager() {

    }

    public static AdmobManager getInstance() {
        if (instance == null) {
            instance = new AdmobManager();
        }
        return instance;
    }

    public LoadAdError getErrAd() {
        return errAd;
    }

    public boolean isShowLoadingDialog() {
        return isShowLoadingDialog;
    }

    public void setShowLoadingDialog(boolean showLoadingDialog) {
        isShowLoadingDialog = showLoadingDialog;
    }

    public boolean isHasAds() {
        return hasAds;
    }

    public void setHasAds(boolean hasAds) {
        this.hasAds = hasAds;
    }

    public boolean isHasAdjust() {
        return hasAdjust;
    }

    public void setHasAdjust(boolean hasAdjust) {
        this.hasAdjust = hasAdjust;
    }

    public boolean isHasLog() {
        return hasLog;
    }

    public void setHasLog(boolean hasLog) {
        this.hasLog = hasLog;
    }

    public long getCustomTimeLoadingDialog() {
        return customTimeLoadingDialog;
    }

    public void setCustomTimeLoadingDialog(long customTimeLoadingDialog) {
        this.customTimeLoadingDialog = customTimeLoadingDialog;
    }

    public void init(Context context, String deviceID) {
        try {
            Log.d("log_admob", "init: Admob");
            MobileAds.initialize(context, initializationStatus -> {
            });
            MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList(deviceID)).build());

        } catch (Exception e) {
            e.printStackTrace();
        }
        initialize(context);
    }

    private void initialize(Context context) {
        if (!AudienceNetworkAds.isInitialized(context)) {
            AudienceNetworkAds.buildInitSettings(context).withInitListener(initResult -> {
            }).initialize();
        }
    }

    public AdRequest getAdRequest() {
        if (!hasAds || PurchaseManager.getInstance().isPurchased() || PurchaseManagerInApp.getInstance().isPurchased()) {
            return null;
        }
        AdRequest.Builder builder = new AdRequest.Builder();
        return builder.build();
    }

    public void loadInterAds(Activity context, String id, AdCallback callback) {
        log("request inter: " + id + "");
        AdRequest request = getAdRequest();
        if (request == null) {
            callback.onAdFailedToLoad(errAd);
            return;
        }
        InterstitialAd.load(context, id, request, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                if (callback != null) {
                    callback.onAdFailedToLoad(loadAdError);
                }
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                if (callback != null) {
                    callback.onResultInterstitialAd(interstitialAd);
                }
                interstitialAd.setOnPaidEventListener(adValue -> trackRevenueSolar.trackRevenueInterSolar(adValue,interstitialAd));
            }
        });
    }

    public void loadInterAds(Context context, String id, AdCallback callback) {
        log("request inter: " + id + "");
        AdRequest request = getAdRequest();
        if (request == null) {
            callback.onAdFailedToLoad(errAd);
            return;
        }
        InterstitialAd.load(context, id, request, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                if (callback != null) {
                    callback.onAdFailedToLoad(loadAdError);
                }
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                if (callback != null) {
                    callback.onResultInterstitialAd(interstitialAd);
                }
                interstitialAd.setOnPaidEventListener(adValue -> trackRevenueSolar.trackRevenueInterSolar(adValue, interstitialAd));
            }
        });
    }

    public void log(String s) {
        if (hasLog) {
            Log.d("android_log", "log: " + s);
        }
    }

    public void loadAndShowSplashOpenApp(Context context, String id, AdCallback callback) {
        AdRequest request = getAdRequest();
        if (request == null) {
            callback.onAdFailedToLoad(errAd);
            callback.onNextScreen();
            return;
        }

        AppOpenAd.AppOpenAdLoadCallback loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd appResumeAd) {
                appResumeAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        if (callback != null) {
                            callback.onAdClosed();
                            callback.onNextScreen();
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        if (callback != null) {
                            callback.onAdClosed();
                            callback.onNextScreen();

                        }
                    }
                });
                appResumeAd.show((Activity) context);
                appResumeAd.setOnPaidEventListener(adValue -> trackRevenueSolar.trackRevenueOpenAppAd(adValue, appResumeAd));
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                if (callback != null) {
                    callback.onAdClosed();
                    callback.onNextScreen();
                }
            }
        };

        AppOpenAd.load(context, id, request, loadCallback);
    }

    private boolean isAppOpenAdLoading = false;
    private boolean isAppOpenAdShowing = false;

    public void loadAppOpenAd(Context context, String id, final AdCallback callback) {
        AdRequest request = getAdRequest();
        if (request == null) {
            callback.onAdFailedToLoad(errAd);
            callback.onNextScreen();
            return;
        }
        AppOpenAd.AppOpenAdLoadCallback loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd appResumeAd) {
                callback.onResultOpenAppAd(appResumeAd);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                if (callback != null) {
                    callback.onAdClosed();
                    callback.onNextScreen();
                }
            }
        };

        AppOpenAd.load(context, id, request, loadCallback);
    }

    /**
     * Shows the preloaded App Open Ad if available.
     *
     * @param activity      The current Activity context where the ad should be shown.
     * @param showCallback  A callback to notify about ad display events (dismissed, failed to show).
     */
    public void showAppOpenAd(Activity activity, AppOpenAd appResumeAd, final AdCallback callback) {
        appResumeAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                if (callback != null) {
                    callback.onAdClosed();
                    callback.onNextScreen();
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
                if (callback != null) {
                    callback.onAdClosed();
                    callback.onNextScreen();

                }
            }
        });
        appResumeAd.show(activity);
        appResumeAd.setOnPaidEventListener(adValue -> trackRevenueSolar.trackRevenueOpenAppAd(adValue, appResumeAd));

    }

    public void showInterstitial(Activity context, final InterstitialAd mInterstitialAd, AdCallback callback) {
        showInterstitial(context, mInterstitialAd, false, callback);
    }

    public void showInterstitial(Activity context, final InterstitialAd mInterstitialAd, boolean hasLoadingWhenShow, AdCallback callback) {

        if (mInterstitialAd == null || PurchaseManager.getInstance().isPurchased()) {
            if (callback != null) {
                callback.onAdFailedToShowFullScreenContent(errAd);
                callback.onAdClosed();
                callback.onNextScreen();
            }

            return;
        }

        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                context.sendBroadcast(new Intent(PrepareLoadingAdsDialog.ACTION_DISMISS_DIALOG));
                if (AppOpenManager.getInstance().isInitialized()) {
                    AppOpenManager.getInstance().enableAppResume();
                }
                if (callback != null) {
                    callback.onAdClosed();
                    callback.onNextScreen();
                    callback.onClickClose();
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                if (AppOpenManager.getInstance().isInitialized()) {
                    AppOpenManager.getInstance().enableAppResume();
                }
                context.sendBroadcast(new Intent(PrepareLoadingAdsDialog.ACTION_DISMISS_DIALOG));
                if (callback != null) {
                    callback.onAdClosed();
                    callback.onNextScreen();
                }
            }

            @Override
            public void onAdShowedFullScreenContent() {
                if (!hasLoadingWhenShow) {
                    context.sendBroadcast(new Intent(PrepareLoadingAdsDialog.ACTION_DISMISS_DIALOG));
                } else {
                    context.sendBroadcast(new Intent(PrepareLoadingAdsDialog.ACTION_CLEAR_TEXT_AD));
                }
                if (callback != null) {
                    callback.onAdShowedFullScreenContent();
                }
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                if (callback != null) {
                    callback.onAdClicked();
                }
            }
        });

        if (context != null && !context.isDestroyed() && ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            long timeShowLoadingDlg = 0;
            if (isShowLoadingDialog) {
                PrepareLoadingAdsDialog.start(context);
                timeShowLoadingDlg = customTimeLoadingDialog;
            }
            if (AppOpenManager.getInstance().isInitialized()) {
                AppOpenManager.getInstance().disableAppResume();
            }
            new Handler().postDelayed(() -> {
                log("show inter: " + mInterstitialAd.getAdUnitId());
                mInterstitialAd.show(context);
            }, timeShowLoadingDlg);
        } else {
            if (callback != null) {
                callback.onAdClosed();
                callback.onNextScreen();
            }
        }
    }

    @Deprecated
    public void loadBanner(final Activity mActivity, String id) {
        final FrameLayout adContainer = mActivity.findViewById(R.id.banner_container);
        loadBanner(mActivity, id, adContainer);
    }


    public void loadBanner(final Activity mActivity, String id, final FrameLayout adContainer) {
        log("Request Banner :" + id);

        AdRequest request = getAdRequest();
        if (request == null) {
            adContainer.removeAllViews();
            adContainer.setVisibility(View.GONE);
            return;
        }
        try {
            AdView adView = new AdView(mActivity);
            adView.setAdUnitId(id);
            AdSize adSize = getAdSize(mActivity);
            adView.setAdSize(adSize);
            adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            adView.loadAd(request);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    adContainer.removeAllViews();
                    adContainer.setVisibility(View.GONE);
                }


                @Override
                public void onAdLoaded() {
                    adContainer.removeAllViews();
                    adContainer.setVisibility(View.VISIBLE);
                    adContainer.addView(adView);
                    adView.setOnPaidEventListener(adValue -> trackRevenueSolar.trackRevenueBannerSolar(adValue, adView, id));
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadBanner(final Activity mActivity, String id, final FrameLayout adContainer, AdCallback callback) {
        log("Request Banner :" + id);

        AdRequest request = getAdRequest();
        if (request == null) {
            adContainer.removeAllViews();
            adContainer.setVisibility(View.GONE);
            return;
        }
        try {
            AdView adView = new AdView(mActivity);
            adView.setAdUnitId(id);
            AdSize adSize = getAdSize(mActivity);
            adView.setAdSize(adSize);
            adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            adView.loadAd(request);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    adContainer.removeAllViews();
                    adContainer.setVisibility(View.GONE);
                    callback.onAdFailedToLoad(loadAdError);
                }


                @Override
                public void onAdLoaded() {
                    callback.onAdLoaded();
                    adContainer.removeAllViews();
                    adContainer.setVisibility(View.VISIBLE);
                    adContainer.addView(adView);
                    adView.setOnPaidEventListener(adValue -> trackRevenueSolar.trackRevenueBannerSolar(adValue, adView, id));
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    if (callback != null) {
                        callback.onAdImpression();
                    }
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    if (callback != null) {
                        callback.onAdClicked();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private AdSize getAdSize(Activity mActivity) {
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mActivity, adWidth);

    }

    public void preloadNative(Context context, String id, AdCallback callback) {
        Log.d("AdmobLogger", "preloadNative: " + id);
        loadUnifiedNativeAd(context, id, new AdCallback() {
            @Override
            public void onNativeAds(NativeAd nativeAd) {
                callback.onNativeAds(nativeAd);
                nativeAd.setOnPaidEventListener(adValue -> trackRevenueSolar.trackRevenueNativeSolar(adValue, nativeAd, id));
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError i) {
                callback.onAdFailedToLoad(i);
            }
            @Override
            public void onAdImpression() {
                super.onAdImpression();
                callback.onAdImpression();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                callback.onAdClicked();
            }
        });
    }

    public void preloadFullScreenNative(Context context, String id, AdCallback callback) {
        Log.d("AdmobLogger", "preloadFsNative: " + id);
        loadFullScreenUnifiedNativeAd(context, id, new AdCallback() {
            @Override
            public void onNativeAds(NativeAd nativeAd) {
                callback.onNativeAds(nativeAd);
                nativeAd.setOnPaidEventListener(adValue -> trackRevenueSolar.trackRevenueNativeSolar(adValue, nativeAd, id));
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError i) {
                callback.onAdFailedToLoad(i);
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                callback.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                callback.onAdImpression();
            }
        });
    }

    public void showNative(Context context, NativeAd nativeAd, FrameLayout placeHolder, NativeAdType type) {
        if (nativeAd == null) {
            placeHolder.setVisibility(View.GONE);
            return;
        }

        placeHolder.setVisibility(View.VISIBLE);
        Log.d("AdTestSource: ", nativeAd.getResponseInfo().getMediationAdapterClassName());
//        boolean isMeta = Objects.equals(Objects.requireNonNull(nativeAd.getResponseInfo()).getMediationAdapterClassName().toLowerCase(), "com.google.ads.mediation.facebook.facebookmediationadapter".toLowerCase());
        int customNative = getLayoutNative(type);
        @SuppressLint("InflateParams") NativeAdView nativeAdView = (NativeAdView) LayoutInflater.from(context).inflate(customNative, null);
        onBindAdView(nativeAd, nativeAdView);
        placeHolder.removeAllViews();
        placeHolder.addView(nativeAdView);
    }

    public void showNative(Context context, NativeAd nativeAd, FrameLayout placeHolder, NativeAdType type, boolean  isHideInvisibility) {
        if (nativeAd == null) {
            if (isHideInvisibility) {
                placeHolder.setVisibility(View.INVISIBLE);
            } else {
                placeHolder.setVisibility(View.GONE);
            }
            return;
        }

        placeHolder.setVisibility(View.VISIBLE);
        Log.d("AdTestSource: ", nativeAd.getResponseInfo().getMediationAdapterClassName());
//        boolean isMeta = Objects.equals(Objects.requireNonNull(nativeAd.getResponseInfo()).getMediationAdapterClassName().toLowerCase(), "com.google.ads.mediation.facebook.facebookmediationadapter".toLowerCase());
        int customNative = getLayoutNative(type);
        @SuppressLint("InflateParams") NativeAdView nativeAdView = (NativeAdView) LayoutInflater.from(context).inflate(customNative, null);
        onBindAdView(nativeAd, nativeAdView);
        placeHolder.removeAllViews();
        placeHolder.addView(nativeAdView);
    }

    private int getLayoutNative(NativeAdType type) {
        switch (type) {
            case BIG:
//                if (isMeta) {
                    return R.layout.custom_native_meta_big;
//                } else {
//                    return R.layout.custom_native_ads_2;
//                }
            case SMALL:
//                if (isMeta) {
                    return R.layout.custom_native_meta_small;
//
//                } else {
//                    return R.layout.custom_native_ads_1;
//                }
            case MEDIUM:
//                if (isMeta) {
                    return R.layout.custom_native_meta_regular;
//                } else {
//                    return R.layout.custom_native_ads_3;
//                }
            case FULLSCREEN:
                return R.layout.custom_full_screen_native_ads;
            default:
                return R.layout.custom_native_ads_2;
        }
    }

    public void loadNative(Context context, String id, FrameLayout placeHolder, NativeAdType nativeAdType) {
        log("Request NativeAd :" + id);
        loadUnifiedNativeAd(context, id, new AdCallback() {
            @Override
            public void onNativeAds(NativeAd nativeAd) {
                showNative(context, nativeAd, placeHolder, nativeAdType);
                nativeAd.setOnPaidEventListener(adValue -> trackRevenueSolar.trackRevenueNativeSolar(adValue,nativeAd, id));
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError i) {
                placeHolder.removeAllViews();
                placeHolder.setVisibility(View.GONE);
            }
        });
    }

    public void loadNative(Context context, String id, FrameLayout placeHolder, NativeAdType nativeAdType, AdCallback callback) {
        log("Request NativeAd :" + id);
        loadUnifiedNativeAd(context, id, new AdCallback() {
            @Override
            public void onNativeAds(NativeAd nativeAd) {
                showNative(context, nativeAd, placeHolder, nativeAdType);
                nativeAd.setOnPaidEventListener(adValue -> trackRevenueSolar.trackRevenueNativeSolar(adValue,nativeAd, id));
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError i) {
                placeHolder.removeAllViews();
                placeHolder.setVisibility(View.GONE);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                if (callback != null) {
                    callback.onAdImpression();
                }
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                if (callback != null) {
                    callback.onAdClicked();
                }
            }
        });
    }

    public void loadFullScreenUnifiedNativeAd(Context context, String id, final AdCallback callback) {
        AdRequest request = getAdRequest();
        if (request == null) {
            callback.onAdFailedToLoad(errAd);
            return;
        }
        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setMediaAspectRatio(MediaAspectRatio.ANY).setVideoOptions(videoOptions).build();
        AdLoader adLoader = new AdLoader.Builder(context, id).forNativeAd(nativeAd -> {
            if (callback != null) {
                callback.onNativeAds(nativeAd);
            }
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                if (callback != null) {
                    callback.onAdFailedToLoad(loadAdError);
                }
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                if (callback != null) {
                    callback.onAdImpression();
                }
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                if (callback != null) {
                    callback.onAdClicked();
                }
            }
        }).withNativeAdOptions(adOptions).build();
        adLoader.loadAd(request);
    }

    private void registerDialogBehaviorReceiver(Context context, FrameLayout placeHolder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction() != null) {
                        if (placeHolder != null) {
                            if (intent.getAction().equals(ACTION_CLOSE_NATIVE_ADS)) {
                                placeHolder.setVisibility(View.GONE);
                            } else if (intent.getAction().equals(ACTION_OPEN_NATIVE_ADS)) {
                                placeHolder.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }, initDialogBehaviorIntentFilter(), Context.RECEIVER_NOT_EXPORTED);
        } else {
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction() != null) {
                        if (placeHolder != null) {
                            if (intent.getAction().equals(ACTION_CLOSE_NATIVE_ADS)) {
                                placeHolder.setVisibility(View.GONE);
                            } else if (intent.getAction().equals(ACTION_OPEN_NATIVE_ADS)) {
                                placeHolder.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }, initDialogBehaviorIntentFilter());
        }
    }

    private IntentFilter initDialogBehaviorIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CLOSE_NATIVE_ADS);
        intentFilter.addAction(ACTION_OPEN_NATIVE_ADS);
        return intentFilter;
    }

    private void loadUnifiedNativeAd(Context context, String id, final AdCallback callback) {
        AdRequest request = getAdRequest();
        if (request == null) {
            callback.onAdFailedToLoad(errAd);
            return;
        }
        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
        AdLoader adLoader = new AdLoader.Builder(context, id).forNativeAd(nativeAd -> {
            if (callback != null) {
                callback.onNativeAds(nativeAd);
            }
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                if (callback != null) {
                    callback.onAdFailedToLoad(loadAdError);
                }
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                if (callback != null) {
                    callback.onAdImpression();
                }
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                if (callback != null) {
                    callback.onAdClicked();
                }
            }
        }).withNativeAdOptions(adOptions).build();
        adLoader.loadAd(request);
    }

    private void onBindAdView(NativeAd nativeAd, NativeAdView adView) {
        try {
            adView.setMediaView(adView.findViewById(R.id.ad_media));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            if (nativeAd.getPrice() == null) {
//                adView.getPriceView().setVisibility(View.INVISIBLE);
//            } else {
//                adView.getPriceView().setVisibility(View.VISIBLE);
//                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            if (nativeAd.getStore() == null) {
//                adView.getStoreView().setVisibility(View.INVISIBLE);
//            } else {
//                adView.getStoreView().setVisibility(View.VISIBLE);
//                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.GONE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        adView.setNativeAd(nativeAd);

    }

    public void loadRewardAd(Context context, String id, RewardedAdLoadCallback adLoadCallback) {
        log("Request RewardAd :" + id);

        AdRequest request = getAdRequest();
        if (request == null) {
            adLoadCallback.onAdFailedToLoad(errAd);
            return;
        }

        RewardedAd.load(context, id, request, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);
                // Listener to get AD value
                rewardedAd.setOnPaidEventListener(adValue -> trackRevenueSolar.trackRevenueRewardAd(adValue, rewardedAd));
            }
        });
    }

    public void showRewardAdInterstitial(Activity activity, RewardedInterstitialAd rewardedAd, AdCallback callback) {
        if (!hasAds || rewardedAd == null || PurchaseManager.getInstance().isPurchased()) {
            callback.onAdFailedToShowFullScreenContent(errAd);
            return;
        }
        log("Show RewardAd :" + rewardedAd.getAdUnitId());

        rewardedAd.show(activity, callback::onUserEarnedReward);
    }

    @SuppressLint("HardwareIds")
    public String getDeviceId(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return md5(android_id).toUpperCase();
    }

    private String md5(final String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            // Create Hex String
            StringBuilder hexString;
            hexString = new StringBuilder();
            for (byte b : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & b));
                while (h.length() < 2) h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException ignored) {
        }
        return "";
    }

    public void hasAds(boolean hasAds) {
        this.hasAds = hasAds;
    }

    public void hasAdjust(boolean b) {
        this.hasAdjust = b;
    }

    public AdRequest getAdCollapsibleBannerRequest() {
        if (!hasAds || PurchaseManager.getInstance().isPurchased()) {
            return null;
        }
//        AdRequest.Builder builder = new AdRequest.Builder();
//        return builder.build();
        Bundle extras = new Bundle();
        extras.putString("collapsible", "bottom");
        AdRequest.Builder adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras);

        return adRequest.build();
    }

    // In AdmobManager.java

// ... (existing imports and code) ...

    public void loadAlternateCollapsibleBanner(
            final Activity mActivity,
            List<String> idsInput, // List of Ad Unit IDs
            final FrameLayout adContainer,
            final AdListener adListenerCallback // Optional: A callback for the final result
    ) {
        List<String> ids = new ArrayList<>(idsInput); // Create a mutable copy

        if (ids.isEmpty()) {
            log("loadAlternateCollapsibleBanner: All IDs failed or list empty.");
            if (adContainer != null) {
                adContainer.removeAllViews();
                adContainer.setVisibility(View.GONE);
            }
            if (adListenerCallback != null) {
                // You might want to define a more specific error for this case
                adListenerCallback.onAdFailedToLoad(new LoadAdError(0, "All Ad IDs failed or list was empty.", "com.google.android.gms.ads", null, null));
            }
            return;
        }

        String currentId = ids.get(0);
        log("loadAlternateCollapsibleBanner: Attempting to load ID: " + currentId);

        AdRequest request = getAdCollapsibleBannerRequest(); // Your existing method for collapsible request
        if (request == null) {
            // If the request itself can't be generated (e.g., ads disabled, purchased)
            // then all subsequent calls will also fail for the same reason.
            log("loadAlternateCollapsibleBanner: AdRequest is null for ID: " + currentId);
            if (adContainer != null) {
                adContainer.removeAllViews();
                adContainer.setVisibility(View.GONE);
            }
            if (adListenerCallback != null) {
                adListenerCallback.onAdFailedToLoad(errAd); // Use your existing errAd or a new specific one
            }
            return;
        }

        try {
            final AdView adView = new AdView(mActivity);
            adView.setAdUnitId(currentId);
            AdSize adSize = getCollapsibleBannerAdSize(mActivity); // Your existing method
            adView.setAdSize(adSize);
            adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    log("loadAlternateCollapsibleBanner: Ad loaded successfully for ID: " + currentId);
                    if (adContainer != null) {
                        adContainer.removeAllViews();
                        adContainer.setVisibility(View.VISIBLE);
                        adContainer.addView(adView);
                    }
                    // Paid event listener
                    adView.setOnPaidEventListener(adValue -> {
                        // Assuming trackRevenueSolar is accessible here
                        // You might need to adjust how you get the AdmobManager instance or pass trackRevenueSolar
                        if (trackRevenueSolar != null) { // Add null check if necessary
                            trackRevenueSolar.trackRevenueBannerSolar(adValue, adView, currentId);
                        }
                    });

                    if (adListenerCallback != null) {
                        adListenerCallback.onAdLoaded(); // Notify success
                    }
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    log("loadAlternateCollapsibleBanner: Failed to load ID: " + currentId + ". Error: " + loadAdError.getMessage());
                    ids.remove(0); // Remove the failed ID
                    // Try the next ID in the list
                    loadAlternateCollapsibleBanner(mActivity, ids, adContainer, adListenerCallback);
                }

                // You can also override other AdListener methods if needed (onAdClicked, onAdImpression, etc.)
                // and forward them to the adListenerCallback if it's designed to handle them.
                // For example:
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    if (adListenerCallback != null) {
                        adListenerCallback.onAdClicked();
                    }
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    if (adListenerCallback != null) {
                        adListenerCallback.onAdImpression();
                    }
                }
            });

            adView.loadAd(request);

        } catch (Exception e) {
            log("loadAlternateCollapsibleBanner: Exception during ad setup for ID: " + currentId + ". Error: " + e.getMessage());
            e.printStackTrace();
            // If an exception occurs during setup, treat it like a failed ad load for this ID
            ids.remove(0);
            loadAlternateCollapsibleBanner(mActivity, ids, adContainer, adListenerCallback);
        }
    }

    public void loadCollapsibleBanner(final Activity mActivity, String id, final FrameLayout adContainer) {
        log("Request Banner :" + id);

        AdRequest request = getAdCollapsibleBannerRequest();
        if (request == null) {
            adContainer.removeAllViews();
            adContainer.setVisibility(View.GONE);
            return;
        }
        try {
            AdView adView = new AdView(mActivity);
            adView.setAdUnitId(id);
            AdSize adSize = getCollapsibleBannerAdSize(mActivity);
            adView.setAdSize(adSize);
            adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            adView.loadAd(request);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    adContainer.removeAllViews();
                    adContainer.setVisibility(View.GONE);
                }


                @Override
                public void onAdLoaded() {
                    adContainer.removeAllViews();
                    adContainer.setVisibility(View.VISIBLE);
                    adContainer.addView(adView);
                    adView.setOnPaidEventListener(adValue -> {
                        trackRevenueSolar.trackRevenueBannerSolar(adValue,adView,id);
                    });
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AdSize getCollapsibleBannerAdSize(Activity mActivity) {
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getLandscapeAnchoredAdaptiveBannerAdSize(mActivity, adWidth);

    }

    //    ads
    public void loadAlternateInter(Context context, List<String> idsInput, AdCallback callback) {
        List<String> ids = new ArrayList<>(idsInput);
        if (ids.isEmpty()) {
            Log.d("AdmobLogger", "loadAlternateInter: " + "empty");
            return;
        }
        loadInterAds(context, ids.get(0), new AdCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError i) {
                super.onAdFailedToLoad(i);
                Log.w("AdmobLogger", "loadAlternateInter: " + "fail-" + ids.get(0));
                ids.remove(0);
                if (ids.isEmpty()) {
                    callback.onAdFailedToLoad(i);
                } else {
                    loadAlternateInter(context, ids, callback);
                }
            }

            @Override
            public void onResultInterstitialAd(InterstitialAd interstitialAd) {
                super.onResultInterstitialAd(interstitialAd);
                callback.onResultInterstitialAd(interstitialAd);
                Log.i("AdmobLogger", "loadAlternateInter: " + "success-" + ids.get(0));
            }
        });
    }

    public void preloadAlternateNative(Context context, List<String> idsInput, AdCallback callback) {
        List<String> ids = new ArrayList<>(idsInput);
        if (ids.isEmpty()) {
            Log.d("AdmobLogger", "loadAlternateNative: " + "empty");
            callback.onNativeAds(null);
            return;
        }
        preloadNative(context, ids.get(0), new AdCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError i) {
                super.onAdFailedToLoad(i);
                Log.w("AdmobLogger", "loadAlternateNative: " + "fail-" + ids.get(0));
                ids.remove(0);
//                if (ids.isEmpty()) {
//                    callback.onAdFailedToLoad(i);
//                } else {
                    preloadAlternateNative(context, ids, callback);
//                }
            }

            @Override
            public void onNativeAds(NativeAd nativeAd) {
                super.onNativeAds(nativeAd);
                callback.onNativeAds(nativeAd);
                Log.i("AdmobLogger", "loadAlternateNative: " + "success-" + ids.get(0));
            }
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                if (callback != null) {
                    callback.onAdClicked();
                }
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                if (callback != null) {
                    callback.onAdImpression();
                }
            }
        });
    }

    public void preloadFullScreenAlternateNative(Context context, List<String> idsInput, AdCallback callback) {
        List<String> ids = new ArrayList<>(idsInput);
        if (ids.isEmpty()) {
            Log.d("AdmobLogger", "loadAlternatefsNative: " + "empty");
            callback.onNativeAds(null);
            return;
        }
        preloadFullScreenNative(context, ids.get(0), new AdCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError i) {
                super.onAdFailedToLoad(i);
                Log.w("AdmobLogger", "loadAlternatefsNative: " + "fail-" + ids.get(0));
                ids.remove(0);
//                if (ids.isEmpty()) {
//                    callback.onAdFailedToLoad(i);
//                } else {
                    preloadAlternateNative(context, ids, callback);
//                }
            }

            @Override
            public void onNativeAds(NativeAd nativeAd) {
                super.onNativeAds(nativeAd);
                callback.onNativeAds(nativeAd);
                Log.i("AdmobLogger", "loadAlternatefsNative: " + "success-" + ids.get(0));
            }
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                if (callback != null) {
                    callback.onAdClicked();
                }
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                if (callback != null) {
                    callback.onAdImpression();
                }
            }
        });
    }

    public void loadAlternateBanner(Activity act, List<String> ids, final FrameLayout adContainer) {
        if (ids.isEmpty()) {
            Log.d("AdmobLogger", "loadAlternateBanner: empty");
            return;
        }
        loadBanner(act, ids.get(0), adContainer, new AdCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError i) {
                super.onAdFailedToLoad(i);
                Log.w("AdmobLogger", "loadAlternateBanner: " + "fail-" + ids.get(0));
                ids.remove(0);
                if (!ids.isEmpty()) {
                    adContainer.setVisibility(View.VISIBLE);
                    loadAlternateBanner(act, ids, adContainer);
                }

            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.i("AdmobLogger", "loadAlternateBanner: " + "success-" + ids.get(0));
            }
        });
    }

    public void loadAlternateBanner(Activity act, List<String> ids, final FrameLayout adContainer, AdCallback callback) {
        if (ids.isEmpty()) {
            Log.d("AdmobLogger", "loadAlternateBanner: empty");
            return;
        }
        loadBanner(act, ids.get(0), adContainer, new AdCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError i) {
                super.onAdFailedToLoad(i);
                Log.w("AdmobLogger", "loadAlternateBanner: " + "fail-" + ids.get(0));
                ids.remove(0);
                if (!ids.isEmpty()) {
                    adContainer.setVisibility(View.VISIBLE);
                    loadAlternateBanner(act, ids, adContainer, callback);
                }

            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.i("AdmobLogger", "loadAlternateBanner: " + "success-" + ids.get(0));
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                if (callback != null) {
                    callback.onAdClicked();
                }
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                if (callback != null) {
                    callback.onAdImpression();
                }
            }
        });
    }

    public enum NativeAdType {
        FULLSCREEN, BIG, MEDIUM, SMALL
    }

}

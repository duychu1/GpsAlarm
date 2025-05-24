package com.common.control;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.LogLevel;
import com.common.control.manager.AdmobManager;
import com.common.control.manager.AppOpenManager;
import com.common.control.manager.PurchaseManager;
import com.common.control.manager.PurchaseManagerInApp;
import com.common.control.model.PurchaseModel;
import com.common.control.utils.AppUtils;

import java.util.List;


public abstract class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        SharePrefUtils.getInstance().init(this);
        AdmobManager.getInstance().init(this, isShowAdsTest() ? AdmobManager.getInstance().getDeviceId(this) : "");
        AdmobManager.getInstance().hasAds(hasAds());

        onApplicationCreate();

        if (enableAdsResume()) {
            AppOpenManager.getInstance().init(this, getOpenAppAdId());
        }
        AdmobManager.getInstance().setShowLoadingDialog(isShowDialogLoadingAd());

        AppUtils.getInstance().setPolicyUrl(getPolicyUrl());
        AppUtils.getInstance().setEmail(getEmailSupport());
        AppUtils.getInstance().setSubject(getSubjectSupport());
        AdmobManager.getInstance().setHasLog(true);
        if (isInitBilling()) {
            PurchaseManager.getInstance().init(this, getPurchaseList());
            PurchaseManagerInApp.getInstance().init(this, getPurchaseListInApp());
        }

        if (hasAdjust()) {
            AdmobManager.getInstance().hasAdjust(true);
            String environment = BuildConfig.DEBUG ? AdjustConfig.ENVIRONMENT_SANDBOX : AdjustConfig.ENVIRONMENT_PRODUCTION;
            AdjustConfig config = new AdjustConfig(this, getAdjustAppToken(), environment);
            config.setLogLevel(LogLevel.VERBOSE);
            //remove log
//        config.setLogLevel(LogLevel.SUPRESS);
//            Adjust.onCreate(config);
            registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
        }
    }


    private static class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }

    }


    protected abstract void onApplicationCreate();

    protected abstract boolean hasAdjust();

    protected abstract String getAdjustAppToken();

    protected abstract boolean hasAds();

    protected abstract boolean isShowDialogLoadingAd();

    protected abstract boolean isShowAdsTest();

    protected abstract boolean enableAdsResume();

    protected abstract String getOpenAppAdId();

    protected abstract String getPolicyUrl();

    protected abstract String getSubjectSupport();

    protected abstract String getEmailSupport();

    protected abstract boolean isInitBilling();

    protected abstract List<PurchaseModel> getPurchaseList();

    protected abstract List<PurchaseModel> getPurchaseListInApp();

    public abstract Class getFirstActForOpenApp();

}

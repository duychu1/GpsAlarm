package com.ruicomp.gpsalarm

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.common.control.BaseApplication
import com.common.control.manager.AppOpenManager
import com.common.control.model.PurchaseModel
import com.ruicomp.gpsalarm.datastore2.di.dataStoreModule
import com.ruicomp.gpsalarm.feature.outer.Lfo1Activity
import com.ruicomp.gpsalarm.feature.outer.Lfo2Activity
import com.ruicomp.gpsalarm.feature.outer.OnboardActivity
import com.ruicomp.gpsalarm.feature.outer.PermissionActivity
import com.ruicomp.gpsalarm.feature.outer.SplashActivity
import com.ruicomp.gpsalarm.remote_config.RemoteConfigManager
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

@HiltAndroidApp
class MyApp: BaseApplication(), Application.ActivityLifecycleCallbacks {
    companion object{
        const val PRODUCT_SUBS = "sub"
        const val PRODUCT_LIFETIME = "lifetime"
    }
    val TAG = "GpsAlarm"
    private val lsActivity = ArrayList<Activity>()



    override fun onApplicationCreate() {
        startKoin {
            androidContext(this@MyApp)
            modules(dataStoreModule)
        }
        RemoteConfigManager.instance?.loadRemote()

        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(Lfo1Activity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(Lfo2Activity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(OnboardActivity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionActivity::class.java)
//        AppOpenManager.getInstance().disableAppResumeWithRewardActivity(HomeActivity::class.java)
//        AppOpenManager.getInstance().specialAppResumeWithActivity(IncomingActivity::class.java)
        registerActivityLifecycleCallbacks(this)
        EventLogger.init(applicationContext)

    }

    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        super.onActivityPreCreated(activity, savedInstanceState)
        val removableActivities = ArrayList<Activity>()
        if (activity is SplashActivity) {
            if (lsActivity.isNotEmpty()) {
                for (a in lsActivity) {
                    a.finish()
                    removableActivities.add(a)
                }
            }
        }
        lsActivity.removeAll(removableActivities.toSet())
        lsActivity.add(activity)
    }

    override fun hasAdjust(): Boolean {
        return false
    }

    override fun getAdjustAppToken(): String? {
        return null
    }

    override fun hasAds(): Boolean {
        return true
    }

    override fun isShowDialogLoadingAd(): Boolean {
        return true
    }

    override fun isShowAdsTest(): Boolean {
        return BuildConfig.TEST_AD || BuildConfig.DEBUG
    }

    override fun enableAdsResume(): Boolean {
        return true
    }

    override fun getOpenAppAdId(): String {
        return BuildConfig.app_reopen
    }

    override fun getPolicyUrl(): String {
        return ""
    }

    override fun getSubjectSupport(): String {
        return ""
    }

    override fun getEmailSupport(): String {
        return ""
    }


    override fun isInitBilling(): Boolean {
        return true
    }

    override fun getPurchaseList(): List<PurchaseModel?>? {
        return listOf<PurchaseModel>(PurchaseModel(PRODUCT_SUBS, PurchaseModel.ProductType.SUBS))
    }

    override fun getPurchaseListInApp(): List<PurchaseModel?>? {
        return listOf<PurchaseModel>(PurchaseModel(PRODUCT_LIFETIME, PurchaseModel.ProductType.INAPP))
    }

    override fun getFirstActForOpenApp(): Class<*> {
        return MainActivity::class.java
    }


    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityResumed(p0: Activity) {
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }


}
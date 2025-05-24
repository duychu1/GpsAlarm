package com.common.control.base;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.common.control.BaseLanguageActivity;
import com.google.firebase.analytics.FirebaseAnalytics;

public abstract class BaseActivity<T extends ViewDataBinding> extends BaseLanguageActivity {
    protected T binding;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Context context;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        this.context = newBase;
    }

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        setTheme(R.style.Theme_AppThemeDark);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initLanguage();
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        binding.executePendingBindings();
        initView();
        addEvent();
    }


    public void logEventFirebase(String value, String token) {
        logEventFirebase(value);
        logEventAdjust(token);
    }

    public void logEventFirebase(String value) {
        try {
            Log.d("android_log", "logEvent: " + value);
            Bundle bundle = new Bundle();
            bundle.putString("EVENT", value);
            mFirebaseAnalytics.logEvent(value, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logEventAdjust(String token) {
        AdjustEvent adjustEvent = new AdjustEvent(token);
        Adjust.trackEvent(adjustEvent);
    }

    public void setUserProperty(String name) {
        try {
            mFirebaseAnalytics.setUserProperty(name, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initLanguage() {

    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void addEvent();


}



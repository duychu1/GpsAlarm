package com.common.control.base;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;


abstract public class BaseFragment<T extends ViewDataBinding> extends Fragment implements View.OnClickListener {
    protected Context context;
    protected View rootView;
    protected OnActionCallback callback;
    protected T binding;

    public void setCallback(OnActionCallback callback) {
        this.callback = callback;
    }

    @Override
    public final void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        addEvent();
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void addEvent();


    protected String[] getPermissions() {
        return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    @Override
    public void onClick(View v) {
    }

}

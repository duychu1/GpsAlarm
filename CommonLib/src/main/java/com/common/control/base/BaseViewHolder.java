package com.common.control.base;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;


abstract public class BaseViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    protected T binding;

    public BaseViewHolder(T viewDataBinding) {
        super(viewDataBinding.getRoot());
        binding = viewDataBinding;
    }

    abstract public void loadData(Object item);
}

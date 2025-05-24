package com.common.control.interfaces;

public interface RateCallback {

    void onMaybeLater();

    void onSubmit(String review);

    void onRate();

    void starRate(float v);

}

package com.common.control.interfaces;

public interface PermissionCallback {
    void onPermissionGranted();

    void onPermissionDenied();

    void onPressDenied();

    void onPressGrant();
}

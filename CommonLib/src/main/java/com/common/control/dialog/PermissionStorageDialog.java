package com.common.control.dialog;

import static com.common.control.utils.PermissionUtils.PERMISSIONS_STORAGE;
import static com.common.control.utils.PermissionUtils.RQC_REQUEST_PERMISSION_ANDROID_11;
import static com.common.control.utils.PermissionUtils.RQC_REQUEST_PERMISSION_ANDROID_BELOW;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.common.control.R;
import com.common.control.interfaces.PermissionCallback;
import com.common.control.utils.PermissionUtils;


public class PermissionStorageDialog extends AppCompatActivity implements View.OnClickListener {

    public static PermissionCallback callback;

    public static void start(Activity context, PermissionCallback permissionCallback) {
        callback = permissionCallback;
        Intent intent = new Intent(context, PermissionStorageDialog.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_storage);
        if (PermissionUtils.isStoragePermissionGranted(this)) {
            if (callback != null) {
                callback.onPermissionGranted();
            }
            finish();
            return;
        }
        addEvent();
    }

    private void addEvent() {
        findViewById(R.id.bt_deny).setOnClickListener(this);
        findViewById(R.id.bt_grant).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bt_deny) {
            if (callback != null) {
                callback.onPermissionDenied();
            }
            finish();
            return;
        }
        if (id == R.id.bt_grant) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.requestPermissions(PERMISSIONS_STORAGE, RQC_REQUEST_PERMISSION_ANDROID_BELOW);
                }
                return;
            }

            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", this.getPackageName())));
                this.startActivityForResult(intent, RQC_REQUEST_PERMISSION_ANDROID_11);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                this.startActivityForResult(intent, RQC_REQUEST_PERMISSION_ANDROID_11);
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callback == null) {
            finish();
            return;
        }
        if (requestCode == RQC_REQUEST_PERMISSION_ANDROID_11) {
            if (PermissionUtils.isStoragePermissionGranted(this)) {
                callback.onPermissionGranted();
            } else {
                callback.onPermissionDenied();
            }
        }
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != RQC_REQUEST_PERMISSION_ANDROID_BELOW || callback == null) {
            finish();
            return;
        }

        if (PermissionUtils.isStoragePermissionGranted(this)) {
            callback.onPermissionGranted();
        } else {
            callback.onPermissionDenied();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callback = null;
    }
}

package com.common.control.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.common.control.R;
import com.common.control.interfaces.PermissionCallback;
import com.common.control.utils.PermissionUtils;


public class PermissionSystemDialog extends AppCompatActivity {

    private static PermissionCallback permissionCallback;

    public static void start(Activity context, PermissionCallback callback, String... permissions) {
        permissionCallback = callback;
        Intent intent = new Intent(context, PermissionSystemDialog.class);
        intent.putExtra("data", permissions);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_normal);
        initViews();
    }

    private void initViews() {
        String[] permissions = getIntent().getStringArrayExtra("data");
        if (PermissionUtils.permissionGranted(this, permissions)) {
            if (permissionCallback != null) {
                permissionCallback.onPermissionGranted();
            }
            finish();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionUtils.requestPermission(this, permissions);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionCallback != null) {
            if (PermissionUtils.permissionGranted(this, permissions)) {
                permissionCallback.onPermissionGranted();
            } else {
                permissionCallback.onPermissionDenied();
            }
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        permissionCallback = null;
    }
}

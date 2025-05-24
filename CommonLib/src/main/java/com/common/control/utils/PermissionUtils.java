package com.common.control.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.common.control.R;


public class PermissionUtils {
    public static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final int RQC_REQUEST_PERMISSION_ANDROID_11 = 51233;
    public static final int RQC_REQUEST_PERMISSION_ANDROID_BELOW = 53233;
    private static final int REQUEST_PERMISSION_ANY = 100225;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void showDefaultPermissionDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.pl_grant_permission)
                .setMessage(R.string.pl_grant_permission_desc)
                .setPositiveButton("Go Settings", (dialog, which) -> {
                    final Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    context.startActivity(intent);
                    dialog.dismiss();
                }).setNegativeButton(R.string.pl_cancel, (dialog, which) -> dialog.cancel())
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestPermission(Activity activity, String... permission) {
        activity.requestPermissions(permission, REQUEST_PERMISSION_ANY);
    }

    public static boolean permissionGranted(Activity activity, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
}

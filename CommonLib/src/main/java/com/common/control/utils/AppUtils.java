package com.common.control.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AppUtils {
    private static AppUtils instance;
    private static final String TAG = AppUtils.class.getName();
    private String policyUrl;
    private String subject;
    private String email;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setPolicyUrl(String policyUrl) {
        this.policyUrl = policyUrl;
    }

    private AppUtils() {
    }


    public static AppUtils getInstance() {
        if (instance == null) {
            instance = new AppUtils();
        }
        return instance;
    }


    public void shareApp(Context context) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody =
                "https://play.google.com/store/apps/details?id=" + context.getPackageName();
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent, "Share to"));
    }

    public void support(Context context) {
        Intent mailIntent = new Intent(Intent.ACTION_VIEW);
        Uri data =
                Uri.parse("mailto:?SUBJECT=" + subject + "&body=" + "" + "&to=" + email);
        mailIntent.setData(data);
        context.startActivity(Intent.createChooser(mailIntent, "Send mail..."));
    }

    public void rateApp(Context context) {
        try {
            context.startActivity(
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + context.getPackageName())
                    )
            );
        } catch (ActivityNotFoundException anfe) {
            context.startActivity(
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())
                    )
            );
        }
    }

    public void showPolicy(Context context) {
        openWeb(context, policyUrl);
    }

    public void openWeb(Context context, String url) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                    url)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void log(String text) {
        Log.d(TAG, text);
    }


    public void saveFile(InputStream fin, String savePath, String nameFile) {
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(new File(savePath + "/" + nameFile));
            int lenght = 0;
            byte buff[] = new byte[1024];
            lenght = fin.read(buff);
            while (lenght > 0) {
                fout.write(buff, 0, lenght);
                lenght = fin.read(buff);
            }
            fin.close();
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String formatTime(long duration) {
        return formatTime(duration, false);
    }

    public String formatTime(long duration, boolean isHour) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        if (!isHour) {
            formatter = new SimpleDateFormat("mm:ss");
        }
        Date date = new Date(duration);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

    public String formatDate(long duration) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date(duration);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

    public void shareFile(Context context, File file) {
        try {
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            shareFile(context, uri);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void shareFile(Context context, Uri uri) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getFileName(context, uri));
            emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            emailIntent.setDataAndType(uri, "*/*");
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(emailIntent);
        } catch (Exception ex) {
            try {
                Intent intent = ShareCompat.IntentBuilder.from((Activity) context).setType(context.getContentResolver().getType(uri)).setStream(uri).getIntent();
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                Intent createChooser = Intent.createChooser(intent, "Share File");
                createChooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (createChooser.resolveActivity(context.getPackageManager()) == null) {
                    return;
                }
                context.startActivity(createChooser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @SuppressLint("Range")
    private String getFileName(Context context, Uri uri) {
        String result = "";
        try {
            if (uri.getScheme().equals("content")) {
                try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }
            }
            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}

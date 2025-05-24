package com.common.control.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.text.DecimalFormat;


public class FileUtils {
    private static FileUtils instance;

    public static FileUtils getInstance() {
        if (instance == null) {
            instance = new FileUtils();
        }
        return instance;
    }

    private FileUtils() {

    }

    public String getPathFromUri(final Context context, final Uri uri) {
        String path = "";
        try {
            path = RealPathUtil.getPathFromData(context, uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (TextUtils.isEmpty(path)) {
                path = uri.toString();
                int indexOf = path.indexOf(":");
                if (indexOf > 0) {
                    path = path.substring(indexOf + 3);
                }
                path = Uri.decode(path);
            }
            if (!TextUtils.isEmpty(path) && path.contains("/raw:")) {
                String str = path;
                path = str.substring(str.indexOf("/raw:") + 5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("0.00");
        String fileSizeString;
        if (fileSize <= 0) {
            fileSizeString = "0KB";
        } else if (fileSize < (1024 * 1024)) {
            fileSizeString = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < (1024 * 1024 * 1024)) {
            fileSizeString = df.format((double) fileSize / (1024 * 1024)) + "MB";
        } else {
            fileSizeString = df.format((double) fileSize / (1024 * 1024 * 1024)) + "GB";
        }
        return fileSizeString;
    }

    public String getFileExtensionNoPoint(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        return getFileExtensionNoPoint(new File(path));
    }

    private String getFileExtensionNoPoint(File file) {
        if (file == null || file.isDirectory()) {
            return "";
        }
        String fileName = file.getName();
        if (fileName.length() > 0) {
            int lastIndex = fileName.lastIndexOf('.');
            if ((lastIndex > -1) && (lastIndex < (fileName.length() - 1))) {
                return fileName.substring(lastIndex + 1);
            }
        }
        return "";
    }


    private boolean isCompareFiles(String path1, String path2) {
        if (TextUtils.isEmpty(path1) || TextUtils.isEmpty(path2)) {
            return false;
        }
        if (path1.equalsIgnoreCase(path2)) {
            return true;
        } else {
            return isCompareFiles(new File(path1), new File(path2));
        }
    }

    private boolean isCompareFiles(File file1, File file2) {
        if (file1 == null || file2 == null) {
            return false;
        }
        return file1.getPath().equalsIgnoreCase(file2.getPath());
    }

    public String getFileNameNoExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        return getFileNameNoExtension(new File(filePath));
    }

    public String getFileNameNoExtension(File file) {
        if (file == null) {
            return "";
        }
        String filename = file.getName();
        if (!TextUtils.isEmpty(filename)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    private boolean isFileExist(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                return file.exists();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private String getFileMimeTypeFromExtension(String fileType) {
        try {
            if (TextUtils.isEmpty(fileType)) {
                return "*/*";
            }
            fileType = fileType.replace(".", "");
            if (fileType.equalsIgnoreCase("docx") || fileType.equalsIgnoreCase("wps")) {
                fileType = "doc";
            } else if (fileType.equalsIgnoreCase("xlsx")) {
                fileType = "xls";
            } else if (fileType.equalsIgnoreCase("pptx")) {
                fileType = "ppt";
            }
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            if (mimeTypeMap.hasExtension(fileType)) {
                return mimeTypeMap.getMimeTypeFromExtension(fileType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "*/*";
    }

}
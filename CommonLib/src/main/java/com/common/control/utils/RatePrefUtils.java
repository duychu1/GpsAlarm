package com.common.control.utils;

public class RatePrefUtils {

    public static boolean isRated() {
        return SharePrefUtils.getInstance().getBoolean("rate", false);
    }

    public static void forceRate() {
        SharePrefUtils.getInstance().put("rate", true);
    }

}

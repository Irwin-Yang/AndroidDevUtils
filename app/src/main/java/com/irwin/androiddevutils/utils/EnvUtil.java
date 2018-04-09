package com.irwin.androiddevutils.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Process;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Irwin on 2017/8/24.
 * Environment utils.
 */

public class EnvUtil {

    /**
     * Tell if Application debuggable.
     * @param context
     * @return
     */
    public static boolean isAppDebuggable(Context context) {
        PackageInfo pkginfo = getPackageInfo(context);
        if (pkginfo != null) {
            ApplicationInfo info = pkginfo.applicationInfo;
            return (info != null && (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
        }
        return false;
    }

    /**
     * Get package info.
     * @param context
     * @return
     */
    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }

    /**
     * Get Screen info.
     * @param context
     * @return
     */
    public static DisplayMetrics getScreenInfo(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    /**
     * Restart application.
     * @param context
     */
    public static void restartApplication(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Dial a number.
     * @param context
     * @param phoneNumber
     */
    public static void dialTo(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNumber);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * Tell if apk installed.
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<String>();
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        return packageNames.contains(packageName);
    }


    /**
     * Tell if apk installed.
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkInstall(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
            return (packageInfo != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Tell if there are any activity match the intent specified.
     * @param context
     * @param intent
     * @return
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * Tell if Service is running.
     *
     * @param context
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(200);
        if (serviceList != null && serviceList.size() > 0) {
            for (int i = 0; i < serviceList.size(); i++) {
                if (serviceList.get(i).service.getClassName().equals(className)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get process name of current process.
     *
     * @param context
     * @return
     */
    public static String getMyProcessName(Context context) {
        return getProcessName(context, Process.myPid());
    }

    /**
     * Get process name.
     *
     * @param context
     * @param processId
     * @return
     */
    public static String getProcessName(Context context, int processId) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processList = manager.getRunningAppProcesses();
        if (processList != null && processList.size() > 0) {
            for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
                if (process.pid == processId) {
                    return process.processName;
                }
            }
        }
        return "";
    }

    /**
     * Tell if current process is main process.
     *
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        context = context.getApplicationContext();
        return context.getPackageName().equals(getMyProcessName(context));
    }

    /**
     * Tell if app in background
     *
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                return (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
            }
        }
        return false;
    }


}

package cm.android.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Debug;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cm.java.util.Utils;

/**
 * 系统环境Util类
 */
public class SystemUtil {

    private static final Logger logger = LoggerFactory.getLogger(SystemUtil.class);


    /**
     * 判断进程是否正在运行
     *
     * @param name 运行的进程名
     */
    @TargetApi(3)
    public static boolean isProcessRunning(Context ctx, String name) {
        ActivityManager am = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> apps = am.getRunningAppProcesses();
        for (RunningAppProcessInfo app : apps) {
            if (app.processName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(3)
    public static boolean isTopActivity(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am
                .getRunningAppProcesses();
        if (list != null && list.isEmpty()) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo process : list) {
            if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && process.processName.equals(ctx.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断Service是否正在运行
     *
     * @param serviceName service名
     * @param processName 该service所在进程名
     */
    public static boolean isServiceRunning(Context ctx, String serviceName,
            String processName) {
        ActivityManager manager = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())
                    && processName.equals(service.process)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取栈顶activity
     */
    public static String getTopActivityPackageName(Context context) {
        List<RunningTaskInfo> taskInfos;
        // 判断程序是否处于桌面
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        taskInfos = am.getRunningTasks(1);
        String packageName = taskInfos.get(0).topActivity.getPackageName();
        return packageName;
    }

    /**
     * 获取正在运行的进程列表
     */
    @TargetApi(3)
    public static List<RunningAppProcessInfo> getRunningProcess(Context ctx) {
        ActivityManager am = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> apps = am.getRunningAppProcesses();
        return apps;
    }

    /**
     * 获取应用下所有Activity
     */
    @TargetApi(4)
    public static ArrayList<String> getActivities(Context ctx) {
        ArrayList<String> result = new ArrayList<String>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.setPackage(ctx.getPackageName());
        for (ResolveInfo info : ctx.getPackageManager().queryIntentActivities(
                intent, 0)) {
            result.add(info.activityInfo.name);
        }
        return result;
    }

    @TargetApi(5)
    public static Debug.MemoryInfo getRunningProcessMemoryInfo(Context context,
            String packageName) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> list2 = am.getRunningAppProcesses();
        for (RunningAppProcessInfo ti : list2) {
            // if (ti.processName.equals("system")
            // || ti.processName.equals("com.android.phone")) {
            // continue;
            // }

            if (ti.processName.equals(packageName)) {
                int[] pids = new int[]{ti.pid};
                Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(pids);
                return memoryInfos[0];
            }
        }
        return null;
    }

    /**
     * 根据processName获得RunningAppProcessInfo
     */
    @TargetApi(3)
    public static RunningAppProcessInfo getRunningAppProcessInfo(
            Context context, String processName) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> list2 = am.getRunningAppProcesses();
        for (RunningAppProcessInfo ti : list2) {
            if (ti.processName.equals(processName)) {
                return ti;
            }
        }
        return null;
    }

    public static boolean isRunningInEmulator() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("getprop ro.kernel.qemu");
            os = new DataOutputStream(process.getOutputStream());
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "UTF-8"));
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            // getprop ro.kernel.qemu == 1  在模拟器
            // getprop ro.product.model == "sdk"  在模拟器
            // getprop ro.build.tags == "test-keys"  在模拟器
            boolean qemuKernel = (Integer.valueOf(in.readLine()) != 0);
            return qemuKernel;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return true;
        } finally {
            cm.java.util.IoUtil.closeQuietly(os);
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * 判断手机是否root，不弹出root请求框<br/>
     */
    public static boolean isRoot() {
        String binPath = "/system/bin/su";
        String xBinPath = "/system/xbin/su";
        if (new File(binPath).exists() && isExecutable(binPath)) {
            return true;
        }
        if (new File(xBinPath).exists() && isExecutable(xBinPath)) {
            return true;
        }
        return false;
    }

    private static boolean isExecutable(String filePath) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("ls -l " + filePath);
            // 获取返回内容
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String str = in.readLine();
            logger.info("str = " + str);
            if (str != null && str.length() >= 4) {
                char flag = str.charAt(3);
                if (flag == 's' || flag == 'x') {
                    return true;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return false;
    }

    public static boolean isAppForeground(Context paramContext, String paramString) {
        if (TextUtils.isEmpty(paramString)) {
            ;
        }
        ActivityManager.RunningTaskInfo localRunningTaskInfo = getFirstRunningTaskInfo(
                paramContext);

        if (localRunningTaskInfo == null) {
            return false;
        }

        return TextUtils.equals(localRunningTaskInfo.baseActivity.getPackageName(), paramString);
    }

    public static ActivityManager.RunningTaskInfo getFirstRunningTaskInfo(Context paramContext) {
        List localList = ((ActivityManager) paramContext.getSystemService(Context.ACTIVITY_SERVICE))
                .getRunningTasks(1);
        if ((Utils.isEmpty(localList))) {
            return null;
        }
        return (ActivityManager.RunningTaskInfo) localList.get(0);
    }

    public static String getTopActivityName(Context context) {
        List<ActivityManager.RunningTaskInfo> taskInfos;
        // 判断程序是否处于桌面
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        taskInfos = am.getRunningTasks(1);
        String activityName = taskInfos.get(0).topActivity.getClassName();
        return activityName;
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        logger.info("pid = " + pid);
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

}

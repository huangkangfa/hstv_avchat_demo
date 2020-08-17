package com.android.baselib.utils

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.android.baselib.global.AppGlobal.context
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

object Process {
    /**
     * 获取进程号对应的进程名
     * @param pid 进程号
     * @return 进程名
     */
    fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim { it <= ' ' }
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        return null
    }

    /**
     * 获取前台线程包名
     *
     * 当不是查看当前App，且SDK大于21时，
     * 需添加权限 `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"/>`
     *
     * @return 前台应用包名
     */
    fun getForegroundProcessName(): String? {
        val manager = context
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val infos =
            manager.runningAppProcesses
        if (infos != null && infos.size != 0) {
            for (info in infos) {
                if (info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return info.processName
                }
            }
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            val packageManager: PackageManager = context.packageManager
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            val list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            println(list)
            if (list.size > 0) { // 有"有权查看使用权限的应用"选项
                try {
                    val info = packageManager.getApplicationInfo(
                        context.getPackageName(), 0
                    )
                    val aom = context
                        .getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                    if (aom.checkOpNoThrow(
                            AppOpsManager.OPSTR_GET_USAGE_STATS,
                            info.uid,
                            info.packageName
                        ) != AppOpsManager.MODE_ALLOWED
                    ) {
                        context.startActivity(intent)
                    }
                    if (aom.checkOpNoThrow(
                            AppOpsManager.OPSTR_GET_USAGE_STATS,
                            info.uid,
                            info.packageName
                        ) != AppOpsManager.MODE_ALLOWED
                    ) {
                        Log.d("getForegroundApp", "没有打开\"有权查看使用权限的应用\"选项")
                        return null
                    }
                    val usageStatsManager = context
                        .getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                    val endTime = System.currentTimeMillis()
                    val beginTime = endTime - 86400000 * 7
                    val usageStatses =
                        usageStatsManager.queryUsageStats(
                            UsageStatsManager.INTERVAL_BEST,
                            beginTime,
                            endTime
                        )
                    if (usageStatses == null || usageStatses.isEmpty()) return null
                    var recentStats: UsageStats? = null
                    for (usageStats in usageStatses) {
                        if (recentStats == null || usageStats.lastTimeUsed > recentStats.lastTimeUsed) {
                            recentStats = usageStats
                        }
                    }
                    return recentStats?.packageName
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            } else {
                Log.d("getForegroundApp", "无\"有权查看使用权限的应用\"选项")
            }
        }
        return null
    }

    /**
     * 获取后台服务进程
     *
     * 需添加权限 `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"/>`
     *
     * @return 后台服务进程
     */
    fun getAllBackgroundProcesses(): HashMap<String, List<String>>? {
        val am = context
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val infos = am.runningAppProcesses
        val map: HashMap<String, List<String>> = HashMap()
        for (info in infos) {
            if (info.processName != null) {
                map[info.processName] = info.pkgList.toList()
            }
        }
        return map
    }

    /**
     * 杀死所有的后台服务进程
     *
     * 需添加权限 `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"/>`
     *
     * @return 被暂时杀死的服务集合
     */
    fun killAllBackgroundProcesses(): Set<String>? {
        val am = context
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var infos = am.runningAppProcesses
        val set: MutableSet<String> = HashSet()
        for (info in infos) {
            for (pkg in info.pkgList) {
                am.killBackgroundProcesses(pkg)
                set.add(pkg)
            }
        }
        infos = am.runningAppProcesses
        for (info in infos) {
            for (pkg in info.pkgList) {
                set.remove(pkg)
            }
        }
        return set
    }

    /**
     * 杀死后台服务进程
     *
     * 需添加权限 `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"/>`
     *
     * @param packageName 包名
     * @return `true`: 杀死成功<br></br>`false`: 杀死失败
     */
    fun killBackgroundProcesses(packageName: String?): Boolean {
        if (TextUtils.isEmpty(packageName)) return false
        val am = context
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var infos = am.runningAppProcesses
        if (infos == null || infos.size == 0) return true
        for (info in infos) {
            if (Arrays.asList(*info.pkgList).contains(packageName)) {
                am.killBackgroundProcesses(packageName)
            }
        }
        infos = am.runningAppProcesses
        if (infos == null || infos.size == 0) return true
        for (info in infos) {
            if (Arrays.asList(*info.pkgList).contains(packageName)) {
                return false
            }
        }
        return true
    }
}
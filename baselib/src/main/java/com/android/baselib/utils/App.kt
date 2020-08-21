package com.android.baselib.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.text.TextUtils
import android.util.Log
import com.android.baselib.global.AppGlobal.context
import com.android.baselib.utils.Process.getForegroundProcessName
import java.io.File

/**
 * 获取版本号
 */
fun getVersionName(): String {
    var versionName = ""
    try {
        val pi: PackageInfo =
            context.packageManager.getPackageInfo(context.packageName, 0)
        versionName = pi.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return versionName
}

/**
 * 获取包名
 */
fun getPackageName(): String {
    return context.packageName
}

/**
 * 获取自己当前进程名
 */
fun getProcessName(): String? {
    return Process.getProcessName(android.os.Process.myPid())
}

/**
 * 判断App是否安装
 *
 * @param context     上下文
 * @param packageName 包名
 * @return `true`: 已安装<br></br>`false`: 未安装
 */
fun isInstallApp(
    context: Context,
    packageName: String
): Boolean {
    return !TextUtils.isEmpty(packageName) && getLaunchAppIntent(
        context,
        packageName
    ) != null
}

/**
 * 安装App(支持6.0)
 *
 * @param context  上下文
 * @param filePath 文件路径
 */
fun installApp(context: Context, fileProviderStr: String, filePath: String) {
    val temp = getFileByPath(filePath)
    if (temp != null)
        installApp(context, fileProviderStr, temp)
}

/**
 * 安装App（支持6.0）
 *
 * @param context 上下文
 * @param file    文件
 */
fun installApp(context: Context, fileProviderStr: String, file: File) {
    if (!isFileExists(file)) return
    context.startActivity(getInstallAppIntent(context, fileProviderStr, file))
}

/**
 * 安装App（支持6.0）
 *
 * @param activity    activity
 * @param filePath    文件路径
 * @param requestCode 请求值
 */
fun installApp(activity: Activity, fileProviderStr: String, filePath: String, requestCode: Int) {
    getFileByPath(filePath)?.let { installApp(activity, fileProviderStr, it, requestCode) }
}

/**
 * 安装App(支持6.0)
 *
 * @param activity    activity
 * @param file        文件
 * @param requestCode 请求值
 */
fun installApp(activity: Activity, fileProviderStr: String, file: File, requestCode: Int) {
    if (!isFileExists(file)) return
    activity.startActivityForResult(
        getInstallAppIntent(activity, fileProviderStr, file),
        requestCode
    )
}

/**
 * 静默安装App
 *
 * 非root需添加权限 `<uses-permission android:name="android.permission.INSTALL_PACKAGES" />`
 *
 * @param filePath 文件路径
 * @return `true`: 安装成功<br></br>`false`: 安装失败
 */
fun installAppSilent(file: File): Boolean {
    if (!isFileExists(file)) return false
    val command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install ${file.absolutePath}"
    val commandResult: CommandResult = execCmd(
        command,
        !isSystemApp(context),
        true
    )
    return commandResult.successMsg != null && commandResult.successMsg!!.toLowerCase()
        .contains("success")
}

/**
 * 卸载App
 *
 * @param context     上下文
 * @param packageName 包名
 */
fun uninstallApp(context: Context, packageName: String) {
    if (TextUtils.isEmpty(packageName)) return
    context.startActivity(getUninstallAppIntent(packageName))
}

/**
 * 卸载App
 *
 * @param activity    activity
 * @param packageName 包名
 * @param requestCode 请求值
 */
fun uninstallApp(
    activity: Activity,
    packageName: String?,
    requestCode: Int
) {
    if (TextUtils.isEmpty(packageName)) return
    activity.startActivityForResult(packageName?.let { getUninstallAppIntent(it) }, requestCode)
}

/**
 * 静默卸载App
 *
 * 非root需添加权限 `<uses-permission android:name="android.permission.DELETE_PACKAGES" />`
 *
 * @param context     上下文
 * @param packageName 包名
 * @param isKeepData  是否保留数据
 * @return `true`: 卸载成功<br></br>`false`: 卸载成功
 */
fun uninstallAppSilent(
    context: Context,
    packageName: String,
    isKeepData: Boolean
): Boolean {
    if (TextUtils.isEmpty(packageName)) return false
    val command =
        "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall " + (if (isKeepData) "-k " else "") + packageName
    val commandResult: CommandResult = execCmd(
        command,
        !isSystemApp(context),
        true
    )
    return commandResult.successMsg != null && commandResult.successMsg!!.toLowerCase()
        .contains("success")
}


/**
 * 判断App是否有root权限
 *
 * @return `true`: 是<br></br>`false`: 否
 */
fun isAppRoot(): Boolean {
    val result: CommandResult = execCmd("echo root", true)
    if (result.result === 0) {
        return true
    }
    if (result.errorMsg != null) {
        Log.d("isAppRoot", result.errorMsg!!)
    }
    return false
}

/**
 * 打开App
 *
 * @param context     上下文
 * @param packageName 包名
 */
fun launchApp(context: Context, packageName: String?) {
    if (TextUtils.isEmpty(packageName)) return
    context.startActivity(getLaunchAppIntent(context, packageName))
}

/**
 * 打开App
 *
 * @param activity    activity
 * @param packageName 包名
 * @param requestCode 请求值
 */
fun launchApp(activity: Activity, packageName: String?, requestCode: Int) {
    if (TextUtils.isEmpty(packageName)) return
    activity.startActivityForResult(
        getLaunchAppIntent(activity, packageName),
        requestCode
    )
}

/**
 * 获取App包名
 *
 * @param context 上下文
 * @return App包名
 */
fun getAppPackageName(context: Context): String? {
    return context.packageName
}

/**
 * 获取App具体设置
 *
 * @param context 上下文
 */
fun getAppDetailsSettings(context: Context) {
    getAppDetailsSettings(context, context.packageName)
}

/**
 * 获取App具体设置
 *
 * @param context     上下文
 * @param packageName 包名
 */
fun getAppDetailsSettings(
    context: Context,
    packageName: String
) {
    if (TextUtils.isEmpty(packageName)) return
    context.startActivity(getAppDetailsSettingsIntent(packageName))
}

/**
 * 判断App是否是系统应用
 *
 * @param context 上下文
 * @return `true`: 是<br></br>`false`: 否
 */
fun isSystemApp(context: Context): Boolean {
    return isSystemApp(context, context.packageName)
}

/**
 * 判断App是否是系统应用
 *
 * @param context     上下文
 * @param packageName 包名
 * @return `true`: 是<br></br>`false`: 否
 */
fun isSystemApp(
    context: Context,
    packageName: String?
): Boolean {
    return if (TextUtils.isEmpty(packageName)) false else try {
        val pm = context.packageManager
        val ai = pm.getApplicationInfo(packageName!!, 0)
        ai.flags and ApplicationInfo.FLAG_SYSTEM != 0
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        false
    }
}

/**
 * 获取App签名
 *
 * @param context 上下文
 * @return App签名
 */
fun getAppSignature(context: Context): Array<Signature>? {
    return getAppSignature(context, context.packageName)
}

/**
 * 获取App签名
 *
 * @param context     上下文
 * @param packageName 包名
 * @return App签名
 */
@SuppressLint("PackageManagerGetSignatures")
fun getAppSignature(
    context: Context,
    packageName: String?
): Array<Signature>? {
    return if (TextUtils.isEmpty(packageName)) null else try {
        val pm = context.packageManager
        val pi =
            pm.getPackageInfo(packageName!!, PackageManager.GET_SIGNATURES)
        pi?.signatures
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        null
    }
}

/**
 * 判断App是否处于前台
 *
 * @param context 上下文
 * @return `true`: 是<br></br>`false`: 否
 */
fun isAppForeground(context: Context): Boolean {
    val manager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val infos = manager.runningAppProcesses
    if (infos == null || infos.size == 0) return false
    for (info in infos) {
        if (info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            return info.processName == context.packageName
        }
    }
    return false
}

/**
 * 判断App是否处于前台
 *
 * 当不是查看当前App，且SDK大于21时，
 * 需添加权限 `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"/>`
 *
 * @param context     上下文
 * @param packageName 包名
 * @return `true`: 是<br></br>`false`: 否
 */
fun isAppForeground(
    context: Context?,
    packageName: String
): Boolean {
    return !TextUtils.isEmpty(packageName) && packageName == getForegroundProcessName()
}

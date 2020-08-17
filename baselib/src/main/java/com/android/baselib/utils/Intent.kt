package com.android.baselib.utils

import android.app.Activity
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

/**
 * 获取安装App(支持6.0)的意图
 *
 * @param file 文件
 * @return intent
 */
fun getInstallAppIntent(
    context: Context,
    fileProviderStr: String,
    file: File?
): Intent? {
    if (file == null) return null
    val intent =
        Intent(Intent.ACTION_VIEW)
    val type: String? = if (Build.VERSION.SDK_INT < 23) {
        "application/vnd.android.package-archive"
    } else {
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtension(file))
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val contentUri =
            FileProvider.getUriForFile(context, fileProviderStr, file)
        intent.setDataAndType(contentUri, type)
    }
    intent.setDataAndType(Uri.fromFile(file), type)
    return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}

/**
 * 获取卸载App的意图
 *
 * @param packageName 包名
 * @return intent
 */
fun getUninstallAppIntent(packageName: String): Intent {
    val intent =
        Intent(Intent.ACTION_DELETE)
    intent.data = Uri.parse("package:$packageName")
    return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}

/**
 * 获取打开App的意图
 *
 * @param context     上下文
 * @param packageName 包名
 * @return intent
 */
fun getLaunchAppIntent(
    context: Context,
    packageName: String?
): Intent? {
    return context.packageManager.getLaunchIntentForPackage(packageName!!)
}

/**
 * 获取App具体设置的意图
 *
 * @param packageName 包名
 * @return intent
 */
fun getAppDetailsSettingsIntent(packageName: String): Intent {
    val intent =
        Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
    intent.data = Uri.parse("package:$packageName")
    return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}

/**
 * 获取分享文本的意图
 *
 * @param content 分享文本
 * @return intent
 */
fun getShareTextIntent(content: String?): Intent {
    val intent =
        Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, content)
    return intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}

/**
 * 获取分享图片的意图
 *
 * @param content   文本
 * @param imagePath 图片文件路径
 * @return intent
 */
fun getShareImageIntent(
    content: String?,
    imagePath: String?
): Intent? {
    return getShareImageIntent(
        content,
        getFileByPath(imagePath!!)
    )
}

/**
 * 获取分享图片的意图
 *
 * @param content 文本
 * @param image   图片文件
 * @return intent
 */
fun getShareImageIntent(
    content: String?,
    image: File?
): Intent? {
    return if (!isFileExists(image)) null else getShareImageIntent(
        content,
        Uri.fromFile(image)
    )
}

/**
 * 获取分享图片的意图
 *
 * @param content 分享文本
 * @param uri     图片uri
 * @return intent
 */
fun getShareImageIntent(
    content: String?,
    uri: Uri?
): Intent {
    val intent =
        Intent(Intent.ACTION_SEND)
    intent.putExtra(Intent.EXTRA_TEXT, content)
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    intent.type = "image/*"
    return intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}

/**
 * 获取其他应用组件的意图
 *
 * @param packageName 包名
 * @param className   全类名
 * @return intent
 */
fun getComponentIntent(
    packageName: String,
    className: String
): Intent {
    return getComponentIntent(packageName, className, null)
}

/**
 * 获取其他应用组件的意图
 *
 * @param packageName 包名
 * @param className   全类名
 * @param bundle      bundle
 * @return intent
 */
fun getComponentIntent(
    packageName: String,
    className: String,
    bundle: Bundle?
): Intent {
    val intent =
        Intent(Intent.ACTION_VIEW)
    if (bundle != null) intent.putExtras(bundle)
    val cn = ComponentName(packageName, className)
    intent.component = cn
    return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}

/**
 * 获取关机的意图
 *
 * 需添加权限 `<uses-permission android:name="android.permission.SHUTDOWN"/>`
 *
 * @return intent
 */
val shutdownIntent: Intent
    get() {
        val intent =
            Intent(Intent.ACTION_SHUTDOWN)
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

/**
 * 获取拍照的意图
 *
 * @param outUri 输出的uri
 * @return 拍照的意图
 */
fun getCaptureIntent(outUri: Uri): Intent {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
    return intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
}

/**
 * 跳转界面 附带过场动画
 * @param context
 * @param startAnim
 * @param endAnim
 */
fun goAnimActivity(
    context: Context,
    intent: Intent,
    startAnim: Int,
    endAnim: Int
) {
    context.startActivity(intent)
    (context as Activity).overridePendingTransition(startAnim, endAnim)
}

fun goAnimActivityWithReturn(
    context: Context,
    intent: Intent,
    startAnim: Int,
    endAnim: Int,
    requstCode: Int
) {
    (context as Activity).startActivityForResult(intent, requstCode)
    context.overridePendingTransition(startAnim, endAnim)
}

/**
 * 隐式跳转
 */
fun goIntent(context: Context, vararg data: String) {
    //创建一个隐式的 Intent 对象：Action 动作
    val intent = Intent()
    //设置 Intent 的动作为清单中指定的action
    intent.action = data[0]
    if (data.size > 1) {
        //添加与清单中相同的自定义category
        intent.addCategory(data[1])
    }
    context.startActivity(intent)
}

fun goIntentWithReturn(
    context: Context,
    requstCode: Int,
    vararg data: String
) {
    val intent = Intent()
    intent.action = data[0]
    if (data.size > 1) {
        intent.addCategory(data[1])
    }
    (context as Activity).startActivityForResult(intent, requstCode)
}

/**
 * 显式跳转
 */
fun goIntent(context: Context, t: Class<*>) {
    val it = Intent(context, t)
    context.startActivity(it)
}

fun goIntentWithReturn(
    context: Context,
    t: Class<*>,
    requstCode: Int
) {
    val it = Intent(context, t)
    (context as Activity).startActivityForResult(it, requstCode)
}

/**
 * 跳转至指定网页
 */
fun goWebIntent(context: Context, url: String) {
    val uri = Uri.parse(url)
    val intent =
        Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}

fun goWebIntentWithReturn(
    context: Context,
    url: String,
    requstCode: Int
) {
    val uri = Uri.parse(url)
    val intent =
        Intent(Intent.ACTION_VIEW, uri)
    (context as Activity).startActivityForResult(intent, requstCode)
}

/**
 * 打开手机自带的移动网络设置
 */
fun goSysMobileNetWork(context: Context) {
    val netSettingsIntent =
        Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)
    context.startActivity(netSettingsIntent)
}

/**
 * 打开手机自带的WiFi网络设置
 */
fun goSysWiFiNetWork(context: Context) {
    val wifiSettingsIntent =
        Intent(Settings.ACTION_WIFI_SETTINGS)
    context.startActivity(wifiSettingsIntent)
}

/**
 * 电话拨号
 */
//跳转至拨号界面
fun goSysTelIntent(context: Context, tel: String) {
    val uri = Uri.parse("tel:$tel")
    val intent =
        Intent(Intent.ACTION_DIAL, uri)
    context.startActivity(intent)
}

//直接拨打电话
fun goSysCallTelIntent(context: Context, tel: String) {
    val uri = Uri.parse("tel:$tel")
    val intent =
        Intent(Intent.ACTION_CALL, uri)
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 发送短信
 */
//跳转至短信-不指定人
fun goSysSmsIntent(context: Context, str: String?) {
    val intent =
        Intent(Intent.ACTION_VIEW)
    intent.putExtra("sms_body", str)
    intent.type = "vnd.android-dir/mms-sms"
    context.startActivity(intent)
}

//跳转至短信-指定特定的人
fun goSysSmsIntentWithSomeone(
    context: Context,
    who: String,
    str: String?
) {
    val uri = Uri.parse("smsto:$who") //指定接收者
    val intent =
        Intent(Intent.ACTION_SENDTO, uri)
    intent.putExtra("sms_body", str)
    context.startActivity(intent)
}

/**
 * 发送邮件
 */
fun goSysEmailIntent(
    context: Context,
    where: String,
    title: String?,
    content: String?
) {
    val intent =
        Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:$where")
    intent.putExtra(Intent.EXTRA_SUBJECT, title)
    intent.putExtra(Intent.EXTRA_TEXT, content)
    context.startActivity(intent)
}

/**
 * 调用系统音乐播放器
 */
fun goSysMusicPlayerIntent(context: Context, url: String) {
    val intent =
        Intent(Intent.ACTION_VIEW)
    val uri = Uri.parse(url)
    intent.setDataAndType(uri, "audio/mp3")
    context.startActivity(intent)
}

/**
 * 调用系统视频播放器
 */
fun goSysVideoPlayerIntent(context: Context, url: String) {
    val intent =
        Intent(Intent.ACTION_VIEW)
    val uri = Uri.parse(url)
    intent.setDataAndType(uri, "video/mp4")
    context.startActivity(intent)
}

/**
 * 调用系统搜索
 */
fun goSysSearchIntent(context: Context, str: String?) {
    val intent = Intent()
    intent.action = Intent.ACTION_WEB_SEARCH
    intent.putExtra(SearchManager.QUERY, str)
    context.startActivity(intent)
}

/**
 * 包名方式跳转至指定APP
 * @param context
 * @param packageName
 * @return
 */
fun goAppIntentByPackageName(
    context: Context,
    packageName: String
): String? {
    try {
        val it =
            context.packageManager.getLaunchIntentForPackage(packageName!!)
        context.startActivity(it)
    } catch (e: Exception) {
        return e.message
    }
    return null
}

/**
 * 通过包名获取应用程序的名称。
 *
 * @param context     Context对象。
 * @param packageName 包名。
 * @return 返回包名所对应的应用程序的名称。
 */
fun getAppNameByPackageName(
    context: Context,
    packageName: String
): String? {
    val pm = context.packageManager
    var name: String? = null
    try {
        name = pm.getApplicationLabel(
            pm.getApplicationInfo(
                packageName,
                PackageManager.GET_META_DATA
            )
        ).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return name
}

/**
 * Uri方式跳转至指定APP
 * @param context
 * @param s
 * @return
 */
fun goAppIntentByString(context: Context, s: String): String? {
    try {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(s)
            )
        )
    } catch (e: Exception) {
        return e.message
    }
    return null
}

/**
 * 获取系统图片或者拍摄照片
 */
private var file //拍照图片缓存地址
        : File? = null
private var avatarPath //图片文件地址
        : String? = null

//跳转至获取图片或照片
fun goSysBitmapIntent(context: Context, requstCode: Int) {
    //创建打开照相机功能的intent
    val dir =
        File(Environment.getExternalStorageDirectory(), "image_cache")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    file =
        File(dir, System.currentTimeMillis().toString() + ".jpg")
    val uri = Uri.fromFile(file)
    val photoIntent =
        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
    photoIntent.putExtra("type", 1)
    avatarPath =
        file!!.absolutePath

    //创建一个打开图库的intent
    val storeIntent =
        Intent(Intent.ACTION_PICK)
    storeIntent.type = "image/*"
    storeIntent.putExtra("type", 0)
    storeIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")

    //创建intent chooser
    val chooser =
        Intent.createChooser(photoIntent, "请选择")
    chooser.putExtra(
        Intent.EXTRA_INITIAL_INTENTS,
        arrayOf(storeIntent)
    )

    //发送chooser
    (context as Activity).startActivityForResult(chooser, requstCode)
}

//onActivity反馈获取图片Uri
fun onActivityResult_SysBitmapIntent(
    context: Context,
    data: Intent?
): Uri? {
    var uri: Uri? = null
    if (data == null || data.data.toString().contains("file:")) {
        //拍照选择的头像文件
        uri = Uri.parse(avatarPath)
    } else if (data.data.toString().contains("content:")) {
        //是图像在图库中的地址
        uri = data.data
        val cr = context.contentResolver
        val c = cr.query(uri!!, null, null, null, null)
        c!!.moveToNext()
        //该图像在sd卡上地址
        avatarPath =
            c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA))
        var bm: Bitmap? = null
        try {
            bm = MediaStore.Images.Media.getBitmap(cr, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        c.close()
        if (bm != null) uri = Uri.parse(avatarPath)
    }
    val f = File(uri.toString())
    return if (f.exists()) uri else null
}

/**
 * 获取手机内所有应用程序信息
 * @param context
 * @return
 */
fun getAllApplicationInfo(context: Context): List<PackageInfo> {
    val packageManager = context.packageManager
    return packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
}

/**
 * 判断对应包名的程序是否安装
 * @param packageName
 * @return
 */
fun isInstallByread(packageName: String): Boolean {
    return File("/data/data/$packageName").exists()
}
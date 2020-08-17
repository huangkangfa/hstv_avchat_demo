package com.android.baselib.global

import android.content.Context
import android.os.Environment
import com.android.baselib.global.AppGlobal.context
import com.android.baselib.utils.deleteDir
import com.android.baselib.utils.isExternalStorageWriteable
import java.io.File
import java.math.BigDecimal

object CacheGlobal {

    private const val CACHE_GLIDE_FILE_NAME = "cache_glide"

    private const val CACHE_LOG_FILE_NAME = "cache_log"

    fun initDir() {
        getLogCacheDir()
        getGlideCacheDir()
    }

    fun getLogCacheDir(): String {
        return getDiskCacheDir(child = CACHE_LOG_FILE_NAME).absolutePath
    }

    fun getGlideCacheDir(): String {
        return getDiskCacheDir(child = CACHE_GLIDE_FILE_NAME).absolutePath
    }


    private fun getBaseDiskCacheDir(): File? {
        return if (isExternalStorageWriteable()) {
            context.externalCacheDir
        } else {
            context.cacheDir
        }
    }

    private fun getDiskCacheDir(
        parent: String? = getBaseDiskCacheDir()
            .toString(),
        child: String
    ): File {
        val file = File(parent, child)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absoluteFile
    }

    /**
     * 获取缓存大小
     * @param context
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getTotalCacheSize(context: Context): String? {
        val fs: Array<File>? = null
        return getTotalCacheSize(context, *fs!!)
    }

    @Throws(Exception::class)
    fun getTotalCacheSize(
        context: Context,
        vararg dir: File?
    ): String? {
        var cacheSize = getFolderSize(context.cacheDir)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            cacheSize += getFolderSize(context.externalCacheDir)
        }
        if (dir.isNotEmpty()) {
            for (f in dir) {
                cacheSize += getFolderSize(f)
            }
        }
        return getFormatSize(cacheSize.toDouble())
    }

    /**
     * 清除缓存
     * @param context
     */
    fun clearAllCache(context: Context) {
        val fs: Array<File>? = null
        clearAllCache(context, *fs!!)
    }

    fun clearAllCache(context: Context, vararg dir: File?) {
        deleteDir(context.cacheDir)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            deleteDir(context.externalCacheDir)
        }
        if (dir.isNotEmpty()) {
            for (f in dir) {
                deleteDir(f)
            }
        }
    }

    // 获取文件大小
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    @Throws(Exception::class)
    private fun getFolderSize(file: File?): Long {
        var size: Long = 0
        try {
            val fileList = file!!.listFiles()
            if (fileList != null)
                for (i in fileList.indices) {
                    // 如果下面还有文件
                    size = if (fileList[i].isDirectory) {
                        size + getFolderSize(fileList[i])
                    } else {
                        size + fileList[i].length()
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * 格式化单位
     * @param size
     * @return
     */
    private fun getFormatSize(size: Double): String? {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return "0K"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 =
                BigDecimal(kiloByte.toString())
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "K"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 =
                BigDecimal(megaByte.toString())
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "M"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 =
                BigDecimal(gigaByte.toString())
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return (result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB")
    }
}
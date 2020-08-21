import com.android.baselib.global.AppGlobal.context
import com.android.baselib.global.CacheGlobal
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.ClassicFlattener
import com.elvishew.xlog.formatter.message.json.DefaultJsonFormatter
import com.elvishew.xlog.formatter.message.throwable.DefaultThrowableFormatter
import com.elvishew.xlog.formatter.message.xml.DefaultXmlFormatter
import com.elvishew.xlog.formatter.stacktrace.DefaultStackTraceFormatter
import com.elvishew.xlog.formatter.thread.DefaultThreadFormatter
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.ConsolePrinter
import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.unit.Subunits

/**
 * 日志打印初始化
 */
fun initXLog(){
    val config = LogConfiguration.Builder()
        .logLevel(LogLevel.ALL) // 指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
        .t() // 允许打印线程信息，默认禁止
        .jsonFormatter(DefaultJsonFormatter()) // 指定 JSON 格式化器，默认为 DefaultJsonFormatter
        .xmlFormatter(DefaultXmlFormatter()) // 指定 XML 格式化器，默认为 DefaultXmlFormatter
        .throwableFormatter(DefaultThrowableFormatter()) // 指定可抛出异常格式化器，默认为 DefaultThrowableFormatter
        .threadFormatter(DefaultThreadFormatter()) // 指定线程信息格式化器，默认为 DefaultThreadFormatter
        .stackTraceFormatter(DefaultStackTraceFormatter()) // 指定调用栈信息格式化器，默认为 DefaultStackTraceFormatter
        .build()

    val androidPrinter: Printer =
        AndroidPrinter() // 通过 com.elvishew.xlog.XLog.Log 打印日志的打印器

    val filePrinter: Printer =
        FilePrinter.Builder(CacheGlobal.getLogCacheDir()) // 指定保存日志文件的路径
            .fileNameGenerator(DateFileNameGenerator()) // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
            .backupStrategy(FileSizeBackupStrategy(1024 * 1024 * 20)) // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
            .cleanStrategy(FileLastModifiedCleanStrategy(1024 * 1024 * 20)) // 指定日志文件清除策略，默认为 NeverCleanStrategy()
            .flattener(ClassicFlattener()) // 指定日志平铺器，默认为 DefaultFlattener
            .build()
    XLog.init(config,androidPrinter,filePrinter)
}

/**
 * 屏幕适配初始化
 */
fun initAutoSize(){
    AutoSizeConfig.getInstance()
        .setBaseOnWidth(true)
        .unitsManager
        .setSupportDP(false)
        .setSupportSP(false)
        .supportSubunits = Subunits.MM
}
















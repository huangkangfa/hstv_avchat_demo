package com.android.hsdemo.http

import com.android.baselib.utils.Preferences
import com.android.baselib.utils.getVersionName
import com.android.hsdemo.BuildConfig
import okhttp3.OkHttpClient
import rxhttp.wrapper.annotation.DefaultDomain
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.ssl.SSLSocketFactoryImpl
import rxhttp.wrapper.ssl.X509TrustManagerImpl
import java.util.concurrent.TimeUnit

object NetConfig {
    /**
     * 定义域名
     */
    @DefaultDomain()
    const val baseUrl = BuildConfig.BASE_URL

    fun init() {
        RxHttp.init(getDefaultOkHttpClient(), BuildConfig.DEBUG)
        initHttpDefaultHeads()
    }
}

/**
 * 获取默认HttopClient
 */
fun getDefaultOkHttpClient(): OkHttpClient {
    val trustAllCert = X509TrustManagerImpl()
    val sslSocketFactory = SSLSocketFactoryImpl(trustAllCert)
    return OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .sslSocketFactory(sslSocketFactory, trustAllCert) //添加信任证书
        .build()
}

/**
 * 默认headers配置
 */
fun initHttpDefaultHeads() {
    val map = HashMap<String, String>()
    map["curtime"] = System.currentTimeMillis().toString()
    map["platformType"] = "1"
    map["osType"] = "1"
    map["version"] = getVersionName()
    map["token"] = Preferences.getString("token", "")
    RxHttpJavaHelper.initPublicHeaders(map)
}

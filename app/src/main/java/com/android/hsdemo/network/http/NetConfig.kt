package com.android.hsdemo.network.http

import com.android.hsdemo.KEY_TOKEN
import com.android.baselib.utils.Preferences
import com.android.hsdemo.BuildConfig
import okhttp3.OkHttpClient
import rxhttp.RxHttp
import rxhttp.wrapper.ssl.SSLSocketFactoryImpl
import rxhttp.wrapper.ssl.X509TrustManagerImpl
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager


object NetConfig {

    fun init() {
        RxHttp.init(getDefaultOkHttpClient(), BuildConfig.DEBUG)
        initHttpDefaultHeads()
    }

}

/**
 * 获取默认HttopClient
 */
fun getDefaultOkHttpClient(): OkHttpClient {
    val trustAllCert: X509TrustManager = X509TrustManagerImpl()
    val sslSocketFactory: SSLSocketFactory = SSLSocketFactoryImpl(trustAllCert)
    return OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .sslSocketFactory(sslSocketFactory, trustAllCert) //添加信任证书
        .build()
}

/**
 * 默认headers配置
 */
fun initHttpDefaultHeads() {
    RxHttp.setOnParamAssembly { p -> //此方法在子线程中执行，即请求发起线程
        //添加公共请求头
        p.addHeader("platformType", "1")
        p.addHeader("token", Preferences.getString(KEY_TOKEN, ""))
        p
    }
}

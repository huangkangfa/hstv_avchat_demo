/**
 * app的相关信息
 */
object App {

    // App名称
    const val APP_NAME = "hsdemo"

    // app application id
    const val APPLICATION_ID = "com.android.hsdemo"

    // 版本名称
    const val APP_VERSION_NAME = "1.0.0"

    // 版本号
    const val APP_VERSION_CODE = 1

    const val COMPILESDKVERSION = 30
    const val BUILDTOOLSVERSION = "29.0.3"
    const val MINSDKVERSION = 19
    const val TARGETSDKVERSION = 30
    const val ANDROID_JUNIT_RUNNER = "androidx.test.runner.AndroidJUnitRunner"
}

/**
 * debug配置信息
 */
object DebugConfig {

    //签名信息
    const val SIGN_STORE_FILE = "hs.jks"
    const val SIGN_STORE_PASSWORD = "hs123456"
    const val SIGN_KEY_ALIAS = "hs123456"
    const val SIGN_KEY_PASSWORD = "hs123456"

    //基本域名
    const val BASE_URL = "http://test.asus-tbox-api.eyuntx.com/"

    //渠道名称
    const val PRODUCT_CHANNEL = "hkf"

    //腾讯云音视频sdkAppId
    const val TENCENT_APP_ID = 1400414829

    //腾讯云音视频secretkey
    const val TENCENT_LIVE_SECRETKEY = "MTcb6RyKUazDDxSFpJbpujJe42pXAPZV"
}

/**
 * release配置信息
 */
object ReleaseConfig {

    //签名信息
    const val SIGN_STORE_FILE = "hs.jks"
    const val SIGN_STORE_PASSWORD = "hs123456"
    const val SIGN_KEY_ALIAS = "hs123456"
    const val SIGN_KEY_PASSWORD = "hs123456"

    //基本域名
    const val BASE_URL = "http://test.asus-tbox-api.eyuntx.com/"

    //渠道名称
    const val PRODUCT_CHANNEL = "coffee"

    //腾讯云音视频sdkAppId
    const val TENCENT_APP_ID = 1400414829

    //腾讯云音视频secretkey
    const val TENCENT_LIVE_SECRETKEY = "MTcb6RyKUazDDxSFpJbpujJe42pXAPZV"

}

/**
 * 依赖版本
 */
object Versions {
    const val KOTLIN = "1.3.72"
    const val GRADLE = "4.0.1"

    const val ANDROIDX_TEST_EXT = "1.1.1"
    const val ANDROIDX_TEST = "1.2.0"
    const val APPCOMPAT = "1.1.0"
    const val RECYCLERVIEW = "1.1.0"
    const val CORE_KTX = "1.3.0"
    const val ESPRESSO_CORE = "3.2.0"
    const val JUNIT = "4.13"

    const val OKHTTP = "4.8.1"
    const val RXHTTP = "2.3.5"

    const val RXJAVA = "3.0.2"
    const val RXANDROID = "3.0.0"
    const val RXANDROIDLIFE = "3.0.0"
    const val RXBINDING = "3.1.0"
    const val RXLIFE_COROUTINE = "2.0.0"
    const val RXLIFE = "2.0.0"
    const val RXPERMISSIONS = "0.10.2"

    const val AUTODISPOSE = "1.4.0"

    const val TIMBER = "4.7.1"

    const val NAVIGATION = "2.3.0"

    const val MATERIAL = "1.2.0-alpha05"

    const val GLIDE = "4.11.0"
    const val GLIDE_TRANSFORMATIONS = "4.1.0"

    const val BINDING_COLLECTION_ADAPTER = "4.0.0"

    const val LIFECYCLE = "2.3.0-alpha04"
    const val CONSTRAINTLAYOUT = "2.0.0-beta4"

    const val GSON = "2.8.6"

    const val AUTOSIZE = "1.2.1"

    const val MULTIDEX = "2.0.1"

    const val XLOG = "1.6.1"

    const val TENCENT_LITEAV = "latest.release"

    const val TENCENT_IM = "4.9.1"

}

/**
 * 插件版本
 */
object Build {
    const val GRADLE = "com.android.tools.build:gradle:${Versions.GRADLE}"
    const val KOTLIN_GRADLE_PLUGIN =
        "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}"
}

/**
 * Android基础库
 */
object SupportLibs {
    const val KOTLIN_STDLIB = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN}"
    const val ANDROIDX_APPCOMPAT = "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"
    const val ANDROIDX_CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"

    const val ANDROIDX_RECYCLERVIEW = "androidx.recyclerview:recyclerview:${Versions.RECYCLERVIEW}"
    const val ANDROIDX_CONSTRAINTLAYOUT =
        "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINTLAYOUT}"
    const val ANDROIDX_LIFECYCLE = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE}"
}

/**
 * Android测试库
 */
object AndroidTestingLib {
    const val ANDROIDX_TEST_RULES = "androidx.test:rules:${Versions.ANDROIDX_TEST}"
    const val ANDROIDX_TEST_RUNNER = "androidx.test:runner:${Versions.ANDROIDX_TEST}"
    const val JUNIT = "junit:junit:${Versions.JUNIT}"
    const val ANDROIDX_TEST_EXT_JUNIT = "androidx.test.ext:junit:${Versions.ANDROIDX_TEST_EXT}"
    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"
}

/**
 * 第三方其他库
 */
object Libs {

    //navigation
    const val NAVIGATION_FRAGMENT_KTX =
        "androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION}"
    const val NAVIGATION_UI_KTX = "androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION}"

    //glide
    const val GLIDE_OKHTTP = "com.github.bumptech.glide:okhttp3-integration:${Versions.GLIDE}"
    const val GLIDE = "com.github.bumptech.glide:glide:${Versions.GLIDE}"
    const val GLIDE_TRANSFORMATIONS =
        "jp.wasabeef:glide-transformations:${Versions.GLIDE_TRANSFORMATIONS}"

    //material
    const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"

    //okhttp
    const val OKHTTP = "com.squareup.okhttp3:okhttp:${Versions.OKHTTP}"

    //rx
    const val RXHTTP = "com.ljx.rxhttp:rxhttp:${Versions.RXHTTP}"
    const val RXLIFE_COROUTINE =
        "com.ljx.rxlife:rxlife-coroutine:${Versions.RXLIFE_COROUTINE}" //管理协程生命周期，页面销毁，关闭请求
    const val RXLIFE = "com.ljx.rxlife3:rxlife-rxjava:${Versions.RXLIFE}"
    const val RXJAVA = "io.reactivex.rxjava3:rxjava:${Versions.RXJAVA}"
    const val RXANDROID = "io.reactivex.rxjava3:rxandroid:${Versions.RXANDROID}"
    const val RXANDROIDLIFE = "com.ljx.rxlife3:rxlife-rxjava:${Versions.RXANDROIDLIFE}"
    const val RXBINDING_CORE = "com.jakewharton.rxbinding3:rxbinding-core:${Versions.RXBINDING}"
    const val RXPERMISSIONS = "com.github.tbruyelle:rxpermissions:${Versions.RXPERMISSIONS}"

    //autodispose
    const val AUTODISPOSE = "com.uber.autodispose:autodispose:${Versions.AUTODISPOSE}"
    const val AUTODISPOSEANDROID =
        "com.uber.autodispose:autodispose-android:${Versions.AUTODISPOSE}"
    const val AUTODISPOSEARCHCOMPONENTS =
        "com.uber.autodispose:autodispose-android-archcomponents:${Versions.AUTODISPOSE}"

    //adapter
    const val BINDING_COLLECTION_ADAPTER =
        "me.tatarka.bindingcollectionadapter2:bindingcollectionadapter:${Versions.BINDING_COLLECTION_ADAPTER}"
    const val BINDING_COLLECTION_ADAPTER_RECYCLERVIEW =
        "me.tatarka.bindingcollectionadapter2:bindingcollectionadapter-recyclerview:${Versions.BINDING_COLLECTION_ADAPTER}"
    const val BINDING_COLLECTION_ADAPTER_VIEWPAGER2 =
        "me.tatarka.bindingcollectionadapter2:bindingcollectionadapter-viewpager2:${Versions.BINDING_COLLECTION_ADAPTER}"

    const val TIMBER = "com.jakewharton.timber:timber:${Versions.TIMBER}"

    //autosize
    const val AUTOSZIE = "me.jessyan:autosize:${Versions.AUTOSIZE}"

    //multidex
    const val MULTIDEX = "androidx.multidex:multidex:${Versions.MULTIDEX}"

    //gson
    const val GSON = "com.google.code.gson:gson:${Versions.GSON}"

    //日志
    const val XLOG = "com.elvishew:xlog:${Versions.XLOG}"

    //腾讯云视频
    const val TENCENT_LITEAV = "com.tencent.liteav:LiteAVSDK_TRTC:${Versions.TENCENT_LITEAV}"

    //腾讯IM
    const val TENCENT_IM = "com.tencent.imsdk:imsdk:${Versions.TENCENT_IM}"
}

/**
 * 注解库
 */
object Kapts {

    //glide
    const val GLIDE_COMPILER = "com.github.bumptech.glide:compiler:${Versions.GLIDE}"

    //rx
    const val RXHTTP_COMPILER = "com.ljx.rxhttp:rxhttp-compiler:${Versions.RXHTTP}"

}
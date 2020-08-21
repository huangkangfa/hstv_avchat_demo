package com.android.hsdemo.network;

import com.android.hsdemo.BuildConfig;

import rxhttp.wrapper.annotation.DefaultDomain;

public class Url {
    @DefaultDomain //设置为默认域名
    public static String baseUrl = BuildConfig.BASE_URL;
}

package com.android.hsdemo.http;

import java.util.HashMap;

import io.reactivex.functions.Function;
import rxhttp.wrapper.param.Param;
import rxhttp.wrapper.param.RxHttp;

class RxHttpJavaHelper {
    /**
     * 设置公共头部
     */
    public static void initPublicHeaders(HashMap<String, String> map) {
        RxHttp.setOnParamAssembly(new Function<Param, Param>() {
            @Override
            public Param apply(Param p) { //此方法在子线程中执行，即请求发起线程
                //添加公共请求头
                for (String key : map.keySet()) {
                    p.addHeader(key, map.get(key));
                }
                return p;
            }
        });
    }

}

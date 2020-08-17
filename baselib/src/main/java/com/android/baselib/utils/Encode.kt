package com.android.baselib.utils

import android.os.Build
import android.text.Html
import android.util.Base64
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/8/7
 * desc  : 编码解码相关工具类
</pre> *
 */
open class Encode{
    companion object {
        /**
         * URL编码
         *
         * 若系统不支持指定的编码字符集,则直接将input原样返回
         *
         * @param input   要编码的字符
         * @param charset 字符集
         * @return 编码为字符集的字符串
         */
        /**
         * URL编码
         *
         * 若想自己指定字符集,可以使用[.urlEncode]方法
         *
         * @param input 要编码的字符
         * @return 编码为UTF-8的字符串
         */
        @JvmOverloads
        fun urlEncode(input: String, charset: String? = "UTF-8"): String {
            return try {
                URLEncoder.encode(input, charset)
            } catch (e: UnsupportedEncodingException) {
                input
            }
        }
        /**
         * URL解码
         *
         * 若系统不支持指定的解码字符集,则直接将input原样返回
         *
         * @param input   要解码的字符串
         * @param charset 字符集
         * @return URL解码为指定字符集的字符串
         */
        /**
         * URL解码
         *
         * 若想自己指定字符集,可以使用 [.urlDecode]方法
         *
         * @param input 要解码的字符串
         * @return URL解码后的字符串
         */
        @JvmOverloads
        fun urlDecode(input: String, charset: String? = "UTF-8"): String {
            return try {
                URLDecoder.decode(input, charset)
            } catch (e: UnsupportedEncodingException) {
                input
            }
        }

        /**
         * Base64编码
         *
         * @param input 要编码的字符串
         * @return Base64编码后的字符串
         */
        fun base64Encode(input: String): ByteArray {
            return base64Encode(input.toByteArray())
        }

        /**
         * Base64编码
         *
         * @param input 要编码的字节数组
         * @return Base64编码后的字符串
         */
        @JvmStatic
        fun base64Encode(input: ByteArray?): ByteArray {
            return Base64.encode(input, Base64.NO_WRAP)
        }

        /**
         * Base64编码
         *
         * @param input 要编码的字节数组
         * @return Base64编码后的字符串
         */
        fun base64Encode2String(input: ByteArray?): String {
            return Base64.encodeToString(input, Base64.NO_WRAP)
        }

        /**
         * Base64解码
         *
         * @param input 要解码的字符串
         * @return Base64解码后的字符串
         */
        fun base64Decode(input: String?): ByteArray {
            return Base64.decode(input, Base64.NO_WRAP)
        }

        /**
         * Base64解码
         *
         * @param input 要解码的字符串
         * @return Base64解码后的字符串
         */
        @JvmStatic
        fun base64Decode(input: ByteArray?): ByteArray {
            return Base64.decode(input, Base64.NO_WRAP)
        }

        /**
         * Base64URL安全编码
         *
         * 将Base64中的URL非法字符�?,/=转为其他字符, 见RFC3548
         *
         * @param input 要Base64URL安全编码的字符串
         * @return Base64URL安全编码后的字符串
         */
        fun base64UrlSafeEncode(input: String): ByteArray {
            return Base64.encode(input.toByteArray(), Base64.URL_SAFE)
        }

        /**
         * Html编码
         *
         * @param input 要Html编码的字符串
         * @return Html编码后的字符串
         */
        fun htmlEncode(input: CharSequence): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Html.escapeHtml(input)
            } else {
                // 参照Html.escapeHtml()中代码
                val out = StringBuilder()
                var i = 0
                val len = input.length
                while (i < len) {
                    val c = input[i]
                    if (c == '<') {
                        out.append("&lt;")
                    } else if (c == '>') {
                        out.append("&gt;")
                    } else if (c == '&') {
                        out.append("&amp;")
                    } else if (c.toInt() in 0xD800..0xDFFF) {
                        if (c.toInt() < 0xDC00 && i + 1 < len) {
                            val d = input[i + 1]
                            if (d.toInt() in 0xDC00..0xDFFF) {
                                i++
                                val codepoint =
                                    0x010000 or (c.toInt() - 0xD800 shl 10) or d.toInt() - 0xDC00
                                out.append("&#").append(codepoint).append(";")
                            }
                        }
                    } else if (c.toInt() > 0x7E || c < ' ') {
                        out.append("&#").append(c.toInt()).append(";")
                    } else if (c == ' ') {
                        while (i + 1 < len && input[i + 1] == ' ') {
                            out.append("&nbsp;")
                            i++
                        }
                        out.append(' ')
                    } else {
                        out.append(c)
                    }
                    i++
                }
                out.toString()
            }
        }

        /**
         * Html解码
         *
         * @param input 待解码的字符串
         * @return Html解码后的字符串
         */
        fun htmlDecode(input: String?): CharSequence {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(input)
            }
        }
    }

}
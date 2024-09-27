package com.android.launcher3.common.network.interceptor

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import okhttp3.FormBody
import okhttp3.Request
import java.net.URLEncoder
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HttpSignUtils {

    val TAG = HttpSignUtils::class.java.name

    val sn = "sn"

    val x_gal_sign = "x-baehug-sign"

    val sysVersion = "sysVersion"

    val baehug = "baehug-auth-v1"

    val token = "baehug-token"

    /**
     * 测试环境
     */
    private val accessKeyId = "8e4dd0d8e4b9990745c3518a2df6e3a0"

    private val secretAccessKey = "9afa385316c48118ec90d5418da5a090"

    /**
     * 正式环境
     */
    private val accessKeyId_Pro = "2a716810632b4a79b74baacac1d5239b"

    private val secretAccessKey_Pro = "5b9e01d38ffd49e7af3161263d936e37"



    fun getSysVersionSdk(): Int {
        return Build.VERSION.SDK_INT
    }


    fun getSysSn(): String {
        return "unauthorized"
    }


    fun getGalSign(path: String, map: Map<String, Any>,request: Request): String {
        val timestamp = Date().time.toString()
        val signature = generateSignature(path, map, timestamp,request)
        return "$baehug/$accessKeyId/$timestamp/$signature"
    }


    fun getAppVersionCode(context: Context): Int {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1
        }
    }


    @Throws(Exception::class)
    private fun hmacSHA256(secret: String?, message: String): String {
        var hash = ""
        val hmacSha256 = Mac.getInstance("HmacSHA256")
        val secret_key = SecretKeySpec(secret!!.toByteArray(), "HmacSHA256")
        hmacSha256.init(secret_key)
        val bytes = hmacSha256.doFinal(message.toByteArray())
        hash = byteArrayToHexString(bytes)
        return hash
    }

    private fun byteArrayToHexString(b: ByteArray?): String {
        val hs = StringBuilder()
        var stmp: String
        var n = 0
        while (b != null && n < b.size) {
            stmp = Integer.toHexString(b[n].toInt() and 0XFF)
            if (stmp.length == 1) hs.append('0')
            hs.append(stmp)
            n++
        }
        return hs.toString().lowercase(Locale.getDefault())
    }

    private fun generateSignature(
        path: String,
        mapContent: Map<String, Any>,
        timestamp: String,
        request: Request
    ): String {
        var signingKey: String? = null
        return try {
            signingKey = generateSigningKey(timestamp)
            val treeMap = TreeMap<String?, Any>()
            treeMap.putAll(mapContent!!)
            val content = getParmarContent(mapContent,request)
            var str = ""
            str = if (TextUtils.isEmpty(content)) {
                "$baehug/$accessKeyId/$timestamp$path/"
            } else {
                "$baehug/$accessKeyId/$timestamp$path/$content"
            }
            val signature = hmacSHA256(signingKey, str)
            Log.d(TAG, "signature: $signingKey")
            Log.d(TAG, "path: $path")
            signature
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Throws(Exception::class)
    private fun generateSigningKey(time: String): String {
        val str = "baehug-auth-v1/$accessKeyId/$time"
        val signingKey = hmacSHA256(secretAccessKey, str)
        Log.d(TAG, "generateSigningKey: $str")
        Log.d(TAG, "generateSigningKey2: $signingKey")
        return signingKey
    }

    private fun getParmarContent(mapContent: Map<String, Any>, request: Request): String {
        val body = request.body()
        val sortedParamMap: SortedMap<String?, Any> = TreeMap(mapContent)
        val sb = StringBuilder()
        val iterator: Iterator<Map.Entry<String?, Any>> = sortedParamMap.entries.iterator()
        while (iterator.hasNext()) {
            val (key, value) = iterator.next()
            sb.append(URLEncoder.encode(key))
            sb.append("=")
            val encodeValues = if (body == null || body is FormBody ){
                URLEncoder.encode(value.toString())
            }else{
                if (value is String){
                    "%22${URLEncoder.encode(value.toString())}%22"
                }else{
                    URLEncoder.encode(value.toString())
                }
            }
            val replace = encodeValues.replace("+", "%20")
            sb.append(replace)
            if (iterator.hasNext()) {
                sb.append("&")
            }
        }
        return sb.toString()
    }
}
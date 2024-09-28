package com.android.launcher3.common.network.interceptor

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.parser.Feature
import okhttp3.*
import okio.Buffer
import java.io.IOException

open class HeaderInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val map = if (request.body() == null) {
            getQueryMap(request.url())
        } else {
            if (request.body() is FormBody){
                getFormBody(request.body() as FormBody)
            }else{
                getBodyMap(request)
            }
        }
        var path = request.url().encodedPath()
        val paths = path.split("v1".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        if (paths != null && paths.size == 2) {
            path = paths[1]
        }
        val sign: String = HttpSignUtils.getGalSign(path, map.toMap(),request)
        val requestBuilder = request.newBuilder()
            .addHeader(HttpSignUtils.x_gal_sign, sign)
            .addHeader(HttpSignUtils.sn, HttpSignUtils.getSysSn())
        request = requestBuilder.build()
        return chain.proceed(request)
    }

   
    private fun getQueryMap(httpUrl: HttpUrl): LinkedHashMap<String, Any> {
        val queryMap = LinkedHashMap<String, Any>(httpUrl.querySize())
        for (i in 0 until httpUrl.querySize()) {
            val name = httpUrl.queryParameterName(i)
            val value = httpUrl.queryParameterValue(i)
            queryMap[name] = value
        }
        return queryMap
    }


    private fun getFormBody(body: FormBody): LinkedHashMap<String, Any> {
        val queryMap = LinkedHashMap<String, Any>(body.size())
        for (i in 0 until body.size()) {
            val name = body.name(i)
            val value = body.value(i)
            queryMap[name] = value
        }
        return queryMap
    }


    @Throws(IOException::class)
    protected fun getBodyMap(request: Request): LinkedHashMap<String, Any> {
        if (request.body() != null) {
            val buffer = Buffer()
            request.body()!!.writeTo(buffer)
            var requestBody = buffer.readUtf8()
            if (request.url().toString().contains("pp/location/request/user")){
                requestBody = requestBody.replace("\\","\\\\")
            }
            return JSON.parseObject(requestBody, LinkedHashMap::class.java, Feature.OrderedField) as LinkedHashMap<String, Any>
        }
        return LinkedHashMap()
    }
}
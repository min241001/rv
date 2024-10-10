package com.renny.contractgridview.base

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object GsonUtil {

    private val gson: Gson by lazy { Gson() }

    fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }

    fun <T> fromJson(json: String, clazz: Class<T>): T? {
        return try {
            gson.fromJson(json, clazz)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun <T> fromJson(json: String, type: Type): T? {
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun <T> fromJsonList(json: String, clazz: Class<T>): List<T>? {
        return fromJson(json, TypeToken.getParameterized(List::class.java, clazz).type)
    }

    fun <T> fromJsonList(json: String, type: Type): List<T>? {
        return fromJson(json, TypeToken.getParameterized(List::class.java, type).type)
    }
}

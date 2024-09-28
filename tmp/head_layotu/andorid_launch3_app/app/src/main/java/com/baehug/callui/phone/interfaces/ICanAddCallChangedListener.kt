package com.baehug.callui.phone.interfaces

/**
 * Author : yanyong
 * Date : 2024/8/17
 * Details : 取消或添加电话监听
 */
interface ICanAddCallChangedListener {

    fun onCanAddCallChanged(canAddCall: Boolean)

}
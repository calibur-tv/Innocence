package com.riuir.calibur.data

/**
 *************************************
 * 作者：韩宝坤
 * 日期：2017/11/18
 * 邮箱：hanbaokun@outlook.com
 * 描述：
 *************************************
 */
data class ResponseWrapper<T>(var code: Int, var data: T, var message: String)
package com.riuir.calibur.utils

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule

/**
 *************************************
 * 作者：韩宝坤
 * 日期：2017/11/18
 * 邮箱：hanbaokun@outlook.com
 * 描述：
 *************************************
 */
@GlideModule
internal class CustomGlideModule : AppGlideModule() {
    /**
     * 设置内存缓存大小10M
     */
    val cacheSize = 10 * 1024 * 1024

    override fun applyOptions(context: android.content.Context?, builder: com.bumptech.glide.GlideBuilder) {
        builder.setMemoryCache(LruResourceCache(cacheSize))
    }

    /**
     * 关闭解析AndroidManifest
     */
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}
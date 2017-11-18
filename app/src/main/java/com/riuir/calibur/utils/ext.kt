package com.riuir.calibur.utils

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

/**
 *************************************
 * 作者：韩宝坤
 * 日期：2017/11/18
 * 邮箱：hanbaokun@outlook.com
 * 描述：
 *************************************
 */

fun Context.toast(msg:String, length:Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, msg, length).show()
}

fun loadImage(imageView: ImageView, url: String?) =
        Glide.with(imageView.context)
                .load(url).transition(DrawableTransitionOptions().crossFade(500))
                .into(imageView)

fun loadAsset(imageView: ImageView, id: Int) =
        Glide.with(imageView.context)
                .load(id)
                .into(imageView)
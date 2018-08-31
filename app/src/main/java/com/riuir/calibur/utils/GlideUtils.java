package com.riuir.calibur.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ScreenUtils;
import com.riuir.calibur.utils.glide.GlideCenterCropRoundTransform;

import java.io.File;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.riuir.calibur.utils.GlideOptions.bitmapTransform;

/**
 * ************************************
 * 作者：韩宝坤
 * 日期：2017/12/24
 * 邮箱：hanbaokun@outlook.com
 * 描述：
 * ************************************
 */
public class GlideUtils {


    //默认加载
    public static void loadImageView(Context mContext, String path, ImageView mImageView) {
        Glide.with(mContext).load(path).into(mImageView);
    }

    //默认加载
    public static void loadImageViewFromFile(Context mContext, File file, ImageView mImageView) {
        Glide.with(mContext).load(file).into(mImageView);
    }

    //模糊加载
    public static void loadImageViewBlur(Context mContext, String path, ImageView mImageView) {
        Glide.with(mContext).load(path).apply(bitmapTransform(new BlurTransformation(15))).into(mImageView);
    }

    //圆形加载
    public static void loadImageViewCircle(Context mContext, String path, ImageView mImageView) {
        Glide.with(mContext).load(path).apply(bitmapTransform(new CircleCrop())).into(mImageView);
    }

    //圆角加载
    public static void loadImageViewRoundedCorners(Context mContext, String path, ImageView mImageView,int rounds) {
        Glide.with(mContext).load(path).apply(bitmapTransform(new RoundedCornersTransformation(DensityUtils.dp2px(mContext,rounds), 0, RoundedCornersTransformation.CornerType.ALL)))
                .into(mImageView);
    }
    //圆角 centerCrop加载
    public static void loadImageViewCenterCropRoundedCorners(Context mContext, String path, ImageView mImageView,int rounds){
        RequestOptions myOptions = new RequestOptions()
                .transform(new GlideCenterCropRoundTransform(mContext,rounds));
        Glide.with(mContext)
                .load(path)
                .apply(myOptions)
                .into(mImageView);
    }

    //preview加载
    public static void loadImageViewpreview(Context mContext, String path, final PhotoView photoView) {
        GlideApp.with(mContext).load(path).into(new SimpleTarget() {

            @Override
            public void onResourceReady(@NonNull Object resource, @Nullable Transition transition) {
                //photoview重写了设置图片的方法 ，所以不能像普通的imageview去对待
                photoView.setImageDrawable((Drawable) resource);
            }
        });
    }


    //加载指定大小
    public static void loadImageViewSize(Context mContext, String path, int width, int height, ImageView mImageView) {
        GlideApp.with(mContext).load(path).override(width, height).into(mImageView);
    }

    //设置加载中以及加载失败图片
    public static void loadImageViewLoding(Context mContext, String path, ImageView mImageView, int lodingImage, int errorImageView) {
        GlideApp.with(mContext).load(path).placeholder(lodingImage).error(errorImageView).into(mImageView);
    }

    //设置加载中以及加载失败图片并且指定大小
    public static void loadImageViewLodingSize(Context mContext, String path, int width, int height, ImageView mImageView, int lodingImage, int errorImageView) {
        GlideApp.with(mContext).load(path).override(width, height).placeholder(lodingImage).error(errorImageView).into(mImageView);
    }

    //设置跳过内存缓存
    public static void loadImageViewCache(Context mContext, String path, ImageView mImageView) {
        GlideApp.with(mContext).load(path).skipMemoryCache(true).into(mImageView);
    }

    //设置下载优先级
    public static void loadImageViewPriority(Context mContext, String path, ImageView mImageView) {
        GlideApp.with(mContext).load(path).priority(Priority.NORMAL).into(mImageView);
    }

    /**
     * 策略解说：
     * <p>
     * all:缓存源资源和转换后的资源
     * <p>
     * none:不作任何磁盘缓存
     * <p>
     * source:缓存源资源
     * <p>
     * result：缓存转换后的资源
     */

    //设置缓存策略
    public static void loadImageViewDiskCache(Context mContext, String path, ImageView mImageView) {
        GlideApp.with(mContext).load(path).diskCacheStrategy(DiskCacheStrategy.ALL).into(mImageView);
    }

    /**
     * api也提供了几个常用的动画：比如crossFade()
     * animate方法失效了
     */

    //设置加载动画
    public static void loadImageViewAnim(Context mContext, String path, int anim, ImageView mImageView) {
        Glide.with(mContext).load(path).transition(new DrawableTransitionOptions().crossFade()).into(mImageView);
    }

    /**
     * 会先加载缩略图
     */

    //设置缩略图支持
    public static void loadImageViewThumbnail(Context mContext, String path, ImageView mImageView) {
        Glide.with(mContext).load(path).thumbnail(0.1f).into(mImageView);
    }

    /**
     * api提供了比如：centerCrop()、fitCenter()等
     */

    //设置动态转换
    public static void loadImageViewCrop(Context mContext, String path, ImageView mImageView) {
        GlideApp.with(mContext).load(path).centerCrop().into(mImageView);
    }

    //设置动态GIF加载方式
    public static void loadImageViewDynamicGif(Context mContext, String path, ImageView mImageView) {
        GlideApp.with(mContext).asGif().load(path).into(mImageView);

    }

    //设置静态GIF加载方式
    public static void loadImageViewStaticGif(Context mContext, String path, ImageView mImageView) {
        GlideApp.with(mContext).asBitmap().load(path).into(mImageView);
    }

    //设置监听的用处 可以用于监控请求发生错误来源，以及图片来源 是内存还是磁盘

    //设置监听请求接口
//    public static void loadImageViewListener(Context mContext, String path, ImageView mImageView, RequestListener<String, GlideDrawable> requstlistener) {
//        Glide.with(mContext).load(path).listener(requstlistener).into(mImageView);
//    }

    //项目中有很多需要先下载图片然后再做一些合成的功能，比如项目中出现的图文混排

    //设置要加载的内容
//    public static void loadImageViewContent(Context mContext, String path, SimpleTarget<GlideDrawable> simpleTarget) {
//        Glide.with(mContext).load(path).centerCrop().into(simpleTarget);
//    }

    //清理磁盘缓存
    public static void GuideClearDiskCache(Context mContext) {
        //理磁盘缓存 需要在子线程中执行
        Glide.get(mContext).clearDiskCache();
    }

    //清理内存缓存
    public static void GuideClearMemory(Context mContext) {
        //清理内存缓存  可以在UI主线程中进行
        Glide.get(mContext).clearMemory();
    }


    public static final int FULL_SCREEN = 0;
    public static final int THIRD_SCREEN = 1;
    public static final int HALF_SCREEN = 2;
    public static final int THIRD_SCREEN_CENTER_CROP = 3;

    //设置图片url地址 并且添加七牛云的图片处理
    public static String setImageUrl(Context context,String url,int style){
        int screenWidth = ScreenUtils.getScreenWidth(context);

        if (url.contains(Constants.API_IMAGE_BASE_URL)){
        }else {
            url = Constants.API_IMAGE_BASE_URL+url;
        }

        String imageUrl = "";
        if (style == FULL_SCREEN){
            imageUrl = url+"?imageMogr2/thumbnail/"+screenWidth*2+"x";
        } else if (style == THIRD_SCREEN){
            imageUrl = url+"?imageMogr2/thumbnail/"+((screenWidth*2)/3)+"x";
        } else if (style == HALF_SCREEN){
            imageUrl = url+"?imageMogr2/thumbnail/"+screenWidth+"x";
        }else if (style == THIRD_SCREEN_CENTER_CROP){
            imageUrl = url+"?imageMogr2/gravity/Center/crop/"+((screenWidth*2)/3)+"x"+((screenWidth*2)/3);
        }else {
            imageUrl = url;
        }
        LogUtils.d("activetrending","image url finish = "+imageUrl);


        return imageUrl;
    }

    //设置图片url地址 并且添加七牛云的图片处理
    //只用作三个图片并列成一行的情况
    public static String setImageUrl(Context context,String url,int width,int height){
        int screenWidth = ScreenUtils.getScreenWidth(context);

        if (url.contains(Constants.API_IMAGE_BASE_URL)){
        }else {
            url = Constants.API_IMAGE_BASE_URL+url;
        }

        String imageUrl = "";
        int px = 0;
        if (width>=height){
            px = height;
        }else {
            px = width;
        }

        imageUrl = url+"?imageMogr2/gravity/Center/crop/"+px+"x"+px
                    +"/thumbnail/"+((screenWidth*2)/3)+"x";

        LogUtils.d("activetrendingTTT","image url finish = "+imageUrl);


        return imageUrl;
    }

    public static int getImageHeightDp(Context context,int pictureHeight,int pictureWidth,float padding,int pictureNumber){
        double screenWidth = DensityUtils.px2dp(context, ScreenUtils.getScreenWidth(context));

        double h = pictureHeight;
        double w = pictureWidth;
        double m = h/w;

        LogUtils.d("image_1","screenWidth = "+screenWidth+",m = "+m+
                ",heigth = "+h+" , width = "+w);

        double height = (screenWidth/pictureNumber-padding)*m;

        return DensityUtils.dp2px(context, (float) height);
    }
}

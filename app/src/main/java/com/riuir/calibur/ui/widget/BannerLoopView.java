package com.riuir.calibur.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.ui.common.IHandler;
import com.riuir.calibur.ui.common.UIHandler;
import com.riuir.calibur.ui.home.card.PostDetailActivity;
import com.riuir.calibur.ui.home.image.ImageDetailActivity;
import com.riuir.calibur.ui.home.score.ScoreDetailActivity;
import com.riuir.calibur.utils.GlideUtils;

import java.util.ArrayList;
import java.util.List;

import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
import calibur.core.http.models.followList.BannerLoopInfo;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import github.chenupt.springindicator.SpringIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BannerLoopView extends RelativeLayout {
    Context context;
    Activity activity;
    ViewPager viewPager;
    SpringIndicator indicator;
    BannerLoopAdapter adapter;
    ApiGet apiGet;
    List<BannerLoopInfo> bannerLoopList;
    List<String> titles = new ArrayList<>();
    View view;
    private UIHandler handler = new UIHandler(Looper.getMainLooper());

    public BannerLoopView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public BannerLoopView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public BannerLoopView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView(){
        activity = (Activity) context;
        setHandler();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.banner_loop_view_layout,this,true);
        viewPager = view.findViewById(R.id.banner_loop_view_pager);


    }
    public void setApiGet(ApiGet apiGet){
        this.apiGet = apiGet;
        setNet();
    }

    private void setNet() {

        RetrofitManager.getInstance().getService(APIService.class)
                .getCallBannerLoop()
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<List<BannerLoopInfo>>(){
                    @Override
                    public void onSuccess(List<BannerLoopInfo> bannerLoopInfos) {
                        bannerLoopList = bannerLoopInfos;
                        setBannerLoopInfo();
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                    }
                });

    }

    public void setBannerLoopInfo(){
        setTitle();
        adapter = new BannerLoopAdapter();
        viewPager.setAdapter(adapter);
        handler.sendEmptyMessageDelayed(0,1000);
//        indicator = view.findViewById(R.id.banner_loop_view_indicator);
//        indicator.setViewPager(viewPager);
    }

    private void setTitle() {
        for (int i = 0; i < bannerLoopList.size(); i++) {
            titles.add(""+i+1);
        }
    }

    class BannerLoopAdapter extends PagerAdapter{

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public int getCount() {
            return bannerLoopList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.main_image_list_item,null);
            RoundedImageView imageView = view.findViewById(R.id.main_image_list_item_image);
            imageView.getLayoutParams().height = DensityUtils.dp2px(context,149);
            TextView name = view.findViewById(R.id.main_image_list_item_image_name);
            GlideUtils.loadImageView(context,
                    GlideUtils.setImageUrl(context,bannerLoopList.get(position).getPoster(),GlideUtils.FULL_SCREEN),
                    imageView);
            name.setText(bannerLoopList.get(position).getTitle());
            setListener(imageView,position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private void setListener(RoundedImageView imageView, final int position) {
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setJump(position);
            }
        });
    }

    private void setJump(int position) {
        BannerLoopInfo data = bannerLoopList.get(position);
        String url = data.getLink();
        String type = "";
        String idStr = "";
        int id;
        Intent intent = new Intent();
        if (url.contains("https://www.calibur.tv/")){
            //本站链接
            url = url.replace("https://www.calibur.tv/","");
            if (url.contains("post")){
                type = "post";
            }else if (url.contains("pin")){
                type = "pin";
            }else if (url.contains("review")){
                type = "review";
            }
            if (url.contains("\\?")){
                url = url.split("\\?")[0];
            }
            idStr = url.replace(type,"").replace("/","");
            id = Integer.parseInt(idStr);
            if (type.equals("post")){
                intent.setClass(context, PostDetailActivity.class);
                intent.putExtra("cardID",id);
            }else if (type.equals("pin")){
                intent.setClass(context, ImageDetailActivity.class);
                intent.putExtra("imageID",id);
            }else {
                intent.setClass(context, ScoreDetailActivity.class);
                intent.putExtra("scoreID",id);
            }
            activity.startActivity(intent);

        }else {
            //非本站链接
        }
    }

    public void setHandler() {
        handler.setHandler(new IHandler() {
            @Override
            public void handleMessage(Message msg) {
                handler(msg);
            }
        });
    }

    private int loopTime = 3;
    private int timePosition = 0;
    private void handler(Message msg){
        switch (msg.what){
            case 0:
                if (loopTime==0){
                    if (viewPager.getCurrentItem()<bannerLoopList.size()-1){
                        viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                    }else {
                        viewPager.setCurrentItem(0);
                    }
                    loopTime = 4;
                }else {
                    loopTime--;
                }
                handler.sendEmptyMessageDelayed(0,1000);
                break;
        }
    }
}

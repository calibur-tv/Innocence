package com.riuir.calibur.assistUtils.activityUtils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.riuir.calibur.ui.home.card.CardPreviewPictureActivity;
import com.riuir.calibur.ui.home.card.CardShowInfoActivity;

import java.util.ArrayList;

public class PerviewImageUtils {
    public static final void startPerviewImage(Context context, ArrayList<String> previewImagesList, String imageUrl, View clickView){
        Intent toPreviewActivityIntent = new Intent(context,CardPreviewPictureActivity.class);

        toPreviewActivityIntent.putStringArrayListExtra("previewImagesList", previewImagesList);

        toPreviewActivityIntent.putExtra("imageUrl",imageUrl);
        //版本大于5.0的时候带有动画
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.startActivity(toPreviewActivityIntent, ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                    clickView, "ToPreviewImageActivity").toBundle());
        }else {
            context.startActivity(toPreviewActivityIntent);
        }
    }

}

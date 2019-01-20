package calibur.share.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;

import calibur.foundation.FoundationContextHolder;
import calibur.share.R;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import calibur.core.http.models.share.ShareDataModel;


public class WXShareUtils {
    private IWXAPI api;
    public static String APP_ID = "wx938caba780eb9fd1";

    private int scene = 0;
    private LoadBitmapTask loadBitmapTask;

    public WXShareUtils(Context context){
        api = WXAPIFactory.createWXAPI(context, APP_ID,true);
    }

    public WXShareUtils register() {
        // 微信分享
        api.registerApp(APP_ID);
        return this;
    }

    public void unregister() {
        try {
            api.unregisterApp();
            if (loadBitmapTask!=null){
                loadBitmapTask.cancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toShareWX(ShareDataModel shareData){
        scene = SendMessageToWX.Req.WXSceneSession;
        loadBitmapTask = new LoadBitmapTask();
        loadBitmapTask.execute(shareData);
    }

    public void toShareWXTimeline(ShareDataModel shareData){
        scene = SendMessageToWX.Req.WXSceneTimeline;
        loadBitmapTask = new LoadBitmapTask();
        loadBitmapTask.execute(shareData);
    }

    public static byte[] getBytesByBitmap(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
        return buffer.array();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {

        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);            }
                i = bmp.getHeight();
                j = bmp.getHeight();
            }
        }
    }

    class LoadBitmapTask extends AsyncTask<ShareDataModel,String,WXMediaMessage>{

        @Override
        protected WXMediaMessage doInBackground(ShareDataModel... shareDatas) {
            Bitmap thumbBmp;
            ShareDataModel shareData = shareDatas[0];

            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl =shareData.getLink();
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = shareData.getTitle();
            msg.description = shareData.getDesc();
            try {
                Bitmap thumb = BitmapFactory.decodeStream(new URL(shareData.getImage()).openStream());
                //注意下面的这句压缩，120，150是长宽。//一定要压缩，不然会分享失败
                thumbBmp = Bitmap.createScaledBitmap(thumb,120,120,true);
            } catch (IOException e) {
                e.printStackTrace();
                thumbBmp = BitmapFactory.decodeResource(FoundationContextHolder.getContext().getResources(), R.drawable.calibur_launcher);
            }
            msg.thumbData =bmpToByteArray(thumbBmp,false);

            return msg;
        }

        @Override
        protected void onPostExecute(WXMediaMessage msg) {
            super.onPostExecute(msg);
            //构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message =msg;
            //WX聊天的scene值
            req.scene = scene;
//        req.userOpenId = getOpenId();
            //调用api接口，发送数据到微信
            api.sendReq(req);
        }
    }

}


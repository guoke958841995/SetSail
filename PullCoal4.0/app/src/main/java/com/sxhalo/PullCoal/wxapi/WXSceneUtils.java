package com.sxhalo.PullCoal.wxapi;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.common.base.Config;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by amoldZhang on 2018/6/26.
 */

public class WXSceneUtils {


    private IWXAPI wxApi;
    private Activity activity;

    public WXSceneUtils(Activity activity){
        this.activity = activity;
        //实例化
        wxApi = WXAPIFactory.createWXAPI(activity, new Config().weChat_APPID);
        wxApi.registerApp(new Config().weChat_APPID);
    }


    /**
     * 发送到聊天界面——WXSceneSession
     *  flage == true WXSceneTimeline 朋友圈
     *  flage == false WXSceneSession 朋友
     */
    public void setWXSceneSession(String title,String targetUrl,String summary,boolean flage){
        IWXAPI wxApi = MyAppLication.getIWXAPI();
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = targetUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = summary;
        Bitmap thumb = BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon_lmb);
        msg.thumbData = bitmap2Bytes(thumb,30);

        // 构造一个Resp
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        // 将req的transaction设置到resp对象中，其中bundle为微信传递过来的intent所带的内容，通过getExtras方法获取
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = flage ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        boolean b = wxApi.sendReq(req); //如果调用成功微信,会返回true
//        Toast.makeText(activity,"调用" + b,Toast.LENGTH_LONG).show();
    }

    /**
     * Bitmap转换成byte[]并且进行压缩,压缩到不大于maxkb
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap, int maxkb) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        int options = 100;
        while (output.toByteArray().length > maxkb&& options != 10) {
            output.reset(); //清空output
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到output中
            options -= 10;
        }
        return output.toByteArray();
    }


    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}

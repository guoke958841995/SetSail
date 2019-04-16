package com.sxhalo.PullCoal.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.view.View;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.APPMessage;
import com.sxhalo.PullCoal.model.SendCarEntity;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionListener;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionUtil;
import com.sxhalo.PullCoal.ui.daiglog.BottomMenuDialog;
import com.sxhalo.PullCoal.ui.imageloader.GlideImageLoader;
import com.sxhalo.PullCoal.utils.GHLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amoldZhang on 2017/1/16.
 */
public class PhotoSelection {

    private int maxImgCount = 1;
    private Activity mActivity;
    //    private String iconName = "icon_image.png";
    private BottomMenuDialog bottomMenuDialog;
//    private String thumbnailPath;

    /**
     * 选择图片的返回码
     */
    public final static int SELECT_IMAGE_RESULT_CODE = 200;
    /**
     * 当前选择的图片的路径
     */
    public String mImagePath;

    /**
     * needCorp 是否需要裁剪 true 是 false 否
     * */
    public  PhotoSelection(Activity mActivity,int maxImgCount, boolean needCorp){
        this.mActivity = mActivity;
        this.maxImgCount = maxImgCount;
        initImagePicker(needCorp);
    }

    private void initImagePicker(boolean needCorp) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(needCorp);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    public void setSelection(boolean flage, Bitmap bm){
        bottomMenuDialog = new BottomMenuDialog.Builder(mActivity)
                .addMenu("拍\u3000\u3000照", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new PermissionUtil().requestPermissions(mActivity,new String[]{Manifest.permission.CAMERA}, new PermissionListener() {
                            @Override
                            public void onGranted() { //用户同意授权
                                bottomMenuDialog.dismiss();
                                takePhoto();
                            }

                            @Override
                            public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

                            }
                        });
                    }
                }).addMenu("相\u3000\u3000册", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new PermissionUtil().requestPermissions(mActivity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
                            @Override
                            public void onGranted() { //用户同意授权
                                bottomMenuDialog.dismiss();
                                pickPhoto();
                            }

                            @Override
                            public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

                            }
                        });

                    }
                }).create(flage,bm);
        bottomMenuDialog.show();
    }

    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        ImagePicker.getInstance().setSelectLimit(maxImgCount);
        Intent intent = new Intent(mActivity, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
        mActivity.startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE);
    }


    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
        //打开选择,本次允许选择的数量
        ImagePicker.getInstance().setSelectLimit(maxImgCount);
        Intent intent1 = new Intent(mActivity, ImageGridActivity.class);
//        /* 如果需要进入选择的时候显示已经选中的图片，
//        * 详情请查看ImagePickerActivity
//        * */
//        intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
        mActivity.startActivityForResult(intent1, SELECT_IMAGE_RESULT_CODE);
    }

    //用来接收用户选择的图片
    ArrayList<ImageItem> images = null;
    String[] proj = { MediaStore.MediaColumns.DATA };
    public void setUploadImage(Intent data,String fieldName) {
        try {
            String imagePath = "";
            images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            if (images != null) {
                imagePath = images.get(0).path;
                toUploadFile(imagePath,fieldName);
            }
        } catch (Exception e) {
            GHLog.e("图片显示", e.toString());
        }

    }

    //上传磅单 type = 0 货运单上传磅单  type = 1 派车单上传磅单
    public void setUploadPoundList(Intent data,String fieldName, int type) {
        try {
            String imagePath = "";
            images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            if (images != null) {
                imagePath = images.get(0).path;
                toUploadFileUtils(imagePath,fieldName, type);
            }
        } catch (Exception e) {
            GHLog.e("图片显示", e.toString());
        }
    }

    /**
     * 图片上传
     * @param fileUrl
     * @param transportOrderCode
     */
    private void toUploadFileUtils(final String fileUrl, String transportOrderCode, int type) {
        try {
            LinkedHashMap<String,String> prm = new LinkedHashMap<String, String>();
            prm.put("transportOrderCode",transportOrderCode);
            new DataUtils(mActivity,prm).uploadSendCar(type, fileUrl,new DataUtils.DataBack<List<APPData<Map<String,Object>>>>() {
                @Override
                public void getData(List<APPData<Map<String,Object>>> dataMemager) {
                    if(dataMemager == null){
                        return;
                    }
                    if (callback != null){
                        callback.onCallBack(dataMemager,fileUrl);
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if (callback != null){
                        callback.onError(e,fileUrl);
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("图片联网过程中",e.toString());
        }
    }


    /**
     * 将图片上传服务器
     */
    private void toUploadFile(final String fileUrl, String fieldName) {

        LinkedHashMap<String,String> prm = new LinkedHashMap<String, String>();
        prm.put("fieldName",fieldName);
        try {
            new DataUtils(mActivity,prm).getPictureUpload(fileUrl,new DataUtils.DataBack<APPMessage>() {
                @Override
                public void getData(APPMessage dataMemager) {
                    if(dataMemager == null){
                        return;
                    }
                    if (callback != null){
                        callback.onCallBack(dataMemager,fileUrl);
                    }
                    //获取图片缩略图，避免OOM
    //                Bitmap bitmap = ImageUtils.getImageThumbnail(fileUrl, ImageUtils.getHeight(mActivity) , ImageUtils.getWidth(mActivity) );
    //                if (logoutIcon != null){
    //                    logoutIcon.setImageBitmap(bitmap);
    //                }
    //                if (Icon != null){
    //                    Icon.setImageBitmap(bitmap);
    //                }
                }

                @Override
                public void getError(Throwable e) {
                    if (callback != null){
                        callback.onError(e,fileUrl);
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("图片联网过程中",e.toString());

        }
    }


    public  Callback callback;
    //实现数据传递
    public void onCallBack(Callback callback) {
        this.callback = callback;
    }
    //创建接口
    public interface Callback<T> {
        void onCallBack(T OBJ,String imagePhth);
        void onError(Throwable e,String imagePhth);
    }

    /**
     * 读取图片的旋转的角度
     * @param path  图片绝对路径 
     * @return  图片的旋转角度 
     */
    public int readPicureDegree (String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt (ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     * @param bm  需要旋转的图片
     * @param degree  旋转角度 
     * @return  旋转后的图片
     */
    public Bitmap rotateBitmapByDegree (Bitmap bm, int degree) {
        Bitmap returnBitmap = null;

        //根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        try {
            returnBitmap = Bitmap.createBitmap(bm, 0,0, bm.getWidth(), bm.getHeight(), matrix, true);
        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }

        if (returnBitmap == null) {
            returnBitmap = bm;
        }

        if (bm != returnBitmap) {
            bm.recycle();
        }
        return returnBitmap;
    }
}

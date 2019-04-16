package com.sxhalo.PullCoal.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.MineDynamic;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionListener;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionUtil;
import com.sxhalo.PullCoal.ui.NoScrollGridView;
import com.sxhalo.PullCoal.ui.daiglog.BottomMenuDialog;
import com.sxhalo.PullCoal.ui.imageloader.Bimp;
import com.sxhalo.PullCoal.ui.imageloader.GlideImageLoader;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.switchbtn.SwitchButton;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.Rom;
import com.sxhalo.PullCoal.utils.StringUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 矿口动态信息发布界面
 * Created by amoldZhang on 2017/11/7.
 */
public class MineDynamicReleaseActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.map_type)
    TextView mapType;
    @Bind(R.id.dynamic_content)
    EditText dynamicContent;
    @Bind(R.id.drever_num)
    EditText dreverNum;
    @Bind(R.id.attr_list_grid)
    NoScrollGridView selectionGridView;
    @Bind(R.id.message_notify_switch)
    SwitchButton messageNotifySwitch;
    @Bind(R.id.iv_upload)
    ImageView ivUpload;
    @Bind(R.id.image_gridview)
    NoScrollGridView imageGridview;

    private List<FilterEntity> filterEntityList = new ArrayList<FilterEntity>();
    private QuickAdapter<FilterEntity> mAdaptet;
    private FilterEntity filterEntity;
    private int ifGoods = 0;
    private BottomMenuDialog bottomMenuDialog;
    private String mImagePath;                       //选择图片路径

    //头像选择
    private ArrayList<ImageItem> selImageList = new ArrayList<ImageItem>(); //当前选择的所有图片
    private int maxImgCount = 9;               //允许选择图片最大数
    private QuickAdapter<ImageItem> imageAdapter;


    @Override
    protected void createMainView(Bundle savedInstanceState) {
        //图片选择器初始化
        initImagePicker();
         /*
         * 防止键盘挡住输入框
         * 不希望遮挡设置activity属性 android:windowSoftInputMode="adjustPan"
         * 希望动态调整高度 android:windowSoftInputMode="adjustResize"
         */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.
                SOFT_INPUT_ADJUST_PAN);
        //锁定屏幕
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_mine_dynamic_release);
        setData();
    }

    private void setData() {
        FilterEntity filterEntity1 = new FilterEntity();
        filterEntity1.setChecked(true);
        filterEntity1.dictCode = "1";
        filterEntity1.dictValue = "缓慢";
        FilterEntity filterEntity2 = new FilterEntity();
        filterEntity2.setChecked(false);
        filterEntity2.dictCode = "2";
        filterEntity2.dictValue = "拥堵";
        FilterEntity filterEntity3 = new FilterEntity();
        filterEntity3.setChecked(false);
        filterEntity3.dictCode = "0";
        filterEntity3.dictValue = "秒装";
        filterEntityList.add(0, filterEntity1);
        filterEntityList.add(1, filterEntity2);
        filterEntityList.add(2, filterEntity3);

        filterEntity = filterEntityList.get(0);
    }

    @Override
    protected void initTitle() {
        mapType.setVisibility(View.VISIBLE);
        mapType.setTextSize(16f);
        mapType.setText("发布");
        title.setText("发布动态");
        initView();
    }

    private void initView() {
        messageNotifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    ifGoods = 1;
                } else {
                    ifGoods = 0;
                }
            }
        });

        mAdaptet = new QuickAdapter<FilterEntity>(mContext, R.layout.item_goods_attrs, filterEntityList) {
            @Override
            protected void convert(BaseAdapterHelper helper, FilterEntity data, final int pos) {
                try {
                    TextView attrName = (TextView) helper.getView().findViewById(R.id.attr_name);
                    attrName.setText(data.dictValue);
                    attrName.setTextSize(16f);
                    /**
                     * 根据选中状态来设置item的背景和字体颜色
                     */
                    if (data.isChecked()) {
                        attrName.setBackgroundResource(R.drawable.background_blue_shape);
                        attrName.setTextColor(Color.WHITE);
                    } else {
                        attrName.setBackgroundResource(R.drawable.background_gray_unselected_shape);
                        attrName.setTextColor(Color.BLACK);
                    }
                } catch (Exception e) {
                    GHLog.e(getClass().getName(),e.toString());
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                }
            }
        };
        selectionGridView.setAdapter(mAdaptet);
        selectionGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //设置当前选中的位置的状态为非。
                if (!filterEntityList.get(arg2).isChecked()) {
                    filterEntityList.get(arg2).setChecked(true);
                    filterEntity = filterEntityList.get(arg2);
                }
                for (int i = 0; i < filterEntityList.size(); i++) {
                    //跳过已设置的选中的位置的状态
                    if (i == arg2) {
                        continue;
                    }
                    filterEntityList.get(i).setChecked(false);
                }
                mAdaptet.notifyDataSetChanged();
            }
        });


    }

    @Override
    protected void getData() {

    }

    @OnClick({R.id.title_bar_left, R.id.map_type, R.id.iv_upload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.map_type:
                showProgressDialog("正在压缩图片，请稍等...");
                List<File> fileList = new ArrayList<File>();
                if (getImages().size()> 0 ){
                    for (ImageItem imageItem:getImages()){
                        try {
                            if (StringUtils.isEmpty(imageItem.path) || !new File(imageItem.path).exists() ){
                                imageItem.path = "-1";
                            }
                        } catch (Exception e) {
                            GHLog.e(getClass().getName(),e.toString());
                            imageItem.path = "-1";
                        }
                        if (!imageItem.path.equals("-1")){
                            File file;
                            if (Rom.isVivo() || Rom.isOppo()){
                                file = Bimp.scal(imageItem.path); //将图片压缩至需要大小
                            }else{
                                file = Bimp.compress(imageItem.path); //将图片压缩至需要大小
                            }
                            fileList.add(file);
                        }
                    }
                }
                dismisProgressDialog();
                onSubmit(fileList);
                break;
            case R.id.iv_upload:
                setSelection(false, null);
                break;
        }
    }

    private void onSubmit(List<File> fileList) {
        try {
            String mineMouthId = getIntent().getStringExtra("InfoDepartId");
            if (mineMouthId == null) {
                return;
            }
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("mineMouthId", mineMouthId);
            params.put("goodsAvailable", ifGoods + "");
            if (StringUtils.isEmpty(dreverNum.getText().toString().trim())) {
                displayToast("亲，当前排队车辆数尚未输入！");
                return;
            }
//            if ("0".equals(dreverNum.getText().toString().trim())) {
//                displayToast("亲，您输入的数字必须大于0 ！");
//                return;
//            }
            params.put("vehicleQueueLength", dreverNum.getText().toString().trim());
            params.put("isCongestion", filterEntity.dictCode);

            if (StringUtils.isEmpty(dynamicContent.getText().toString().trim())) {
                displayToast("亲，您还没有输入当前矿口动态！");
                return;
            }
            //用户第二次点击时，让按钮失去监听
            mapType.setOnClickListener(null);
            params.put("summary", dynamicContent.getText().toString().trim());
            new DataUtils(this, params).getCoalCompanyRealtimeInfoCreate(fileList, new DataUtils.DataBack<MineDynamic>() {
                @Override
                public void getData(MineDynamic dataList) {
                    try {
                        if (dataList == null) {
                            return;
                        }
                        displayToast("动态发布成功！");
                        setResult(200, new Intent(MineDynamicReleaseActivity.this, MineDynamicActivity.class));
                        finish();
                    } catch (Exception e) {
                        GHLog.e(getClass().getName(),e.toString());
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if (Rom.isVivo()|| Rom.isOppo()){
                        displayToast("动态发布成功！");
                        setResult(200, new Intent(MineDynamicReleaseActivity.this, MineDynamicActivity.class));
                        finish();
                    }else{
                        displayToast("动态发布失败，请稍后再试！");
                        finish();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
        }
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }




    private void showSelectImageView() {
        imageAdapter = new QuickAdapter<ImageItem>(this, R.layout.adapter_image,selImageList) {
            @Override
            protected void convert(BaseAdapterHelper helper, ImageItem image, int position) {
                try {
                    ImageView  ivImage = (ImageView) helper.getView().findViewById(R.id.iv_image);
                    if (position == selImageList.size()) {
                        ivImage.setImageBitmap(BitmapFactory.decodeResource(
                                getResources(), R.drawable.icon_addpic_unfocused));
                        if (position == 9) {
                            ivImage.setVisibility(View.GONE);
                        }
                    } else {
                        if (StringUtils.isEmpty(image)){
                            selImageList.remove(position);
                        }else{
                            if (image.path.equals("-1")){
                                getImageManager().loadResImage(R.drawable.icon_addpic_unfocused,ivImage);
                            }else{
                                getImageManager().loadLocalImage(image.path,ivImage);
                            }
                        }
                    }
                } catch (Exception e) {
                    GHLog.e(getClass().getName(),e.toString());
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                }
            }
        };
        imageGridview.setAdapter(imageAdapter);
        imageGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                if (position == selImageList.size()-1 && isAdded) {
                    setSelection(false, null);
                } else {
                    //打开预览
                    Intent intentPreview = new Intent(MineDynamicReleaseActivity.this, ImagePreviewDelActivity.class);
                    intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) getImages());
                    intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                    intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                    startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                }
            }
        });
    }


    //用来接收用户选择的图片
    ArrayList<ImageItem> images = new ArrayList<ImageItem>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT){
                ArrayList<ImageItem> selectImage = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (selectImage.size() == 1){
                    images.add(selectImage.get(0));
                }else{
                    images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                }
                ArrayList<ImageItem> list = new ArrayList<>();
                list.addAll(images);
                //过滤图片路径不存在的无效图片
                for (ImageItem imageItem:list){
                    try {
                        if (StringUtils.isEmpty(imageItem.path) || !new File(imageItem.path).exists() ){
                            images.remove(imageItem);
                        }
                    } catch (Exception e) {
                        GHLog.e(getClass().getName(),e.toString());
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                        images.remove(imageItem);
                    }
                }
                if (images != null) {
                    selImageList.clear();
                    selImageList.addAll(images);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    selImageList.clear();

                    selImageList.addAll(images);
                }
            }
        }

        if (0 < selImageList.size() && data != null){
            if (selImageList.size() < maxImgCount) {
                displayToast("共选择 " + selImageList.size() + " 张照片");
            }
            if (selImageList.size() < maxImgCount) {
                selImageList.add(selImageList.size(),new ImageItem());
                isAdded = true;
            } else {
                isAdded = false;
            }
        }
        if (imageAdapter == null) {
            showSelectImageView();
        } else {
            imageAdapter.replaceAll(selImageList);
        }
        if (selImageList.size() == 0) {
            imageGridview.setVisibility(View.GONE);
            ivUpload.setVisibility(View.VISIBLE);
        } else {
            imageGridview.setVisibility(View.VISIBLE);
            ivUpload.setVisibility(View.GONE);
        }
    }

    private boolean isAdded;   //是否额外添加了最后一个图片
    public List<ImageItem> getImages() {
        //由于图片未选满时，最后一张显示添加图片，因此这个方法返回真正的已选图片
        if (isAdded) return new ArrayList<>(selImageList.subList(0, selImageList.size() - 1));
        else return selImageList;
    }

    /**
     * 选择图片的返回码
     */
    public static final int REQUEST_CODE_SELECT = 100;  // 图片选择 拍照
    public static final int REQUEST_CODE_PREVIEW = 101;  //预览

    public void setSelection(boolean flage, Bitmap bm) {
        bottomMenuDialog = new BottomMenuDialog.Builder(this)
                .addMenu("拍\u3000\u3000照", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new PermissionUtil().requestPermissions(mContext,new String[]{Manifest.permission.CAMERA}, new PermissionListener() {
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
                        new PermissionUtil().requestPermissions(mContext,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
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
                }).create(flage, bm);
        bottomMenuDialog.show();
    }

    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        //打开选择,本次允许选择的数量
        ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
        Intent intent = new Intent(MineDynamicReleaseActivity.this, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
        startActivityForResult(intent, REQUEST_CODE_SELECT);
    }


    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
        //打开选择,本次允许选择的数量
        ImagePicker.getInstance().setSelectLimit(maxImgCount);
        Intent intent1 = new Intent(MineDynamicReleaseActivity.this, ImageGridActivity.class);
        /* 如果需要进入选择的时候显示已经选中的图片，
        * 详情请查看ImagePickerActivity
        * */
        intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);

        startActivityForResult(intent1, REQUEST_CODE_SELECT);
    }

}
